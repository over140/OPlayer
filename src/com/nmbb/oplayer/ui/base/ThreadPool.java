package com.nmbb.oplayer.ui.base;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Build;

public class ThreadPool {
	private AtomicBoolean mStopped = new AtomicBoolean(Boolean.FALSE);
	private ThreadPoolExecutor mQueue;

	public ThreadPool() {
		if (Build.VERSION.SDK_INT > 8) {
			mQueue = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);
			mQueue.allowCoreThreadTimeOut(true);
		} else {
			mQueue = new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);
		}
	}

	public void start(Runnable run) {
		mQueue.execute(run);
	}

	public void stop() {
		if (!mStopped.get()) {
			mQueue.shutdownNow();
			mStopped.set(Boolean.TRUE);
		}
	}

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ThreadPool #" + mCount.getAndIncrement());
		}
	};
}
