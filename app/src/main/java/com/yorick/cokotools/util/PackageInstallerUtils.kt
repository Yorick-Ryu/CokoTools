package com.yorick.cokotools.util

import android.content.Context
import android.content.pm.IPackageInstaller
import android.content.pm.IPackageInstallerSession
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import java.lang.reflect.InvocationTargetException

object PackageInstallerUtils {
    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    fun createPackageInstaller(
        installer: IPackageInstaller?,
        installerPackageName: String?,
        installerAttributionTag: String?,
        userId: Int
    ): PackageInstaller {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PackageInstaller::class.java.getConstructor(
                IPackageInstaller::class.java,
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            ).newInstance(installer, installerPackageName, installerAttributionTag, userId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PackageInstaller::class.java.getConstructor(
                IPackageInstaller::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            ).newInstance(installer, installerPackageName, userId)
        } else {
            PackageInstaller::class.java.getConstructor(
                Context::class.java,
                PackageManager::class.java,
                IPackageInstaller::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            ).newInstance(
                ApplicationUtils.application,
                ApplicationUtils.application?.packageManager,
                installer,
                installerPackageName,
                userId
            )
        }
    }

    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    fun createSession(session: IPackageInstallerSession?): PackageInstaller.Session {
        return PackageInstaller.Session::class.java.getConstructor(
            IPackageInstallerSession::class.java
        )
            .newInstance(session)
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun getInstallFlags(params: PackageInstaller.SessionParams?): Int {
        return PackageInstaller.SessionParams::class.java.getDeclaredField("installFlags")[params] as Int
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun setInstallFlags(params: PackageInstaller.SessionParams?, newValue: Int) {
        PackageInstaller.SessionParams::class.java.getDeclaredField("installFlags")[params] =
            newValue
    }
}