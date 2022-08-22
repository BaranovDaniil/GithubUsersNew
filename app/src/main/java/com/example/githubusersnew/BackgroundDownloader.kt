package com.example.githubusersnew

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "BackgroundDownloader"
private const val MESSAGE_DOWNLOAD = 0

class BackgroundDownloader<in T : Any>(private val responseHandler: Handler) : HandlerThread(TAG),
    DefaultLifecycleObserver {
    val fragmentLifecycleObserver: DefaultLifecycleObserver =
        object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                Log.i(TAG, "Starting background thread")
                start()
                looper
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                Log.i(TAG, "Destroying background thread")
                quit()
            }
        }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, Int>()
    private val githubFetchr = GithubFetcher()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, sinceParam: Int) {
        requestMap[target] = sinceParam
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    private fun handleRequest(target: T) {
        val sinceParam = requestMap[target] ?: return
        githubFetchr.fetchUsers(sinceParam)
        responseHandler.post(Runnable {
            if (requestMap[target] != sinceParam || hasQuit) {
                return@Runnable
            }
            requestMap.remove(target)
        })

    }
}