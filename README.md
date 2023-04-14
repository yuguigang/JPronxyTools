JProxyTools 打包
------------------------------------------------------------

>充分测试的打包方式：运行命令行```./gradlew jpackageImage```生成镜像
### 准备
- 当前JDK版本为Java 17。
- Windows：打包安装包需要安装 [WIX TOOLSET](https://wixtoolset.org/) ，并将其bin目录添加到环境变量中。
- 其他平台缺失的包一般可以根据提示通过包管理器直接安装。
- 打包镜像不需要其他打包工具

### 方式一（Gradle Task）
> 使用gradle自定义任务拼接命令进行打包（不要变动项目的gradle版本设置，同时请确认gradle运行在java17环境下）
- 构建镜像 -> ```gradlew package2Image```
- 构建安装包 -> ```gradlew package2Installer```

### 方式二（Badass JLink Plugin）

> 只支持Gradle
- 构建镜像 -> ```gradlew jpackageImage``` 构建的镜像包含运时
- 构建安装包 -> ```gradlew jpackage```



