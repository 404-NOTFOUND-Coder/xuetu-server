# Xuetu-server

#### 介绍
学途-后端项目代码

#### 软件架构
前端：Vue.js + elementUI

后端：SpringBoot + MyBatis

#### 安装教程
1.配置maven环境；

2.拉取依赖包

3.在application.yml文件中datasource下进行数据库配置

数据库连接池在datasource中进行配置：url为数据库地址，username为数据库用户名，password为数据库连接密码

mybatis中配置所有实体类所在的路径和映射文件

configuration中配置数据库日志

server中port为服务端口号，默认为8081

alipay中需要配置

appId 支付宝沙箱应用id

appPrivateKey 支付宝沙箱应用私钥

alipayPublicKey 支付宝沙箱公钥

notifyUrl 支付宝沙箱支付成功后回调地址
