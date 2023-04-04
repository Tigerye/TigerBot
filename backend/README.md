# X-PAI

虎博科技-X项目-人工智能平台(Platform of Artificial Intelligence, PAI)

## 模块定义 - Modules

### x-pai-api: 接口定义层

### x-pai-biz: 业务逻辑层

### x-pai-dal: 数据访问层

### x-pai-com: 公共组件层

### x-pai-service: 服务控制层

#### 用户认证 - auth

用户统一权限认证，管理所有系统的用户登陆、认证、权限管理

- APPID: x-pai-auth
- DOMAIN：pai.tigerobo.com/auth/
- MAIN-CLASS：com.tigerobo.x.pai.service.XPaiAuthServiceApplication.class

#### 业务系统 - biz

- APPID: x-pai-biz
- DOMAIN：pai.tigerobo.com/biz/
- MAIN-CLASS：com.tigerobo.x.pai.service.XPaiBizServiceApplication.class

#### 模型仓库 - repo

- APPID: x-pai-repo
- DOMAIN：pai.tigerobo.com/repo/
- MAIN-CLASS：com.tigerobo.x.pai.service.XPaiRepoServiceApplication.class

#### 统一服务 - serving

- APPID: x-pai-serving
- DOMAIN：pai.tigerobo.com/serving/
- MAIN-CLASS：com.tigerobo.x.pai.service.XPaiServingServiceApplication.class

#### 计算引擎 - engine

- APPID: x-pai-repo
- DOMAIN：pai.tigerobo.com/engine/
- MAIN-CLASS：com.tigerobo.x.pai.service.XPaiEngineServiceApplication.class

#### 前端接口 - web

- DOMAIN：pai.tigerobo.com/


#### todo 
1,视频转码迁移到单独的机器,现在依赖西梅后台机器http://admin-api.aigauss.com/api/ximei/video/videoUrlEncode 


#### search
调用pai-search模块rest接口
