package com.nmbb.oplayer.ui.vitamio;

import io.vov.vitamio.Vitamio;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.nmbb.oplayer.R;

public class InitActivity extends Activity {
	public static final String FROM_ME = "fromVitamioInitActivity";
	public static final String EXTRA_MSG = "EXTRA_MSG";
	public static final String EXTRA_FILE = "EXTRA_FILE";
	private ProgressDialog mPD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		new AsyncTask<Object, Object, Object>() {
			@Override
			protected void onPreExecute() {
				mPD = new ProgressDialog(InitActivity.this);
				mPD.setCancelable(false);
				mPD.setMessage(getText(R.string.init_decoders));
				mPD.show();
			}

			@Override
			protected Object doInBackground(Object... params) {

				Vitamio.initialize(getApplicationContext());
//				if (Vitamio.isInitialized(getApplicationContext()))
//					return null;
//
//				//反射解压
//				try {
//					Class c = Class.forName("io.vov.vitamio.Vitamio");
//					Method extractLibs = c.getDeclaredMethod("extractLibs", new Class[] { android.content.Context.class, int.class });
//					extractLibs.setAccessible(true);
//					extractLibs.invoke(c, new Object[] { getApplicationContext(), R.raw.libarm });
//					
////					Field vitamioLibraryPath = c.getDeclaredField("vitamioLibraryPath");
////
////					 AndroidContextUtils.getDataDir(ctx) + "libs/"
//					
//				} catch (NoSuchMethodException e) {
//					Log.e("extractLibs", e);
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}

				uiHandler.sendEmptyMessage(0);
				return null;
			}
		}.execute();
	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mPD.dismiss();
			Intent src = getIntent();
			Intent i = new Intent();
			i.setClassName(src.getStringExtra("package"), src.getStringExtra("className"));
			i.setData(src.getData());
			i.putExtras(src);
			i.putExtra(FROM_ME, true);
			startActivity(i);

			finish();
		}
	};
}
