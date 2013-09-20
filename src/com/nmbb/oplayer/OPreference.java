package com.nmbb.oplayer;

import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

/** 存储系统设置 */
public class OPreference {

	private static final String PREFERENCE_NAME = "preference.db";

	private SharedPreferences mPreference;

	public OPreference(Context ctx) {
		mPreference = ctx.getApplicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public boolean putStringAndCommit(String key, String value) {
		return mPreference.edit().putString(key, value).commit();
	}

	public boolean putIntAndCommit(String key, int value) {
		return mPreference.edit().putInt(key, value).commit();
	}

	public boolean putBooleanAndCommit(String key, boolean value) {
		return mPreference.edit().putBoolean(key, value).commit();
	}

	public boolean putIntAndCommit(ContentValues values) {
		SharedPreferences.Editor editor = mPreference.edit();
		for (Entry<String, Object> value : values.valueSet()) {
			editor.putString(value.getKey(), value.getValue().toString());
		}
		return editor.commit();
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String defValue) {
		return mPreference.getString(key, defValue);
	}

	public int getInt(String key) {
		return getInt(key, -1);
	}

	public int getInt(String key, int defValue) {
		return mPreference.getInt(key, defValue);
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mPreference.getBoolean(key, defValue);
	}
}
