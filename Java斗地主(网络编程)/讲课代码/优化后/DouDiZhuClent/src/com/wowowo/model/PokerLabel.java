package com.wowowo.model;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
//扑克标签类
public class PokerLabel extends JLabel implements Comparable{
	
	private int id;
	
	private String name;
	
	private int num;
	
	private boolean isOut;
	
	private boolean isUp;
	
	private boolean isSelected;  //是否选中

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
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

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	
	public PokerLabel()
	{
		this.setSize(105, 150);
	}
	
	public PokerLabel(int id,String name,int num)
	{
		this.id=id;
		this.name=name;
		this.num=num;
		this.setSize(105, 150);
	}
	
	public PokerLabel(int id,String name,int num,boolean isOut,boolean isUp)
	{
		this.id=id;
		this.name=name;
		this.num=num;
		this.isOut=isOut;
		this.isUp=isUp;
		
		if(isUp)
			turnUp();
		else {
			turnDown();
		}		
		
		this.setSize(105, 150);
	}
	
	public void turnUp()
	{
		this.setIcon(new ImageIcon("images/poker/"+id+".jpg"));
	}
	
	public void turnDown()
	{
		this.setIcon(new ImageIcon("images/poker/down.jpg"));
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		PokerLabel pokerLabel=(PokerLabel)arg0;
		
		if(this.num>pokerLabel.num)
			return 1;
		else if(this.num<pokerLabel.num)
			return -1;
		else		
		    return 0;
	}
	
	

	
}
