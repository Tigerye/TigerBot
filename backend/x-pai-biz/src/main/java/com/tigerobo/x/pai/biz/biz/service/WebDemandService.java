package com.tigerobo.x.pai.biz.biz.service;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.biz.entity.*;
import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.biz.DemandDetailVo;
import com.tigerobo.x.pai.api.vo.biz.DemandPhase;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.enums.DemandLogTypeEnum;
import com.tigerobo.x.pai.api.enums.PermissionTypeEnum;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.process.ApiProcessor;
import com.tigerobo.x.pai.biz.biz.process.ModelProcessor;
import com.tigerobo.x.pai.biz.biz.process.TaskProcessor;
import com.tigerobo.x.pai.biz.converter.DatasetConvert;
import com.tigerobo.x.pai.biz.converter.DemandConvert;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.*;
import com.tigerobo.x.pai.dal.biz.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebDemandService implements IBusinessDetailFetchService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DemandDao demandDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private WebGroupService webGroupService;
    @Autowired
    private TaskModelDao taskModelDao;
    @Autowired
    private ContractInfoDao contractInfoDao;
    @Autowired
    private DemandLogDao demandLogDao;
    @Autowired
    private EntityTagDao entityTagDao;
    @Autowired
    private TagService tagService;
    @Autowired
    private TaskDatasetDao taskDatasetDao;
    @Autowired
    private DatasetDao datasetDao;
    @Autowired
    private DemandDatasetService demandDatasetService;
    @Autowired
    private TaskProcessor taskProcessor;
    @Autowired
    private ModelProcessor modelProcessor;
    @Autowired
    private ApiProcessor apiProcessor;

    @Autowired
    private WebModelService webModelService;
    @Autowired
    @Qualifier(value = "executorFactory")
    private ExecutorFactory executorFactory;

    public Demand getByUuid(String uuid) {

        DemandDo demandDo = demandDao.getByUuid(uuid);

        return DemandConvert.convert(demandDo);
    }

    public void auditPass(String uuid, Integer userId) {
        UserDo userDo = userDao.getById(userId);
        Preconditions.checkArgument(userDo != null, "用户未登录");

        if (userDo.getRoleType()!=1){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }

        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo != null, "需求不存在");

        Integer phase = demandDo.getPhase();

        boolean canAudit = Demand.Phase.WAIT_AUDIT.getVal().equals(phase)||Demand.Phase.AUDIT_DECLINE.getVal().equals(phase);
        if (!canAudit) {
            throw new IllegalArgumentException("当前状态不能审核");
        }

        DemandDo update = new DemandDo();
        update.setId(demandDo.getId());
        update.setPhase(Demand.Phase.AUDIT_PASS.getVal());
        update.setAuditTime(new Date());
        update.setUpdateBy(String.valueOf(userId));
        demandDao.update(update);
        //todo 添加日志
    }

    public void auditDecline(String uuid,String reason,Integer userId) {
        UserDo userDo = userDao.getById(userId);
        Preconditions.checkArgument(userDo != null, "用户未登录");

        if (userDo.getRoleType()!=1){
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }
        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo != null, "需求不存在");

        Integer phase = demandDo.getPhase();

        if (!Demand.Phase.WAIT_AUDIT.getVal().equals(phase)) {
            throw new IllegalArgumentException("当前状态不能审核");
        }

        DemandDo update = new DemandDo();
        update.setId(demandDo.getId());
        update.setPhase(Demand.Phase.AUDIT_DECLINE.getVal());
        if (!StringUtils.isEmpty(reason)){
            update.setReason(reason);
        }
        update.setUpdateBy(String.valueOf(userId));
        demandDao.update(update);

        addDemandLog(demandDo.getId(),userDo, DemandLogTypeEnum.AUDIT_DECLINE.getType(),reason);
        //todo 添加日志
    }
    @Deprecated
    public void addEvaluation(String uuid,String evaluation){
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid),"需求id为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(evaluation),"评估文件地址为空");
        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo!=null,"需求不存在");

        TaskDo taskDo = taskDao.getByDemandUuid(uuid);
        Preconditions.checkArgument(demandDo!=null,"需求任务不存在");
        TaskDo update = new TaskDo();
        update.setId(taskDo.getId());
        update.setEvaluation(evaluation);
        taskDao.update(update);

        //todo 添加日志
    }


    public void addDemandEvaluation(String uuid,String evaluation){
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid),"需求id为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(evaluation),"评估文件地址为空");
        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo!=null,"需求不存在");

        DemandDo update = new DemandDo();
        update.setId(demandDo.getId());
        update.setEvaluation(evaluation);
        demandDao.update(update);

        //todo 添加日志
    }


    public void deleteDemand(String uuid,String reason){
        Preconditions.checkArgument(!StringUtils.isEmpty(uuid),"需求id为空");
        Preconditions.checkArgument(!StringUtils.isEmpty(reason),"请填写原因");

        Integer userId = ThreadLocalHolder.getUserId();
        UserDo userDo = userDao.getById(userId);
        if (userDo==null||userDo.getRoleType()==0){
            log.info("del:userId:{}",userId);
            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION);
        }

        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo!=null,"需求不存在");

        DemandDo update = new DemandDo();
        update.setId(demandDo.getId());
        update.setIsDeleted(true);

        update.setReason(reason);
        update.setUpdateBy(String.valueOf(userId));

        demandDao.update(update);

        addDemandLog(demandDo.getId(),userDo, DemandLogTypeEnum.DELETE.getType(),reason);
    }


    public void testPass(String uuid, Integer userId) {
        UserDo userDo = userDao.getById(userId);
        Preconditions.checkArgument(userDo != null, "用户不存在");

        DemandDo demandDo = demandDao.getByUuid(uuid);
        Preconditions.checkArgument(demandDo != null, "需求不存在");

        Integer phase = demandDo.getPhase();

        if (!Demand.Phase.WAIT_TEST.getVal().equals(phase)) {
            throw new IllegalArgumentException("当前状态不能测试通过");
        }

        DemandDo update = new DemandDo();
        update.setId(demandDo.getId());
        update.setPhase(Demand.Phase.TEST_PASS.getVal());
        update.setTestPassTime(new Date());
        update.setUpdateBy(String.valueOf(userId));
        demandDao.update(update);
        //todo 添加日志
    }


    public List<DemandPhase> getDemandPhase(String demandUuid) {

        DemandDo demand = demandDao.getByUuid(demandUuid);
        if (demand == null) {
            return new ArrayList<>();
        }
        List<DemandPhase> phases = new ArrayList<>();
        Date createTime = demand.getCreateTime();
        Integer phase = demand.getPhase();
        List<Demand.Phase> usePhaseList = Arrays.asList(
                Demand.Phase.WAIT_AUDIT,
                Demand.Phase.AUDIT_PASS,
                Demand.Phase.ON_DEVELOP,
                Demand.Phase.WAIT_TEST,
                Demand.Phase.TEST_PASS,
                Demand.Phase.HAS_SIGN_CONTRACT,
                Demand.Phase.COMPLETED
        );


        boolean preStatus = false;
        for (int i = 0; i < usePhaseList.size(); i++) {

            Demand.Phase phaseEnum = usePhaseList.get(i);
            Date time = null;
            if (!preStatus) {
                switch (phaseEnum) {
                    case WAIT_AUDIT:
                        time = demand.getCreateTime();
                        break;
                    case AUDIT_PASS:
                        time = demand.getAuditTime();
                        break;
                    case ON_DEVELOP:
                        time = demand.getAdoptTime();
                        break;
                    case WAIT_TEST:
                        time = demand.getModelOnlineTime();
                        break;
                    case TEST_PASS:
                        time = demand.getTestPassTime();
                        break;
                    case HAS_SIGN_CONTRACT:
                        time = demand.getContractCompleteTime();
                        break;
                    case COMPLETED:
                        time = demand.getCompleteTime();
                        break;
                }
            }
            boolean same = phaseEnum.getVal().equals(phase);
            if (same) {
                preStatus = true;
            }
            phases.add(buildPhase(same, phaseEnum, time));
        }

        return phases;
    }

    private DemandPhase buildPhase(boolean same, Demand.Phase phaseEnum, Date time) {

        return DemandPhase.builder().date(time).name(phaseEnum.getDesc()).currentPhase(same).build();
    }


    public void modelOnline(String demandUuid,String modelUuid){

        DemandDo demandDo = demandDao.getByUuid(demandUuid);
        Preconditions.checkArgument(demandDo!=null,"需求不存在");

        Integer phase = demandDo.getPhase();

        if (!Demand.Phase.WAIT_TEST.getVal().equals(phase)||demandDo.getModelOnlineTime()==null){
            DemandDo update = new DemandDo();

            update.setId(demandDo.getId());
            update.setPhase(Demand.Phase.WAIT_TEST.getVal());
            if (demandDo.getModelOnlineTime() == null){
                update.setModelOnlineTime(new Date());
            }
            update.setModelUuid(modelUuid);
            demandDao.update(update);
        }
    }

    public void adoptDemand(DemandDo demandDo, Integer userId) {

        if (demandDo == null) {
            return;
        }
        if (Demand.Phase.AUDIT_PASS.getVal().equals(demandDo.getPhase()) || demandDo.getAdoptTime() == null) {
            DemandDo update = new DemandDo();
            update.setId(demandDo.getId());
            if (demandDo.getAdoptTime()==null){
                update.setAdoptTime(new Date());
            }
            if (Demand.Phase.AUDIT_PASS.getVal().equals(demandDo.getPhase())){
                update.setPhase(Demand.Phase.ON_DEVELOP.getVal());
            }
            demandDao.update(update);
        }
        //todo,log
    }

    public List<PermissionTypeEnum> getTaskDemandPermissionTypeEnums(Demand demand) {
        List<PermissionTypeEnum> permissionList = new ArrayList<>();

        Integer userId = ThreadLocalHolder.getUserId();

        if (userId != null) {
            String uuid = demand.getUuid();

            UserDo userDo = userDao.getById(userId);
            boolean platAdmin = userDo != null && UserRoleTypeEnum.hasRole(userDo.getRoleType(), UserRoleTypeEnum.SUPER_MAN);

            String createBy = demand.getCreateBy();
            boolean owner = Objects.equals(createBy, String.valueOf(userId));
            Demand.Phase phase = demand.getPhase();
            if (platAdmin){
                permissionList.add(PermissionTypeEnum.ADMIN);
            }
            if (owner || platAdmin) {
                permissionList.add(PermissionTypeEnum.VIEW_DEMAND_SUGGEST);
                if (Demand.Phase.COMPLETED != phase && Demand.Phase.CANCEL != phase) {
                    permissionList.add(PermissionTypeEnum.DEMAND_MODIFY);
                    permissionList.add(PermissionTypeEnum.ADD_FILE);
                }
                if (Demand.Phase.WAIT_TEST == phase) {
                    permissionList.add(PermissionTypeEnum.TEST_PASS);

                }
                ContractInfoPo contract = null;

                Integer contractId = demand.getContractId();
                if (contractId!=null&&contractId>0){
                    contract = contractInfoDao.getById(contractId);
                }
                if (Demand.Phase.WAIT_TEST == phase||Demand.Phase.TEST_PASS == phase) {
                    if (contract == null){
                        if (owner){
                            permissionList.add(PermissionTypeEnum.SIGN_CONTRACT);
                        }
                    }
                }

                if (contract !=null){
                    permissionList.add(PermissionTypeEnum.VIEW_CONTRACT);
                }
                if (Demand.Phase.TEST_PASS == phase||Demand.Phase.HAS_SIGN_CONTRACT == phase) {
                    permissionList.add(PermissionTypeEnum.COMPLETE);
                }
            }
            if (platAdmin) {
                if (Demand.Phase.COMPLETED != phase && Demand.Phase.CANCEL != phase) {
                    permissionList.add(PermissionTypeEnum.ADD_DEMAND_SUGGEST);
                    permissionList.add(PermissionTypeEnum.ADD_DATASET);
                    if (Demand.Phase.WAIT_AUDIT == phase||Demand.Phase.AUDIT_DECLINE==phase) {
                        permissionList.add(PermissionTypeEnum.AUDIT);
                    }
                    if (Demand.Phase.WAIT_AUDIT == phase) {
                        permissionList.add(PermissionTypeEnum.AUDIT_DECLINE);
                    }
                }
            }
            if ((!owner||platAdmin)&&Demand.Phase.COMPLETED != phase &&Demand.Phase.TEST_PASS != phase &&Demand.Phase.HAS_SIGN_CONTRACT != phase && Demand.Phase.CANCEL != phase && Demand.Phase.WAIT_AUDIT != phase) {
                TaskModelDo userTaskModel = taskModelDao.getUserTaskModel(uuid, String.valueOf(userId));
                permissionList.add(userTaskModel == null ? PermissionTypeEnum.TASK_JOIN : PermissionTypeEnum.TASK_SUBMIT);
            }

        }

        return permissionList;
    }


    private void addDemandLog(Integer demandId,UserDo user,Integer logType,String content){

        DemandLogPo logPo = new DemandLogPo();

        logPo.setDemandId(demandId);
        logPo.setType(logType);
        logPo.setContent(content);

        logPo.setOperatorId(user.getId());
        logPo.setOperator(user.getName());

        demandLogDao.addLog(logPo);

    }


    public DemandDetailVo getDetail(String uuid){
        Integer userId = ThreadLocalHolder.getUserId();
        UserDo userDo = userDao.getById(userId);
        boolean special = isSpecial(userDo);
        DemandDetailVo taskVo = doGetDetail(uuid, true, special, userId);

        return taskVo;
    }
    private boolean isSpecial(UserDo userDo) {
        boolean special = false;
        if (userDo !=null&& UserRoleTypeEnum.hasRole(userDo.getRoleType(),UserRoleTypeEnum.SUPER_MAN)){
            special = true;
        }
        return special;
    }



    public DemandDetailVo doGetDetail(String uuid, boolean needApi, boolean specialUser, Integer userId){
        Demand demand = getByUuid(uuid);
//        Preconditions.checkArgument(demand!=null,"需求不存在");
        if (demand == null){
            return null;
        }
        String createBy = demand.getCreateBy();

        Integer taskScope = demand.getScope();
        boolean isTaskPrivate = taskScope !=null&&taskScope.equals(Group.Scope.PERSONAL.getVal());
        if (isTaskPrivate&&!specialUser){
            boolean demandSameUser = String.valueOf(userId).equals(createBy);
            if (!demandSameUser){
                return null;
            }
        }

        String groupUuid = demand.getGroupUuid();

        Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
        if (group == null){
            group = new Group();
        }

        demand.setGroup(group);

        DemandDetailVo demandDetailVo = new DemandDetailVo();

        String evaluation = demand.getEvaluation();
        Map<String,Object> task = new HashMap<>();
        task.put("evaluation",evaluation);
        demandDetailVo.setTask(task);

        List<EntityTagDo> tags = entityTagDao.getDemandTags(demand.getId());
        if (!CollectionUtils.isEmpty(tags)){
            List<String> collect = tags.stream().map(tag -> tag.getTagUid()).distinct().collect(Collectors.toList());
            List<Tag> tagsList = tagService.get(collect);
            demandDetailVo.setTagList(tagsList);
//            TagFactory.sort(taskVo.getTagList());

        }else {
            demandDetailVo.setTagList(new ArrayList<>());
        }


        demandDetailVo.setDemand(demand);
        demandDetailVo.setId(demand.getId());
        demandDetailVo.setUuid(demand.getUuid());
        // 关联数据集
        demandDetailVo.setFileList(getTaskDatasetList(uuid));

        Model model = webModelService.getByUuid(demand.getModelUuid());
        demandDetailVo.setModel(model);
        if (model!=null){
            if (needApi){
                try {
                    API profile = this.executorFactory.get(model.getUuid()).profileClean();
                    demandDetailVo.setApi(profile);
                }catch (Exception ex){
                    log.warn("api is not ready:model:{}",model.getUuid());
                }
            }
        }

        List<DemandDataset> demandDatasetList = demandDatasetService.getDemandDatasetList(demand.getUuid());
        demandDetailVo.setDatasetList(demandDatasetList);

        List<DemandPhase> demandPhase = getDemandPhase(demand.getUuid());
        demandDetailVo.setDemandPhaseList(demandPhase);

        int total = this.buildIndices(demandDetailVo);

        demandDetailVo.setCallNum(total);

        // 返回结果
        return demandDetailVo;
    }


    private DemandDetailVo getBase(String id){
        Demand demand = getByUuid(id);
        if (demand == null){
            return null;
        }
        DemandDetailVo demandDetailVo = new DemandDetailVo();
        demandDetailVo.setDemand(demand);
        demandDetailVo.setId(demand.getId());
        demandDetailVo.setUuid(demand.getUuid());
        return demandDetailVo;
    }

    public List<Dataset> getTaskDatasetList(String taskUuid){

        List<TaskDatasetDo> datasetList = taskDatasetDao.getByTaskUuid(taskUuid);

        if (!CollectionUtils.isEmpty(datasetList)){
            List<Integer> datasetId = datasetList.stream().map(d -> d.getDatasetId()).collect(Collectors.toList());

            List<DatasetDo> datasetDos = datasetDao.getByIds(datasetId);
            return DatasetConvert.po2dtoList(datasetDos);
        }
        return new ArrayList<>();
    }

    public int buildIndices(DemandDetailVo demand) {
        // 任务浏览总量
        if (demand!=null&&!StringUtils.isEmpty(demand.getUuid())){
            demand.addIndexItem(this.taskProcessor.indices(demand.getUuid()));
        }
//
//        // 模型下载总量
//        if (demand.getModel()!=null){
//            demand.addIndexItem(this.modelProcessor.indices(demand.getModel()));
//        }
//        // 接口调用总量 & 接口调用量按月
//        if (demand.getApi()!=null){
//            demand.addIndexItem(this.apiProcessor.indices(demand.getApi()));
//            return apiProcessor.getTotal(demand.getApi());
//        }
//

        return 0;
    }

    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {
        return getBase(id);
    }
}
