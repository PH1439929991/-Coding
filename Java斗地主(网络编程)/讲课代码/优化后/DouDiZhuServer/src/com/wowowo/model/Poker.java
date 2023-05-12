package com.wowowo.model;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

/**
* author: 呆萌老师
* qq: 2398779723
* weixin: it_daimeng
*/
//扑克类
public class Poker {
	
	private int id; //扑克id
	
	private String name;//扑克名称
	
	private int num; //扑克数量 
	
	private boolean isOut; //扑克是否打出

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public Poker()
	{}
	
	public Poker(int id,String name,int num)
	{
		this.id=id;
		this.name=name;
		this.num=num;
	}
	public Poker(int id,String name,int num,boolean isOut)
	{
		this.id=id;
		this.name=name;
		this.num=num;
		this.isOut=isOut;
	}

}
