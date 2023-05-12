package com.wowowo.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.Data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wowowo.model.Message;
import com.wowowo.model.Player;
import com.wowowo.model.Poker;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
public class MainFrame {
	
	//创建玩家列表
	public List<Player> players=new ArrayList<Player>();	
	
	public int index=0;
	//存放扑克列表
	public List<Poker> allPokers=new ArrayList<Poker>();
	
	//存放底牌
	public List<Poker> lordPokers=new ArrayList<Poker>();
	
	public int step=0;  //牌局的进展步骤
	
	public MainFrame()
	{
		//创建扑克列表
		createPokers();		
		
		try {
			
			//1.创建服务器端socket
			ServerSocket serverSocket=new ServerSocket(8888);
			
			while(true)
			{
				//2.接收客户端的socket
				Socket socket= serverSocket.accept();			
				//3.开启线程 处理客户端的socket
				AcceptThread acceptThread=new AcceptThread(socket);
				acceptThread.start();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("服务器端异常");
		}
		
	}
	//创建一个接收线程  处理客户端的信息
	class AcceptThread extends Thread
	{
		Socket socket;
		
		public AcceptThread(Socket socket)
		{
			this.socket=socket;
			
		}
		
		public void run()
		{
			 try {
				DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
				
				while(true)
				{
					String msg=dataInputStream.readUTF();	
					
					if(step==0)
					{
						//创建player对象
						Player player=new Player(index++,msg);
						player.setSocket(socket);
						//存入玩家列表
						players.add(player);
						
						System.out.println(msg+"上线了");
						
						System.out.println("当前上线人数:"+players.size());
						
				        //玩家人数到齐,发给三个玩家
						if(players.size()==3)
						{
							  fapai();
							  step=1;
						}
					}
					
					else if(step==1)  //接收抢地主的信息
					{
						System.out.println("接收抢地主的消息");
						
						JSONObject msgJsonObject=  JSON.parseObject(msg); 
						
						int typeid=msgJsonObject.getInteger("typeid");
						
					    int playerid=msgJsonObject.getInteger("playerid");
					    
					    String content=msgJsonObject.getString("content");
					    					    
					    //抢地主
					    if(typeid==2)
					    {
					    	//重新组将一个 消息对象 ，添加地主牌
					    	Message sendMessage=new Message(typeid,playerid,content,lordPokers);
					    	
					    	msg=JSON.toJSONString(sendMessage);		
					    	
					    	step=2;					    	
					    }
					    
					   //不抢 将客户端发过来的不抢的信息 原样群发到所有玩家
					    
					    sendMessageToClient(msg);	
					    
					}
					else if(step==2) //出牌和不出牌和游戏结束
					{
						sendMessageToClient(msg); //转发到所有的客户端
						
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("服务器异常:"+e.getMessage());
			}
			 
			 
		}
	}
	
	//群发消息到客户端
	public void sendMessageToClient(String msg)
	{
		for(int i=0;i<players.size();i++)
		{
			   DataOutputStream dataOutputStream;
			try {
				dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
				 dataOutputStream.writeUTF(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   
			  
		}
		
	}
	
	
	//创建所有的扑克列表
	public void createPokers()
	{		
		//创建大王 ，小王
	    Poker dawang=new Poker(0,"大王",17);	
	    Poker xiaowang=new Poker(1,"小王",16);
	    
	    allPokers.add(dawang);
	    allPokers.add(xiaowang);
	    
	    //创建其它扑克
	    String[] names=new String[]{"2","A","K","Q","J","10","9","8","7","6","5","4","3"};
	    String[] colors=new String[]{"黑桃","红桃","梅花","双块"};
	    
	    int id=2;
	    int num=15;
	    //遍历扑克的种类
	    for(String name:names)
	    {
	    	//遍历每个种类的花色
	    	for(String color:colors)
	    	{
	    		Poker poker=new Poker(id++,color+name,num);
	    		
	    		allPokers.add(poker);
	    	}
	    	num--;
	    }	    
	    //洗牌
	    Collections.shuffle(allPokers);
	}	
	//发牌
	public void fapai()
	{
		  //发给三个玩家
		  for(int i=0;i<allPokers.size();i++)
		  {
			  //最后三张留给地主牌
			   if(i>=51)
			   {
				   lordPokers.add(allPokers.get(i));
			   }
			   else {
				//依次分发给三个玩家
				   if(i%3==0)
					   players.get(0).getPokers().add(allPokers.get(i));
				   else if(i%3==1)
					   players.get(1).getPokers().add(allPokers.get(i));
				   else
					   players.get(2).getPokers().add(allPokers.get(i));
			}
		  }
		  
		  //将玩家的信息发送到客户端
		  //{"id":1,"name":"aa","socket":"","pokers":[{"id":1,"name":"黑桃k","num":13},{},{}]} 
		  for(int i=0;i<players.size();i++)
		  {
			 try {
				DataOutputStream dataOutputStream=new DataOutputStream(players.get(i).getSocket().getOutputStream());
			
				String jsonString=JSON.toJSONString(players);
				
				dataOutputStream.writeUTF(jsonString);				
				
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
		   
		  }
		
	}
	

	
	

}
