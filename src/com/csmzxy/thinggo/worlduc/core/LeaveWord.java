package com.csmzxy.thinggo.worlduc.core;

import java.io.Serializable;
import java.util.Vector;


public class LeaveWord implements Serializable {
	
	protected String id;		//����ID
	protected String content;	//��������
	protected String time;		//����ʱ��
	protected Person author;		//������
	protected Vector<LeaveWord> replyList;	//�ظ��б�
	
	public LeaveWord(){};
	
	public LeaveWord(String id, String content, String time, Person author){
		this.id = id;
		this.content = content;
		this.time = time;
		this.author = author;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public void addReplyWord(LeaveWord word){
		if(replyList == null){
			replyList = new Vector<LeaveWord>();
		}
		replyList.add(word);
	}
	
	public int getReplyCount() {
		return replyList==null?0:replyList.size();
	}
	
	public Vector<LeaveWord> getReplyList() {
		return replyList;
	}
	
	public Person getAuthor() {
		return author;
	}
	public void setAuthor(Person author) {
		this.author = author;
	}
		
	public String toString(){
		return "��"+ author.getName() + "|" + id + "����" + content + "��";
	}	
}
