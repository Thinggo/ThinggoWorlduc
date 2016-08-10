package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;

public class FriendGroup implements Serializable {
	private String id;
	private String name;
	private boolean isChecked;
	
	public FriendGroup(String id, String name){
		this.id = id;
		this.name = name;
		this.isChecked = false;
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
	
	public String toString(){
		return name;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
