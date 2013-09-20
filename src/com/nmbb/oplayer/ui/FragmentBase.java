package com.nmbb.oplayer.ui;

import com.nmbb.oplayer.R;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentBase extends Fragment {

	protected ListView mListView;
	protected View mLoadingLayout;
	protected MainActivity mParent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParent = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_file, container, false);
		mListView = (ListView) v.findViewById(android.R.id.list);
		mLoadingLayout = v.findViewById(R.id.loading);
		return v;
	}

	public boolean onBackPressed() {
		return false;
	}

	public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		if (mParent != null)
			mParent.registerReceiver(receiver, filter);
	}

	public void unregisterReceiver(BroadcastReceiver receiver) {
		if (mParent != null)
			mParent.unregisterReceiver(receiver);
	}

	public void registerContentObserver(Uri uri, boolean notifyForDescendents, ContentObserver observer) {
		if (mParent != null)
			mParent.getContentResolver().registerContentObserver(uri, notifyForDescendents, observer);
	}

	public void unregisterContentObserver(ContentObserver observer) {
		if (mParent != null)
			mParent.getContentResolver().unregisterContentObserver(observer);
	}
}
