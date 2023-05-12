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
* author:������ʦ
* qq:2398779723
* weixin:it_daimeng
*/
public class MainFrame {
	
	//��������б�
	public List<Player> players=new ArrayList<Player>();	
	
	public int index=0;
	//����˿��б�
	public List<Poker> allPokers=new ArrayList<Poker>();
	
	//��ŵ���
	public List<Poker> lordPokers=new ArrayList<Poker>();
	
	public int step=0;  //�ƾֵĽ�չ����
	
	public MainFrame()
	{
		//�����˿��б�
		createPokers();		
		
		try {
			
			//1.������������socket
			ServerSocket serverSocket=new ServerSocket(8888);
			
			while(true)
			{
				//2.���տͻ��˵�socket
				Socket socket= serverSocket.accept();			
				//3.�����߳� ����ͻ��˵�socket
				AcceptThread acceptThread=new AcceptThread(socket);
				acceptThread.start();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("���������쳣");
		}
		
	}
	//����һ�������߳�  ����ͻ��˵���Ϣ
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
						//����player����
						Player player=new Player(index++,msg);
						player.setSocket(socket);
						//��������б�
						players.add(player);
						
						System.out.println(msg+"������");
						
						System.out.println("��ǰ��������:"+players.size());
						
				        //�����������,�����������
						if(players.size()==3)
						{
							  fapai();
							  step=1;
						}
					}
					
					else if(step==1)  //��������������Ϣ
					{
						System.out.println("��������������Ϣ");
						
						JSONObject msgJsonObject=  JSON.parseObject(msg); 
						
						int typeid=msgJsonObject.getInteger("typeid");
						
					    int playerid=msgJsonObject.getInteger("playerid");
					    
					    String content=msgJsonObject.getString("content");
					    					    
					    //������
					    if(typeid==2)
					    {
					    	//�����齫һ�� ��Ϣ���� ����ӵ�����
					    	Message sendMessage=new Message(typeid,playerid,content,lordPokers);
					    	
					    	msg=JSON.toJSONString(sendMessage);		
					    	
					    	step=2;					    	
					    }
					    
					   //���� ���ͻ��˷������Ĳ�������Ϣ ԭ��Ⱥ�����������
					    
					    sendMessageToClient(msg);	
					    
					}
					else if(step==2) //���ƺͲ����ƺ���Ϸ����
					{
						sendMessageToClient(msg); //ת�������еĿͻ���
						
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("�������쳣:"+e.getMessage());
			}
			 
			 
		}
	}
	
	//Ⱥ����Ϣ���ͻ���
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
	
	
	//�������е��˿��б�
	public void createPokers()
	{		
		//�������� ��С��
	    Poker dawang=new Poker(0,"����",17);	
	    Poker xiaowang=new Poker(1,"С��",16);
	    
	    allPokers.add(dawang);
	    allPokers.add(xiaowang);
	    
	    //���������˿�
	    String[] names=new String[]{"2","A","K","Q","J","10","9","8","7","6","5","4","3"};
	    String[] colors=new String[]{"����","����","÷��","˫��"};
	    
	    int id=2;
	    int num=15;
	    //�����˿˵�����
	    for(String name:names)
	    {
	    	//����ÿ������Ļ�ɫ
	    	for(String color:colors)
	    	{
	    		Poker poker=new Poker(id++,color+name,num);
	    		
	    		allPokers.add(poker);
	    	}
	    	num--;
	    }	    
	    //ϴ��
	    Collections.shuffle(allPokers);
	}	
	//����
	public void fapai()
	{
		  //�����������
		  for(int i=0;i<allPokers.size();i++)
		  {
			  //�����������������
			   if(i>=51)
			   {
				   lordPokers.add(allPokers.get(i));
			   }
			   else {
				//���ηַ����������
				   if(i%3==0)
					   players.get(0).getPokers().add(allPokers.get(i));
				   else if(i%3==1)
					   players.get(1).getPokers().add(allPokers.get(i));
				   else
					   players.get(2).getPokers().add(allPokers.get(i));
			}
		  }
		  
		  //����ҵ���Ϣ���͵��ͻ���
		  //{"id":1,"name":"aa","socket":"","pokers":[{"id":1,"name":"����k","num":13},{},{}]} 
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
