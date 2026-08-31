# KangApp

KangApp 是一个简单的 Android 工具 App。目前第一个功能是 **Laundry 查询**。

## 当前功能

- App 名称：KangApp
- 手机端左侧滑出功能菜单
- Laundry 查询作为第一个功能
- 点击「开始查询」后同时检查：
  - 辅2地下1号洗衣机
  - 辅2地下2号洗衣机
  - 辅2地下3号洗衣机
  - 辅2地下洗鞋机
  - 辅2地下烘干机
- 界面只显示：
  - 可用
  - 使用中
  - 不可用
  - 未知
- 不显示胖乖生活网页
- 不显示 URL、判断原因或调试日志
- 不会自动每 30 秒轮询；需要时点击「重新查询」

## 查询方式

Android 版不再使用 Python Playwright。

KangApp 在后台创建 Android WebView，访问二维码对应的胖乖生活 H5 地址，
等待页面跳转/渲染，然后读取最终 URL 与页面文字并判断状态。

状态判断逻辑与原 Python 版本保持一致：

1. 检测到「设备工作中 / 工作中，请更换设备使用 / 工作中」 -> 使用中
2. 检测到明确停用、维修、离线等信息 -> 不可用
3. 正常进入 h5.qiekj.com，且没有上述情况 -> 可用
4. 网络失败、超时、无法识别 -> 未知

## 开发环境

建议使用较新的 Android Studio。

项目配置：

- Kotlin / Compose
- Jetpack Compose Material 3
- Compose BOM 2026.08.00
- compileSdk 37
- targetSdk 37
- minSdk 26（Android 8.0+）
- JDK 17
- Android Gradle Plugin 9.2.0
- Gradle 9.4.1

## 打开项目

1. 解压 `KangApp.zip`
2. Android Studio -> Open
3. 选择 `KangApp` 文件夹
4. 等待 Gradle Sync
5. 如果 Android Studio 提示安装 Android SDK 37，点击安装
6. 连接 Android 手机并打开 USB debugging，或使用 Emulator
7. 点击 Run

## 生成 APK

Android Studio：

`Build -> Generate App Bundles or APKs -> Generate APKs`

Debug APK 通常会生成在：

`app/build/outputs/apk/debug/app-debug.apk`

## Gradle Wrapper / Windows 快速构建

项目包含 `gradle/wrapper/gradle-wrapper.properties`，版本指向 Gradle 9.4.1。

如果本机还没有 Gradle Wrapper，可以在 PowerShell 运行：

`./setup-gradle-wrapper.ps1`

脚本会从 Gradle 官方下载 Gradle 9.4.1，然后为工程生成标准 Wrapper。

之后可以运行：

`./build-debug-apk.ps1`

生成的 APK 位于：

`app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions 自动构建

项目已经包含：

`.github/workflows/android-build.yml`

推送到 GitHub 的 `main` 分支后，GitHub Actions 会自动构建 Debug APK。
也可以在 Actions 页面手动运行 `Build KangApp APK`，然后下载
`KangApp-debug-apk` artifact。

## 后续扩展

左侧菜单已经预留为 KangApp 的主导航结构。以后可以继续加入：

- 校园工具
- 宿舍网络检测
- 其他公共设备查询
- 课程工具
- 文件工具
- 其他模块
