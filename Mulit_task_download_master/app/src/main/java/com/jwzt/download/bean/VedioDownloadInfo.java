package com.jwzt.download.bean;

import java.io.Serializable;


/**
 * 视频下载信息
 *
 */
public class VedioDownloadInfo implements Serializable{
	private String id;//视频id

	private String name;//视频名称

	private String newsAbstract;//视频描述

	private String newsPic;//视频贴图地址

	private String subTitle;//子标题

	private String playpath;//视频播放的路
	
	private String savepath;//视频的下载路
	
	private long currentposition;//当前下载到的位置
	
	private long vediolength;//下载文件的
	
	private int downState;//文件当前的下载状
	
	

	
	
	public VedioDownloadInfo(String id, 
			String name,
			String newsAbstract,
			String newsPic,
			String subTitle, 
			String playpath,
			String savepath,
			long currentposition,
			long vediolength,
			int downState) {
		super();
		this.id = id;
		this.name = name;
		this.newsAbstract = newsAbstract;
		this.newsPic = newsPic;
		this.subTitle = subTitle;
		this.playpath = playpath;
		this.savepath = savepath;
		this.currentposition = currentposition;
		this.vediolength = vediolength;
		this.downState=downState;
		
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNewsAbstract() {
		return newsAbstract;
	}

	public void setNewsAbstract(String newsAbstract) {
		this.newsAbstract = newsAbstract;
	}

	public String getNewsPic() {
		return newsPic;
	}

	public void setNewsPic(String newsPic) {
		this.newsPic = newsPic;
	}



	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getPlaypath() {
		return playpath;
	}

	public void setPlaypath(String playpath) {
		this.playpath = playpath;
	}

	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public long getCurrentposition() {
		return currentposition;
	}

	public void setCurrentposition(long currentposition) {
		this.currentposition = currentposition;
	}

	public long getVediolength() {
		return vediolength;
	}

	public void setVediolength(long vediolength) {
		this.vediolength = vediolength;
	}

	public int getDownState() {
		return downState;
	}

	public void setDownState(int downState) {
		this.downState = downState;
	}

	@Override
	public String toString() {
		return "VedioDownloadInfo [id=" + id + ", name=" + name
				+ ", newsAbstract=" + newsAbstract + ", newsPic=" + newsPic
				+ ", subTitle=" + subTitle + ", playpath=" + playpath
				+ ", savepath=" + savepath + ", currentposition="
				+ currentposition + ", vediolength=" + vediolength
				+ ", downState=" + downState + "]";
	}

	
}
