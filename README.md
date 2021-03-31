## 介绍

MyPatcher是一款导出增量补丁文件的IDEA插件。

基于fun90的代码改造而来，感谢：https://github.com/fun90/patcher

## 下载

在IDEA的插件管理中搜索 MyPatcher

## 主要功能

1. 可以手动选择导出的修改过的编译文件或源码文件
2. 可以在Version Control中按修改日志导出的修改过的编译文件或源码文件
3. 可以手动选择文件或在Version Control中复制修改过的文件路径

## 更新日志

1. 2021.03.26 新增功能：WEB路径支持选择修改，不再固定为WEB-INF
2. 2021.03.27 新增功能：对项目目录进行选择时，会自动选中该目录下所有文件
3. 2021.03.31 修复功能：兼容Idea各版本
4. 2021.04.01 修复功能：WEB路径支持webapp、WebRoot、web、webapps