# X-PAI

虎博科技-X项目-人工智能平台(Platform of Artificial Intelligence, PAI)

## x-pai-service: 服务控制层

### web页面接口清单

#### 用户/用户组

- 用户注册: auth/register -> web/user/register, web/user-register
- 用户登陆: auth/login -> web/user/login, web/user-login
- 用户组列表: group/list -> web/group/list, web/group-list
- 我的模型: model/query -> web/mine/model-list, web/mine-model-list
- 我的任务: task/query -> web/mine/task-list, web/mine-task-list
- 我的需求: demand/query -> web/mine/demand-list, web/mine-demand-list

#### 应用

- 应用列表: task/query ->
- 应用详情: task/detail ->
- 相关应用: task/query ->

#### 需求

- 需求创建: demand/create -> web/demand/create, web/demand-create
- 需求列表: demand/query -> 
- 数据集上传(sts): dataset/oss-token ->

#### 任务

- 需求/任务认领: model/create -> web/task/join, web/task-join
- ~~任务创建~~: task/create ->
- 任务详情: task/detail ->
- ~~任务列表~~: task/query ->

#### 模型

- 模型详情: model/detail ->
- 模型列表: model/query ->
- 模型上传(sts): submission/oss-token ->
- 模型上传: submission/create -> web/submission/create, web/submission-create


