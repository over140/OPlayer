package com.nmbb.oplayer.exception;

import android.util.Log;

public class Logger {

	private static boolean isLog = true;
	private static final String TAG = "OPlayer";

	public static void setLog(boolean isLog) {
		Logger.isLog = isLog;
	}

	public static boolean getIsLog() {
		return isLog;
	}

	public static void d(String tag, String msg) {
		if (isLog) {
			Log.d(tag, msg);
		}
	}

	public static void d(String msg) {
		Log.d(TAG, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually
	 * identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static void d(String tag, String msg, Throwable tr) {
		if (isLog) {
			Log.d(tag, msg, tr);
		}
	}

	public static void e(Throwable tr) {
		if (isLog) {
			Log.e(TAG, "", tr);
		}
	}

	public static void i(String msg) {
		if (isLog) {
			Log.i(TAG, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if (isLog) {
			Log.i(tag, msg);
		}
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually
	 * identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static void i(String tag, String msg, Throwable tr) {
		if (isLog) {
			Log.i(tag, msg, tr);
		}

	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually
	 * identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static void e(String tag, String msg) {
		if (isLog) {
			Log.e(tag, msg);
		}
	}

	public static void e(String msg) {
		if (isLog) {
			Log.e(TAG, msg);
		}
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param tag Used to identify the source of a log message. It usually
	 * identifies the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (isLog) {
			Log.e(tag, msg, tr);
		}
	}

	public static void systemErr(String msg) {
		// if (true) {
		if (isLog) {
			if (msg != null) {
				Log.e(TAG, msg);
			}

		}
	}

}
