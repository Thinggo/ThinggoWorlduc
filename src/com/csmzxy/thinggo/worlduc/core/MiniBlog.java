package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;

public class MiniBlog implements Serializable {
	private String id;
	private String category;
	private String mood;
	private String content;
	private String time;
	
	public MiniBlog(String id, String content, String time){
		this.id = id;
		this.content = content;
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
