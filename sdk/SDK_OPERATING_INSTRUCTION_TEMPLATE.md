
# 概述
 ![enter image description here](https://img.shields.io/badge/jdk-v1.7+-blue.svg) ![enter image description here](https://img.shields.io/shippable/5444c5ecb904a4b21567b0ff.svg) ![size](https://img.shields.io/badge/size-15kB-green.svg)
> xxx接口提供了XX服务，具有xxx，xxx，方法。

# XXX接口使用文档
> 使用接口前应配置SDK属性，对应的文件为application.properties
## 使用前准备
1、 添加maven依赖

        <dependency>
			<groupId></groupId>
		    <artifactId></artifactId>		    
        </dependency>
2、 接口属性配置
workbench.sdk.[模块].[服务].url=http://ip:port/webcontext/service-url-mapping
> xx接口的url，必填

workbench.sdk.[模块].[服务].channel=with-sign
> 调用xx接口使用的通道，非必填。默认为with-sign。

## SDK 方法说明
##### 保存(save)
1、参数

| 参数名            |    含义      |  
| :--------:  | :----- : |
| userId        | 用户id       |
| userName        | 用户名称      |


1、返回值


