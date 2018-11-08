
# 概述
 ![enter image description here](https://img.shields.io/badge/jdk-v1.7+-blue.svg) ![enter image description here](https://img.shields.io/shippable/5444c5ecb904a4b21567b0ff.svg) ![size](https://img.shields.io/badge/size-15kB-green.svg)
> 对外提供sdk应该遵循以下规范
# SDK开发说明
##### 1. 继承顶级抽象类
例如： class UserCenter  extends com.yonyou.iuap.baseservice.sdk.AbstractBaseSdk
##### 2. 实现抽象方法
protected String getUrlPreKey() {return "workbench.rest.user";}
>备注： 此方法返回的值对应的是使用方的application.properties中，配置的接口连接(类级)。
>例如： iuap.rest.workbench.user=http://127.0.0.1:8080/wbalone/user-rest。
>method上配置的requestmapping通过sdk类中的静态属性表示。在访问时进行拼接
##### 3. 实现具体的业务方法
    public  void getByUserId(String userId){
        //拼装url
        String             url = getUrlPre()+GET_BY_USER_ID;
        //拼装参数
        Map<String,String> map    =new HashMap();
        map.put("userId",userId);
        //发起请求
        JsonResponse<WBUser> jsonResponse = SdkHttpToolkit.doGet(url,map);
    }
    
# 服务间调用接口开发规范   
##### 1. 接口
>要求
1、Content-type:application/json
2、返回值通过JsonResponse封装 com.yonyou.iuap.baseservice.sdk.response.JsonResponse

	@RequestMapping(value = "/pagingList", method = RequestMethod.GET)
	public @ResponseBody JsonResponse pagingList(){
	
	}

##### 2. 数据结构
{"status":0,"msg":"成功","data":{"avator":"dfsf","dr":0,"email":"dddd","id":"sdfsdf"}}

##### 2. 验权
1、接口应该带有with-sign，例如 http://127.0.0.1:8080/wbalone/user-rest-with-sign/save

2、在wbalone-shiro.xml中，filterChainDefinitions里应添加配置/user-rest-with-sign/** = signAuth

# SDK方法命名规范
* save
* update
* delete
* get
* page
* list
* tree
# RESTFUL 接口规范
##### HTTP接口命名规范
> 遵循restfull命名规范

##### URL命名原则
* 不用大写
* 用中杠-不用下杠_
* 参数列表要encode
* URI中的名词表示资源集合，使用复数形式
##### 资源表示方式
* 资源集合
>* /zoos     &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;         //所有动物园
>* /zoos/1/animals   &nbsp; &nbsp; &nbsp; &nbsp; //id为1的动物园中的所有动物

* 单个资源
>* /zoos/1  &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//id为1的动物园
>* /zoos/1;2;3 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//id为1，2，3的动物园


##### URL分级
* 第一级Pattern为模块,比如组织管理/orgz, 网格化：/grid
* 第二级Pattern为资源分类或者功能请求，优先采用资源分类
##### CRUD请求定义规范（restful）
> 如果为资源分类，则按照RESTful的方式定义第三级Pattern，RESTful规范中，资源必须采用资源的名词复数定义。
##### URL分级
> /在url中表达层级，用于按实体关联关系进行对象导航，一般根据id导航 例如：/zoos/{id}
> url不要过深，尽量使用查询参数代替路径中的实体。例如用/animals?zoo=1&area=3代替/zoos/1/areas/3/animals/4
##### 组合实体
> 服务器端的组合实体必须在uri中通过父实体的id导航访问。

> 组合实体不是first-class的实体，它的生命周期完全依赖父实体，无法独立存在，在实现上通常是对数据库表中某些列的抽象，不直接对应表，也无id。一个常见的例子是 User — Address，Address是对User表中zipCode/country/city三个字段的简单抽象，无法独立于User存在。必须通过User索引到Address：GET /user/1/addresses

##### request定义要求
|             |          |
| :--------:  | :----- : |
| 查询        | GET       |
| 创建        | POST      |
| 更新        | PUT       |
| 删除        | DELETE    |
##### 安全性和幂等性
* 安全性：不会改变资源状态，可以理解为只读的。
* 幂等性：执行1次和执行N次，对资源状态改变的效果是等价的。 


|            |     安全性     | 等幂性     |
| :--------  | :-----:  | :-----:  |
| GET        | √       | √       |
| POST       | ×       | ×       |
| PUT        | ×       | √       |
| DELETE     | ×       | √       |

##### 复杂查询
> //TODO 整理分页，排序，过滤条件等规范
##### 错误处理
>Java 服务器端一般用异常表示 RESTful API 的错误。API 可能抛出两类异常：业务异常和非业务异常。业务异常由自己的业务代码抛出，表示一个用例的前置条件不满足、业务规则冲突等，比如参数校验不通过、权限校验失败。非业务类异常表示不在预期内的问题，通常由类库、框架抛出，或由于自己的代码逻辑错误导致，比如数据库连接失败、空指针异常、除0错误等等。

* 不要发生了错误但给2xx响应，客户端可能会缓存成功的http请求；
* 正确设置http状态码，不要自定义；
* Response body 提供 1) 错误的代码（日志/问题追查）；2) 错误的描述文本（展示给用户）


>业务类异常必须提供2种信息：

>>如果抛出该类异常，HTTP 响应状态码应该设成什么；
>>异常的文本描述；

>在Controller层使用统一的异常拦截器：

>>设置 HTTP 响应状态码：对业务类异常，用它指定的 HTTP code；对非业务类异常，统一500；
Response Body 的错误码：异常类名
Response Body 的错误描述：对业务类异常，用它指定的错误文本；对非业务类异常，线上可以统一文案如“服务器端错误，请稍后再试”，开发或测试环境中用异常的 stacktrace，服务器端提供该行为的开关。

##### 服务型资源
>除了资源简单的CRUD，服务器端经常还会提供其他服务，这些服务无法直接用上面提到的URI映射。可以把这些服务看成资源，计算的结果是资源的含义，按服务属性选择合适的HTTP方法
##### 异步任务
>对耗时的异步任务，服务器端接受客户端传递的参数后，应返回创建成功的任务资源，其中包含了任务的执行状态。客户端可以轮训该任务获得最新的执行进度
##### API的演进
常见的三种方式：
* 在uri中放版本信息：GET /v1/users/1
* Accept Header：Accept: application/json+v1
* 自定义 Header：X-Api-Version: 1
>第一种虽然不优雅，但是最方便。
##### URI失效
随着系统发展，总有一些API失效或者迁移，对失效的API，返回404 not found 或 410 gone；对迁移的API，返回 301 重定向
##### 安全
* 敏感字段谨慎返回
* encode
* 404 500等统一处理


