package com.csmzxy.thinggo.worlduc.core;

@SuppressWarnings("serial")
public class FriendRequest extends Person {
	private String reqId;
	private String time; // Ê±¼ä
	private String remark; // ±¸×¢
	private boolean isGreet;
	

	public FriendRequest(String reqId, String name, String imgUrl, String homeUrl,
			String location, String time, String remark) {
		super(name, imgUrl, homeUrl, location);
		this.reqId = reqId;
		this.setTime(time);
		this.setRemark(remark);
		setGreet(false);
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isGreet() {
		return isGreet;
	}

	public void setGreet(boolean isGreet) {
		this.isGreet = isGreet;
	}
	
}
