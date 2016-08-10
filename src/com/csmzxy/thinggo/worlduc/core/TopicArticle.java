package com.csmzxy.thinggo.worlduc.core;

public class TopicArticle extends LeaveWord {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String title;
	protected String url;
	protected String viewCount;
	protected String replyCount;
	public static String BASE_URL = "http://group.worlduc.com";
	public TopicArticle(String time, String url, String title, String content, String viewCount, String replyCount){
		this.time = time;
		this.url = url;
		this.title = title;
		this.viewCount = viewCount;
		this.replyCount = replyCount;
		this.content = content;
		int i = url.indexOf("tid=");
    	if(i>0){
    		String tid = url.substring(i+4);
    		this.id = tid;
    	}		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return BASE_URL+url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getViewCount() {
		return viewCount;
	}
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}
	public int getReplyCount() {
		return Integer.parseInt(replyCount);
	}
	public void setReplyCount(String replyCount) {
		this.replyCount = replyCount;
	}
	
	public String toString(){
		return "("+ id + "," + title + ","+ content +"," + viewCount  +"," + replyCount +")";
	}
}
