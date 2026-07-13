package com.example.than_pkg_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

abstract class PkgHandler {
    protected var context: Context? = null
    protected var activity: Activity? = null
    var pendingResult: MethodChannel.Result? = null

    abstract fun handle(method: String, call: MethodCall, result: MethodChannel.Result)

    open fun updateContext(context: Context, activity: Activity?) {
        this.context = context
        this.activity = activity
    }

    open fun onDetachedFromActivity() {
        this.activity = null
    }
    open fun onNewIntent(intent: Intent) {}
}