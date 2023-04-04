package com.tigerobo.x.pai.biz.biz.service;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.biz.entity.API;
import com.tigerobo.x.pai.api.biz.entity.Dataset;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.TaskQueryVo;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.api.vo.biz.TaskVo;
import com.tigerobo.x.pai.api.vo.biz.req.TaskDeleteFileReq;
import com.tigerobo.x.pai.api.vo.biz.req.TaskUploadFileReq;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.biz.process.ModelProcessor;
import com.tigerobo.x.pai.biz.biz.process.TaskProcessor;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.converter.DatasetConvert;
import com.tigerobo.x.pai.biz.converter.TaskConvert;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.*;
import com.tigerobo.x.pai.dal.biz.entity.*;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebTaskService implements IBusinessDetailFetchService {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private TaskDatasetDao taskDatasetDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private WebDemandService webDemandService;

    @Autowired
    @Qualifier(value = "executorFactory")
    private ExecutorFactory executorFactory;
    @Autowired
    private ModelProcessor modelProcessor;
    @Autowired
    private ApiProcessor apiProcessor;

    @Autowired
    private TaskProcessor taskProcessor;

    @Autowired
    private WebGroupService webGroupService;

    @Autowired
    private TaskModelDao taskModelDao;

    @Autowired
    private ModelDao modelDao;
    @Autowired
    private EntityTagDao entityTagDao;


    @Autowired
    private TagService tagService;

    @Autowired
    private ShareLogService shareLogService;

    @Autowired
    private ApiDao apiDao;
    @Autowired
    private BusinessCommentService businessCommentService;
    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private WebModelService webModelService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private ApiBaseService apiBaseService;
    public int count() {
        return taskDao.count();
    }

    public Task getTask(String uuid) {
        TaskDo po = taskDao.getByUuid(uuid);
        return TaskConvert.convert(po);
    }


    public PageVo<TaskVo> query(TaskQueryVo queryVo) {
        Integer userId = ThreadLocalHolder.getUserId();
        PageVo<TaskVo> pageInfo = new PageVo<>();
        pageInfo.setPageNum(queryVo.getPageNum());
        pageInfo.setPageSize(queryVo.getPageSize());

        List<ApiDo> onlineList = apiBaseService.getOnlineListCache();
        Map<String, ApiDo> apiMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(onlineList)) {

            for (ApiDo apiDo : onlineList) {
                apiMap.put(apiDo.getUuid(), apiDo);
            }
        }
        final boolean isAdmin = roleService.isAdmin(userId);
//        List<TaskVo> taskVos = buildTaskVos(queryVo, tag, taskDos, isAdmin, apiMap);

        List<TaskVo> baseVos = getBaseVosCache();
        if (!isAdmin&&!CollectionUtils.isEmpty(baseVos)){
            final String userIdStr = userId == null?null:userId.toString();
            baseVos = baseVos.stream().filter(vo->{
                final Task task = vo.getTask();
                final String createBy = task.getCreateBy();
                if (Objects.equals(userIdStr,createBy)){
                    return true;
                }
                final Integer scope = task.getScope();
                if (scope!=null){
                    if (scope.equals(20)){
                        task.setGroup(new Group());
                    }
                    return !scope.equals(10);
                }
                return true;
            }).collect(Collectors.toList());
        }

        if (baseVos!=null){
            baseVos.parallelStream().forEach(vo->{
                final ApiDo apiDo = apiMap.get(vo.getTask().getModelUuid());
                this.buildIndices(vo, false, apiDo);
            });
        }

        List<TaskVo> targetList = new ArrayList<>();
        if (baseVos!=null&&baseVos.size()>0){
            String tag = getTagParam(queryVo);
            List<String> modelUids = initModelCategorys(queryVo);
            String keyword = queryVo.getKeyword();
            for (TaskVo taskVo : baseVos) {
                if (queryFilter(tag, modelUids, keyword, taskVo)) {
                    targetList.add(taskVo);
                }
            }
        }

        List<TaskVo> headList = sortLogic(targetList);
        pageInfo.setList(headList);
        pageInfo.setTotal(headList.size());
        return pageInfo;
    }


    private List<TaskVo> getBaseVosCache(){
        String key = "pai:task:vos";

        final String s = redisCacheService.get(key);
        if (!StringUtils.isEmpty(s)){
            return JSON.parseArray(s,TaskVo.class);
        }
        final List<TaskVo> baseVos = getBaseVos();

        final String json = JSON.toJSONString(baseVos);

        redisCacheService.set(key,json,10*60);
        return baseVos;
    }
    private List<TaskVo> getBaseVos(){
        List<TaskDo> taskDos = taskDao.completeList();
        if (CollectionUtils.isEmpty(taskDos)){
            return new ArrayList<>();
        }

        List<TaskVo> taskVos = new ArrayList<>();

        Map<Integer, List<Tag>> demandTagEntityMap = getDemandTagMap(taskDos);
        Map<String,TaskVo> apiIdVoMap = new HashMap<>();
        Map<String,TaskDo> uuidPoMap = new HashMap<>();

        for (TaskDo taskDo : taskDos) {
            String modelUuid = taskDo.getModelUuid();
            if (StringUtils.isEmpty(modelUuid)) {
                continue;
            }
            uuidPoMap.put(taskDo.getUuid(),taskDo);
            Task task = TaskConvert.convert(taskDo);
            TaskVo taskVo = new TaskVo();
            taskVo.setTask(task);
            List<Tag> tags = demandTagEntityMap.get(taskDo.getDemandId());
            if (tags == null){
                tags = new ArrayList<>();
            }
            taskVo.setTagList(tags);
            apiIdVoMap.put(modelUuid,taskVo);
            taskVos.add(taskVo);
        }

        taskVos.parallelStream().forEach(vo->{
            final Task task = vo.getTask();
            final String uuid = task.getUuid();
            final TaskDo taskDo = uuidPoMap.get(uuid);
            final String createBy = taskDo.getCreateBy();
            final String groupUuid = taskDo.getGroupUuid();
            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
            if (group == null){
                task.setGroup(new Group());
            }else {
                task.setGroup(group);
            }
        });


        final List<ModelCategoryDto> basicTags = webModelService.getBasicTags();

        Map<String,ModelCategoryDto> categoryDtoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(basicTags)){
            for (ModelCategoryDto basicTag : basicTags) {
                categoryDtoMap.put(basicTag.getUid(),basicTag);
            }
        }
        Map<String,ApiDo> apiIdMap = new HashMap<>();

        List<ApiDo> onlineList = apiBaseService.getOnlineListCache();
        for (ApiDo apiDo : onlineList) {
            final String uuid = apiDo.getUuid();
            final TaskVo taskVo = apiIdVoMap.get(uuid);
            if (taskVo == null){
                continue;
            }
            apiIdMap.put(uuid,apiDo);
            API profile = this.executorFactory.buildExecutable(apiDo).profileClean();
            taskVo.setApi(profile);
            if (profile != null) {
                String baseModelUid = profile.getBaseModelUid();
                ModelCategoryDto baseModel = categoryDtoMap.get(baseModelUid);
                if (baseModel != null) {
                    taskVo.setBaseModel(baseModel);
                }
            }
        }


        return taskVos;
    }

    private Map<Integer, List<Tag>> getDemandTagMap(List<TaskDo> taskDos) {
        Map<Integer,List<Tag>> demandTagEntityMap = new HashMap<>();

        final List<Integer> demandIds = taskDos.stream().map(t -> t.getDemandId()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        final List<EntityTagDo> demandTags = entityTagDao.getDemandTags(demandIds);


        Map<Integer,List<EntityTagDo>> demandTagMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(demandTags)){
            for (EntityTagDo demandTag : demandTags) {
                final Integer entityId = demandTag.getEntityId();
                final List<EntityTagDo> entityTagDos = demandTagMap.computeIfAbsent(entityId, k -> new ArrayList<>());
                entityTagDos.add(demandTag);
            }
            final List<String> tagUidList = demandTags.stream()
                    .map(dt -> dt.getTagUid()).distinct().collect(Collectors.toList());

            final List<Tag> tags = tagService.get(tagUidList);

            Map<String,Tag> tagMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(tags)){
                for (Tag tag : tags) {
                    tagMap.put(tag.getUid(),tag);
                }
            }

            for (Map.Entry<Integer, List<EntityTagDo>> entry : demandTagMap.entrySet()) {
                final Integer demandId = entry.getKey();
                final List<EntityTagDo> value = entry.getValue();

                if(value == null){
                    continue;
                }
                List<Tag> demandSubTags = new ArrayList<>();
                for (EntityTagDo entityTagDo : value) {
                    final Tag tag = tagMap.get(entityTagDo.getTagUid());
                    demandSubTags.add(tag);
                }
                demandTagEntityMap.put(demandId,demandSubTags);
            }

        }
        return demandTagEntityMap;
    }


    private List<String> initModelCategorys(TaskQueryVo queryVo) {


        List<String> modelUids = new ArrayList<>();

        String baseModelUid = queryVo.getBaseModelUid();


        if (!StringUtils.isEmpty(baseModelUid)) {
            modelUids.add(baseModelUid);
        }
        final String cvModelUid = queryVo.getCvUid();
        if (!StringUtils.isEmpty(cvModelUid)) {
            if (!modelUids.contains(cvModelUid)) {
                modelUids.add(cvModelUid);
            }
        }

        final String textModelUid = queryVo.getNlpUid();

        if (!StringUtils.isEmpty(textModelUid)) {
            if (!modelUids.contains(textModelUid)) {
                modelUids.add(textModelUid);
            }
        }
        return modelUids;
    }

    private boolean queryFilter(String tag, List<String> baseModelUids, String keyword, TaskVo taskVo) {
        if (taskVo == null) {
            return false;
        }
        if (!StringUtils.isEmpty(tag)) {
            List<Tag> tagList = taskVo.getTagList();
            if (CollectionUtils.isEmpty(tagList)) {
                return false;
            }
            boolean hasSameTag = false;
            for (Tag subTag : tagList) {
                if (subTag.getUid().equalsIgnoreCase(tag)) {
                    hasSameTag = true;
                }
            }

            if (!hasSameTag) {
                return false;
            }
        }
        API api = taskVo.getApi();
        if (api == null) {
            return false;
        }
//
//        if (!StringUtils.isEmpty(baseModelUid)&&!baseModelUid.equalsIgnoreCase(api.getBaseModelUid())){
//            return false;
//        }
        if (!CollectionUtils.isEmpty(baseModelUids) &&
                (StringUtils.isEmpty(api.getBaseModelUid()) || !baseModelUids.contains(api.getBaseModelUid()))) {
            return false;
        }

        if (!StringUtils.isEmpty(keyword)) {
            keyword = keyword.toLowerCase();

            Task task = taskVo.getTask();

            String name = task.getName();
            boolean nameContain = name.toLowerCase().contains(keyword);
            Group group = task.getGroup();
            boolean groupContain = group != null && !StringUtils.isEmpty(group.getName()) && group.getName().toLowerCase().contains(keyword);
            ModelCategoryDto baseModel = taskVo.getBaseModel();
            boolean modelTagMatch = false;
            if (baseModel != null) {
                modelTagMatch = baseModel.getText().toLowerCase().contains(keyword);
            }

            if (!nameContain && !groupContain && !modelTagMatch) {
                return false;
            }
        }
        return true;
    }

    private List<TaskVo> sortLogic(List<TaskVo> taskVos) {
        List<TaskVo> scoreList = new ArrayList<>();
        List<TaskVo> headList = new ArrayList<>();
        List<TaskVo> subList = new ArrayList<>();

        if (CollectionUtils.isEmpty(taskVos)) {
            return taskVos;
        }
        taskVos.sort((a, b) -> b.getTask().getScore().compareTo(a.getTask().getScore()));
        Date date = DateUtils.addDays(new Date(), -8);
        int count = 0;
        for (int i = 0; i < taskVos.size(); i++) {
            TaskVo taskVo = taskVos.get(i);
            Task task = taskVo.getTask();
            if (task.getScore() > 0) {
                scoreList.add(taskVo);
                continue;
            }
            Date updateTime = task.getUpdateTime();
            if (count < 3 && updateTime != null && updateTime.after(date)) {
                headList.add(taskVo);
                count++;
            } else {
                subList.add(taskVo);
            }
        }

        if (headList.size() > 1) {
            headList.sort((a, b) -> {
                return b.getTask().getUpdateTime().compareTo(a.getTask().getUpdateTime());
            });
        }
        subList.sort(Comparator.comparing(TaskVo::getCallNum).reversed());
        headList.addAll(subList);

        scoreList.addAll(headList);
        return scoreList;
    }


    private String getTagParam(TaskQueryVo queryVo) {


        if (!StringUtils.isEmpty(queryVo.getIndustryTagUid())) {
            return queryVo.getIndustryTagUid();
        }


        String tag = null;
        if (queryVo.hasValue("tag")) {
            List<String> tagUidList = queryVo.get("tag", String.class);
            if (!CollectionUtils.isEmpty(tagUidList)) {
                tag = tagUidList.get(0);
            }
        }
        return tag;
    }

    public TaskVo getDetail(String uuid) {
        Integer userId = ThreadLocalHolder.getUserId();

        boolean special = roleService.isAdmin();
        TaskVo taskVo = doGetDetail(uuid, true, special, true, null, null);

        return taskVo;
    }

    public TaskVo getDetailByAppName(String appShortName) {
        Integer userId = ThreadLocalHolder.getUserId();
        boolean special =roleService.isAdmin(userId);

        TaskDo taskDo = taskDao.getByAppName(appShortName);
        if (taskDo == null) {
            return null;
        }
        TaskVo taskVo = doGetDetail(taskDo.getUuid(), true, special, true, null, null);

        return taskVo;
    }


    public void addSharerLog(String uuid, Integer shareId, BusinessEnum shareType) {
        if (shareType == null) {
            return;
        }
        if (shareId != null && shareId > 0) {
            shareLogService.addLog(uuid, shareType.getType(), shareId);
        }
    }

    public TaskVo doGetDetail(String uuid, boolean pageDetail, boolean specialUser, boolean needIndiceDetail,
                              ApiDo apiDo, TaskDo taskDo) {
        if (taskDo == null) {
            taskDo = taskDao.getByUuid(uuid);

            if (taskDo == null) {
                return null;
            }
        }
        String modelUuid = taskDo.getModelUuid();
        if (StringUtils.isEmpty(modelUuid)) {
            return null;
        }
        if (apiDo == null) {
            apiDo = apiDao.getByModelUuid(modelUuid);
        }
        if (apiDo == null) {
            return null;
        }

        String groupUuid = taskDo.getGroupUuid();
        String createBy = taskDo.getCreateBy();

        Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
        if (group == null) {
//            group = new Group();
        }
        TaskVo taskVo = initTaskTag(specialUser, taskDo, group);
        if (taskVo == null) {
            return null;
        }

        initApi(pageDetail, apiDo, modelUuid, taskVo);

        this.buildIndices(taskVo, needIndiceDetail, apiDo);
        // 返回结果

        initNum(uuid, taskVo);

        if (pageDetail) {
            Integer userId = ThreadLocalHolder.getUserId();
            List<UserThumbPo> thumbUpList = userThumbService.getUserList(userId, Arrays.asList(uuid), BusinessEnum.APP.getType());
            if (!CollectionUtils.isEmpty(thumbUpList)) {
                taskVo.setThumbUp(true);
            }

            int commentNum = businessCommentService.countUserCommentNum(userId, uuid, BusinessEnum.APP.getType());
            taskVo.setUserHasComment(commentNum > 0);
        }

        return taskVo;
    }

    private TaskVo getBase(String uuid){
        final TaskDo taskDo = taskDao.getByUuid(uuid);
        Task task = TaskConvert.convert(taskDo);

        TaskVo taskVo = new TaskVo();
        taskVo.setTask(task);
        return taskVo;
    }
    private void initNum(String uuid, TaskVo taskVo) {
        int commentNum = businessCommentService.countFromCache(uuid, BusinessEnum.APP.getType());
        taskVo.setCommentNum(commentNum);
        int thumbUpNum = userThumbService.count(uuid, BusinessEnum.APP.getType());
        taskVo.setThumbUpNum(thumbUpNum);

        int shareNum = shareLogService.getCount(uuid, BusinessEnum.APP.getType());
        taskVo.setShareNum(shareNum);
    }

    private TaskVo initTaskTag(boolean specialUser, TaskDo taskDo, Group group) {
        Task task = TaskConvert.convert(taskDo);

        Integer scope = taskDo.getScope();
        task.setGroup(group);
        if (!specialUser) {
            if (Group.Scope.PERSONAL.getVal().equals(scope)) {
                task.setGroup(new Group());
            } else if (Group.Scope.PRIVATE.getVal().equals(scope)) {
                return null;
            }
        }


        TaskVo taskVo = new TaskVo();
        taskVo.setTask(task);


        List<EntityTagDo> tags = entityTagDao.getDemandTags(task.getDemandId());


        if (!CollectionUtils.isEmpty(tags)) {
            List<String> collect = tags.stream().map(tag -> tag.getTagUid()).distinct().collect(Collectors.toList());
            List<Tag> tagsList = tagService.get(collect);
            taskVo.setTagList(tagsList);
        } else {
            taskVo.setTagList(new ArrayList<>());
        }
        return taskVo;
    }

    private void initApi(boolean needApi, ApiDo apiDo, String modelUuid, TaskVo taskVo) {
        if (!StringUtils.isEmpty(modelUuid) && needApi) {
            try {
                API profile = this.executorFactory.buildExecutable(apiDo).profileClean();
                taskVo.setApi(profile);

                if (profile != null) {
                    String baseModelUid = profile.getBaseModelUid();
                    ModelCategoryDto baseModel = webModelService.getCategoryByUid(baseModelUid);
                    if (baseModel != null) {
                        taskVo.setBaseModel(baseModel);
                    }
                }
            } catch (Exception ex) {
                log.warn("api is not ready:model:{}", modelUuid);
            }
        }
    }


    public List<GroupVo> getTaskGroups(String taskUuid) {

        List<TaskModelDo> taskModels = taskModelDao.getTaskModels(taskUuid);

        if (CollectionUtils.isEmpty(taskModels)) {
            return new ArrayList<>();
        }


        List<String> modelIds = taskModels.stream().map(tm -> tm.getModelUuid()).filter(t -> !StringUtils.isEmpty(t)).collect(Collectors.toList());


        List<ModelDo> userModelList = modelDao.getByUuids(modelIds);

        if (CollectionUtils.isEmpty(userModelList)) {
            return new ArrayList<>();
        }

        Map<String, Group> keyGroupMap = new HashMap<>();
        for (ModelDo modelDo : userModelList) {
            String groupUuid = modelDo.getGroupUuid();
            String createBy = modelDo.getCreateBy();
            Group group = webGroupService.getCreateByOrGroup(createBy, null);
            if (group != null) {
                keyGroupMap.put(group.getUuid(), group);
            }
            if (keyGroupMap.size() >= 5) {
                break;
            }
        }

        if (keyGroupMap.isEmpty()) {
            return new ArrayList<>();
        }
        return keyGroupMap.values().stream().map(k -> GroupVo.builder().group(k).build()).collect(Collectors.toList());
    }


    public void modelOnline(String taskUuId, Integer modelId, String modelUuid, String sceneText, String tectText, ModelApiReq apiReq) {
        TaskDo taskDo = taskDao.getByUuid(taskUuId);
        Preconditions.checkArgument(taskDo != null, "需求任务不存在");

        TaskDo update = new TaskDo();
        update.setId(taskDo.getId());
        update.setModelId(modelId);
        update.setModelUuid(modelUuid);
        update.setStatus(90);
        update.setIntro(sceneText);
        update.setDesc(tectText);
        update.setAppShortName(apiReq.getAppShortName());

        if (!StringUtils.isEmpty(apiReq.getSlogan())){
            update.setSlogan(apiReq.getSlogan());
        }


        Date modelUpdateTime = apiReq.getModelUpdateTime();
        if (modelUpdateTime != null) {
            update.setModelUpdateTime(modelUpdateTime);
        }

        taskDao.update(update);

        webDemandService.modelOnline(taskDo.getDemandUuid(), modelUuid);
    }

    public void buildIndices(TaskVo taskVo, boolean needIndexDetail, ApiDo apiDo) {

        // 任务浏览总量
        Task task = taskVo.getTask();
        if (task == null) {
            return;
        }
        if (!StringUtils.isEmpty(task.getUuid())) {
            final int viewNum = this.taskProcessor.getViewNum(task.getUuid());
            taskVo.addIndexItem(this.taskProcessor.buildIndex(viewNum));

        }

        // 模型下载总量
        String modelUuid = task.getModelUuid();
        if (!StringUtils.isEmpty(modelUuid)) {
            final int modelCount = this.modelProcessor.getModelCount(modelUuid);
            taskVo.addIndexItem(this.modelProcessor.build(modelCount));
            taskVo.setViewNum(modelCount);

            Integer total = this.apiProcessor.doGetTotal(modelUuid, apiDo.getAmlRelIds());
            if (total!=null){
                taskVo.setCallNum(total);
            }
            if (needIndexDetail) {
                taskVo.addIndexItem(this.apiProcessor.indices(modelUuid));
            }else {
                taskVo.addIndexItem(this.apiProcessor.buildIndexItems(total));
            }

        }



    }


    public List<Dataset> getTaskDatasetList(String taskUuid) {

        List<TaskDatasetDo> datasetList = taskDatasetDao.getByTaskUuid(taskUuid);

        if (!CollectionUtils.isEmpty(datasetList)) {
            List<Integer> datasetId = datasetList.stream().map(d -> d.getDatasetId()).collect(Collectors.toList());

            List<DatasetDo> datasetDos = datasetDao.getByIds(datasetId);
            return DatasetConvert.po2dtoList(datasetDos);
        }
        return new ArrayList<>();
    }

    public Task createTask(Task task, Integer userId) {
        Preconditions.checkArgument(userId != null, "用户未登录");


        UserDo userDo = userDao.getById(userId);
        Preconditions.checkArgument(userDo != null, "用户不存在");

        TaskDo convert = TaskConvert.convert(task);
        taskDao.insert(convert);
        return TaskConvert.convert(convert);
    }

    public void addDataset(List<Dataset> datasetList, Task task, Integer userId) {

        TaskUploadFileReq uploadFileReq = new TaskUploadFileReq();
        List<FileData> collect = datasetList.stream().map(dataset -> {
            FileData fileData = new FileData();
            fileData.setName(dataset.getName());
            fileData.setFilePath(dataset.getFilePath());
            return fileData;
        }).collect(Collectors.toList());

        uploadFileReq.setDatasetList(collect);
        uploadFileReq.setTaskUuid(task.getUuid());
        addDataset(uploadFileReq, userId);
    }

    public void addDataset(TaskUploadFileReq fileReq, Integer userId) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(fileReq.getDatasetList()), "数据集为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(fileReq.getTaskUuid()), "id为空");

        String taskUuid = fileReq.getTaskUuid();
        TaskDo taskDo = taskDao.getByUuid(taskUuid);
