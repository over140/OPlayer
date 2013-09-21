package com.nmbb.oplayer.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.nmbb.oplayer.R;
import com.nmbb.oplayer.po.OnlineVideo;
import com.nmbb.oplayer.ui.base.ArrayAdapter;
import com.nmbb.oplayer.ui.helper.XmlReaderHelper;
import com.nmbb.oplayer.ui.player.VideoActivity;
import com.nmbb.oplayer.util.FileUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentOnline extends FragmentBase implements OnItemClickListener {

	/** 缓存视频列表 */
	private static ArrayList<String[]> mOnlineList = new ArrayList<String[]>();
	/** 缓存视频LOGO列表 */
	private static ArrayList<Integer> mOnlineLogoList = new ArrayList<Integer>();
	private WebView mWebView;
	private ListView mListView;
	/** 网页正在加载 */
	private View mLoading;
	/** 历史记录 */
	private List<String> mHistory = new ArrayList<String>();
	/** 显示当前正在加载的url */
	private TextView mUrl;
	private String mTitle;
	private final static ArrayList<OnlineVideo> root = new ArrayList<OnlineVideo>();
	private ArrayList<OnlineVideo> tvs;
	private final static ArrayList<OnlineVideo> videos = new ArrayList<OnlineVideo>();
	private int level = 1;
	private DataAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_online, container,
				false);
		mListView = (ListView) mView.findViewById(android.R.id.list);
		mWebView = (WebView) mView.findViewById(R.id.webview);
		mUrl = (TextView) mView.findViewById(R.id.url);
		mLoading = mView.findViewById(R.id.loading);

		mListView.setOnItemClickListener(this);
		initWebView();
		mAdapter = new DataAdapter(getActivity());
		mListView.setAdapter(new DataAdapter(getActivity()));
		return mView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final OnlineVideo item = mAdapter.getItem(position);
		switch (level) {
		case 1:// 顶级
			level = 2;
			if (position == 0) {
				// 直播 
				if (tvs == null)
					tvs = XmlReaderHelper.getAllCategory(getActivity());
				mAdapter.replace(tvs);
			} else {
				// 视频 
				mAdapter.replace(videos);
			}
			mListView.setAdapter(mAdapter);
			break;
		case 2://
			level = 3;
			if (item.id != null) {
				// 直播
				mAdapter.replace(XmlReaderHelper.getVideos(getActivity(),
						item.id));
				mListView.setAdapter(mAdapter);
			} else {
				clearAndLoad(item.url);
			}
			break;
		case 3:
			level = 4;
			// clearAndLoad(item.url);
			Intent intent = new Intent(getActivity(), VideoActivity.class);
			intent.setData(Uri.parse(item.url));
			// intent.putExtra("path", item.url);
			intent.putExtra(
					"displayName                                                                                                                                                                                            ",
					item.title);
			startActivity(intent);
			break;
		}
	}

	private void clearAndLoad(String url) {
		mLoading.setVisibility(View.VISIBLE);
		mWebView.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		mHistory.clear();
		mWebView.clearView();
		mWebView.loadUrl(url);
	}

	@Override
	public boolean onBackPressed() {
		switch (level) {
		case 1:
			return super.onBackPressed();
		case 2:
			level = 1;
			mAdapter.replace(root);
			break;
		case 3://
			level = 2;
			if (mListView == null || mListView.getVisibility() == View.VISIBLE) {
				mAdapter.replace(tvs);
			} else {
				switchWebViewToListView();
			}
			break;
		case 4:
			level = 3;
			switchWebViewToListView();
			break;
		}
		mListView.setAdapter(mAdapter);
		return true;
	}

	private void switchWebViewToListView() {
		mWebView.clearView();
		mUrl.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		mWebView.setVisibility(View.GONE);
		mLoading.setVisibility(View.GONE);
	}

	/** 初始化WebView */
	private void initWebView() {
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);

		mWebView.setWebViewClient(new WebViewClient() {

			/** 页面开始加载 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mUrl.setText(url);
				mUrl.setVisibility(View.VISIBLE);
			}

			/** 页面加载完成 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mLoading.setVisibility(View.GONE);
				mWebView.setVisibility(View.VISIBLE);
				if (!mHistory.contains(url))
					mHistory.add(0, url);
				mUrl.setVisibility(View.GONE);
				// 取得title
				mTitle = view.getTitle();
			};

			/** 页面跳转 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					final String url) {
				if (FileUtils.isVideoOrAudio(url)) {
					Dialog dialog = new AlertDialog.Builder(getActivity())
							.setIcon(android.R.drawable.btn_star)
							.setTitle("播放/下载").setMessage(url)
							.setPositiveButton("播放", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(getActivity(),
											VideoPlayerActivity.class);
									intent.putExtra("path", url);
									intent.putExtra("title", mTitle);
									startActivity(intent);
								}
							}).setNeutralButton("下载", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (Environment.MEDIA_MOUNTED
											.equals(Environment
													.getExternalStorageState())) {
										MainActivity activity = (MainActivity) getActivity();
										String savePath = Environment
												.getExternalStorageDirectory()
												+ "/";
										if (TextUtils.isEmpty(mTitle))
											savePath += FileUtils
													.getUrlFileName(url);
										else {
											savePath += mTitle
													+ "."
													+ FileUtils
															.getUrlExtension(url);
										}
										activity.mFileDownload.newDownloadFile(
												url, savePath);
										Toast.makeText(
												getActivity(),
												"正在下载 .."
														+ FileUtils
																.getUrlFileName(savePath)
														+ " ，可从本地视频查看进度！",
												Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(getActivity(),
												"请检测SD卡!", Toast.LENGTH_LONG)
												.show();
									}
								}
							}).setNegativeButton("取消", null).create();
					dialog.show();
					return true;
				}
				return false;
			};
		});

		/** 处理后退键 */
		mWebView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView != null
						&& mWebView.canGoBack()) {
					if (mHistory.size() > 1) {
						mHistory.remove(0);
						mWebView.loadUrl(mHistory.get(0));
						return true;
					}
				}
				return false;
			}
		});
	}

	/** 数据适配 */
	private class DataAdapter extends ArrayAdapter<OnlineVideo> {

		public DataAdapter(Context ctx) {
			super(ctx, root);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final OnlineVideo item = getItem(position);
			if (convertView == null) {
				final LayoutInflater mInflater = getActivity()
						.getLayoutInflater();
				convertView = mInflater.inflate(R.layout.fragment_online_item,
						null);
			}
			ImageView thumbnail = (ImageView) convertView
					.findViewById(R.id.thumbnail);
			if (item.iconId > 0)
				thumbnail.setImageResource(item.iconId);
			else
				thumbnail.setImageDrawable(null);
			((TextView) convertView.findViewById(R.id.title))
					.setText(item.title);

			return convertView;
		}

	}

	// ~~~~~~~~~~~~~处理FLASH退出的问题 ~~~~~~~~

	private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mWebView != null) {
			mWebView.pauseTimers();
			if (getActivity().isFinishing()) {
				mWebView.loadUrl("about:blank");
			}
			callHiddenWebViewMethod("onPause");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mWebView != null) {
			mWebView.resumeTimers();
			callHiddenWebViewMethod("onResume");
		}
	}

	static {

		// private final static String[] CATEGORY = { "电视直播", "视频网站" };
		root.add(new OnlineVideo("电视直播", R.drawable.logo_cntv, 1));
		root.add(new OnlineVideo("视频网站", R.drawable.logo_youku, 0));

		videos.add(new OnlineVideo("优酷视频", R.drawable.logo_youku, 0,
				"http://3g.youku.com"));
		videos.add(new OnlineVideo("搜狐视频", R.drawable.logo_sohu, 0,
				"http://m.tv.sohu.com"));
		videos.add(new OnlineVideo("乐视TV", R.drawable.logo_letv, 0,
				"http://m.letv.com"));
		videos.add(new OnlineVideo("爱奇异", R.drawable.logo_iqiyi, 0,
				"http://3g.iqiyi.com/"));
		videos.add(new OnlineVideo("PPTV", R.drawable.logo_pptv, 0,
				"http://m.pptv.com/"));
		videos.add(new OnlineVideo("腾讯视频", R.drawable.logo_qq, 0,
				"http://3g.v.qq.com/"));
		videos.add(new OnlineVideo("56.com", R.drawable.logo_56, 0,
				"http://m.56.com/"));
		videos.add(new OnlineVideo("新浪视频", R.drawable.logo_sina, 0,
				"http://video.sina.cn/"));
		videos.add(new OnlineVideo("土豆视频", R.drawable.logo_tudou, 0,
				"http://m.tudou.com"));
	}
}

/*
 * private boolean loadVideo(final String url) { if (StringUtils.isEmpty(url))
 * return false;
 * 
 * mCurrentUrl = url;
 * 
 * new AsyncTask<Void, Void, OnlineVideo>() {
 * 
 * @Override protected OnlineVideo doInBackground(Void... params) {
 * Log.d("Youku", url); if (url.startsWith("http://m.youku.com")) { return
 * VideoHelper.getYoukuVideo(url); } return null; }
 * 
 * @Override protected void onPostExecute(OnlineVideo result) {
 * super.onPostExecute(result); if (result != null) { Intent intent = new
 * Intent(getActivity(), VideoPlayerActivity.class); intent.putExtra("path",
 * result.url); intent.putExtra("title", result.title); startActivity(intent); }
 * else { mWebView.loadUrl(url); } } }.execute(); return true; }
 */
