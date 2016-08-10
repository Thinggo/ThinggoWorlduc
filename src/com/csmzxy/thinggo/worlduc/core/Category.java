package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
	private String id;
	private String name;
	private String url;
	private List<Category> subCategories;
	
	public Category(String id, String name , String url){
		this.id = id;
		this.name = name;
		this.url = url;
		if(id==null){
			int x = url.indexOf("sid=");
			int y = url.indexOf("&uid=");
			if(y>x)
				this.id = url.substring(x+4,y);
			else
				this.id = url.substring(x+4);
		}
		subCategories = null;
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

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}
	
	public void addSubCategory(String id, String name, String url){
		if(subCategories == null)
			subCategories = new ArrayList<Category>();
		subCategories.add(new Category(id, name, url));
	}
	public void addSubCategory(Category category){
		if(subCategories == null)
			subCategories = new ArrayList<Category>();
		subCategories.add(category);
	}
	
	public void addSubCategories(List<Category> list){
		if(subCategories == null)
			subCategories = new ArrayList<Category>();
		subCategories.addAll(list);
	}
	
	public String toString(){
		return id+"|"+name;
	}

	public String getUrl() {
		return "http://www.worlduc.com"+ url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
