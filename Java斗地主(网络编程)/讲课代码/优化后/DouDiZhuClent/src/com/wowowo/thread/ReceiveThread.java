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

//����һ��������Ϣ���߳�
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
				
				 //���մӷ������˴��ݹ�������Ϣ json�ַ���
				 String jsonString= dataInputStream.readUTF();
				
				 if(step==0)
				 {
					 List<Player> players=new ArrayList<Player>();				
					 //System.out.println(jsonString);
					 //����json�ַ���  "[{},{}]"  [{},{}]
					 //��json�ַ���ת��Ϊjson����
					 JSONArray playerJsonArray = JSONArray.parseArray(jsonString);
					 for(int i=0;i<playerJsonArray.size();i++)
					 {					 
						  //��õ���json����--> ��Ҷ���
						  JSONObject playerJson=(JSONObject) playerJsonArray.get(i);
						  int id=playerJson.getInteger("id");
						  String name=playerJson.getString("name");
						  
						  //����˿��б�
						  List<Poker> pokers=new ArrayList<Poker>();
						  JSONArray pokerJsonArray= playerJson.getJSONArray("pokers");
						  for(int j=0;j<pokerJsonArray.size();j++)
						  {
							  
							  // ÿѭ��һ�� ���һ���˿˶���
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
					 
					 //���3����ҵ���Ϣ��
					 if(players.size()==3)
					 {
						 mainFrame.showAllPlayersInfo(players);
						 step=1; //��ҵ��� ��չ���ڶ���						
					 }
				 }
				 else if(step==1)
				 {
					  //��������������Ϣ���߳��Ƶ���Ϣ
					  JSONObject msgJsonObject= JSONObject.parseObject(jsonString);
					  
					  //������Ϣ����
					  int typeid=msgJsonObject.getInteger("typeid");
					  int playerid=msgJsonObject.getInteger("playerid");
					  String contentString=msgJsonObject.getString("content");
					  //��Ϣ����Ϊ����
					  if(typeid==1) 
						{
						    //1.��������ʾ��������Ϣ
							mainFrame.showMsg(playerid,1);
							
							//2.������һ�ҿ�ʼ������
							if(playerid+1==mainFrame.currentPlayer.getId())							
								mainFrame.getLord(); 
															
						}
					  //����������Ϣ
					  if(typeid==2)
					  {
						  
						   //��õ�����
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
						  
						  //������Լ����ĵ���
						    if(mainFrame.currentPlayer.getId()==playerid)
						   {	
						    	  //��ӵ�����
							      mainFrame.addLordPokers(lordPokers);	
							      
							      //��һ�ҳ��� ��ʾ���Ƶİ�ť
							      mainFrame.showChuPaiJabel();  
							    
						    }
						    
						   //��ʾ����ͼ��
						    mainFrame.showLordIcon(playerid);
						   
						    //֮ǰ����Ϣ������
						    mainFrame.msgLabel.setVisible(false);
						  
						    //������� ������ѡ������б� (�������ܳ���)
						    mainFrame.addClickEventToPoker();  
						    
						
					  }
					  
					  if(typeid==3) //������
					  {
						   //��ʾ�����Ƶ���Ϣ
						   mainFrame.showMsg(playerid,3);
						   
						   //�ж��Լ��ǲ�����һ�� ����� ��ʾ���ư�ť  0->1   1-> 2   2-> 0
						   if(playerid+1==mainFrame.currentPlayer.getId() || playerid-2==mainFrame.currentPlayer.getId())
						   {							    
							     mainFrame.showChuPaiJabel();  
						   }
						   
					  }
					  
					  if(typeid==4) //����
					  {
						  //��ó����б�
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
						  
						 //��ʾ�����б�
						 mainFrame.showOutPokerList(playerid,outPokers);						 
						 
						  //�ж��Լ��ǲ�����һ�� ����� ��ʾ���ư�ť  0->1   1-> 2   2-> 0
						   if(playerid+1==mainFrame.currentPlayer.getId() || playerid-2==mainFrame.currentPlayer.getId())
						   {
							     mainFrame.showChuPaiJabel();  
						   }						 
						   
						   mainFrame.prevPlayerid=playerid; //��¼��һ�����Ƶ����id
						 
					  }
					  
					  //�������Ϸ��������Ϣ
					  if(typeid==5)
					  {
						   if(playerid==mainFrame.currentPlayer.getId())
						   {
							   JOptionPane.showMessageDialog(mainFrame, "Ӯ��");
						   }
						   else {
							   JOptionPane.showMessageDialog(mainFrame, "����");
							   
						   }
						   //��Ϸ���� �ر��߳� �����ڵ�		 				 
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
