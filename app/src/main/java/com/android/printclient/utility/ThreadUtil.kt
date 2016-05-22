package com.android.printclient.utility

/**
 * Created by jianglei on 16/5/22.
 */

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object ThreadUtil {

    // 定义核心线程数，并行线程数
    private val CORE_POOL_SIZE = 3

    // 线程池最大线程数：除了正在运行的线程额外保存多少个线程
    private val MAX_POOL_SIZE = 200

    // 额外线程空闲状态生存时间
    private val KEEP_ALIVE_TIME = 5000

    // 阻塞队列。当核心线程队列满了放入的
    // 初始化一个大小为10的泛型为Runnable的队列
    private val workQueue = ArrayBlockingQueue<Runnable>(
            10)
    // 线程工厂,把传递进来的runnable对象生成一个Thread
    private val threadFactory = object : ThreadFactory {

        // 原子型的integer变量生成的integer值不会重复
        val ineger = AtomicInteger()

        override fun newThread(arg0: Runnable): Thread {
            return Thread(arg0, "Thread:" + ineger.andIncrement)
        }
    }

    var threadpool: ThreadPoolExecutor? = null

    init {
        threadpool = ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME.toLong(), TimeUnit.SECONDS, workQueue, threadFactory)
    }

    fun execute(runnable: Runnable) {
        threadpool!!.execute(runnable)
    }
}