# X-PAI

--- 
虎博科技-X项目-人工智能平台(Platform of Artificial Intelligence, PAI)

## 用户认证 - auth

### 逻辑说明

#### 用户信息

    - account: 账号，仅限 0-9a-z-_ 字符
    - name: 名称，
    - intro: 简介
    - desc: 描述
    - mobile: 手机号
    - avatar: 头像
    - nameEn: 英文名称(预留)
    - introEn: 英文简介(预留)
    - descEn: 英文描述(预留)
    - email: 邮箱(预留)
    - wechat: 微信(预留)
    - website: 个人主页(预留)

#### 用户组信息

    - account: 账号，仅限 0-9a-zA-Z.-_ 字符
    - name: 名称，
    - intro: 简介
    - desc: 描述
    - logo: 组织logo
    - ownerId/ownerUuid: 所有者ID（默认创建者）
    - scope: 范围，
        - UNKNOWN：0, "未知"
        - PERSONAL：10, "个人"
        - PRIVATE：20, "私有"
        - PUBLIC：30, "公开"
    - *nameEn: 英文名称(预留)
    - *introEn:英文简介(预留)
    - *descEn: 英文描述(预留)
    - *mobile: 手机号(预留，默认owner手机号)
    - *email: 邮箱(预留)
    - *website: 个人主页(预留)

#### 用户角色

    - OWNER: 0, "所有者"
    - ADMIN: 10, "管理员"
    - *COMMITTER: 20, "提交者"(预留)
    - DEVELOPER: 30, "开发者"(预留)
    - *REPORTER: 40, "报告者"(预留)
    
    - *PROJECT_OWNER: 100, "项目OWNER"(预留)
    - *PROJECT_ADMIN: 110, "项目管理员"(预留)
    - *PROJECT_COMMITTER: 120, "项目提交者"(预留)
    - *PROJECT_DEVELOPER: 130, "项目开发者"(预留)
    - *PROJECT_REPORTER: 140, "项目报告者"(预留)

    - GUEST: 999, "游客"(默认)
目前用户在权限组的角色仅包含：
- OWNER(所有者，超级管理员)
- ADMIN(管理员，管理权限)
- DEVELOPER(开发者，编辑权限)
- GUEST(游客，无权限)

登陆用户针对某一对象(需求、任务、模型)继承对象所在组的权限：用户对对象所在组的权限 即为 用户对操作对象的权限，
比如用户yicun.chen在future-x下是developer，那么对future-x下的所有对象都具备编辑权限

#### 权限管理

    1.当前权限系统中暂只设定用户组 [所有者-OWNER] 和 [管理员-ADMIN]，用户组OWNER 只能有一个
    2.用户组OWNER可以修改用户组信息(昵称、头像、简介、介绍等)，可切换OWNER和添加组管理员
    3.用户组OWNER和管理员可以发布需求、认领任务、创建模型和提交结果

### 接口定义

#### 用户注册

```curlrc
## 上传用户头像
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/user/upload-avatar' \
--form 'file=@"/Users/chyc/Workspaces/Codes/x-pai/x-pai/x-pai-biz/src/main/resources/tag/icon/industry-government.png"'

## 创建用户
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": {
        "name": "TestUser",
        "account": "user.tchen", /*必传，仅限 0-9a-zA-Z.-_ 字符*/
        "mobile": "13162521958", /*当前必传*/
        "avatar": "",
        "password": "xxxxxxxx", /*当前必传*/
        "wechat": "yicunchen",
        "email": "yicun.chen@tigerobo.com",
        "nameEn": "英文名称(预留)",
        "introEn": "英文简介(预留)",
        "descEn": "英文描述(预留)",
        "website": "个人主页(预留)"
    }
}'
```

#### 用户登陆

```curlrc
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": {
        "account": "cat.big",
        "mobile": "13162521958",
        "password": "xxxxxxxx" /*当前必传*/
    }
}'
```

#### 用户组创建

```curlrc
## 上传用户组logo
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/user/upload-avatar' \
--form 'file=@"/Users/chyc/Workspaces/Codes/x-pai/x-pai/x-pai-biz/src/main/resources/tag/icon/industry-supervise.png"'

## 创建用户
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/group/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "group": {
        "account": "future-x", /*必传，仅限 0-9a-zA-Z.-_ 字符*/
        "name": "X项目组",
        "intro": "简介",
        "desc": "描述",
        "logo": "logo",
        "scope": "PUBLIC", /*范围，UNKNOWN(0, "未知")、PERSONAL(10, "个人")、PRIVATE(20, "私有")、PUBLIC(30, "公开") */
        "nameEn": "英文名称(预留)",
        "introEn": "英文简介(预留)",
        "descEn": "英文描述(预留)",
        "mobile": "手机号(预留，默认owner手机号)",
        "email": "邮箱(预留)",
        "website": "个人主页(预留)",
        "owner":{
            "uuid": "xxxxxx" 
        }
    },
    "authorization": {
        "token": "883c62eb8588c3fefb180d9a9b000bec",
        "uid": "883c62eb8588c3fefb180d9a9b000bec"
    }
}'
```

