package com.nmbb.oplayer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nmbb.oplayer.R;
import com.nmbb.oplayer.business.FileBusiness;
import com.nmbb.oplayer.database.DbHelper;
import com.nmbb.oplayer.po.POMedia;
import com.nmbb.oplayer.service.MediaScannerService;
import com.nmbb.oplayer.service.MediaScannerService.IMediaScannerObserver;
import com.nmbb.oplayer.service.MediaScannerService.MediaScannerServiceBinder;
import com.nmbb.oplayer.ui.base.ArrayAdapter;
import com.nmbb.oplayer.ui.helper.FileDownloadHelper;
import com.nmbb.oplayer.ui.player.VideoActivity;
import com.nmbb.oplayer.util.FileUtils;

public class FragmentFileOld extends FragmentBase implements
		OnItemClickListener, IMediaScannerObserver {

	private FileAdapter mAdapter;
	private FileAdapter mDownloadAdapter;
	private TextView first_letter_overlay;
	private ImageView alphabet_scroller;
	/** 临时列表 */
	private ListView mTempListView;
	private MainActivity mParent;
	private TextView mSDAvailable;
	/** 左下角进度显示 */
	private View mProgress;

	private MediaScannerService mMediaScannerService;

	private ServiceConnection mMediaScannerServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mMediaScannerService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMediaScannerService = ((MediaScannerServiceBinder) service)
					.getService();
			mMediaScannerService.addObserver(FragmentFileOld.this);
			// Toast.makeText(ComponentServiceActivity.this, "Service绑定成功!",
			// Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		// ~~~~~~~~~ 绑定控件
		first_letter_overlay = (TextView) v
				.findViewById(R.id.first_letter_overlay);
		alphabet_scroller = (ImageView) v.findViewById(R.id.alphabet_scroller);
		mTempListView = (ListView) v.findViewById(R.id.templist);
		mSDAvailable = (TextView) v.findViewById(R.id.sd_block);
		mProgress = v.findViewById(android.R.id.progress);

		// ~~~~~~~~~ 绑定事件
		alphabet_scroller.setClickable(true);
		alphabet_scroller.setOnTouchListener(asOnTouch);
		mListView.setOnItemClickListener(this);
		mTempListView.setOnItemClickListener(this);
		mListView.setOnCreateContextMenuListener(OnListViewMenu);
		mTempListView.setOnCreateContextMenuListener(OnTempListViewMenu);

		// ~~~~~~~~~ 加载数据
		mParent = (MainActivity) getActivity();
		// if (new SQLiteHelper(getActivity()).isEmpty())
		// new ScanVideoTask().execute();
		// else
		new DataTask().execute();

		getActivity().bindService(
				new Intent(getActivity().getApplicationContext(),
						MediaScannerService.class),
				mMediaScannerServiceConnection, Context.BIND_AUTO_CREATE);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		getActivity().unbindService(mMediaScannerServiceConnection);
		super.onDestroy();
	}

	/**
	 * 
	 * @param flag
	 *            0 开始扫描 1 正在扫描 2 扫描完成
	 * @param file
	 *            扫描到的视频文件
	 */
	@Override
	public void update(int flag, POMedia media) {
		// Logger.i(flag + " " + media.path);
		switch (flag) {
		case MediaScannerService.SCAN_STATUS_START:

			break;
		case MediaScannerService.SCAN_STATUS_END:// 扫描完成
			if (mProgress != null)
				mProgress.setVisibility(View.GONE);
			new DataTask().execute();
			break;
		case MediaScannerService.SCAN_STATUS_RUNNING:// 扫到一个文件
			if (mAdapter != null && media != null) {
				mAdapter.add(media);
				mAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// SD卡剩余数量
		mSDAvailable.setText(FileUtils.showFileAvailable());

		if (MediaScannerService.isRunning())
			mProgress.setVisibility(View.VISIBLE);
		else
			mProgress.setVisibility(View.GONE);
	}

	ListView.OnCreateContextMenuListener OnListViewMenu = new ListView.OnCreateContextMenuListener() {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			menu.setHeaderTitle(R.string.file_oper);
			menu.add(0, 0, 0, R.string.file_rename);
			menu.add(0, 1, 0, R.string.file_delete);
		}
	};

	ListView.OnCreateContextMenuListener OnTempListViewMenu = new ListView.OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			menu.setHeaderTitle(R.string.file_oper);
			menu.add(0, 2, 0, R.string.file_rename);
			menu.add(0, 3, 0, R.string.file_delete);
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ContextMenuInfo info = item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) info;
		int position = contextMenuInfo.position;
		switch (item.getItemId()) {
		case 0:
			renameFile(mAdapter, mAdapter.getItem(position), position);
			break;
		case 1:
			deleteFile(mAdapter, mAdapter.getItem(position), position);
			break;
		case 2:
			renameFile(mDownloadAdapter, mDownloadAdapter.getItem(position),
					position);
			break;
		case 3:
			deleteFile(mDownloadAdapter, mDownloadAdapter.getItem(position),
					position);
			break;
		}
		return super.onContextItemSelected(item);
	};

	/** 删除文件 */
	private void deleteFile(final FileAdapter adapter, final POMedia f,
			final int position) {
		new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.file_delete)
				.setMessage(getString(R.string.file_delete_confirm, f.title))
				.setNegativeButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									File file = new File(f.path);
									if (file.canRead() && file.exists())
										file.delete();

									// FileBusiness.deleteFile(getActivity(),
									// f);
									new DbHelper<POMedia>().remove(f);
									adapter.delete(position);
								} catch (Exception e) {

								}
							}

						}).setPositiveButton(android.R.string.no, null).show();
	}

	/** 重命名文件 */
	private void renameFile(final FileAdapter adapter, final POMedia f,
			final int position) {
		final EditText et = new EditText(getActivity());
		et.setText(f.title);
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.file_rename)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(et)
				.setNegativeButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String name = et.getText().toString().trim();
								if (name == null || name.trim().equals("")
										|| name.trim().equals(f.title))
									return;

								try {
									File fromFile = new File(f.path);
									File nf = new File(fromFile.getParent(),
											name.trim());
									if (nf.exists()) {
										Toast.makeText(getActivity(),
												R.string.file_rename_exists,
												Toast.LENGTH_LONG).show();
									} else if (fromFile.renameTo(nf)) {
										f.title = name;
										f.path = nf.getPath();
										// FileBusiness.renameFile(getActivity(),
										// f);

										new DbHelper<POMedia>().update(f);
										adapter.notifyDataSetChanged();
									}
								} catch (SecurityException se) {
									Toast.makeText(getActivity(),
											R.string.file_rename_failed,
											Toast.LENGTH_LONG).show();
								}
							}

						}).setPositiveButton(android.R.string.no, null).show();
	}

	public Handler mDownloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			POMedia p;
			String url = msg.obj.toString();
			switch (msg.what) {
			case FileDownloadHelper.MESSAGE_START:// 开始下载
				p = new POMedia();
				p.path = mParent.mFileDownload.mDownloadUrls.get(url);
				p.title = new File(p.path).getName();
				p.status = 0;
				p.file_size = 0;
				if (mDownloadAdapter == null) {
					mDownloadAdapter = new FileAdapter(getActivity(),
							new ArrayList<POMedia>());
					mDownloadAdapter.add(p, url);
					mTempListView.setAdapter(mDownloadAdapter);
					mTempListView.setVisibility(View.VISIBLE);
				} else {
					mDownloadAdapter.add(p, url);
					mDownloadAdapter.notifyDataSetChanged();
				}
				break;
			case FileDownloadHelper.MESSAGE_PROGRESS:// 正在下载
				p = mDownloadAdapter.getItem(url);
				p.temp_file_size = msg.arg1;
				p.file_size = msg.arg2;
				int status = (int) ((msg.arg1 * 1.0 / msg.arg2) * 10);
				if (status > 10)
					status = 10;
				p.status = status;
				mDownloadAdapter.notifyDataSetChanged();
				break;
			case FileDownloadHelper.MESSAGE_STOP:// 下载结束
				p = mDownloadAdapter.getItem(url);
				new DbHelper<POMedia>().create(p);
				// FileBusiness.insertFile(getActivity(), p);
				break;
			case FileDownloadHelper.MESSAGE_ERROR:
				Toast.makeText(getActivity(), url, Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** 单击启动播放 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final POMedia f = parent == mListView ? mAdapter.getItem(position)
				: mDownloadAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), VideoActivity.class);
		intent.setData(Uri.parse(f.path));
		// intent.putExtra("path", f.path);
		intent.putExtra("displayName", f.title);
		startActivity(intent);
	}

	private class DataTask extends AsyncTask<Void, Void, List<POMedia>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingLayout.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}

		@Override
		protected List<POMedia> doInBackground(Void... params) {
			return FileBusiness.getAllSortFiles();
		}

		@Override
		protected void onPostExecute(List<POMedia> result) {
			super.onPostExecute(result);

			mAdapter = new FileAdapter(getActivity(), result);
			mListView.setAdapter(mAdapter);

			mLoadingLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private class FileAdapter extends ArrayAdapter<POMedia> {

		private HashMap<String, POMedia> maps = new HashMap<String, POMedia>();

		public FileAdapter(Context ctx, List<POMedia> l) {
			super(ctx, l);
			maps.clear();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final POMedia f = getItem(position);
			if (convertView == null) {
				final LayoutInflater mInflater = getActivity()
						.getLayoutInflater();
				convertView = mInflater.inflate(R.layout.fragment_file_item,
						null);
			}
			((TextView) convertView.findViewById(R.id.title)).setText(f.title);

			// 显示文件大小
			String file_size;
			if (f.temp_file_size > 0) {
				file_size = FileUtils.showFileSize(f.temp_file_size) + " / "
						+ FileUtils.showFileSize(f.file_size);
			} else {
				file_size = FileUtils.showFileSize(f.file_size);
			}
			((TextView) convertView.findViewById(R.id.file_size))
					.setText(file_size);

			// 显示进度表
			final ImageView status = (ImageView) convertView
					.findViewById(R.id.status);
			if (f.status > -1) {
				int resStauts = getStatusImage(f.status);
				if (resStauts > 0) {
					if (status.getVisibility() != View.VISIBLE)
						status.setVisibility(View.VISIBLE);
					status.setImageResource(resStauts);
				}
			} else {
				if (status.getVisibility() != View.GONE)
					status.setVisibility(View.GONE);
			}
			return convertView;
		}

		public void add(POMedia item, String url) {
			super.add(item);
			if (!maps.containsKey(url))
				maps.put(url, item);
		}

		public void delete(int position) {
			synchronized (mLock) {
				mObjects.remove(position);
			}
			notifyDataSetChanged();
		}

		public POMedia getItem(String url) {
			return maps.get(url);
		}
	}

	private int getStatusImage(int status) {
		int resStauts = -1;
		switch (status) {
		case 0:
			resStauts = R.drawable.down_btn_0;
			break;
		case 1:
			resStauts = R.drawable.down_btn_1;
			break;
		case 2:
			resStauts = R.drawable.down_btn_2;
			break;
		case 3:
			resStauts = R.drawable.down_btn_3;
			break;
		case 4:
			resStauts = R.drawable.down_btn_4;
			break;
		case 5:
			resStauts = R.drawable.down_btn_5;
			break;
		case 6:
			resStauts = R.drawable.down_btn_6;
			break;
		case 7:
			resStauts = R.drawable.down_btn_7;
			break;
		case 8:
			resStauts = R.drawable.down_btn_8;
			break;
		case 9:
			resStauts = R.drawable.down_btn_9;
			break;
		case 10:
			resStauts = R.drawable.down_btn_10;
			break;
		}
		return resStauts;
	}

	/**
	 * A-Z
	 */
	private OnTouchListener asOnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 0
				alphabet_scroller.setPressed(true);
				first_letter_overlay.setVisibility(View.VISIBLE);
				mathScrollerPosition(event.getY());
				break;
			case MotionEvent.ACTION_UP:// 1
				alphabet_scroller.setPressed(false);
				first_letter_overlay.setVisibility(View.GONE);
				break;
			case MotionEvent.ACTION_MOVE:
				mathScrollerPosition(event.getY());
				break;
			}
			return false;
		}
	};

	/**
	 * 显示字符
	 * 
	 * @param y
	 */
	private void mathScrollerPosition(float y) {
		int height = alphabet_scroller.getHeight();
		float charHeight = height / 28.0f;
		char c = 'A';
		if (y < 0)
			y = 0;
		else if (y > height)
			y = height;

		int index = (int) (y / charHeight) - 1;
		if (index < 0)
			index = 0;
		else if (index > 25)
			index = 25;

		String key = String.valueOf((char) (c + index));
		first_letter_overlay.setText(key);

		int position = 0;
		if (index == 0)
			mListView.setSelection(0);
		else if (index == 25)
			mListView.setSelection(mAdapter.getCount() - 1);
		else {
			if (mAdapter != null && mAdapter.getAll() != null) {
				for (POMedia item : mAdapter.getAll()) {
					if (item.title_key.startsWith(key)) {
						mListView.setSelection(position);
						break;
					}
					position++;
				}
			}
		}
	}

	// ~~~~~~~~~~~~~~ 后续弃用，直接使用Vitamio提供的

	// /** 扫描SD卡 */
	// private class ScanVideoTask extends AsyncTask<Void, File,
	// ArrayList<PFile>> {
	// private ProgressDialog pd;
	// private ArrayList<File> files = new ArrayList<File>();
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// pd = new ProgressDialog(getActivity());
	// pd.setMessage("正在扫描视频文件...");
	// pd.setCanceledOnTouchOutside(false);
	// pd.setCancelable(false);
	// pd.show();
	// }
	//
	// @Override
	// protected ArrayList<PFile> doInBackground(Void... params) {
	// // ~~~ 遍历文件夹
	// eachAllMedias(Environment.getExternalStorageDirectory());
	//
	// // ~~~ 提取缩略图、视频尺寸等。
	// FileBusiness.batchBuildThumbnail(getActivity(), files);
	//
	// // ~~~ 入库
	// FileBusiness.batchInsertFiles(getActivity(), files);
	//
	// // ~~~ 查询数据
	// return FileBusiness.getAllSortFiles(getActivity());
	// }
	//
	// @Override
	// protected void onProgressUpdate(final File... values) {
	// pd.setMessage(values[0].getName());
	// }
	//
	// /** 遍历所有文件夹，查找出视频文件 */
	// public void eachAllMedias(File f) {
	// if (f != null && f.exists() && f.isDirectory()) {
	// File[] files = f.listFiles();
	// if (files != null) {
	// for (File file : f.listFiles()) {
	// if (file.isDirectory()) {
	// eachAllMedias(file);
	// } else if (file.exists() && file.canRead() && FileUtils.isVideo(file)) {
	// this.files.add(file);
	// }
	// publishProgress(file);
	// }
	// }
	// }
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<PFile> result) {
	// super.onPostExecute(result);
	// mAdapter = new FileAdapter(getActivity(), result);
	// mListView.setAdapter(mAdapter);
	// pd.dismiss();
	// }
	// }
}
