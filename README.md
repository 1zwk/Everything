
## 一个everthing java版

### 项目目的：实现命令行下的文件快速搜索

### 项目技术： 

- 多线程
- JDBC编程
- 文件系统监控（Apache Commons IO）
- Database（嵌入式H2数据库）	
- Lombok插件，pinyin4j。

### 项目描述：   
   该项目主要分为三个模块：交互层，功能层和数据层。通过交互层的用户选择结果调用功能层里的代码，然后功能层进行数据层
   数据的插入，查找，删除等等。同时在用户启动程序时开启监控，实时文件同步。

### 项目功能：一个仿Everything的文件搜索工具，具体如下：

- 文件索引——多线程，并建立拼音对应（支持多音字，首拼）。
- 搜索文件——JDBC查询
- 文件监控——Monitor类+后台守护线程监视文件的创建和删除。
