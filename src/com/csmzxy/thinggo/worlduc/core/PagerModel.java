package com.csmzxy.thinggo.worlduc.core;

import java.util.ArrayList;
import java.util.List;

public class PagerModel<T> {
	private int curPage;
	private int startPage;
	private int pageSize;
	private int pageCount;
	private List<T> list;
	
	public PagerModel(int curPage){
		this.curPage = curPage;
		this.pageCount = 1;
		this.list = new ArrayList<T>();
		this.startPage = 1;
		this.pageSize = 10;
	}
	public int getCurPage() {
		return curPage;
	}
	public int getStartPage() {
		return startPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getPageCount() {
		return pageCount;
	}
	public List<T> getList() {
		return list;
	}
	public void setCurPage(int curPage) {
		if(curPage>=startPage && curPage<=pageCount)
			this.curPage = curPage;
	}
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public String toString(){
		return "[curPage="+curPage + ",pageCount="+pageCount+"]";
	}
}
