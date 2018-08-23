package com.shevart.mockgramm.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions

@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {
    protected val rxPermission: RxPermissions
            by lazy { RxPermissions(this) }

    @LayoutRes
    protected abstract fun provideLayoutResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(provideLayoutResId())
    }

    final override fun setContentView(layoutResID: Int) {
        throw UnsupportedOperationException("Use provideLayoutResId() method!")
    }

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}