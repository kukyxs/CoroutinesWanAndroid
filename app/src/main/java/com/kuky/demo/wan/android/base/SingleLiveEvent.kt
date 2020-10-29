package com.kuky.demo.wan.android.base

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author kuky.
 * @description
 */
class SingleLiveEvent<T> : LiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(SingleLiveEvent::class.java.simpleName, "Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner, {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    @MainThread
    fun call() {
        value = null
    }
}
