package com.yorick.cokotools.util

import android.content.IIntentReceiver
import android.content.IIntentSender
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

abstract class IIntentSenderAdaptor : IIntentSender.Stub() {
    abstract fun send(intent: Intent?)
    override fun send(
        code: Int,
        intent: Intent,
        resolvedType: String,
        finishedReceiver: IIntentReceiver,
        requiredPermission: String,
        options: Bundle
    ): Int {
        send(intent)
        return 0
    }

    override fun send(
        code: Int,
        intent: Intent,
        resolvedType: String,
        whitelistToken: IBinder,
        finishedReceiver: IIntentReceiver,
        requiredPermission: String,
        options: Bundle
    ) {
        send(intent)
    }
}