#### 用户组授权

```curlrc
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/auth/grant' \
--header 'Content-Type: application/json' \
--data-raw '{
    "user": {              
        "uuid": "xxxx"      /*授予权限的用户UUID*/
    },
    "group": {             
        "uuid": "xxxx"      /*被授予成员的用户组UUID*/
    },
    "role": "ADMIN",        /*权限类型*/
    "authorization": {
        "token": "0e9bead34cdae89510c1538c7193197e",
        "uid": "1f74c93afbd3691e9daad4a83047bc5e",
        "gid": "d7d41bcc9cc008601d9bafe799c4c286" 
    }
}'
```

#### 用户认证

```curlrc
curl --location --request POST 'https://pai-test.tigerobo.com/x-pai-biz/auth/authorize' \
--header 'Content-Type: application/json' \
--data-raw '{
    "authorization": {
        "token": "0e9bead34cdae89510c1538c7193197e",
        "uid": "1f74c93afbd3691e9daad4a83047bc5e",
        "gid": "b90e1038fcde4358e07529f2daae3273"
    }
}'
```

## 业务系统 - biz

### 实体对象

#### 基础：

##### 实体基类

[Entity.java](src/main/java/com/tigerobo/x/pai/api/entity/Entity.java)

```java

@ApiModel(value = "业务模块-实体基础类")
public class Entity {
    @ApiModelProperty(value = "ID")
    @JSONField(serialize = false)
    protected Integer id;
    @ApiModelProperty(value = "UUID")
    protected String uuid;
    @ApiModelProperty(value = "TYPE")
    @Builder.Default
    protected Type type = Type.UNKNOWN;

    @ApiModelProperty(value = "名称")
    protected String name;
    @ApiModelProperty(value = "名称(英文)")
    protected String nameEn;
    @ApiModelProperty(value = "简介")
    protected String intro;
    @ApiModelProperty(value = "简介(英文)")
    protected String introEn;
    @ApiModelProperty(value = "描述")
    protected String desc;
    @ApiModelProperty(value = "描述(英文)")
    protected String descEn;
    @ApiModelProperty(value = "图片")
    protected String image = null;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    @ApiModelProperty(value = "最后修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

    @ApiModelProperty(value = "隶属用户组")
    protected Group group;

    @Getter
    @AllArgsConstructor
    public enum Type {
        UNKNOWN(0, "未知", Module.UNKNOWN),
        USER(1010, "用户", Module.AUTH),
        GROUP(1020, "用户组", Module.AUTH),
        DEMAND(2010, "需求", Module.BIZ),
        TASK(2020, "任务", Module.BIZ),
        MODEL(2030, "模型", Module.BIZ),
        DATASET(2040, "数据集", Module.BIZ),
        SUBMISSION(2050, "提交", Module.BIZ),
        API(2080, "API", Module.BIZ),
        APPLICATION(2090, "应用", Module.BIZ);

        private final Integer val;
        private final String name;
        private final Module module;
    }
}
```

##### 标签类

[Tag.java](src/main/java/com/tigerobo/x/pai/api/entity/Tag.java)

```java

@ApiModel(value = "业务模块-标签信息类")
public class Tag {
    @ApiModelProperty(value = "唯一标识")
    private String uid;
    @ApiModelProperty(value = "文本")
    private String text;
    @ApiModelProperty(value = "文本(英文)")
    private String textEn;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "类型")
    @Builder.Default
    private Type type = Type.UNKNOWN;
    @ApiModelProperty(value = "图片")
    private List<String> image;

    @Getter
    @AllArgsConstructor
    public enum Type {
        UNKNOWN(0, "未知"),
        INDUSTRY(101, "行业"),
        DOMAIN(102, "领域"),

        LANGUAGES(201, "语言"),
        LICENSES(202, "许可"),

        TASKS(210, "任务"),
        LIBRARIES(220, "框架"),
        DATASETS(230, "数据集"),
        STYLE(240, "展示样式");

        private final Integer val;
        private final String name;
    }
}    
```

#### 需求：

[Demand.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Demand.java)

