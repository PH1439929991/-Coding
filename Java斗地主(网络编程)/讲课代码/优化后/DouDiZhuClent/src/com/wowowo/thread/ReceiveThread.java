package com.wowowo.thread;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wowowo.model.Player;
import com.wowowo.model.Poker;
import com.wowowo.view.MainFrame;

//创建一个接收消息的线程
public class ReceiveThread extends Thread{
	
	  private Socket socket;
	  private MainFrame mainFrame;
	  
	  private int step=0;
	  private boolean isRun=true;	
		
		public boolean isRun() {
			return isRun;
		}
		public void setRun(boolean isRun) {
			this.isRun = isRun;
		}
	
	  public ReceiveThread(Socket socket,MainFrame mainFrame)
	  {
		  this.socket=socket;
		  this.mainFrame=mainFrame;		   
	  }
	  public void run()
	  {
		  try {
			DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
			
			while(true)
			{
				 if(isRun==false)
					 break;
				
				 //接收从服务器端传递过来的消息 json字符串
				 String jsonString= dataInputStream.readUTF();
				
				 if(step==0)
				 {
					 List<Player> players=new ArrayList<Player>();				
					 //System.out.println(jsonString);
					 //解析json字符串  "[{},{}]"  [{},{}]
					 //将json字符串转换为json数组
					 JSONArray playerJsonArray = JSONArray.parseArray(jsonString);
					 for(int i=0;i<playerJsonArray.size();i++)
					 {					 
						  //获得当个json对象--> 玩家对象
						  JSONObject playerJson=(JSONObject) playerJsonArray.get(i);
						  int id=playerJson.getInteger("id");
						  String name=playerJson.getString("name");
						  
						  //存放扑克列表
						  List<Poker> pokers=new ArrayList<Poker>();
						  JSONArray pokerJsonArray= playerJson.getJSONArray("pokers");
						  for(int j=0;j<pokerJsonArray.size();j++)
						  {
							  
							  // 每循环一次 获得一个扑克对象
							     JSONObject pokerJSon= (JSONObject) pokerJsonArray.get(j);
							     int pid=pokerJSon.getInteger("id");
							     String pname=pokerJSon.getString("name");
							     int num=pokerJSon.getInteger("num");
							     Poker poker=new Poker(pid,pname,num);						     
							     pokers.add(poker);		
							 
						  }
						  
						  Player player=new Player(id,name,pokers);
						  
						  players.add(player);
						  
					 }
					 
					 //获得3个玩家的信息了
					 if(players.size()==3)
					 {
						 mainFrame.showAllPlayersInfo(players);
						 step=1; //玩家到齐 进展到第二步						
					 }
				 }
				 else if(step==1)
				 {
					  //接收抢地主的消息或者出牌的消息
					  JSONObject msgJsonObject= JSONObject.parseObject(jsonString);
					  
					  //解析消息对象
					  int typeid=msgJsonObject.getInteger("typeid");
					  int playerid=msgJsonObject.getInteger("playerid");
					  String contentString=msgJsonObject.getString("content");
					  //消息类型为不抢
					  if(typeid==1) 
						{
						    //1.主窗口显示不抢的信息
							mainFrame.showMsg(playerid,1);
							
							//2.设置下一家开始抢地主
							if(playerid+1==mainFrame.currentPlayer.getId())							
								mainFrame.getLord(); 
															
						}
					  //抢地主的消息
					  if(typeid==2)
					  {
						  
						   //获得地主牌
						  JSONArray pokersJsonArray= msgJsonObject.getJSONArray("pokers");
						  
						  List<Poker> lordPokers=new ArrayList<Poker>();
						  
						  for(int i=0;i<pokersJsonArray.size();i++)
						  {
							    JSONObject pokerJsonObject =  (JSONObject) pokersJsonArray.get(i);	
								int id=pokerJsonObject.getInteger("id");
								String name=pokerJsonObject.getString("name");
								int num=pokerJsonObject.getInteger("num");
								Poker p=new Poker(id, name, num);
								System.out.println(p);
								lordPokers.add(p);
								
						 }	
						  
						  //如果是自己抢的地主
						    if(mainFrame.currentPlayer.getId()==playerid)
						   {	
						    	  //添加地主牌
							      mainFrame.addLordPokers(lordPokers);	
							      
							      //第一家出牌 显示出牌的按钮
							      mainFrame.showChuPaiJabel();  
							    
						    }
						    
						   //显示地主图标
						    mainFrame.showLordIcon(playerid);
						   
						    //之前的消息框隐藏
						    mainFrame.msgLabel.setVisible(false);
						  
						    //所有玩家 都可以选择出牌列表 (不代表能出牌)
						    mainFrame.addClickEventToPoker();  
						    
						
					  }
					  
					  if(typeid==3) //不出牌
					  {
						   //显示不出牌的消息
						   mainFrame.showMsg(playerid,3);
						   
						   //判断自己是不是下一家 如果是 显示出牌按钮  0->1   1-> 2   2-> 0
						   if(playerid+1==mainFrame.currentPlayer.getId() || playerid-2==mainFrame.currentPlayer.getId())
						   {							    
							     mainFrame.showChuPaiJabel();  
						   }
						   
					  }
					  
					  if(typeid==4) //出牌
					  {
						  //获得出牌列表
                          JSONArray pokersJsonArray= msgJsonObject.getJSONArray("pokers");
						  
						  List<Poker> outPokers=new ArrayList<Poker>();
						  
						  for(int i=0;i<pokersJsonArray.size();i++)
						  {
							    JSONObject pokerJsonObject =  (JSONObject) pokersJsonArray.get(i);	
								int id=pokerJsonObject.getInteger("id");
								String name=pokerJsonObject.getString("name");
								int num=pokerJsonObject.getInteger("num");
								Poker p=new Poker(id, name, num);
								System.out.println(p);
								outPokers.add(p);								
						 }	
						  
						 //显示出牌列表
						 mainFrame.showOutPokerList(playerid,outPokers);						 
						 
						  //判断自己是不是下一家 如果是 显示出牌按钮  0->1   1-> 2   2-> 0
						   if(playerid+1==mainFrame.currentPlayer.getId() || playerid-2==mainFrame.currentPlayer.getId())
						   {
							     mainFrame.showChuPaiJabel();  
						   }						 
						   
						   mainFrame.prevPlayerid=playerid; //记录上一个出牌的玩家id
						 
					  }
					  
					  //如果是游戏结束的消息
					  if(typeid==5)
					  {
						   if(playerid==mainFrame.currentPlayer.getId())
						   {
							   JOptionPane.showMessageDialog(mainFrame, "赢了");
						   }
						   else {
							   JOptionPane.showMessageDialog(mainFrame, "输了");
							   
						   }
						   //游戏结束 关闭线程 清理窗口等		 				 
						   mainFrame.gameOver();
					  }
					
				 }
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  
	  }
	

}
