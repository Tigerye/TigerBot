package com.tigerobo.x.pai.biz.user;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.biz.entity.*;
import com.tigerobo.x.pai.api.dto.ModelCommitDto;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.user.UserModelVo;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.biz.service.*;
import com.tigerobo.x.pai.biz.converter.*;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.*;
import com.tigerobo.x.pai.dal.biz.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserModelService {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskModelDao taskModelDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private DemandDao demandDao;

    @Autowired
    private UserService userService;

    @Autowired
    private WebGroupService webGroupService;

    @Autowired
    private ModelCommitDao modelCommitDao;

    @Autowired
    private RoleService roleService;
    @Autowired
    private ApiProcessor apiProcessor;

    @Autowired
    @Qualifier(value = "executorFactory")
    private ExecutorFactory executorFactory;

    public List<ModelCommitDto> getCommitList(String modelId){
        Integer userId = ThreadLocalHolder.getUserId();
        ModelDo model = modelDao.getByUuid(modelId);
        Preconditions.checkArgument(model!=null,"模型不存在");

        String createBy = model.getCreateBy();
        if (!String.valueOf(userId).equals(createBy)){

            boolean admin = roleService.isAdmin();
            if (!admin){
                throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
            }
        }

        List<ModelCommitPo> modelCommitList = modelCommitDao.getModelCommitList(modelId);

        return ModelCommitConvert.po2dto(modelCommitList);
    }

    public void addCommit(ModelCommitDto modelCommitDto){

        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }
        String modelId = modelCommitDto.getModelId();
        Preconditions.checkArgument(StringUtils.isNotBlank(modelId),"modelId为空");
        ModelDo model = modelDao.getByUuid(modelId);

        Preconditions.checkArgument(model!=null,"模型不存在");
        String createBy = model.getCreateBy();
        Preconditions.checkArgument(String.valueOf(userId).equals(createBy),"用户没权限");


        String name = modelCommitDto.getName();
        String filePath = modelCommitDto.getFilePath();
        String memo = modelCommitDto.getMemo();
        Preconditions.checkArgument(StringUtils.isNotBlank(name),"名称为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(filePath),"文件路径为空");
//        Preconditions.checkArgument(StringUtils.isNotBlank(memo),"提交信息为空");

        ModelCommitPo po = ModelCommitConvert.dto2po(modelCommitDto);
        po.setUserId(userId);
        po.setModelId(modelId);

        modelCommitDao.addCommit(po);
    }

    public void deleteCommit(Integer id){

        ModelCommitPo commitPo = modelCommitDao.load(id);
        if (commitPo == null){
            return;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        if (Objects.equals(userId,commitPo.getUserId())){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }

        modelCommitDao.delete(id);
    }

    public UserModelVo getModelDetail(String modelUuid){

        ModelDo model = modelDao.getByUuid(modelUuid);

        Preconditions.checkArgument(model!=null,"模型不存在");


        UserModelVo userModelVo = buildUserModelVo(model);

        TaskModelDo taskModel = taskModelDao.getByModelId(modelUuid);

        if (taskModel!=null){
            String taskUuid = taskModel.getTaskUuid();
            userModelVo.setApplicationId(taskUuid);

            DemandDo demandDo = demandDao.getByUuid(taskUuid);
            Demand demand = DemandConvert.convert(demandDo);
            if (demand!=null){
                Group group = webGroupService.getCreateByOrGroup(demandDo.getCreateBy(), demandDo.getGroupUuid());
                demand.setGroup(group);
                userModelVo.setDemand(demand);
            }
        }
        try {
            API profile = this.executorFactory.get(model.getUuid()).profileClean();
            userModelVo.setApi(profile);
            if (profile!=null){
                List<IndexItem> indices = this.apiProcessor.indices(profile.getApiKey());
                userModelVo.setIndices(indices);
            }

        }catch (Exception ex){
            log.warn("api is not ready:model:{}",model.getUuid());
        }

        return userModelVo;
    }

    /**
     * 以model为中心
     * @param userId
     * @return
     */
    public PageVo<UserModelVo> getUserTaskList(Integer userId) {

        if (userId == null){
            return null;
        }
        Integer loginUserId = ThreadLocalHolder.getUserId();

        if (!Objects.equals(loginUserId,userId)&&!roleService.isAdmin()){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }

        String userStr = String.valueOf(userId);

        List<ModelDo> userModelList = modelDao.getUserModelList(userStr);
        if (CollectionUtils.isEmpty(userModelList)){
            return null;
        }

        List<String> modelUuIds = userModelList.stream().map(m -> m.getUuid()).collect(Collectors.toList());

        List<TaskModelDo> userTaskModelList = taskModelDao.getByModelIds(modelUuIds);
        if (CollectionUtils.isEmpty(userTaskModelList)){
            return null;
        }

        List<String> taskUuids = userTaskModelList.stream().map(m -> m.getTaskUuid()).collect(Collectors.toList());
//
//        List<Task> userTaskList = getUserAdoptTasks(taskUuids);
//        if (CollectionUtils.isEmpty(userTaskList)){
//            return null;
//        }

        List<Demand> demandList = getUserAdoptDemands(taskUuids);
        if (CollectionUtils.isEmpty(demandList)){
            return null;
        }

        User userByOther = userService.getUserByOther(userId);
        List<UserModelVo> userTaskVos = buildList(userModelList, userTaskModelList, demandList, userByOther);
        PageVo<UserModelVo> pageInfo = new PageVo<>();

        pageInfo.setList(userTaskVos);
        pageInfo.setTotal(CollectionUtils.isEmpty(userTaskVos)?0:userTaskVos.size());

        return pageInfo;
    }

    private List<UserModelVo> buildList(List<ModelDo> modelList, List<TaskModelDo> userTaskModelList,
                                        List<Demand> demandList, User tasker){

        Map<String, TaskModelDo> modelTaskMap = list2map(userTaskModelList, "modelUuid");

        Map<String, Demand> demandMap = list2map(demandList, "uuid");
        List<UserModelVo> userTaskVos = new ArrayList<>();
        for (ModelDo modelDo : modelList) {

            String modelUuid = modelDo.getUuid();

            TaskModelDo taskModelDo = modelTaskMap.get(modelUuid);
            if (taskModelDo == null||StringUtils.isEmpty(taskModelDo.getTaskUuid())){
                continue;
            }

            String taskUuid = taskModelDo.getTaskUuid();
            Demand demand = demandMap.get(taskUuid);


//            Model model = ModelConvert.convert(modelDo);

            UserModelVo userTaskVo = buildUserModelVo(modelDo);

            userTaskVo.setTasker(tasker);

            if (demand!=null){
                userTaskVo.setDemand(demand);
                userTaskVo.setApplicationId(demand.getUuid());
            }


//            userTaskVo.setModel(model);


            userTaskVos.add(userTaskVo);

        }

        return userTaskVos;
    }

    private UserModelVo buildUserModelVo(ModelDo modelDo) {
        UserModelVo userTaskVo = new UserModelVo();
        userTaskVo.setName(modelDo.getName());
        userTaskVo.setCreateTime(modelDo.getCreateTime());

        userTaskVo.setUuid(modelDo.getUuid());
        return userTaskVo;
    }

    private <T> Map<String,T> list2map(List<T> list,String key){
        Map<String,T> map = new HashMap<>();
        if (CollectionUtils.isEmpty(list)){
            return map;
        }
        for (T t : list) {
            String pre = JSON.parseObject(JSON.toJSONString(t)).getString(key);
            if (StringUtils.isEmpty(pre)){
                continue;
            }
            map.put(pre,t);
        }
        return map;
    }

    private String getModelRelTask(String modelUuid,List<TaskModelDo> userTaskModelList){
        return null;
    }

    private List<Task> getUserAdoptTasks(List<String> taskUuids) {

        if (CollectionUtils.isEmpty(taskUuids)){
            return null;
        }

        List<TaskDo> userTaskList = taskDao.getListByUuids(taskUuids);
        List<Task> taskList = userTaskList.stream().map(po -> TaskConvert.convert(po)).collect(Collectors.toList());
        return taskList;
    }


    private List<Demand> getUserAdoptDemands(List<String> taskUuids) {

        if (CollectionUtils.isEmpty(taskUuids)){
            return null;
        }

        List<DemandDo> demandList = demandDao.getByUuids(taskUuids);
        if (CollectionUtils.isEmpty(demandList)){
            return null;
        }
        List<Demand> demands = demandList.stream().map(po -> DemandConvert.convert(po)).collect(Collectors.toList());

        demands.parallelStream().forEach(demand -> {

            Group group = webGroupService.getCreateByOrGroup(demand.getCreateBy(), demand.getGroupUuid());
            demand.setGroup(group);
        });

        //todo
        return demands;
    }
}
