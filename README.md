# 蓝胖子甜品屋
蓝胖子甜品屋是一个微服务架构网购平台(买家端)项目，使用领域驱动设计思想设计，基于 spring cloud 框架实现。



> 温馨小提示：
>
>   由于开发者本人也处于学习阶段，该项目中可能包含设计、代码实现等不合理现象。还请正在观看本项目的您，一定持有怀疑、批评的眼光看待本项目哈，以免给您的学习引入不合理甚至错误的观点。
>
>   如果在观看本项目过程中发现不合理之处，还请您批评指正，谢谢啦。



## 架构

### 项目整体架构图

| ![Architecture](images\micro_service_architecture.svg) |
| :----------------------------------------------------: |
|                **项目整体微服务架构图**                |

> 上图是本项目的架构图，实际开发中还需要：
>
> 1. **分布式链路追踪**。方便接口出错时，定位错误位置等。
>
>    常见解决方案(组件)：
>
>    1. **zipkin + spring cloud sleuth**；
>    2. **skywalking**；
>    3. 等等
>
> 2. **服务的熔断、限流**。
>
>    熔断、限流作用：
>
>    * **熔断**：防止下流把我**拖垮**；
>    * **限流**：防止上游把我**压垮**；
>
>    上游：指上游服务。
>
>    下游：指下游服务。
>
>    "我"：指某个微服务。
>
>    常见解决方案(组件)：
>
>    1. Netflix 的 **hystrix**；
>    2. alibaba 的 **sentinel**；
> 3. 等等
>
>    



### context map

| <img src="images\context_map.svg" alt="context_map" style="zoom:50%;" /> |
| :----------------------------------------------------------: |
|                       **Context Map**                        |

本项目使用 **领域驱动设计思想**(DDD) 进行设计，上图为本人为本项目绘制的 context map。

> 温馨小提示：
>
> ​	如果您对领域驱动设计感兴趣的话，强烈推荐您观看 《领域驱动设计软件核心复杂性应对之道》这本书，DDD 经典之作。



### 微服务分层架构图

| <img src="images\layed_architecture.jpg" alt="layed_architecture" style="zoom:50%;" /> |
| :----------------------------------------------------------: |
|                    **各微服务分层架构图**                    |

架构简单介绍：

#### 常见的3层架构

常见的3层架构分为：

1. 数据访问层 dao
2. 业务逻辑层 service
3. 用户界面层：在 前端后分离前是指前端用户界面(回顾一下 jsp)，vue、react、angular等 mvvm 框架出现后，前后端分离，所以这里常常放各种 controller。



问题：

1. **贫血模型**

  对于这种架构，编码起来会简单很多，可能一上来就建3个 package( controller、service、dao)，然后，看看数据库有哪些表，表里有什么字段，就根据这些字段设计一个 model 类(pojo类)，然后建一个和该 model 对应的 service 类，该 model 需要什么操作就在对应 service 中添加什么方法，然后开始写对应的 controller。很快吧~。

​	但是停下脚步回头看看这样的设计，上述的实体类是不是只有和某张表字段对应的类属性以及各种和该属性对应的 getter、setter。而本属于该实体的各种动作却出现在了 XxxService 类中，有点怪怪的对吧？一个学生对象，从面向对象的角度来看，修改住址等方法属于学生，但却交给了 StudentService实现，修当要修改地址的时候把学生对象传递过去，这个 学生对象 是不是很像一个有手有脚有名字，但却不会动的标本呀。(哈，虽然标本也是对象，但希望您能了解到我想表达的意思)。

​	这种只有属性、getter、setter的实体类，被称为 **贫血模型**。(补充一下下哈，现实世界中，有的类本身就只有属性，没有行为，此时，这样的对象对应的模型不算是贫血模型 - 总不能因为一个人长的像坏人你就说他是坏人吧)。

2. **和数据库耦合**

  架构中，我们可能会创建 **XxxMapper / XxxDao**，然后在该类中，直接操作底层数据库，比如常见的 **MySQL**，然后在 **XxxService** 中直接使用这写 Mapper / Dao 类操作数据库，似乎好像没问题。

   但是，如果某天，底层存储系统由 关系型数据库MySQL 换成 非关系数据(如：MongoDB) 了怎么办？

