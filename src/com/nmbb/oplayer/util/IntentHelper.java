/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nmbb.oplayer.util;

import io.vov.vitamio.utils.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Html;

public final class IntentHelper {

	public static final String MEDIA_PATTERN = "(http[s]?://)+([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?";
	private static final Pattern mMediaPattern;

	static {
		mMediaPattern = Pattern.compile(MEDIA_PATTERN);
	}

	public static Uri getIntentUri(Intent intent) {
		Uri result = null;
		if (intent != null) {
			result = intent.getData();
			if (result == null) {
				final String type = intent.getType();
				String sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (!StringUtils.isEmpty(sharedUrl)) {
					if ("text/plain".equals(type) && sharedUrl != null) {
						result = getTextUri(sharedUrl);
					} else if ("text/html".equals(type) && sharedUrl != null) {
						result = getTextUri(Html.fromHtml(sharedUrl).toString());
					}
				} else {
					Parcelable parce = intent.getParcelableExtra(Intent.EXTRA_STREAM);
					if (parce != null)
						result = (Uri) parce;
				}
			}
		}
		return result;
	}

	private static Uri getTextUri(String sharedUrl) {
		Matcher matcher = mMediaPattern.matcher(sharedUrl);
		if (matcher.find()) {
			sharedUrl = matcher.group();
			if (!StringUtils.isEmpty(sharedUrl)) {
				return Uri.parse(sharedUrl);
			}
		}
		return null;
	}

	public static boolean existPackage(final Context ctx, String packageName) {
		if (!StringUtils.isEmpty(packageName)) {
			for (PackageInfo p : ctx.getPackageManager().getInstalledPackages(0)) {
				if (packageName.equals(p.packageName))
					return true;
			}
		}
		return false;
	}

	public static void startApkActivity(final Context ctx, String packageName) {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(packageName, 0);
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.setPackage(pi.packageName);

			List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				intent.setComponent(new ComponentName(packageName, className));
				ctx.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			Log.e("startActivity", e);
		}
	}
}
