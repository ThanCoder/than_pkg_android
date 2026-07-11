package com.example.than_pkg_android

import android.app.Activity
import android.content.Context
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

abstract class PkgHandler {
    // ရလာမယ့် context နဲ့ activity ကို သိမ်းထားဖို့ variable တွေ ကြေညာလို့ရတယ်
    protected var context: Context? = null
    protected var activity: Activity? = null
    var pendingResult: MethodChannel.Result? = null

    // handle ကတော့ မျိုးဆက်သစ် class တိုင်း မဖြစ်မနေ ကိုယ်ပိုင်ကုဒ် ရေးရမယ် (abstract)
    abstract fun handle(method: String, call: MethodCall, result: MethodChannel.Result)

    // updateContext ကို ဒီမှာတင် တစ်ခါတည်း အလုပ်လုပ်အောင် ရေးထားလိုက်လို့ရတယ်
    open fun updateContext(context: Context, activity: Activity?) {
        this.context = context
        this.activity = activity
    }

    // မလိုရင် override မလုပ်လည်း ရအောင် open ပေးထားတာ
    open fun onDetachedFromActivity() {
        this.activity = null
    }
}