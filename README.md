# CokoTools | 酷客工具箱

适用于vivo/iqoo的工具箱
---
## 目前功能如下：

详细文档：[《酷客工具箱》帮助文档](https://yorick-ryu.github.io/Project/CookTools-doc/)

- 锁频段LockBands
- 状态栏显秒
- 工厂测试EngineerMode
- 电量统计FuelSummary
- 设置勿扰模式
- WIFI密码查看
- 满血充电MAX

## AIDL 使用方式

本项目通过 Shizuku 与系统服务进行 Binder IPC，依赖 hidden-api 模块内的 AIDL 生成接口（非自定义 .aidl 文件）。主要流程如下：

1. 在 `hidden-api/src/main/java/android/**` 中提供 `IPackageManager`、`IPackageInstaller`、`IPackageInstallerSession`、`IIntentSender` 等 AIDL 接口存根。
2. 在 `app/src/main/java/com/yorick/cokotools/util/ShizukuSystemServerApi.kt` 中通过 `SystemServiceHelper.getSystemService("package")` 获取系统服务 `IBinder`，再用 `IPackageManager.Stub.asInterface(...)` 包装为 AIDL 接口。
3. `InstallUtils.kt` 通过 `IPackageInstaller`/`IPackageInstallerSession` 完成安装会话并提交，`IIntentSenderAdaptor.kt` 实现回调以接收安装结果。
4. `PackageInstallerUtils.kt` 和 `IntentSenderUtils.kt` 用反射将 AIDL 接口包装为公开 API 便于业务层调用。
