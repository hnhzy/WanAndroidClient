
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.annotation.StringRes
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.just.agentweb.AgentWeb
import com.just.agentweb.ChromeClientCallbackManager
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.cancelAndJoin
import retrofit2.Call
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Log
 */
fun loge(tag: String, content: String?) {
    Log.e(tag, content ?: tag)
}

/**
 * show toast
 * @param content String
 */
@SuppressLint("ShowToast")
fun Context.toast(content: String) {
    Constant.showToast?.apply {
        setText(content)
        show()
    } ?: run {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).apply {
            Constant.showToast = this
        }.show()
    }
}

/**
 * show toast
 * @param id strings.xml
 */
fun Context.toast(@StringRes id: Int) {
    toast(getString(id))
}

/**
 * async change to sync
 */
suspend fun <T> asyncRequestSuspend(block: (Continuation<T>) -> Unit) = suspendCoroutine<T> {
    block(it)
}

/**
 * cancel coroutines
 */
suspend fun Deferred<Any>?.cancelAndJoinByActive() = this?.run {
    if (isActive) {
        this.cancelAndJoin()
    }
}

/**
 * cancel call request
 */
fun <T> Call<T>?.cancelCall() = this?.run {
    if (!isCanceled) {
        // cancel request
        cancel()
    }
}

/**
 * getAgentWeb
 */
fun String.getAgentWeb(activity: Activity, webContent: ViewGroup, layoutParams: ViewGroup.LayoutParams, receivedTitleCallback: ChromeClientCallbackManager.ReceivedTitleCallback?) = AgentWeb.with(activity)//传入Activity or Fragment
        .setAgentWebParent(webContent, layoutParams)//传入AgentWeb 的父控件
        .useDefaultIndicator()// 使用默认进度条
        .defaultProgressBarColor() // 使用默认进度条颜色
        .setReceivedTitleCallback(receivedTitleCallback) //设置 Web 页面的 title 回调
        .createAgentWeb()//
        .ready()
        .go(this)