package com.hewaiming.MyRecords.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class eRecord extends BmobObject implements Serializable{

	private String recordDate;
	private String recordTime;
	private String name;
	private String address;
	private String content;
	private String image;
	public eRecord(String recordDate, String recordTime, String name, String address, String content, String image) {
		super();
		this.recordDate = recordDate;
		this.recordTime = recordTime;
		this.name = name;
		this.address = address;
		this.content = content;
		this.image = image;
	}
	
	public eRecord() {
		super();
	}
	public String getRecordDate() {
		return recordDate;
	}
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}	
}
