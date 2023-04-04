package com.tigerobo.x.pai.biz.biz.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.biz.entity.*;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.api.vo.biz.ModelQueryVo;
import com.tigerobo.x.pai.api.vo.biz.ModelVo;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.converter.APIConvert;
import com.tigerobo.x.pai.biz.converter.ModelCategoryConvert;
import com.tigerobo.x.pai.biz.converter.ModelConvert;
import com.tigerobo.x.pai.biz.converter.TaskConvert;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.biz.process.ModelProcessor;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.*;
import com.tigerobo.x.pai.dal.biz.dao.model.ModelCategoryDao;
import com.tigerobo.x.pai.dal.biz.entity.*;
import com.tigerobo.x.pai.dal.biz.entity.model.ModelCategoryPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WebModelService {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ModelDao modelDao;
    @Autowired
    private TaskModelDao taskModelDao;

    @Autowired
    private DemandDao demandDao;
    @Autowired
    private WebDemandService webDemandService;
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private WebApiService webApiService;

    @Autowired
    private WebTaskService webTaskService;
    @Autowired
    private EntityTagDao entityTagDao;

    @Autowired
    protected ModelProcessor modelProcessor;
    @Autowired
    @Qualifier(value = "executorFactory")
    private ExecutorFactory executorFactory;
//    @Autowired
//    private TagFactory tagFactory;
    @Autowired
    private TagService tagService;
    @Autowired
    private ApiProcessor apiProcessor;

    @Autowired
    private WebGroupService webGroupService;

    @Autowired
    private ModelCategoryDao modelCategoryDao;

    public ModelCategoryDto getCategoryByUid(String uid){
        if (StringUtils.isBlank(uid)){
            return null;
        }

        ModelCategoryPo po = modelCategoryDao.getByUid(uid);
        return ModelCategoryConvert.po2dto(po);
    }

    public List<ModelCategoryDto> getBasicTags(){

        List<ModelCategoryPo> all = modelCategoryDao.getAll();

        return ModelCategoryConvert.po2dtos(all);
    }


    public List<ModelCategoryDto> getBasicTags(Integer type){
        List<ModelCategoryPo> all = modelCategoryDao.getByType(type);
        return ModelCategoryConvert.po2dtos(all);
    }
//
//    public ModelVo getDetail(String uuid){
//        ModelDo modelPo = modelDao.getByUuid(uuid);
//        if (modelPo ==  null){
//            return null;
//        }
//        ApiDo apiPo = apiDao.getByModelUuid(uuid);
//        if (apiPo == null){
//            return null;
//        }
//        Model model = ModelConvert.convert(modelPo);
//        API api = APIConvert.convert(apiPo);
//        ModelVo modelVo = new ModelVo();
//        modelVo.setModel(model);
//        modelVo.setId(model.getId());
//        modelVo.setUuid(model.getUuid());
//        modelVo.setApi(api);
//
//        buildIndices(modelVo);
//        List<Tag> modelTags = getModelTags(model.getId());
//        if (modelTags == null){
//            modelTags = new ArrayList<>();
//        }
//        modelVo.setTagList(modelTags);
//
//        return modelVo;
//    }
//    public PageVo<ModelVo> query(ModelQueryVo queryVo){
//        PageVo<ModelVo> pageInfo = new PageVo<>();
//
//        pageInfo.setPageNum(queryVo.getPageNum());
//        pageInfo.setPageSize(queryVo.getPageSize());
//        List<ApiDo> onlineList = apiDao.getOnlineList();
//        if (CollectionUtils.isEmpty(onlineList)){
//            return pageInfo;
//        }
//        List<Integer> apiModelIds = onlineList.stream().map(api -> api.getModelId()).collect(Collectors.toList());
//
//        List<Integer> idsInTag = null;
//        Map<String,Object> params = queryVo.getParams();
//        if (params!=null){
//            Object tag = params.get("tag");
//            String tagStr = "";
//            if (tag!=null){
//                if (tag instanceof List){
//                    List list = (List)tag;
//                    if (!CollectionUtils.isEmpty(list)){
//                       tagStr = (String) list.get(0);
//                    }
//                }else if (tag instanceof String){
//                    tagStr = (String) tag;
//                }
//            }
//            if (StringUtils.isNotBlank(tagStr)){
//                List<EntityTagDo> tagList = entityTagDao.getTagList(tagStr, Entity.Type.MODEL.getVal());
//                if (!CollectionUtils.isEmpty(tagList)){
//                    idsInTag = tagList.stream().map(t -> t.getEntityId()).collect(Collectors.toList());
//                }
//            }
//
//        }
//
//        List<Integer> targetModelIds = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(idsInTag)){
//            targetModelIds = apiModelIds.stream().filter(idsInTag::contains).collect(Collectors.toList());
//        }else {
//            targetModelIds = apiModelIds;
//        }
//
//        if (CollectionUtils.isEmpty(targetModelIds)){
//            return pageInfo;
//        }
//
//        List<ModelDo> modelList = modelDao.query(targetModelIds);
//
//        List<ModelVo> build = build(modelList);
//        pageInfo.setList(build);
//        pageInfo.setTotal(build.size());
//        return pageInfo;
//    }
//
//    private List<ModelVo> build(List<ModelDo> modelList) {
//
//        if (CollectionUtils.isEmpty(modelList)){
//            return new ArrayList<>();
//        }
//        List<Model> models = modelList.stream().map(ModelConvert::convert).collect(Collectors.toList());
//
//        Map<Integer, Group> idGroupMap = modelList.stream().parallel().collect(Collectors.toMap(m -> m.getId(), m -> {
//            String createBy = m.getCreateBy();
//            String groupUuid = m.getGroupUuid();
//            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
//            if (group == null) {
//                group = new Group();
//            }
//            return group;
//        }));
//
//        List<ModelVo> modelVos = models.stream().map(m -> {
//            ModelVo modelVo = new ModelVo();
//            modelVo.setModel(m);
//            modelVo.setId(m.getId());
//            modelVo.setUuid(m.getUuid());
//            return modelVo;
//        }).collect(Collectors.toList());
//
//        modelVos.stream().parallel().forEach(modelVo -> {
//            Model model = modelVo.getModel();
////            String createBy = model.getCreateBy();
////            String groupUuid = model.getGroup().getGroupUuid();
////            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
//            Group group = idGroupMap.get(model.getId());
//            model.setGroup(group);
//        });
//
//        List<Integer> ids = modelList.stream().map(m -> m.getId()).collect(Collectors.toList());
//        List<EntityTagDo> modelTags = entityTagDao.getModelTags(ids);
//
//        Map<Integer,List<String>> modelIdTagMap = new HashMap<>();
//        if (!CollectionUtils.isEmpty(modelTags)){
//            for (EntityTagDo modelTag : modelTags) {
//                Integer entityId = modelTag.getEntityId();
//                List<String> tags = modelIdTagMap.computeIfAbsent(entityId, k -> new ArrayList<>());
//                String tagUid = modelTag.getTagUid();
//                if (!tags.contains(tagUid)){
//                    tags.add(tagUid);
//                }
//            }
//        }
//        for (ModelVo modelVo : modelVos) {
//            Integer id = modelVo.getModel().getId();
//            List<String> tags = modelIdTagMap.get(id);
//
//            if (!CollectionUtils.isEmpty(tags)){
//                List<Tag> tagsList = tagService.get(tags);
//                modelVo.setTagList(tagsList);
//            }else {
//                modelVo.setTagList(new ArrayList<>());
//            }
//        }
//
//        // 获取关联需求
//
//        // 获取关联API
//        modelVos.forEach(modelVo -> {
//            Executable executable = this.executorFactory.get(modelVo.getModel().getUuid());
//            if (executable!=null){
//                modelVo.setApi(executable.profile());
//            }
//
//        });
//        modelVos.forEach(this::buildIndices);
//
//
//        // 返回结果
//        return modelVos;
//    }
//
//    private List<Tag> getModelTags(Integer modelId){
//
//        List<EntityTagDo> modelTags = entityTagDao.getModelTags(Arrays.asList(modelId));
//        if (CollectionUtils.isEmpty(modelTags)){
//            return null;
//        }
//        List<String> tagIds = modelTags.stream().map(mt -> mt.getTagUid()).collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(tagIds)){
//            return null;
//        }
//        return tagService.get(tagIds);
//    }

//    public List<IndexItem> buildIndices(ModelVo modelVo) {
//        // 模型浏览总量 & 模型下载总量
//        String uuid = modelVo.getModel().getUuid();
//        modelVo.addIndexItem(this.modelProcessor.getIndices(uuid));
//        // 接口调用总量 & 接口调用量按月
//        modelVo.addIndexItem(this.apiProcessor.indices(uuid));
//
//        return modelVo.getIndices();
//    }

    public Model getByUuid(String uuid) {
        ModelDo modelDo = modelDao.getByUuid(uuid);
        return ModelConvert.convert(modelDo);
    }

    public ModelVo create(String taskUuid, Integer userId) {
        if (userId == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        UserDo user = userDao.getById(userId);
        TaskDo taskDo = taskDao.getByUuid(taskUuid);
        Task task = TaskConvert.convert(taskDo);
        // todo 参数校验

        Preconditions.checkArgument(task != null, "需求任务不存在");

        String demandUuid = task.getDemandUuid();
        Preconditions.checkArgument(StringUtils.isNotBlank(demandUuid), "需求不存在");

        DemandDo demandDo = demandDao.getByUuid(demandUuid);
        Preconditions.checkArgument(demandDo != null, "需求不存在");

        TaskModelDo userTaskModel = taskModelDao.getUserTaskModel(taskUuid, String.valueOf(userId));
        if (userTaskModel != null) {
            throw new IllegalArgumentException("任务已领取");
        }

        Model model = task.toDefaultModel();
        model.setGroupUuid(user.getCurrGroupUuid());

        Group group = new Group();
        group.setId(user.getCurrGroupId());
        group.setGroupUuid(user.getCurrGroupUuid());
        model.setGroup(group);

//        model.setStatus(Model.Status.PREPARE);
//        model.setLimited(Limited.GROUP_PRIVATE);
//        model.setSubject(Model.Subject.TASK_MODEL);

        // todo 插入模型

        log.info("add-model:{}", JSON.toJSONString(model));
        ModelDo modelDo = addModel(user, model);

        TaskModelDo taskModel = new TaskModelDo();
        taskModel.setUuid(IdGenerator.getId());
        taskModel.setTaskId(task.getId());
        taskModel.setTaskUuid(task.getUuid());
        taskModel.setModelId(modelDo.getId());
        taskModel.setModelUuid(modelDo.getUuid());

        taskModel.setStatus(modelDo.getStatus());

        taskModel.setCreateBy(String.valueOf(userId));
        taskModel.setUpdateBy(String.valueOf(userId));
        taskModelDao.insert(taskModel);

        webDemandService.adoptDemand(demandDo, userId);

        ModelVo modelVo = new ModelVo();
        Model convert = ModelConvert.convert(modelDo);
        modelVo.setModel(convert);
        modelVo.setId(convert.getId());
        modelVo.setUuid(convert.getUuid());
        modelVo.setTask(task);

        return modelVo;
    }


    /**
     * 需求任务，适配模型api展示
     * @param apiReq
     */
    public void completeModel(ModelApiReq apiReq) {
        String taskUuid = apiReq.getTaskUuid();
        Integer userId = apiReq.getUserId();
        String apiUrl = apiReq.getApiUrl();

        String style = apiReq.getStyle();
        String demo = apiReq.getDemo();
        TaskModelDo userTaskModel = taskModelDao.getUserTaskModel(taskUuid, String.valueOf(userId));
        Preconditions.checkArgument(userTaskModel != null, "用户任务模型不存在");
        String modelUuid = userTaskModel.getModelUuid();

        ModelDo modeldo = modelDao.getByUuid(modelUuid);
        Preconditions.checkArgument(modeldo != null, "模型不存在");

        modeldo.setApiUri(apiUrl);
        modeldo.setStyle(style);
        modeldo.setStatus(Demand.Phase.COMPLETED.getVal());

        UserDo userDo = userDao.getById(userId);
        if (userDo != null){
            if (!StringUtils.isEmpty(userDo.getCurrGroupUuid())&&userDo.getCurrGroupId()!=null){
                modeldo.setGroupUuid(userDo.getCurrGroupUuid());
                modeldo.setGroupId(userDo.getCurrGroupId());
            }else {
                modeldo.setGroupUuid(userDo.getUuid());
                modeldo.setGroupId(userDo.getId());
            }


        }
        modeldo.setIntro(apiReq.getTectIntro());
        modeldo.setDesc(apiReq.getSceneIntro());
        modelDao.update(modeldo);

        Model convert = ModelConvert.convert(modeldo);
        webApiService.createOrUpdate(convert, demo);

        webTaskService.modelOnline(taskUuid, modeldo.getId(), modelUuid,apiReq.getSceneIntro(),apiReq.getTectIntro(),apiReq);
        //todo
    }

    private ModelDo addModel(UserDo userDo, Model model) {
        Integer userId = userDo.getId();

        ModelDo modelDo = ModelConvert.convert2po(model);

        modelDo.setCreateBy(String.valueOf(userId));
        modelDo.setUpdateBy(String.valueOf(userId));
        modelDo.setGroupId(userDo.getCurrGroupId());
        modelDo.setGroupUuid(userDo.getCurrGroupUuid());
        modelDao.insert(modelDo);
        // todo 建立任务关联

        Preconditions.checkArgument(modelDo.getId() != null, "服务异常，领取失败");
        return modelDo;
    }

    public PageInfo<ModelVo> getUserModels(Integer userId) {
        PageInfo<ModelVo> pageInfo = new PageInfo<>();
        List<ModelDo> modelDoList = modelDao.getUserModelList(String.valueOf(userId));

        if (CollectionUtils.isEmpty(modelDoList)) {
            return null;
        }

        List<Model> modelList = modelDoList.stream().map(po -> ModelConvert.convert(po)).collect(Collectors.toList());
        List<ModelVo> voList = modelList.stream().map(model -> {
            ModelVo modelVo = new ModelVo();
            modelVo.setModel(model);
            modelVo.setId(model.getId());
            modelVo.setUuid(model.getUuid());
            return modelVo;
        }).collect(Collectors.toList());

        voList.parallelStream().forEach(vo->
        {
            Model model = vo.getModel();
            String createBy = model.getCreateBy();
            String groupUuid = model.getGroupUuid();
            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
            if (group!=null){
                model.setGroup(group);
            }
        });
        pageInfo.setList(voList);
        return pageInfo;
    }

}
