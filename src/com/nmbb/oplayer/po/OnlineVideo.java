package com.nmbb.oplayer.po;

import java.util.ArrayList;

public class OnlineVideo {
	public String id;
	/** 标题 */
	public String title;
	public String desc;
	/** LOGO */
	public int iconId = 0;
	public String icon_url;
	/** 播放地址 */
	public String url;
	/** 备用链接 */
	public ArrayList<String> backup_url;
	/** 是否目录 */
	public boolean is_category = false;
	/** 0视频 1电视 */
	public int category;
	/** */
	public int level = 1;

	public OnlineVideo() {

	}

	public OnlineVideo(String title, int iconId, int category) {
		this.title = title;
		this.iconId = iconId;
		this.category = category;
	}

	public OnlineVideo(String title, int iconId, int category, String url) {
		this.title = title;
		this.iconId = iconId;
		this.category = category;
		this.url = url;
	}
}