​	重新写一个 **XxxMapper / XxxDao**，然后疯狂在各个 **XxxService** 类中找，看看哪里用到了老版本的 mapper / dao， 然后作修改。



#### 经典 DDD 分层架构

| <img src="D:\sweetcat_workspace\images\layerd_arch.png" alt="layerd_arch" style="zoom:50%;" /> |
| :----------------------------------------------------------: |
|                    **经典 DDD 分层架构**                     |

分层架构分为2类：

1. 严格分层架构: 上层直接依赖于其直接下层。
2. 松散分层架构: 上层可依赖于说有位于其下层。

  经典 DDD 这种分层有人发现也有点不好，如：Repository接口以及其实现类，如果将  Repository 接口定义及实现一同放到基础设施层，此时位于基础设施层的 repository接口定义及实现需要使用到领域对象，即基础设施层违反分层架构原则，外过来依赖于上层领域层。

  而且，根据稳定依赖原则，基础基础设施层应该依赖于更抽象、更稳定的领域层，所以有人提出了优化版的 DDD 分层架构。



#### 依赖导致原则后的 DDD 分层架构

| <img src="D:\sweetcat_workspace\images\layed_architecture.jpg" alt="layed_architecture" style="zoom:50%;" /> |
| :----------------------------------------------------------: |
|               **依赖导致后的 DDD  分层架构图**               |

  将基础设施层置于用户接口层之上，并将 Repository 接口的 **定义** 与 **实现** 分离。**Repository 接口的定义** 置于领域层，**Repository接口的实现**置于基础设施层，这样一来，就不会违反分层架构的一来原则。



> 补充一下下，仔细思考一来一来导致后的分层架构，它还是分层的吗？？
>
> 似乎，好像没有了吧。整个架构变成了平面的，依赖关系不是上层依赖下层，而是 **具体依赖于抽象**。仔细思考一下 依赖倒置后的 Respository接口定义 和 其具体实现(RespositoryImpl)的关系。依赖倒置后的基础设施层其实是依赖于更加抽象的领域层。
>
> 所以，有人又提出了没有分层概念的 **六边形架构** 架构等。



## 主要技术栈

* **React.js**: 用于前端用户界面的编写。(前端用户界面代码请移步[这里](xxx)) 。

* **spring cloud 框架**

  * **spring cloud gateway**: 服务**网关**。
  * **spring cloud openfeign**: 各**微服务间的调用**。
  * **spring cloud alibaba nacos**: 作为项目的 **服务注册中心** 以及 **配置中心**。
  * **spring cloud alibaba seata**: 优秀的分布式事务解决方案。用于实现服务间分布式事务。

* **alibaba rocketMQ**: 实现基于消息队列的事件驱动架构。

* **redis**：

  * 热点数据缓存；
  * 实现整个 **购物车微服务**( sweetcat-user-trolley )；
  * 借助 **`bitmap`** 实现 **积分中心微服务**( sweetcat-app-credit ) 用户签到功能；

* **redisson**：使用 redisson 框架提供的 **布隆过滤器** 防止 **缓存穿透问题**；

* **sharding jdbc**：用于实现 **分库分表**。

  > alibaba 《java 开发手册 嵩山版》建议：
  >
  > 当表行数超过 **500 万行** 或 单表 **容量超 2G** 才推荐进行分布分表。
  >
  > 如果预计十三年后数据量根本打不到这个标准，请不要分库分表。



## 各服务介绍

* **sweetcat-scaffold**: 模板。

  偷懒用哒。该模块包含了各个微服务所使用到的基本的包结构。

* **sweetcat-api**: 

  供 **spring cloud openfeign** 用的各种 Client（Interfaces) 以及 DTO。

* **sweetcat-commons**:

  包含：

  1. 领域事件定义；
  2. 异常定义；
  3. 全局异常处理类；
  4. 前端响应类定义；
  5. 工具类；

* **sweetcat-gateway**: 网关微服务。

  包括 **spring cloud gateway** 配置等信息。



