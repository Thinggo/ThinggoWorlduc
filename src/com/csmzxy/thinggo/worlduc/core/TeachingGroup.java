package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeachingGroup implements Serializable {
	private String id;		
	private String name;
	private String homeUrl;
	private String picUrl;
	private List<Person> members;
	public static String URL = "http://group.worlduc.com/GroupShow/Home.aspx?gid=%s";
	
	public TeachingGroup(String name, String homeUrl, String picUrl){
//		this.id = id;		
//		homeUrl = String.format(URL, id);
		members = new ArrayList<Person>();
		this.name = name;
		this.picUrl = picUrl;
		this.homeUrl = homeUrl;
		int i = homeUrl.indexOf("gid=");
    	if(i>0){
    		id = homeUrl.substring(i+4);
    	}
		
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

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
		int i = homeUrl.indexOf("gid=");
    	if(i>0){
    		id = homeUrl.substring(i+4);
    	}
	}

	public String getPicUrl() {
		return "http://group.worlduc.com" + picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public List<Person> getMembers() {
		return members;
	}

	public void addMembers(List<Person> members) {
		this.members.addAll(members);
	}
	
	public void addMember(Person p){
		this.members.add(p);
	}

	public static String getURL() {
		return URL;
	}

	public static void setURL(String uRL) {
		URL = uRL;
	}
	
	public String toString(){
		return "["+id+","+name+","+picUrl+"]";
	}
	
}
