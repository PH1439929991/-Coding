package com.wowowo.thread;

import com.alibaba.fastjson.JSON;
import com.wowowo.model.Message;
import com.wowowo.view.MainFrame;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
//计时器的线程
public class CountThread extends Thread{
	
	private int i;
	
	private MainFrame mainFrame;
	
	private boolean isRun;
	
	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public CountThread(int i,MainFrame mainFrame)
	{
		  isRun=true;
		  this.i=i;
		  this.mainFrame=mainFrame;
	}
	
	public void run()
	{
		  while(i>=0 && isRun)
		  {
			    mainFrame.timeLabel.setText(i+"");
			    
			    i--;
			    
			    try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		  
		  Message msg=null;
		  //时间到了 或者 点击过不抢地主的按钮了		 
		  if(i==-1 || isRun==false && mainFrame.isLord==false)
		  {
			  msg=new Message(1,mainFrame.currentPlayer.getId(),"不抢",null);			
		  }
		  
		  //点了抢地主的按钮了
		  if(isRun==false && mainFrame.isLord==true)
		  {
			  msg=new Message(2,mainFrame.currentPlayer.getId(),"抢地主",null);	 
			
		  }
		 
		  //将消息传到服务器端
		  mainFrame.sendThread.setMsg(JSON.toJSONString(msg));	
		  
	}
	

}
