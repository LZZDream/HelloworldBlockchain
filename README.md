### 欢迎来到HelloworldBlockchain  
HelloworldBlockchain是一个Helloworld级别的区块链公链系统。  
HelloworldBlockchain是一个Helloworld级别的数字货币项目。  
HelloworldBlockchain开发调试简单，下载源码，导入idea(eclipse)，无需任何配置，找到类文件com.xingkaichun.helloworldblockchain.node.HelloWorldBlockchainNodeApplication，右键运行，即可启动项目。  
项目架构清晰，详尽的全中文代码注释，代码以可读性为第一要素，适合区块链初学者学习研究。

### 联系方式
无论你心中有任何问题，都可扫二维码添加作者微信(微信号HelloworldBlockchain)
<img alt="作者微信" width="150" height="150" src="http://139.9.125.122:80/document/image/Wechat_HelloworldBlockchain.jpg"/>

### HelloworldBlockchain项目意义  
探索与实践区块链技术。   

### 演示
http://139.9.125.122:80/index.html  
<img alt="HellowordlBlockchain首页" src="http://139.9.125.122:80/document/image/HelloworldBlockchainIndexPage.png"/>

### 集成开发环境搭建
#### eclipse
HelloworldBlockchain项目没有任何复杂依赖，请搜索eclipse如何导入已存在的maven项目。
成功将项目导入eclipse后，找到类文件com.xingkaichun.helloworldblockchain.node.HelloWorldBlockchainNodeApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost:8444 。

#### intellij idea
HelloworldBlockchain项目没有任何复杂依赖，请搜索idea如何导入已存在的maven项目。
成功将项目导入idea后，找到类文件com.xingkaichun.helloworldblockchain.node.HelloWorldBlockchainNodeApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost:8444 。

### 模块架构
#### helloworld-blockchain-crypto
该模块封装了加密相关的工具。1.数字货币账户工具：①账户(私钥、公钥、地址)生成工具。②私钥、公钥、地址的相互转换工具。 2.数字签名与校验。 3.消息摘要：SHA-256、RipeMD160。 4.字节编码方案：base64、base58、hex等等。
#### helloworld-blockchain-core
该模块是整个区块链系统的核心，它代表着一个单机版区块链系统，它在底层维护着一条区块链的完整数据。设计之初，为了精简，它就被设计为不含有网络模块。除了网络模块，它含有一个区块链系统应有的功能，包含1.区块链账户生成 2.挖矿 3.新增区块到区块链 4.新增交易，交易将会被矿工挖矿使用 5.数据校验：区块验证、交易验证 6.分叉处理 7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
#### helloworld-blockchain-netcore
网络版区块链系统实现。底层依赖helloworld-blockchain-core模块。在单机版区块链系统的基础上，新增了网络功能：自动的在整个区块链网络中寻找/发布：节点、区块、交易。
#### helloworld-blockchain-node-transport-dto  
该模块用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 
#### helloworld-blockchain-node
helloworld-blockchain-node： 它集成了三个角色的功能。启动区块链节点后(项目打包、部署在文档下方)，在浏览器输入 http://localhost:8444 进入区块链系统的前台，即可从揽全局，体验区块链浏览器、节点管理员、开发调试等功能。
* 一是作为区块链浏览器的角色，对外提供了查询交易、查询区块、查询区块链网络节点、查询未花费输出等功能。
* 二是作为节点管理员的角色，为用户提供管理本地节点的功能，例如增/删/改网络节点、激活/停用矿工、激活/停用同步器。
* 三是开发调试角色，为开发人员提供了十分便利的调试功能。

### 使用手册 [跳转到手册](http://139.9.125.122:80/document/index.html)
* 获取网站管理员的初始账户
* 登录系统
* 账户地址是什么
* 主动生成账户
* 默认矿工账户
* 设置挖矿地址
* 启动/关闭挖矿
* 查询区块信息
* 查询账户余额

### 打包与发布
项目打包
```  
进入项目目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain   
将所有的子项目都部署到本地maven仓库  
mvn -P package-profile -Dmaven.test.skip=true clean package install  
进入子项目helloworld-blockchain-node目录  
cd helloworld-blockchain-node  
运行打包命令  
mvn -P package-profile -Dmaven.test.skip=true clean package install spring-boot:repackage assembly:single  
```  
发布项目
```  
进入打包结果目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain\helloworld-blockchain-node\target  
解压  
tar -zxvf helloworld-blockchain-node-*.tar.gz  
进入解压文件目录  
cd HelloworldBlockchainNode  
运行启动脚本  
./start.sh restart  
```  
### 系统强制升级机制
系统每一个发布版本，都带有一个截止运行日期。该版本到期后，必须强制升级系统，系统才能正常运行。参考GlobalSetting.SystemVersionConstant.isVersionLegal。

## 进阶
地址并不一定是由公钥产生。P2SH、P2WSH。  
被签名的数据可以自由选择。SIGHASH。

## 开发一款数字货币注意事项
保证区块哈希是区块的摘要，保证区块哈希能代表区块。  
保证交易哈希是交易的摘要，保证交易哈希能代表交易。  
保证交易输出哈希是交易输出的摘要，保证交易输出哈希能代表交易输出。
校验交易手续费
校验挖矿奖励
校验区块、交易的容量、每个字段的取值范围