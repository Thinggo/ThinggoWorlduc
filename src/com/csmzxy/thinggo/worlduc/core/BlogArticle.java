package com.csmzxy.thinggo.worlduc.core;

public class BlogArticle extends TopicArticle {
	private static final long serialVersionUID = 1L;
	private String tid;

	public BlogArticle(String title, String url){
		this("",url,title,"","","");
		this.title = title;
		this.url = url;
		tid = "0";
		int i = url.indexOf("bid=");
    	if(i>0){
    		String str = url.substring(i+4);
    		int j  = str.indexOf("tid=");
    		if(j>=0){
    			this.id = str.substring(0,j-1);
    			this.tid = str.substring(j+4);
    		}else{
    			this.id = url.substring(i+4);
    		}
    	}	
		BASE_URL = "http://www.worlduc.com";
	}
	public BlogArticle(String time, String url, String title, String content, String viewCount, String replyCount){
		super(time, url, title, content, viewCount, replyCount);
		BASE_URL = "http://www.worlduc.com";
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	public String getUrl() {
		return "http://www.worlduc.com"+url;
	}
}
