package com.yorick.cokotools.util

import android.content.pm.IPackageInstaller
import android.content.pm.IPackageManager
import android.os.RemoteException
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object ShizukuSystemServerApi {
    private val PACKAGE_MANAGER: Singleton<IPackageManager?> =
        object : Singleton<IPackageManager?>() {
            override fun create(): IPackageManager {
                return IPackageManager.Stub.asInterface(
                    ShizukuBinderWrapper(
                        SystemServiceHelper.getSystemService("package")
                    )
                )
            }
        }

    @Throws(RemoteException::class)
    fun getPackageInstallerByPackageManager(): IPackageInstaller {
        val packageInstaller = PACKAGE_MANAGER.get()!!.packageInstaller
        return IPackageInstaller.Stub.asInterface(ShizukuBinderWrapper(packageInstaller.asBinder()))
    }
}