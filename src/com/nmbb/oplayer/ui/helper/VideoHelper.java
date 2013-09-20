package com.nmbb.oplayer.ui.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.nmbb.oplayer.util.StringUtils;

import android.os.Build;
import android.text.Html;

public class VideoHelper {

	/** 在线视频对象 */
	public static class OnlineVideo {
		public String url;
		public String title;

		public OnlineVideo() {
		}

		public OnlineVideo(String url) {
			this.url = url;
		}
	}

	/** 获取优酷视频m3u8地址 */
	public static OnlineVideo getYoukuVideo(String url) {
		OnlineVideo result = null;
		String vid = "";
		//提取vid
		vid = substring(url, "vid=", "&");

		if (!StringUtils.isEmpty(vid)) {
			String title = "";
			String html = connect(url);
			if (!StringUtils.isEmpty(html)) {
				//获取title
				title = substring(html, "<title>", "</title>");
				title = substring(title, "", "-优酷3G", title);
				title = Html.fromHtml(title).toString();
			}

			result = new OnlineVideo();
			result.url = "http://v.youku.com/player/getRealM3U8/vid/" + vid + "/type//video.m3u8";
			result.title = title;
		}
		return result;
	}

	public static String substring(String search, String start, String end, String defaultValue) {
		int s_len = start.length();
		int s_pos = StringUtils.isEmpty(start) ? 0 : search.indexOf(start);
		if (s_pos > -1) {
			int end_pos = StringUtils.isEmpty(end) ? -1 : search.indexOf(end, s_pos + s_len);
			if (end_pos > -1)
				return search.substring(s_pos + start.length(), end_pos);
			else
				return search.substring(s_pos + start.length());
		}
		return defaultValue;
	}

	public static String substring(String search, String start, String end) {
		return substring(search, start, end, "");
	}

	private static String connect(String uri) {
		return connect(uri, "UTF8");
	}

	private static String connect(String uri, String charsetName) {
		String result = "";
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			if (Integer.parseInt(Build.VERSION.SDK) < 8) {
				System.setProperty("http.keepAlive", "false");
			}
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				result = readData(is, charsetName);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
		return result;
	}

	public static String readData(InputStream inSream, String charsetName) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return new String(data, charsetName);
	}
}
