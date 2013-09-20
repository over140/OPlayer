package com.nmbb.oplayer.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.nmbb.oplayer.database.SQLiteHelperOrm;
import com.nmbb.oplayer.exception.Logger;
import com.nmbb.oplayer.po.POMedia;

public final class FileBusiness {

	private static final String TABLE_NAME = "files";
	private static final String TAG = "FileBusiness";

	public static List<POMedia> getAllSortFiles() {
		SQLiteHelperOrm db = new SQLiteHelperOrm();
		try {
			Dao<POMedia, Long> dao = db.getDao(POMedia.class);
			QueryBuilder<POMedia, Long> query = dao.queryBuilder();
			query.orderBy("title_key", true);
			return dao.query(query.prepare());
		} catch (SQLException e) {
			Logger.e(e);
		} finally {
			if (db != null)
				db.close();
		}
		return new ArrayList<POMedia>();
		//		new DbHelper<POMedia>().queryForAll(POMedia.class);
	}

	//	/** 获取所有已经排好序的列表 */
	//	public static ArrayList<PFile> getAllSortFiles(final Context ctx) {
	//		ArrayList<PFile> result = new ArrayList<PFile>();
	//		SQLiteHelper sqlite = new SQLiteHelper(ctx);
	//		SQLiteDatabase db = sqlite.getReadableDatabase();
	//		Cursor c = null;
	//		try {
	//			c = db.rawQuery("SELECT " + FilesColumns.COL_ID + "," + FilesColumns.COL_TITLE + "," + FilesColumns.COL_TITLE_PINYIN + "," + FilesColumns.COL_PATH + "," + FilesColumns.COL_DURATION + "," + FilesColumns.COL_POSITION + "," + FilesColumns.COL_LAST_ACCESS_TIME + "," + FilesColumns.COL_THUMB + "," + FilesColumns.COL_FILE_SIZE + " FROM files", null);
	//			while (c.moveToNext()) {
	//				PFile po = new PFile();
	//				int index = 0;
	//				po._id = c.getLong(index++);
	//				po.title = c.getString(index++);
	//				po.title_pinyin = c.getString(index++);
	//				po.path = c.getString(index++);
	//				po.duration = c.getInt(index++);
	//				po.position = c.getInt(index++);
	//				po.last_access_time = c.getLong(index++);
	//				po.thumb = c.getString(index++);
	//				po.file_size = c.getLong(index++);
	//				result.add(po);
	//			}
	//		} finally {
	//			if (c != null)
	//				c.close();
	//		}
	//		db.close();
	//
	//		Collections.sort(result, new Comparator<PFile>() {
	//
	//			@Override
	//			public int compare(PFile f1, PFile f2) {
	//				char c1 = f1.title_pinyin.length() == 0 ? ' ' : f1.title_pinyin.charAt(0);
	//				char c2 = f2.title_pinyin.length() == 0 ? ' ' : f2.title_pinyin.charAt(0);
	//				return c1 == c2 ? 0 : (c1 > c2 ? 1 : -1);
	//			}//相等返回0，-1 f2 > f2，-1
	//
	//		});
	//		return result;
	//	}
	//
	//	/** 重命名文件 */
	//	public static void renameFile(final Context ctx, final PFile p) {
	//		SQLiteHelper sqlite = new SQLiteHelper(ctx);
	//		SQLiteDatabase db = sqlite.getWritableDatabase();
	//		try {
	//			ContentValues values = new ContentValues();
	//			values.put(FilesColumns.COL_TITLE, p.title);
	//			values.put(FilesColumns.COL_TITLE_PINYIN, PinyinUtils.chineneToSpell(p.title.charAt(0) + ""));
	//			values.put(FilesColumns.COL_PATH, p.path);
	//			db.update(TABLE_NAME, values, FilesColumns.COL_ID + " = ?", new String[] { p._id + "" });
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//			try {
	//				db.close();
	//			} catch (Exception e) {
	//			}
	//		}
	//	}
	//
	//	/** 删除文件 */
	//	public static int deleteFile(final Context ctx, final PFile p) {
	//		SQLiteHelper sqlite = new SQLiteHelper(ctx);
	//		SQLiteDatabase db = sqlite.getWritableDatabase();
	//		int result = -1;
	//		try {
	//			result = db.delete(TABLE_NAME, FilesColumns.COL_ID + " = ?", new String[] { p._id + "" });
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//			try {
	//				db.close();
	//			} catch (Exception e) {
	//			}
	//		}
	//		return result;
	//	}
	//
	//	public static void insertFile(final Context ctx, final PFile p) {
	//		SQLiteHelper sqlite = new SQLiteHelper(ctx);
	//		SQLiteDatabase db = sqlite.getWritableDatabase();
	//		try {
	//			ContentValues values = new ContentValues();
	//			values.put(FilesColumns.COL_TITLE, p.title);
	//			values.put(FilesColumns.COL_TITLE_PINYIN, PinyinUtils.chineneToSpell(p.title.charAt(0) + ""));
	//			values.put(FilesColumns.COL_PATH, p.path);
	//			values.put(FilesColumns.COL_LAST_ACCESS_TIME, System.currentTimeMillis());
	//			values.put(FilesColumns.COL_FILE_SIZE, p.file_size);
	//			db.insert(TABLE_NAME, "", values);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//			try {
	//				db.close();
	//			} catch (Exception e) {
	//			}
	//		}
	//	}
	//
	//	/** 批量提取视频的缩略图已经视频的宽高 */
	//	public static ArrayList<PFile> batchBuildThumbnail(final Context ctx, final ArrayList<File> files) {
	//		ArrayList<PFile> result = new ArrayList<PFile>();
	//
	//		for (File f : files) {
	//			PFile pf = new PFile();
	//			try {
	//				if (f.exists() && f.canRead()) {
	//					//取出视频的一帧图像
	////					Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(ctx, f.getAbsolutePath(), Video.Thumbnails.MINI_KIND);
	////					if (bitmap == null) {
	////						//缩略图创建失败
	////						bitmap = Bitmap.createBitmap(ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL_WIDTH, ThumbnailUtils.TARGET_SIZE_MINI_THUMBNAIL_HEIGHT, Bitmap.Config.RGB_565);
	////						Log.e(TAG, "batchBuildThumbnail createBitmap faild : " + f.getAbsolutePath());
	////					}
	////
	////					pf.width = bitmap.getWidth();
	////					pf.height = bitmap.getHeight();
	////
	////					//缩略图
	////					bitmap = ThumbnailUtils.extractThumbnail(bitmap, ThumbnailUtils.dipToPX(ctx, ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL_WIDTH), ThumbnailUtils.dipToPX(ctx, ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL_HEIGHT), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	////					if (bitmap != null) {
	////						File thum = new File(f.getParent(), f.getName() + ".jpg");
	////						pf.thumb = thum.getAbsolutePath();
	////						//thum.createNewFile();
	////						FileOutputStream iStream = new FileOutputStream(thum);
	////						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, iStream);
	////						iStream.close();
	////					}
	////
	////					if (bitmap != null)
	////						bitmap.recycle();
	//					
	//				}
	//			} catch (Exception e) {
	//				Log.e(TAG, e);
	//				continue;
	//			} finally {
	//				result.add(pf);
	//			}
	//		}
	//
	//		return result;
	//	}
	//
	//	/** 批量插入数据 */
	//	public static void batchInsertFiles(final Context ctx, final ArrayList<File> files) {
	//		SQLiteHelper sqlite = new SQLiteHelper(ctx);
	//		SQLiteDatabase db = sqlite.getWritableDatabase();
	//		try {
	//			db.beginTransaction();
	//
	//			SQLiteStatement stat = db.compileStatement("INSERT INTO files(" + FilesColumns.COL_TITLE + "," + FilesColumns.COL_TITLE_PINYIN + "," + FilesColumns.COL_PATH + "," + FilesColumns.COL_LAST_ACCESS_TIME + "," + FilesColumns.COL_FILE_SIZE + ") VALUES(?,?,?,?,?)");
	//			for (File f : files) {
	//				String name = f.getName();
	//				int index = 1;
	//				stat.bindString(index++, name);// title
	//				stat.bindString(index++, PinyinUtils.chineneToSpell(name.charAt(0) + ""));// title_pinyin
	//				stat.bindString(index++, f.getPath());// path
	//				stat.bindLong(index++, System.currentTimeMillis());// last_access_time
	//				stat.bindLong(index++, f.length());
	//				stat.execute();
	//			}
	//			db.setTransactionSuccessful();
	//		} catch (BadHanyuPinyinOutputFormatCombination e) {
	//			e.printStackTrace();
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//			db.endTransaction();
	//			try {
	//				db.close();
	//			} catch (Exception e) {
	//			}
	//		}
	//	}
}
