
# 概述
 ![enter image description here](https://img.shields.io/badge/jdk-v1.8+-blue.svg) ![enter image description here](https://img.shields.io/shippable/5444c5ecb904a4b21567b0ff.svg) ![size](https://img.shields.io/badge/size-272kB-green.svg)
> iuap-pap-baseservice框架属于iuap快速开发体系中的后台支持部分，通过与iuap前端开发框架结合,可快速的实现一套业务表单系统开发.

### 特性
* 集成图形化快速建模工具——[开发者中心](https://developer.yonyoucloud.com/)
* 快速启动业务表单开发
* 简化单表/主子表CRUD服务开发及通用性的持久化能力
* 简化iuap参照组件集成
* 提供iuap应用平台组件(流程、多租户、编码规则、打印、导入导出、附件、数据权限)可插拔式集成
* 简化[tinper](http://tinper.org/)前端组件集成
### 功能架构
![](docs/images/architecture_v1.png)
## Package Diagram
![](docs/images/PackageDiagram_v4.PNG)

## 安装及快速开始
> 示例工程仅展示了主子表通用服务能力，可插拔的组件能力需要依赖iuap应用平台
#### 环境依赖
本示例依赖配置好的maven+git环境，并且在maven的setting.xml中添加相应的repository
``` xml
<repository>
    <id>iUAP-Snapshots</id>
    <name>iUAP-Snapshots</name>
    <url>http://maven.yonyoucloud.com/nexus/content/repositories/iUAP-Snapshots/</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
<repository>
    <id>iUAP-Stagings</id>
    <name>iUAP-Stagings</name>
    <url>http://maven.yonyoucloud.com/nexus/content/repositories/iUAP-Stagings/</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
</repository>
```

#### 运行示例
下载源码
```bash
git clone https://github.com/sungine/iuap-pap-baseservice-demo.git
cd iuap-pap-baseservice-demo
```

推荐以Intellij IDEA打开工程
修改application.properties中数据库链接部分配置
``` ini 
jdbc.type=mysql
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://${yourMysqlIP}:3306/${yourMysqlDb}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
jdbc.username=${yourMysqlUser}
jdbc.password=${yourMysqlPsw}


```

运行sql

示例工程使用了mysql数据库,执行以下脚本创建示例table
``` sql
-- 示例主表
CREATE TABLE `demo` (
  `ID` varchar(36) NOT NULL,
  `TS` varchar(50) DEFAULT NULL,
  `UNITID` varchar(1000) DEFAULT NULL,
  `NAME` varchar(50) DEFAULT NULL,
  `LY_SM` varchar(50) DEFAULT NULL,
  `JFYQ` varchar(50) DEFAULT NULL,
  `ZY_CD` varchar(50) DEFAULT NULL,
  `CREATE_TIME` varchar(50) DEFAULT NULL,
  `CODE` varchar(50) DEFAULT NULL,
  `BEGIN_DATE` varchar(50) DEFAULT NULL,
  `KPI_LEVEL` varchar(50) DEFAULT NULL,
  `JD_BL` decimal(28,8) DEFAULT NULL,
  `RWPF` decimal(28,3) DEFAULT NULL,
  `UPDATE_NAME` varchar(36) DEFAULT NULL,
  `ZRR` varchar(500) DEFAULT NULL,
  `DBR` varchar(50) DEFAULT NULL,
  `CREATE_NAME` varchar(36) DEFAULT NULL,
  `XBR` varchar(500) DEFAULT NULL,
  `STATE` varchar(50) DEFAULT NULL,
  `ZR_DW` varchar(36) DEFAULT NULL,
  `UPDATE_TIME` varchar(50) DEFAULT NULL,
  `END_DATE` varchar(50) DEFAULT NULL,
  `LY_CODE` varchar(50) DEFAULT NULL,
  `DB_INFO` varchar(50) DEFAULT NULL,
  `XB_DW` varchar(500) DEFAULT NULL,
  `KPI_FLAG` varchar(65) DEFAULT NULL,
  `TENANT_ID` varchar(50) DEFAULT NULL,
  `QT_LD` varchar(50) DEFAULT NULL,
  `ZBR` varchar(500) DEFAULT NULL,
  `DR` decimal(65,3) DEFAULT NULL,
  `CREATE_USER` varchar(64) DEFAULT NULL,
  `LAST_MODIFIED` varchar(64) DEFAULT NULL,
  `LAST_MODIFY_USER` varchar(64) DEFAULT NULL,
  `BPM_STATE` decimal(11,0) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 示例子表
CREATE TABLE `demo_sub` (
  `id` varchar(36) NOT NULL,
  `NAME` varchar(50) DEFAULT NULL,
  `TENANT_ID` varchar(50) DEFAULT NULL,
  `UPDATE_TIME` varchar(50) DEFAULT NULL,
  `SUB_NAME` varchar(50) DEFAULT NULL,
  `UPDATE_NAME` varchar(36) DEFAULT NULL,
  `SUB_ID` varchar(36) DEFAULT '',
  `CREATE_TIME` varchar(50) DEFAULT NULL,
  `CREATE_NAME` varchar(36) DEFAULT NULL,
  `BEGIN_DATE` varchar(50) DEFAULT NULL,
  `SUB_MS` varchar(200) DEFAULT NULL,
  `END_DATE` varchar(50) DEFAULT NULL,
  `SUB_CODE` varchar(50) DEFAULT NULL,
  `FK_ID_YGDEMO_YW_SUB` varchar(36) DEFAULT NULL,
  `ZBR` varchar(50) DEFAULT NULL,
  `TS` varchar(50) DEFAULT NULL,
  `DR` decimal(65,3) DEFAULT NULL,
  `CREATE_USER` varchar(64) DEFAULT NULL,
  `LAST_MODIFIED` varchar(64) DEFAULT NULL,
  `LAST_MODIFY_USER` varchar(64) DEFAULT NULL,
  `BPM_STATE` decimal(11,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


```


启动服务
在src/main/test目录内找到com.yonyou.iuap.test.App类
``` java
public class App {
    public static void main(String[] args) throws Exception {
        AppStarter.run(App.class);
    }
}
```
以apllication方式运行之,正常时会显示如下日志,其中开放的接口在日志中可以见到
``` log

__     __     __     __          _____ _                 _ 
\ \   / /     \ \   / /         / ____| |               | |
 \ \_/ /__  _ _\ \_/ /__  _   _| |    | | ___  _   _  __| |
  \   / _ \| '_ \   / _ \| | | | |    | |/ _ \| | | |/ _` |
   | | (_) | | | | | (_) | |_| | |____| | (_) | |_| | (_| |
   |_|\___/|_| |_|_|\___/ \__,_|\_____|_|\___/ \__,_|\__,_|


服务器端口:9688
上下文路径:/baseservice-demo

应用地址:http://localhost:9688/baseservice-demo

##########代理服务器准备启动##########
#default=http://172.20.52.107:8080
default=http://172.20.53.35:8080
.....
...............
...................
.....................
......................
.......................
........................
.........................
文件解压成功！！！！！！
九月 28, 2018 9:35:58 上午 org.apache.catalina.core.StandardContext setPath
警告: A context path must either be an empty string or start with a '/' and do not end with a '/'. The path [/] does not meet these criteria and has been changed to []
代理启动进度:100%
九月 28, 2018 9:35:58 上午 org.apache.coyote.AbstractProtocol init
信息: Initializing ProtocolHandler ["http-nio-9688"]
九月 28, 2018 9:35:58 上午 org.apache.tomcat.util.net.NioSelectorPool getSharedSelector
信息: Using a shared selector for servlet write/read
九月 28, 2018 9:35:58 上午 org.apache.catalina.core.StandardService startInternal
信息: Starting service Tomcat
九月 28, 2018 9:35:58 上午 org.apache.catalina.core.StandardEngine startInternal
信息: Starting Servlet Engine: Apache Tomcat/8.5.0
......

09:36:25.447 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/saveAssoVo],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericAssoController.saveAssoVo(com.yonyou.iuap.baseservice.vo.GenericAssoVo<T>)
09:36:25.448 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/getAssoVo],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericAssoController.getAssoVo(org.springframework.data.domain.PageRequest,com.yonyou.iuap.mvc.type.SearchParams)
09:36:25.449 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/list],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.demo.controller.DemoController.list(org.springframework.data.domain.PageRequest,com.yonyou.iuap.mvc.type.SearchParams)
09:36:25.449 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/get],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericController.get(org.springframework.data.domain.PageRequest,com.yonyou.iuap.mvc.type.SearchParams)
09:36:25.449 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/delete],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericController.delete(T,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse) throws java.lang.Exception
09:36:25.449 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/save],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericController.save(T)
09:36:25.450 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/deleteBatch],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericController.deleteBatch(java.util.List<T>,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse) throws java.lang.Exception
09:36:25.450 [] [] [localhost-startStop-1] INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/demo/saveBatch],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.Object com.yonyou.iuap.baseservice.controller.GenericController.saveBatch(java.util.List<T>)
......
09:36:26.250 [] [] [localhost-startStop-1] INFO  o.s.web.servlet.DispatcherServlet - FrameworkServlet 'springServlet': initialization completed in 1845 ms
九月 28, 2018 9:36:26 上午 org.apache.coyote.AbstractProtocol start
信息: Starting ProtocolHandler [http-nio-9688]
访问地址:http://localhost:9688/



```



