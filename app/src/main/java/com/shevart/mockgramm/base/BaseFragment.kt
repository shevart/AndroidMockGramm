package com.shevart.mockgramm.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseFragment : Fragment() {
    protected val forceContext: Context
        get() = context!!
    protected val rxPermission: RxPermissions
            by lazy { RxPermissions(this) }

    @LayoutRes
    protected abstract fun provideLayoutResId(): Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        return inflater.inflate(provideLayoutResId(), container, false)
    }

    protected fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(@StringRes msgResId: Int) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show()
    }

    protected fun handleErrorDefault(e: Throwable) {
        e.printStackTrace()
        showToast(e.localizedMessage)
    }

    protected fun backByBackButton() {
        activity?.onBackPressed()
    }

    protected fun log(msg: String) {
        log(tag = this::class.java.simpleName, msg = msg)
    }

    protected fun log(tag: String, msg: String) {
        Log.d(tag, msg)
    }
}