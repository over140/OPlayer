package com.nmbb.oplayer.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nmbb.oplayer.R;
import com.nmbb.oplayer.po.PFile;
import com.nmbb.oplayer.ui.base.ArrayAdapter;
import com.nmbb.oplayer.util.FileUtils;

public class FileDownloadAdapter extends ArrayAdapter<PFile> {
	private HashMap<String, PFile> maps = new HashMap<String, PFile>();

	private LayoutInflater mInflater;

	public FileDownloadAdapter(Context ctx, ArrayList<PFile> l) {
		super(ctx, l);
		mInflater = LayoutInflater.from(ctx);
		maps.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final PFile f = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_file_item, null);
		}
		((TextView) convertView.findViewById(R.id.title)).setText(f.title);

		//显示文件大小
		String file_size;
		if (f.temp_file_size > 0) {
			file_size = FileUtils.showFileSize(f.temp_file_size) + " / " + FileUtils.showFileSize(f.file_size);
		} else {
			file_size = FileUtils.showFileSize(f.file_size);
		}
		((TextView) convertView.findViewById(R.id.file_size)).setText(file_size);

		//显示进度表
		final ImageView status = (ImageView) convertView.findViewById(R.id.status);
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

	public void add(PFile item, String url) {
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

	public PFile getItem(String url) {
		return maps.get(url);
	}
}
