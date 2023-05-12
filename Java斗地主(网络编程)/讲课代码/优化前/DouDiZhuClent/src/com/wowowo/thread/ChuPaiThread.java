package com.wowowo.thread;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wowowo.model.Message;
import com.wowowo.model.Poker;
import com.wowowo.model.PokerLabel;
import com.wowowo.view.MainFrame;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
public class ChuPaiThread extends Thread{
	
	private int time;
	
	private MainFrame mainFrame;
	
	private boolean isRun;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public ChuPaiThread(int time,MainFrame mainFrame)
	{
		 isRun=true;
		 this.time=time;
		 this.mainFrame=mainFrame;
	}
	
	public  void run()
	{
		  while(time>=0 && isRun)
		  {			   
			     mainFrame.timeLabel.setText(time+"");
			     time--;
			     
			     try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     
		  }
		  Message message=null;
		  //如果是不出 (一种是时间到了 一种是 选择不出)
		  if(time==-1 || isRun==false && mainFrame.isOut==false)
		  {
			   message=new Message(3,mainFrame.currentPlayer.getId(),"不出",null);	
			   
			   //转换为json 交给 sendThread发送到服务器去
				  String msg=JSON.toJSONString(message);
				  mainFrame.sendThread.setMsg(msg);
		  }
		 
		  //出牌
		  if(isRun==false && mainFrame.isOut==true)
		  {
			  message=new Message(4,mainFrame.currentPlayer.getId(),"出牌",changePokerLableToPoker(mainFrame.selectedPokerLabels));
			  
			  //转换为json 交给 sendThread发送到服务器去
			  String msg=JSON.toJSONString(message);
			  mainFrame.sendThread.setMsg(msg);
			  //将当前发送出去的扑克牌 从扑克牌列表中移除
			  mainFrame.removeOutPokerFromPokerList();	
			  
			  //如果扑克列表的数量为0 代表赢了
			  if(mainFrame.pokerLabels.size()==0)
			  {
				   message=new Message(5, mainFrame.currentPlayer.getId(), "游戏结束", null);
				   msg=JSON.toJSONString(message);
				   try {
					 Thread.sleep(100);
					 mainFrame.sendThread.setMsg(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
			  }
			
			  
		  }
		  
		
		
		  
	}
	
	public List<Poker> changePokerLableToPoker(List<PokerLabel> selectedPokerLabels)
	{
		     List<Poker> list=new ArrayList<Poker>();
		     for(int i=0;i<selectedPokerLabels.size();i++)
		     {
		    	    PokerLabel pokerLabel=selectedPokerLabels.get(i);
		    	    Poker poker=new Poker(pokerLabel.getId(), pokerLabel.getName(), pokerLabel.getNum());
		    	    list.add(poker);
		     }
		     
		     return list;
	}
	

}