```java

@ApiModel(value = "业务模块-业务需求详情类")
public class Demand extends Entity {
    @ApiModelProperty(value = "预算金额")
    private Double budget;
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;
    @ApiModelProperty(value = "需求期限")
    private Integer duration;
    @ApiModelProperty(value = "需求阶段")
    @Builder.Default
    private Phase phase = Phase.IMPLEMENT;
}    
```

#### 任务：

[Task.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Task.java)

```java

@ApiModel(value = "业务模块-需求任务详情类")
public class Task extends Entity {
    @ApiModelProperty(value = "预算金额")
    private Double budget;
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date deliveryDate;
    @ApiModelProperty(value = "模型样式")
    private Style style;
    @ApiModelProperty(value = "状态")
    @Builder.Default
    private Status status = Status.IMPLEMENT;
    @ApiModelProperty(value = "隶属需求ID")
    private Integer demandId;
    @ApiModelProperty(value = "隶属需求UUID")
    private String demandUuid;

    @ApiModelProperty(value = "最佳模型ID")
    private Integer modelId;
    @ApiModelProperty(value = "最佳模型UUID")
    private String modelUuid;
}    
```

#### 模型：

[Model.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Model.java)

```java

@ApiModel(value = "业务模块-算法模型详情类")
public class Model extends Entity {
    @ApiModelProperty(value = "受限等级")
    private Limited limited = Limited.UNRESTRICTED_PUBLIC;
    @ApiModelProperty(value = "模型仓库地址：包括branch,Tag")
    private String repoAddr;
    @ApiModelProperty(value = "API调用地址")
    private String apiUri;
    @ApiModelProperty(value = "演示样式")
    private Style style = UNKNOWN;
    @ApiModelProperty(value = "状态")
    private Status status = Status.PREPARE;
}
```

#### 提交:

[Submission.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Submission.java)

```java

@ApiModel(value = "业务模块-结果提交详情类")
public class Submission extends Entity {
    @ApiModelProperty(value = "当前状态")
    private Status status = Status.UNKNOWN;
    @ApiModelProperty(value = "提交的模型文件地址")
    private String filePath;
    @ApiModelProperty(value = "评估分数")
    private Double score;
    @ApiModelProperty(value = "关联模型ID")
    private Integer modelId;
    @ApiModelProperty(value = "关联模型UUID")
    private String modelUuid;
    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;
    @ApiModelProperty(value = "补充说明")
    private Map<String, Object> extras;
}    
```

#### 接口：

[API.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/API.java)

```java

@ApiModel(value = "业务模块-服务接口详情类")
public class API extends Entity {
    @ApiModelProperty(value = "uri")
    private String uri;
    @ApiModelProperty(value = "demo")
    private Map<String, Object> demo;
    @ApiModelProperty(value = "演示样式")
    private Style style = UNKNOWN;
    @ApiModelProperty(value = "状态")
    private Status status = Status.UNKNOWN;
    @ApiModelProperty(value = "隶属模型ID")
    private Integer modelId;
    @ApiModelProperty(value = "隶属模型UUID")
    private String modelUuid;
    @ApiModelProperty(value = "隶属用户组")
    private Group group;
    @ApiModelProperty(value = "API主键(兼容性)")
    @Deprecated
    private String apiKey;
    @ApiModelProperty(value = "API地址(兼容性)")
    @Deprecated
    private String apiUri;
    @ApiModelProperty(value = "API样式(兼容性)")
    @Deprecated
    private Style apiStyle;
    @ApiModelProperty(value = "API样列(兼容性)")
    @Deprecated
    private Map<String, Object> apiDemo;
}
```

#### ~~应用~~：

[Application.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Application.java)

```java

@ApiModel(value = "业务模块-服务接口详情类")
public class Application extends Entity {
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "演示样式")
    private Style style = UNKNOWN;
}    
```

### 实体状态

#### 需求阶段:

    - *UNKNOWN: 0, "未知", "默认非公开阶段(草稿)"
    - *PREPARE: 10, "预备", "需求讨论、咨询、询价等(预留)"
    - *SUBMIT: 20, "提交中", "需求提交中，待审核状态"
    - REVIEW: 30, "审核中", "提交完成，需求审核中, 包括任务拆分、数据准备等"
    - *DATESET: 40, "数据处理中", "数据预处理(预留)"
    - IMPLEMENT: 50, "进行中", "审核通过，需求执行中"
    - *MODIFY: 60, "变更中", "需求执行变更(预留)"
    - *ACCEPTANCE: 70, "验收中", "需求验收中"
    - *SETTLEMENT: 80, "结算中", "验收通过，交易结算中(预留)"
    - COMPLETED: 90, "完成", "需求完成"

