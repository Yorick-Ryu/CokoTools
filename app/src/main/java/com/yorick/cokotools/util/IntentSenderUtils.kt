package com.yorick.cokotools.util

import android.content.IIntentSender
import android.content.IntentSender
import java.lang.reflect.InvocationTargetException

object IntentSenderUtils {
    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    fun newInstance(binder: Any): IntentSender {
        return IntentSender::class.java.getConstructor(IIntentSender::class.java)
            .newInstance(binder)
    }
}