//        Task task = taskProcessor.get2(taskUuid);

        Preconditions.checkArgument(taskDo != null, "需求任务不存在");

        List<FileData> datasetList = fileReq.getDatasetList();
        String createBy = StringUtils.isEmpty(userId) ? "sys" : String.valueOf(userId);
        for (FileData fileData : datasetList) {
            DatasetDo dataset = new DatasetDo();
            dataset.setFilePath(fileData.getFilePath());
            dataset.setName(fileData.getName());
            dataset.setUuid(IdGenerator.getId());
            dataset.setCreateBy(createBy);
            dataset.setUpdateBy(createBy);
//            dataset.setGroupUuid();
//            dataset.setGroup(authorization.getGroup());

            datasetDao.insert(dataset);

            Integer id = dataset.getId();
            Preconditions.checkArgument(id != null && id > 0);


            TaskDatasetDo taskDatasetDo = new TaskDatasetDo();
            taskDatasetDo.setTaskUuid(taskUuid);
            taskDatasetDo.setDatasetId(dataset.getId());
            taskDatasetDo.setDatasetUuid(dataset.getUuid());
            taskDatasetDo.setUuid(IdGenerator.getId());
            taskDatasetDo.setCreateBy(createBy);
            taskDatasetDo.setUpdateBy(createBy);
            taskDatasetDo.setTaskId(taskDo.getId());
            taskDatasetDo.setScene(Dataset.Scene.OTHER.getVal());
            taskDatasetDao.insert(taskDatasetDo);
        }
    }

    public void delDataset(TaskDeleteFileReq req) {

        Integer userId = ThreadLocalHolder.getUserId();
        Preconditions.checkArgument(userId != null, "请重新登录");
//        authorizeService.authorize(req);

        String datasetUuid = req.getDatasetUuid();
        Preconditions.checkArgument(!StringUtils.isEmpty(datasetUuid), "参数为空");
        String taskUuid = req.getTaskUuid();
        TaskDo taskDo = taskDao.getByUuid(taskUuid);
        Task task = TaskConvert.convert(taskDo);
        Preconditions.checkArgument(task != null, "需求任务不存在");
        taskDatasetDao.del(taskUuid, datasetUuid, userId);
    }

    public PageInfo<TaskVo> getHomeTask(Integer userId) {

        PageInfo<TaskVo> pageInfo = new PageInfo<>();
        if (userId == null) {
            return pageInfo;
        }
        String userStr = String.valueOf(userId);
        List<TaskModelDo> mineTaskModels = taskModelDao.getMine(userStr);
        if (CollectionUtils.isEmpty(mineTaskModels)) {
            return pageInfo;
        }
        List<String> taskUuids = mineTaskModels.stream().filter(tm -> tm.getTaskId() != null).map(tm -> tm.getTaskUuid()).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(taskUuids)) {
            return null;
        }
        List<TaskDo> userTask = taskDao.getListByUuids(taskUuids);
        if (CollectionUtils.isEmpty(userTask)) {
            return pageInfo;
        }
        List<Task> taskList = userTask.stream().map(po -> TaskConvert.convert(po)).collect(Collectors.toList());

        List<TaskVo> taskVos = taskList.stream().map(t -> {
            TaskVo taskVo = new TaskVo();

            taskVo.setTask(t);
            return taskVo;
        }).collect(Collectors.toList());

        taskVos.parallelStream().forEach(taskVo -> {
            Task task = taskVo.getTask();
            String createBy = task.getCreateBy();
            String groupUuid = task.getGroupUuid();
            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
            if (group != null) {
                task.setGroup(group);
            }
        });

        pageInfo.setList(taskVos);

        return pageInfo;
    }

    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {
        return getBase(id);
    }
}
