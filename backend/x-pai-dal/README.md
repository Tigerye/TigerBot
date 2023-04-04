#### 业务系统实体类型(值)

``` java
public enum Type {
    UNKNOWN(0, "未知"),
    DEMAND(10, "需求"),
    TASK(20, "任务"),
    MODEL(30, "模型"),
    DATASET(40, "数据集"),
    APPLICATION(90, "应用");

    private final Integer val;
    private final String name;
}
```

#### 数据库内实体对象ID取值范围约定：
| 实体            | 名称      | ID范围   |
| --------------- | -------- | ------------------------ | 
| auth-user       | 用户      | 1-99999999               |
| auth-group      | 用户组    | personal-group: 1-99999999 </br> other-group: 100000000+|
| biz-demand      | 需求      | 100000000-199999999      |
| biz-task        | 任务      | 200000000-299999999      |
| biz-model       | 模型      | 300000000-399999999      |
| biz-dataset     | 数据集    | 400000000-499999999      |
| biz-submission  | 提交      | 500000000-599999999      |
| biz-api         | 接口      | 800000000-899999999      |
| biz-application | 应用      | 900000000-999999999      |

```mysql
ALTER TABLE `x_pai`.`xpai-auth-user`
    AUTO_INCREMENT = 1;
ALTER TABLE `x_pai`.`xpai-auth-group`
    AUTO_INCREMENT = 100000000;

ALTER TABLE `x_pai`.`xpai-biz-demand`
    AUTO_INCREMENT = 100000000;
ALTER TABLE `x_pai`.`xpai-biz-task`
    AUTO_INCREMENT = 200000000;
ALTER TABLE `x_pai`.`xpai-biz-model`
    AUTO_INCREMENT = 300000000;
ALTER TABLE `x_pai`.`xpai-biz-dataset`
    AUTO_INCREMENT = 400000000;
ALTER TABLE `x_pai`.`xpai-biz-application`
    AUTO_INCREMENT = 900000000;
```

#### 实体状态刘庄

#### OSS文件存储目录设定
- 用户
    - auth/user/UUID/avatar.xxx : 用户头像
    - auth/group/UUID/logo.xxx ：用户组LOGO
- 业务
    - biz/application/UUID/image.xxx
    - biz/demand/UUID/image.xxx
    - biz/task/UUID/image.xxx
    - biz/model/UUID/image.xxx
    - biz/tag/UID/icon/image.xxx