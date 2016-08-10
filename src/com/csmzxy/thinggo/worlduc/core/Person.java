package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;

public class Person implements Serializable {
	protected String name;        //姓名    
	protected String uid;			//用户Id
	protected String imgUrl;		//头像地址
	protected String location;	//所在省份
	protected String homeUrl;
	protected boolean checked;
	private static String HOME_URL_FMT = "http://www.worlduc.com/SpaceShow/Index.aspx?uid=%s";
    
    public Person(){}
    public Person(String id, String name, String imgUrl){
    	this(name,imgUrl,"","");
    	this.uid = id;
    	this.homeUrl = String.format(HOME_URL_FMT, id);
    	
    }
    public Person(String name, String homeUrl){
    	this(name,"",homeUrl,"");
    }
    public Person(String name, String imgUrl, String homeUrl, String loc){
    	this.name = name;
    	this.imgUrl = imgUrl;
    	this.homeUrl = homeUrl;
    	int i = homeUrl.indexOf("uid=");
    	if(i>0){
    		uid = homeUrl.substring(i+4);
    	}
    	this.location = loc;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String toString(){
		return name + "|" + uid;
	}
	public String getImgUrl() {
		if(!imgUrl.startsWith("http://"))
			return "http://www.worlduc.com"+imgUrl;
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getHomeUrl() {
		return homeUrl;
	}
	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
