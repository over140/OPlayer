package com.nmbb.oplayer.ui.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.util.Log;

import com.nmbb.oplayer.ui.base.ThreadPool;

public class FileDownloadHelper {
	private static final String TAG = "FileDownloadHelper";
	/** 线程池 */
	private ThreadPool mPool = new ThreadPool();
	/** 开始下载 */
	public static final int MESSAGE_START = 0;
	/** 更新进度 */
	public static final int MESSAGE_PROGRESS = 1;
	/** 下载结束 */
	public static final int MESSAGE_STOP = 2;
	/** 下载出错 */
	public static final int MESSAGE_ERROR = 3;
	/** 中途终止 */
	private volatile boolean mIsStop = false;
	private Handler mHandler;
	public volatile HashMap<String, String> mDownloadUrls = new HashMap<String, String>();

	public FileDownloadHelper(Handler handler) {
		if (handler == null)
			throw new IllegalArgumentException("handler不能为空!");

		this.mHandler = handler;
	}

	public void stopALl() {
		mIsStop = true;
		mPool.stop();
	}

	/**
	 * 下载一个新的文件
	 * 
	 * @param url
	 * @param savePath
	 */
	public void newDownloadFile(final String url, final String savePath) {
		if (mDownloadUrls.containsKey(url))
			return;
		else
			mDownloadUrls.put(url, savePath);
		mPool.start(new Runnable() {

			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_START, url));
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				InputStream inputStream = null;
				FileOutputStream outputStream = null;
				try {
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					final int size = (int) entity.getContentLength();
					inputStream = entity.getContent();
					if (size > 0 && inputStream != null) {
						outputStream = new FileOutputStream(savePath);
						int ch = -1;
						byte[] buf = new byte[1024];
						//每秒更新一次进度
						new Timer().schedule(new TimerTask() {

							@Override
							public void run() {
								try {
									FileInputStream fis = new FileInputStream(new File(savePath));
									int downloadedSize = fis.available();
									if (downloadedSize >= size)
										cancel();
									mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_PROGRESS, downloadedSize, size, url));
								} catch (Exception e) {

								}
							}
						}, 50, 1000);

						while ((ch = inputStream.read(buf)) != -1 && !mIsStop) {
							outputStream.write(buf, 0, ch);
						}
						outputStream.flush();
					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
					mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_ERROR, url + ":" + e.getMessage()));
				} finally {
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (IOException ex) {
					}
					try {
						if (inputStream != null)
							inputStream.close();
					} catch (IOException ex) {
					}
				}
				mDownloadUrls.remove(url);
				mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_STOP, url));
			}
		});
	}
}
