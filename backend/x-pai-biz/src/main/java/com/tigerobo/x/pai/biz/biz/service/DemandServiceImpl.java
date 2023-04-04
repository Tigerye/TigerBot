package com.tigerobo.x.pai.biz.biz.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.biz.entity.Dataset;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.api.biz.entity.Task;
import com.tigerobo.x.pai.api.vo.biz.DemandQueryVo;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.api.entity.IndexItem;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.biz.process.ImageFactory;
import com.tigerobo.x.pai.biz.biz.process.TaskProcessor;
import com.tigerobo.x.pai.biz.converter.DemandConvert;
import com.tigerobo.x.pai.biz.converter.TaskConvert;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.DemandDao;
import com.tigerobo.x.pai.dal.biz.dao.EntityTagDao;
import com.tigerobo.x.pai.dal.biz.dao.TaskDao;
import com.tigerobo.x.pai.dal.biz.entity.DemandDo;
import com.tigerobo.x.pai.dal.biz.entity.EntityTagDo;
import com.tigerobo.x.pai.dal.biz.entity.TaskDo;
import lombok.extern.slf4j.Slf4j;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DemandServiceImpl {

    @Autowired
    private ImageFactory imageFactory;

    //    @Autowired
//    private TagFactory tagFactory;
    @Autowired
    private TagService tagService;
    @Autowired
    private WebTaskService webTaskService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DemandDao demandDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private TaskProcessor taskProcessor;
    @Autowired
    private WebGroupService webGroupService;
    @Autowired
    private EntityTagDao entityTagDao;


    public PageInfo<DemandVo> getTopList() {

        List<DemandDo> top = demandDao.getTop();

        PageInfo<DemandVo> pageInfo = new PageInfo<>();

        if (CollectionUtils.isEmpty(top)) {
            return pageInfo;
        }
        List<DemandVo> list = top.stream().map(t -> DemandConvert.convert(t))
                .map(d -> DemandVo.builder().demand(d).build()).collect(Collectors.toList());
        pageInfo.setList(list);
        return pageInfo;
    }

    public int countUserView(Integer userId) {
        User user = userService.getById(userId);
        List<Integer> scopeList = new ArrayList<>();
        scopeList.add(Group.Scope.PUBLIC.getVal());
        String createBy = null;
        if (user != null && UserRoleTypeEnum.hasRole(user.getRoleType(), UserRoleTypeEnum.SUPER_MAN)) {
            scopeList.add(Group.Scope.PERSONAL.getVal());
        } else {
            if (user != null) {
                createBy = String.valueOf(userId);
            }
        }
        DemandQueryVo queryVo = new DemandQueryVo();
        DemandQueryVo.Param param = new DemandQueryVo.Param();
        param.setScopeList(scopeList);
        queryVo.setParams(param);

        int count = demandDao.countUserCanView(createBy, queryVo);
        return count;
    }

    public PageVo<DemandVo> demandList(DemandQueryVo queryVo) {

        Integer userId = ThreadLocalHolder.getUserId();
        initParam(queryVo, userId);
        long quStart = System.currentTimeMillis();
        String createBy = userId != null ? String.valueOf(userId) : null;

        DemandQueryVo.Param params = queryVo.getParams();
        String keyword = params.getKeyword();
        List<String> groupUuids = null;
        List<String> creators = null;
        if (StringUtils.isNotBlank(keyword)) {
            List<GroupDo> groupDos = groupDao.likeNameList(keyword);
            if (!CollectionUtils.isEmpty(groupDos)) {
                groupUuids = groupDos.stream().map(g -> g.getUuid()).collect(Collectors.toList());
            }

            List<UserDo> userDos = userDao.equalNameList(keyword);
            if (!CollectionUtils.isEmpty(userDos)) {
                creators = userDos.stream().map(userDo -> String.valueOf(userDo.getId())).collect(Collectors.toList());
            }
        }

        List<DemandDo> query = demandDao.query(queryVo, createBy, groupUuids, creators);
        log.info("demand-qv2:qu-delta:{}", System.currentTimeMillis() - quStart);
        PageVo<DemandVo> pageInfo = new PageVo<>();
        pageInfo.setPageNum(queryVo.getPageNum());
        pageInfo.setPageSize(queryVo.getPageSize());
        if (!CollectionUtils.isEmpty(query)) {
            Page<DemandDo> demandPageInfo = (Page<DemandDo>) query;
            long total = demandPageInfo.getTotal();
            List<Integer> demandIdList = query.stream().map(q -> q.getId()).collect(Collectors.toList());
            List<TaskDo> taskList = taskDao.getListByDemandIds(demandIdList);

            Map<Integer, TaskDo> taskDoMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(taskList)) {
                for (TaskDo taskDo : taskList) {
                    taskDoMap.put(taskDo.getDemandId(), taskDo);
                }
            }
            List<DemandVo> demandVos = query.stream()
                    .parallel().map(demandDo -> buildDemand(demandDo, taskDoMap)).collect(Collectors.toList());

            pageInfo.setList(demandVos);
            pageInfo.setTotal(total);
        }
        return pageInfo;
    }

    private void initParam(DemandQueryVo queryVo, Integer userId) {
        User user = userService.getById(userId);
        List<Integer> scopeList = new ArrayList<>();

        scopeList.add(Group.Scope.PUBLIC.getVal());
        if (user != null) {

            if (UserRoleTypeEnum.SUPER_MAN.getBit().equals(user.getRoleType())) {
                scopeList.add(Group.Scope.PUBLIC.getVal());
                scopeList.add(Group.Scope.PERSONAL.getVal());
            }
        }

        DemandQueryVo.Param params = queryVo.getParams();
        if (params != null) {
            List<String> domain = params.getDomain();
            if (!CollectionUtils.isEmpty(domain)) {
                domain = domain.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                params.setDomain(domain);
            }
            List<String> industry = params.getIndustry();
            if (!CollectionUtils.isEmpty(industry)) {
                industry = industry.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
                params.setIndustry(industry);
            }
//
//            if (!CollectionUtils.isEmpty(params.getPhase())){
//                List<Integer> phaseList = new ArrayList<>();
//                for (String phaseVal : params.getPhase()) {
//                    Demand.Phase phase = Demand.Phase.valueOf2(phaseVal);
//                    if (phase!=null&&!phaseList.contains(phase.getVal())){
//                        phaseList.add(phase.getVal());
//                    }
//                }
//                if (phaseList.size()>0){
//                    params.setPhaseList(phaseList);
//                }
//            }
        }
        if (params == null) {
            params = new DemandQueryVo.Param();
            queryVo.setParams(params);
        }
        params.setScopeList(scopeList);
        String orderBy = queryVo.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "id desc";
        } else {
            if (orderBy.endsWith("asc")) {
                orderBy = "id asc";
            } else {
                orderBy = "id desc";
            }
        }
        queryVo.setOrderBy(orderBy);

        PageHelper.startPage(queryVo.getPageNum(), queryVo.getPageSize());
    }


    private DemandVo buildDemand(DemandDo demandDo, Map<Integer, TaskDo> taskDoMap) {
        String createBy = demandDo.getCreateBy();
        String groupUuid = demandDo.getGroupUuid();

        Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
        DemandVo demandVo = new DemandVo();

        Demand demand = DemandConvert.convert(demandDo);

        if (group == null) {
            group = new Group();
        }
        demand.setGroup(group);

        demandVo.setDemand(demand);

        Integer demandId = demandDo.getId();
        TaskDo taskDo = taskDoMap.get(demandId);
        Task task = TaskConvert.convert(taskDo);
        if (task == null) {
            task = new Task();
        }
        demandVo.setTaskList(Arrays.asList(task));
        buildIndices(demandVo);

        demandVo.setId(demand.getId());
        demandVo.setUuid(demand.getUuid());

        demandVo.setTagList(getDemandTags(demandId));
        return demandVo;
    }


    public PageInfo<DemandVo> getGroupDemand(Integer groupId) {

        if (groupId == null) {
            return new PageInfo<>();
        }

        List<DemandDo> userDemandDoList = demandDao.getGroupDemandList(groupId);
        if (CollectionUtils.isEmpty(userDemandDoList)) {
            return new PageInfo<>();
        }
        return buildDemandVoPageInfo(userDemandDoList);
    }

    public PageInfo<DemandVo> getUserDemand(Integer userId) {

        if (userId == null) {
            return new PageInfo<>();
        }

        List<DemandDo> userDemandDoList = demandDao.getUserDemandList(String.valueOf(userId));
        if (CollectionUtils.isEmpty(userDemandDoList)) {
            return new PageInfo<>();
        }
        return buildDemandVoPageInfo(userDemandDoList);
    }


    private PageInfo<DemandVo> buildDemandVoPageInfo(List<DemandDo> dbDemandList) {
        PageInfo<DemandVo> pageInfo = new PageInfo<>();
        List<Demand> demandList = dbDemandList.stream().map(po -> DemandConvert.convert(po)).collect(Collectors.toList());

        List<DemandVo> voList = demandList.stream().map(demand -> DemandVo.builder().demand(demand).build()).collect(Collectors.toList());


        List<String> uuids = demandList.stream().map(d -> d.getUuid()).collect(Collectors.toList());

        List<TaskDo> taskDoList = taskDao.getListByUuids(uuids);

        Map<String, Task> taskMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(taskDoList)) {
            for (TaskDo taskDo : taskDoList) {
                Task task = TaskConvert.convert(taskDo);
                taskMap.put(task.getDemandUuid(), task);
            }

        }

        voList.parallelStream().forEach(vo -> {
            Demand demand = vo.getDemand();
            String createBy = demand.getCreateBy();
            String groupUuid = demand.getGroupUuid();
            Group group = webGroupService.getCreateByOrGroup(createBy, groupUuid);
            Task task = taskMap.get(demand.getUuid());
            if (group != null) {
                demand.setGroup(group);
                if (task != null) {
                    task.setGroup(group);
                }
            }
            if (task != null) {
                vo.setTaskList(Arrays.asList(task));
            }
            List<Tag> demandTags = getDemandTags(demand.getId());
            vo.setTagList(demandTags);
            buildIndices(vo);
        });
        List<DemandVo> collect = voList.stream().filter(vo -> vo.getTaskList() != null && vo.getTaskList().size() > 0).collect(Collectors.toList());
        pageInfo.setList(collect);
        pageInfo.setTotal(collect.size());

        return pageInfo;
    }


    private List<Tag> getDemandTags(Integer id) {

        List<EntityTagDo> demandTags = entityTagDao.getDemandTags(id);

        if (CollectionUtils.isEmpty(demandTags)) {
            return new ArrayList<>();
        }
        List<String> tagUids = demandTags.stream().map(dt -> dt.getTagUid()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagUids)) {
            return new ArrayList<>();
        }
        List<Tag> tagsList = tagService.get(tagUids);
        return tagsList == null ? new ArrayList<>() : tagsList;
    }

    public List<IndexItem> buildIndices(DemandVo demandVo) {
        // 任务浏览总量
        List<Task> taskList = demandVo.getTaskList();
        if (CollectionUtils.isEmpty(taskList)) {
            return new ArrayList<>();
        }
        demandVo.addIndexItem(this.taskProcessor.indices(demandVo.getDemand().getUuid()));

        return demandVo.getIndices();
    }

    @Transactional(value = "paiTm")
    public DemandVo createOrUpdate(WebRepVo requestVo, Integer userId) {

        UserDo user = userDao.getById(userId);
        Preconditions.checkArgument(user != null, "用户不能为空");
        Demand demand = requestVo.getOrDefault("demand", Demand.class, null);
        if (demand == null) {
            throw new IllegalArgumentException("需求参数为空");
        }
        List<Dataset> datasetList = requestVo.get("datasetList", Dataset.class);
        List<Tag> tagList = requestVo.get("tagList", Tag.class);
        // TODO 新建需求
        String uuid = demand.getUuid();

        String contractCategoryId = demand.getContractCategoryId();


        if (StringUtils.isBlank(uuid)) {
            return doCreateDemand(userId, demand, datasetList, tagList);
        } else {

            DemandDo demandInDb = demandDao.getByUuid(uuid);
            Preconditions.checkArgument(demandInDb != null, "需求不存在");

            DemandDo demandUpdate = new DemandDo();
            demandUpdate.setId(demandInDb.getId());

            if (!StringUtils.isEmpty(contractCategoryId) && !contractCategoryId.equals(demandInDb.getContractCategoryId()) && !UserRoleTypeEnum.hasRole(user.getRoleType(), UserRoleTypeEnum.SUPER_MAN)) {
                throw new IllegalArgumentException("当前用户不能添加合同分类id");
            }
            demandUpdate.setContractCategoryId(contractCategoryId);
            demandUpdate.setIntro(demand.getIntro());
            demandUpdate.setDesc(demand.getDesc());
            demandUpdate.setScope(demand.getScope());
            demandUpdate.setDeliveryDate(demand.getDeliveryDate());
            demandUpdate.setBudget(demand.getBudget());
            demandDao.update(demandUpdate);
            return null;
        }

    }

    private DemandVo doCreateDemand(Integer userId, Demand demand, List<Dataset> datasetList, List<Tag> tagList) {
        if (StringUtils.isEmpty(demand.getImage())) {
            demand.setImage(imageFactory.getImage());
        }
        demand.setPhase(Demand.Phase.WAIT_AUDIT);

        demand = doCreate(demand, userId);
        addTagList(demand, tagList);

        // TODO 新建默认任务
        Task task = demand.toDefaultTask();
        // 设置 style & image
        if (!CollectionUtils.isEmpty(tagList)) {
            Optional<Tag> tagOptional = tagList.stream().filter(t -> t.getType() == Tag.Type.STYLE).findFirst();
            if (tagOptional.isPresent()) {
                Tag tag = tagService.get(tagOptional.get().getUid());
//                task.setStyle(Style.valueOf2(tag.getUid().split("-")[0]));
                task.setImage(tag.getIcon());
            }
        }
        task.setCreateBy(String.valueOf(userId));
        // 设置 status
        task.setStatus(Task.Status.valueOf(demand.getPhase().getVal()));

        task = this.webTaskService.createTask(task, userId);

        addTagList(demand, tagList);
        if (!CollectionUtils.isEmpty(datasetList)) {
            webTaskService.addDataset(datasetList, task, userId);
        }


        return DemandVo.builder().demand(demand).taskList(Arrays.asList(task)).build();
    }


    private Demand doCreate(Demand demand, Integer userId) {


        UserDo userDo = userDao.getById(userId);

        if (userDo == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        // todo 插入需求 DEMAND
        String demandUuid = IdGenerator.getDemandId(demand.getName(), userId);
        demand.setUuid(demandUuid);
        DemandDo db = demandDao.getByUuid(demandUuid);
        Preconditions.checkArgument(db == null, "需求已创建");

        DemandDo demandDo = DemandConvert.convert(demand);

        demandDo.setCreateBy(String.valueOf(userId));
        demandDo.setUpdateBy(String.valueOf(userId));

        String currGroupUuid = userDo.getCurrGroupUuid();
        Integer currGroupId = userDo.getCurrGroupId();

        if (StringUtils.isNotBlank(currGroupUuid)) {
            demandDo.setGroupUuid(currGroupUuid);
            demandDo.setGroupId(currGroupId);
        }
        demandDao.insert(demandDo);

        Preconditions.checkArgument(demandDo.getId() != null, "需求创建失败");

        Demand convert = DemandConvert.convert(demandDo);
//        this.processor.insert(demand, requestVo.getAuthorization());
        // todo 插入标签 TAG


        return convert;
    }

    private void addTagList(Demand demand, List<Tag> tagList) {
        Integer userId = ThreadLocalHolder.getUserId();
        String createBy = userId == null ? "" : String.valueOf(userId);
        if (demand.getId() > 0 && !CollectionUtils.isEmpty(tagList)) {

            for (Tag tag : tagList) {
                EntityTagDo po = new EntityTagDo();
                po.setUuid(IdGenerator.getId());
                po.setEntityUuid(demand.getUuid());
                po.setEntityId(demand.getId());

                po.setEntityType(2010);

                po.setTagType(tag.getType().getVal());
                po.setTagUid(tag.getUid());
                po.setCreateBy(createBy);
                po.setUpdateBy(createBy);
                entityTagDao.insert(po);
            }
        }
    }

}
