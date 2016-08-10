package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;

public class NoticeMessage implements Serializable {
	private String html;
	private String text;
	private String time;

	public NoticeMessage(String html, String text, String time){
		this.html = html;
		this.text = text;
		this.time = time;
	}
	
	public String getHtml() {
		return html;
	}


	public void setHtml(String html) {
		this.html = html;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
