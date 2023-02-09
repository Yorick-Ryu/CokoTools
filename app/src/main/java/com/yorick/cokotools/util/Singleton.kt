package com.yorick.cokotools.util

abstract class Singleton<T> {
    private var mInstance: T? = null
    protected abstract fun create(): T
    fun get(): T? {
        synchronized(this) {
            if (mInstance == null) {
                mInstance = create()
            }
            return mInstance
        }
    }
}