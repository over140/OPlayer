package com.nmbb.oplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.nmbb.oplayer.service.MediaScannerService;

/** 文件扫描 */
public class MediaScannerReceiver extends BroadcastReceiver {

	public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "";
	public static final String ACTION_MEDIA_SCANNER_SCAN_DIRECTORY = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Uri uri = intent.getData();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			//扫描整个SD卡目录 
			//FIXME 处理多个SD卡的问题
			//			Environment.getExternalStorageDirectory().getParentFile();
			scanDirectory(context, Environment.getExternalStorageDirectory().getAbsolutePath());
		} else if (uri.getScheme().equals("file")) {
			String path = uri.getPath();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				scanDirectory(context, path);
			} else if (action.equals(ACTION_MEDIA_SCANNER_SCAN_FILE) && path != null) {
				scanFile(context, path);
			} else if (action.equals(ACTION_MEDIA_SCANNER_SCAN_DIRECTORY) && path != null) {
				scanDirectory(context, path);
			}
		}
	}

	/** 扫描文件夹 */
	private void scanDirectory(Context context, String volume) {
		Bundle args = new Bundle();
		args.putString(MediaScannerService.EXTRA_DIRECTORY, volume);
		context.startService(new Intent(context, MediaScannerService.class).putExtras(args));
	}

	private void scanFile(Context context, String path) {
		Bundle args = new Bundle();
		args.putString(MediaScannerService.EXTRA_FILE_PATH, path);
		context.startService(new Intent(context, MediaScannerService.class).putExtras(args));
	}
	//	private static boolean isScanning = false;
	//	//	private boolean isScanningStarted = false;
	//	private IReceiverNotify mNotify;
	//
	//	public MediaScannerReceiver() {
	//	}
	//
	//	public MediaScannerReceiver(IReceiverNotify notify) {
	//		mNotify = notify;
	//	}
	//
	//	public static boolean isScanning(Context ctx) {
	//		return isServiceRunning(ctx, "io.vov.vitamio.MediaScannerService");
	//	}
	//
	//	@Override
	//	public void onReceive(Context context, Intent intent) {
	//		final String action = intent.getAction();
	//		Log.i("MediaScannerReceiver", action);
	//	}
	//
	//	/** 服务是否正在运行 */
	//	public static boolean isServiceRunning(Context ctx, String name) {
	//		ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	//		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	//			if (name.equals(service.service.getClassName()))
	//				return true;
	//		}
	//		return false;
	//	}
}
