package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;

public class SubCategory implements Serializable {
	private String cid;
	private String sid;
	private String uid;
	private String cname;
	public SubCategory(String url, String cname) throws Exception {
        url = url.toLowerCase();
        int x = url.indexOf("sid=");
        int y = url.indexOf("uid=");
        int z = url.indexOf("cid=");
        if (x < 0 || y < 0 || z < 0)
        {
            throw new Exception("URL¸ñÊ½Òì³£:\r\n"+url);
        }
        String sid = url.substring(x + 4);
        x = sid.indexOf("&");
        if (x > 0)
        {
            sid = sid.substring(0, x);
        }
        String uid = url.substring(y + 4);
        y = uid.indexOf("&");
        if (y > 0)
        {
            uid = uid.substring(0, y);
        }

        String cid = url.substring(z + 4);
        z = cid.indexOf("&");
        if (z > 0) {
            cid = cid.substring(0, z);
        }
        this.cid = cid;
        this.sid = sid;
        this.uid = uid;
        this.cname = cname;
    }
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
}