目前仅保留: **SUBMIT**/**IMPLEMENT**/**COMPLETED** 三个阶段</br>

- **REVIEW**: 需求提交成功，即为审核中</br>
- **IMPLEMENT**: 需求进入开发期，即为进行中</br>
- **COMPLETED**: 需求所有任务完成后，即为完成

#### 任务状态:

目前阶段**需求**和**任务**统一处理: </br>

    - *UNKNOWN: 0, "其他", "默认非公开草稿阶段"
    - *PREPARE: 10, "预备", "任务讨论、咨询、询价等(预留)"
    - *SUBMIT: 20, "提交", "任务提交(预留)"
    - REVIEW: 30, "审核", "任务审核(预留)"
    - *DATESET: 40, "数据", "数据预处理(预留)"
    - IMPLEMENT: 50, "执行", "需求执行"
    - *MODIFY: 60, "变更", "需求执行变更(预留)"
    - *ACCEPTANCE: 70, "验收", "需求验收"
    - *SETTLEMENT: 80, "结算", "交易结算"
    - COMPLETED: 90, "完成", "需求完成"

目前仅保留: **SUBMIT**/**IMPLEMENT**/**COMPLETED** 三个状态</br>

- **REVIEW**: 任务提交成功，即为审核中</br>
- **IMPLEMENT**: 任务进入开发期，即为进行中</br>
- **COMPLETED**: 任务模型服务化后，即为完成

#### 模型状态:

    - *UNKNOWN: 0, "未知", "默认非公开阶段(草稿)"
    - PREPARE: 10, "预备", "创建模型"
    - SUBMIT: 20, "提交", "提交结果"
    - *ACCEPTANCE: 70, "验收", "模型验收"
    - *SETTLEMENT: 80, "结算", "交易结算"
    - COMPLETED: 90, "完成", "模型完成"

目前仅保留: **PREPARE**/**SUBMIT**/**COMPLETED** 三个状态</br>

- **PREPARE**: 模型创建成功</br>
- **SUBMIT**: 模型提交阶段，至少提交成功一次</br>
- **COMPLETED**: 模型服务化后，即为完成

#### API状态

    - UNKNOWN: 0, "未知"
    - UNAVAILABLE: 10, "不可用"
    - PENDING: 20, "阻塞"
    - AVAILABLE: 30, "可用". 

目前API状态基于服务响应获得，仅考虑 **UNAVAILABLE**/**AVAILABLE**

#### 样式枚举：

- TEXT_TO_TEXT: 输入文本，输出文本
- TEXT_TO_LABEL: 输入文本，输出标签集
- TEXT_TO_ENTITY: 输入文本，输出实体信息

[Style.java](src/main/java/com/tigerobo/x/pai/api/biz/entity/Style.java)

```java

@ApiModel(value = "业务模块-API演示样式枚举")
public enum Style {
    UNKNOWN(null, null, "unknown", "未知", ""),
    TEXT_TO_TEXT(InType.TEXT, OutType.TEXT, "text2text", "文本2文本", ""),
    TEXT_TO_LABEL(InType.TEXT, OutType.LABEL, "text2label", "文本2标签", "https://x-pai.oss-cn-shanghai-internal.aliyuncs.com/biz/tag/icon/style-text2label.png?Expires=4780561919&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=enEplZlm10OBlfzNzjomvoewb2E%3D"),
    TEXT_TO_ENTITY(InType.TEXT, OutType.ENTITY, "text2entity", "文本2实体", "https://x-pai.oss-cn-shanghai-internal.aliyuncs.com/biz/tag/icon/style-text2entity.png?Expires=4780561919&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=E9nHLfq2UiJG9nBYCUc8OFG1iB0%3D");

    private final InType in;
    private final OutType out;
    private final String name;
    private final String desc;
    private final String image;

    @Getter
    @AllArgsConstructor
    public static enum InType {
        TEXT(1101, "text", "文本"),
        IMAGE(1201, "image", "图片");

        private final int val;
        private final String name;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public static enum OutType {
        TEXT(2101, "text", "文本信息"),
        LABEL(2201, "label", "标签信息"),
        ENTITY(2301, "entity", "实体标记");

        private final int val;
        private final String name;
        private final String desc;
    }
}
```

#### 状态流转

- 需求和任务状态等价，即阶段和状态一一对应
- 所有状态目前由后端控制

### 接口定义

### 统一服务 - serving

### 模型仓库 - repo

### 计算引擎 - engine