### 平台子域微服务

* **sweetcat-app-activity**: 平台活动服务服务。

  用于管理 app 中活动相关内容，如 淘宝双11，京东618等活动预热、开始时，会在 app 界面展示。

* **sweetcat-app-carousel**: 平台轮播图微服务。

  用于管理 app 中轮播图相关内容。

* **sweetcat-app-couponcenter**: 优惠券中心微服务。

  平台优惠券中心微服务。可发布免费优惠券供用户免费领取。

* **sweetcat-app-credit**: 积分中心微服务。

  功能包括：

  1. 用户签到领取积分。
  2. 消耗积分兑换优惠券。
  3. 用户积分收支记录管理。
  4. 用户优惠券兑换记录管理。

* **sweetcat-app-customerservice**：平台客服微服务。

  功能包括：

  1. 客服人员信息注册、管理；
  2. 处理用户反馈；
  3. 订阅 领域事件**`FeedbackSubmittedEvent`** ，在事件发生时，记录用户反馈信息；

* **sweetcat-app-feedback**: 反馈微服务

  功能包括：

  1. 保存用户反馈，并在用户提交反馈信息后触发 领域事件**`FeedbackSubmittedEvent`** ；

* **sweetcat-app-fileupload**: 文件上传微服务。

* **sweetcat-app-geography**: 地址信息微服务。

  功能包括：

  1. 保存中国省、市、街道等信息，以便校验用户收货地址、买家店铺地址是否有效。



### 买家子域微服务

* **sweetcat-user-info**: 买家信息微服务。

  功能包括：

  1. 账号注册；
  2. 用户登录；
  3. 基本信息管理；

* **sweetcat-user-comment**: 用户评论微服务。

  功能包括：

  1. 发布商品评论；
  2. 评论恢复；
  3. 评论管理；

* **sweetcat-user-coupon**: 用户优惠券微服务

  功能包括：

  1. 订阅领域事件**`UserAcquiredCommodityCouponEvent`**。
  2. 已有优惠券管理；

* **sweetcat-user-favorite**: 用户收藏夹微服务

  功能包括：

  1. 收藏商品；
  2. 管理已搜藏商品；

* **sweetcat-user-footprint**: 用户足迹微服务

  功能包括：

  1. 用户足迹(浏览记录)管理；

* **sweetcat-user-information**: 用户通知微服务

  功能包括：

  1. 通知用户来自其他用户的评论。
  2. 通知用户来自平台的通知(如：反馈已处理)

* **sweetcat-user-order**：用户订单微服务

  功能包括：

  1. 订单管理；

* **sweetcat-user-recommend**: 用户推荐微服务

  功能包括：

  1. 发布商品推荐信息；
  2. 管理已发布的商品推荐信息；

* **sweetcat-user-relationship**: 用户管理微服务

  功能包括：

  1. 用户关系管理(关注、取关)；
  2. 粉丝数、关注数修改、查询；

* **sweetcat-user-trolley**: 用户购物车微服务

  功能包括：

  1. 商品加购；
  2. 已加购商品管理(增、删、修改数量、修改规格)；



### 买家子域微服务

* **sweetcat-store-commodity** 商品微服务。

  功能包括：

  1. 商品发布；
  2. 已发布商品信息管理。

* **sweetcat-store-info** 买家店铺信息微服务。

  功能包括：

  1. 添加卖家店铺信息；
  2. 已注册卖家店铺信息管理；

* **sweetcat-store-order** 卖家订单微服务。

  功能包括：

  1. 订阅领域事件**`UserChangedAddressEvent`**，在用户修改订单收货地址时，更改本微服务内相应的订单信息。
  2. 按用户编号查找对应用户的订单信息。



### 外卖骑手子域微服务

* **sweetcat-takeaway-man-info**: 外卖骑手信息微服务。

  功能包括：

  1. 骑手信息注册；
  2. 骑手信息查询；

* **sweetcat-takeaway-order**: 骑手订单微服务。

  功能包括：

  1. 监听领域事件**`UserChangedAddressEvent`**。
  2. 订单信息查询；




