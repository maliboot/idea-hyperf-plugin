IntelliJ IDEA / PhpStorm Hyperf Plugin
========================
[toc]

| Key        | Value                                     |
|------------|-------------------------------------------|
| Plugin Url | https://plugins.jetbrains.com/plugin/23007 |
| ID         | `io.maliboot.www.hyperf`                  |
| Changelog  | [CHANGELOG](CHANGELOG.md)                 |

# About
这是一个对`hyperf`框架/组件、或者依赖`hyperf`组件编写的第三方`vendor`扩展包，提供功能支持的`PHPStorm`插件。

# Install
* 通过插件市场安装 [Hyperf Support](https://plugins.jetbrains.com/plugin/23007) `Settings -> Plugins -> Browse repositories`
* 通过本地磁盘安装 [Hyperf Support](https://github.com/maliboot/idea-hyperf-plugin/releases) `Settings -> Plugins -> Install Plugin from Disk...`，插件jar包可以在[release](https://github.com/maliboot/idea-hyperf-plugin/releases)页面找到
* 预览版本只能通过[release](https://github.com/maliboot/idea-hyperf-plugin/releases)页面安装，里面包含了一些正在测试中的功能

# Feature
## debug
本插件会定位`模板文件`或`代理文件`的断点所在行第一个`ast节点`，计算出该节点的树路径相对偏移量，得出对应文件的断点位置，尽可能让`hyperf`得以在`模板文件`内调试
![Debug Preview](doc/xdebug.png)
>>> hyperf的AOP会生成编译生成代理文件，如`app/IndexController.php`会生成`./runtime/container/proxy/APP_IndexController.proxy.php`代理文件。当程序启动时，`IndexController.php`变成了模板文件不再参与程序运行，`APP_IndexController.proxy.php`成为了实际上真正运行的文件，所以当debug调试程序时所有的`断点`和`堆栈列表日志`都追溯到了代理文件内。

## Lombok
* Lombok支持：[maliboot/lombok](https://github.com/maliboot/lombok)适配，解决该PHP扩展使用时编辑器没有自动补全、参数提示、错误语法高量、导航等功能

# Idea
* 如果使用插件过程中有报错，可以[提交堆栈报告](https://github.com/maliboot/idea-hyperf-plugin/issues/new?title=[BUG]XXX主题&body=...)给我。这对我非常重要，我会及时修复
* 欢迎给我提功能建议👏，如果戳中了需求痛点，我会抽时间来开发。[点这里提需求](https://github.com/maliboot/idea-hyperf-plugin/issues/new?title=%E3%80%90%E5%8A%9F%E8%83%BD%E5%BB%BA%E8%AE%AE%E3%80%91XXX&body=...)