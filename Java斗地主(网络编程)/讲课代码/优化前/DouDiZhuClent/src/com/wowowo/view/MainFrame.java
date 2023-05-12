package com.wowowo.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.InitialContext;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.wowowo.model.Player;
import com.wowowo.model.Poker;
import com.wowowo.model.PokerLabel;
import com.wowowo.thread.ChuPaiThread;
import com.wowowo.thread.CountThread;
import com.wowowo.thread.ReceiveThread;
import com.wowowo.thread.SendThread;
import com.wowowo.util.GameUtil;
import com.wowowo.util.PokerRule;
import com.wowowo.util.PokerType;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
public class MainFrame extends JFrame {

	public MyPanel myPanel;

	public String uname;

	public Socket socket;

	public SendThread sendThread; // 发送消息的线程

	public ReceiveThread receiveThread;// 接收消息的线程

	public Player currentPlayer; // 存放当前玩家对象

	public List<PokerLabel> pokerLabels = new ArrayList<PokerLabel>(); // 存放扑克标签列表

	public JLabel lordLabel1; // 抢地主标签

	public JLabel lordLabel2; // 不叫

	public JLabel timeLabel; // 定时器标签

	public CountThread countThread; // 计数器的线程

	public boolean isLord; // 是否是地主

	public JLabel msgLabel; // 存放消息
	
	public JLabel lordIconLabel; //地主图标
	
	public JLabel chupaiJLabel;//出牌标签
	
	public JLabel buchuJLabel; //不出牌标签
	
	public ChuPaiThread chuPaiThread; //出牌定时器线程
	
	public List<PokerLabel> selectedPokerLabels= new ArrayList<PokerLabel>();//存放选中的扑克列表
	
	public List<PokerLabel> showOutPokerLabels=new ArrayList<PokerLabel>(); //存放当前出牌的列表
	
	public boolean isOut;  //选择的是出牌还是不出牌
	
	public int prevPlayerid=-1; //上一个出牌的玩家id
	

	public MainFrame(String uname, Socket socket) {
		this.uname = uname;
		this.socket = socket;
		// 设置窗口的属性

		this.setSize(1200, 700);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加mypanel
		myPanel = new MyPanel();
		myPanel.setBounds(0, 0, 1200, 700);
		this.add(myPanel);		
		
		//初始化窗口信息
		init();

		// 启动发消息的线程
		sendThread = new SendThread(socket, uname);
		sendThread.start();

		// 启动接收消息的线程
		receiveThread = new ReceiveThread(socket, this);
		receiveThread.start();

	}
	
	//窗口初始化
	public void init()
	{

		//创建消息框
		msgLabel = new JLabel();
		
		chupaiJLabel = new JLabel();
		chupaiJLabel.setBounds(330, 350, 110, 53);
		chupaiJLabel.setIcon(new ImageIcon("images/bg/chupai.png"));
		chupaiJLabel.addMouseListener(new MyMouseEvent());
		chupaiJLabel.setVisible(false);
		this.myPanel.add(chupaiJLabel);

		buchuJLabel = new JLabel();
		buchuJLabel.setBounds(440, 350, 110, 53);
		buchuJLabel.setIcon(new ImageIcon("images/bg/buchupai.png"));
		buchuJLabel.addMouseListener(new MyMouseEvent());
		buchuJLabel.setVisible(false);
		this.myPanel.add(buchuJLabel);

		timeLabel = new JLabel();
		timeLabel.setBounds(550, 350, 50, 50);
		timeLabel.setFont(new Font("Dialog", 0, 30));
		timeLabel.setForeground(Color.red);
		timeLabel.setVisible(false);
		this.myPanel.add(timeLabel);
	}
	

	public void showAllPlayersInfo(List<Player> players) {
		// 1.显示三个玩家的名称

		// 2.显示当前玩家的扑克列表
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getName().equals(uname)) {
				currentPlayer = players.get(i);
			}
		}

		List<Poker> pokers = currentPlayer.getPokers();

		for (int i = 0; i < pokers.size(); i++) {
			// 创建扑克标签
			Poker poker = pokers.get(i);

			PokerLabel pokerLabel = new PokerLabel(poker.getId(),
					poker.getName(), poker.getNum());
			pokerLabel.turnUp(); // 显示正面图
			// 添加到面板中
			this.myPanel.add(pokerLabel);

			this.pokerLabels.add(pokerLabel);

			// 动态的显示出来
			this.myPanel.setComponentZOrder(pokerLabel, 0);
			// 一张一张的显示出来
			GameUtil.move(pokerLabel, 300 + 30 * i, 450);

		}

		// 对扑克列表排序
		Collections.sort(pokerLabels);

		// 重新移动位置
		for (int i = 0; i < pokerLabels.size(); i++) {
			this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			GameUtil.move(pokerLabels.get(i), 300 + 30 * i, 450);
		}

		//System.out.println(currentPlayer);

		if (currentPlayer.getId() == 0) {
			getLord(); // 抢地主
		}

	}

	//抢地主
	public void getLord() {
		// 显示抢地主的按钮 和 定时器按钮
		lordLabel1 = new JLabel();
		lordLabel1.setBounds(330, 400, 104, 46);
		lordLabel1.setIcon(new ImageIcon("images/bg/jiaodizhu.png"));
		lordLabel1.addMouseListener(new MyMouseEvent());
		this.myPanel.add(lordLabel1);

		lordLabel2 = new JLabel();
		lordLabel2.setBounds(440, 400, 104, 46);
		lordLabel2.setIcon(new ImageIcon("images/bg/bujiao.png"));
		lordLabel2.addMouseListener(new MyMouseEvent());

		this.myPanel.add(lordLabel2);

		//显示定时器的图标
		this.timeLabel.setVisible(true);
		
		this.setVisible(true);
		// 重绘
		this.repaint();

		// 启动计时器的线程
		countThread = new CountThread(10, this);
		countThread.start();

	}
	
	//显示出牌的标签
	public void showChuPaiJabel()
	{
		// 显示出牌和不出牌的按钮 和 定时器按钮
		chupaiJLabel.setVisible(true);
		buchuJLabel.setVisible(true);
		timeLabel.setVisible(true);
		
		this.repaint();
		
		chuPaiThread=new ChuPaiThread(30, this);
		chuPaiThread.start();		
		
	}

	// 显示消息(不抢 或 不出)
	public void showMsg(int typeid) {		
	
		msgLabel.setVisible(true);
		msgLabel.setBounds(500, 300, 129, 77);
		if(typeid==1)
		   msgLabel.setIcon(new ImageIcon("images/bg/buqiang.png"));
		if(typeid==3)		
		   msgLabel.setIcon(new ImageIcon("images/bg/buchu.png"));
		this.myPanel.add(msgLabel);
		this.repaint();

	}

	// 添加地主牌
	public void addLordPokers(List<Poker> lordPokers) {
		for (int i = 0; i < lordPokers.size(); i++) {
			Poker poker = lordPokers.get(i);
			
			PokerLabel pokerLabel = new PokerLabel(poker.getId(),
					
			poker.getName(), poker.getNum());
			
			pokerLabel.turnUp(); // 显示正面图			

			this.pokerLabels.add(pokerLabel);

		
		}
		
		Collections.sort(pokerLabels);
		
		for(int i=0;i<pokerLabels.size();i++)
		{
			// 添加到面板中
			this.myPanel.add(pokerLabels.get(i));
			// 动态的显示出来
			this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			// 一张一张的显示出来
			GameUtil.move(pokerLabels.get(i), 300 + 30 * i, 450);

		}
		
		currentPlayer.getPokers().addAll(lordPokers);
	}	
	
	//显示地主图标
	public void showLordIcon(int playerid)
	{
		//创建地主图标对象
		 lordIconLabel=new JLabel();
		 lordIconLabel.setIcon(new ImageIcon("images/bg/dizhu.png"));
		 lordIconLabel.setSize(60, 89);
		
		
		//根据玩家id显示到具体的位置
		 //如果自己是地主
		 if(playerid==currentPlayer.getId())
		 {
			 lordIconLabel.setLocation(200, 450);
		 }
		 else if(playerid+1==currentPlayer.getId() || playerid-2==currentPlayer.getId()) //上家  //  2 0    0 1   1 2
		 {
			 lordIconLabel.setLocation(200, 100);
		 }
		 else { // 下家
			 lordIconLabel.setLocation(950, 100);
		}
		 
		 //添加地主图标到面板上
		 this.myPanel.add(lordIconLabel);
		 
		 this.repaint();  //重绘
		
		 
	}
	
	//给扑克牌添加单击事件
	public void addClickEventToPoker()
	{
		  for(int i=0;i<pokerLabels.size();i++)
		  {
			  pokerLabels.get(i).addMouseListener(new PokerEvent());
		  }
		
	}
	
	//显示出牌的列表
	public void showOutPokerList(int playerid,List<Poker> outPokers)
	{
		 //从窗口上移除之前的出牌的列表
		  for(int i=0;i<showOutPokerLabels.size();i++)
		  {
			     myPanel.remove(showOutPokerLabels.get(i));
		  }		
		
		  //清空之前出牌的列表
		  showOutPokerLabels.clear();
		 
		  //显示当前出牌的列表
		  for(int i=0;i<outPokers.size();i++)
		  {
			   
			    Poker poker=outPokers.get(i);
			    
			    PokerLabel pokerLabel=new PokerLabel(poker.getId(),poker.getName(),poker.getNum());
			    
			    pokerLabel.setLocation(400+30*i, 200);
			    
			    pokerLabel.turnUp();
			    
			    myPanel.add(pokerLabel);
			    
			    showOutPokerLabels.add(pokerLabel);
			    
			    myPanel.setComponentZOrder(pokerLabel, 0);
			    
		  }
		  
		  this.repaint() ; //窗口重绘
		
		
		
	}
	
	//出牌  将出的牌从当前玩家的扑克列表中移除
	public void removeOutPokerFromPokerList()
	{
		  //1.从当前玩家的扑克列表中移除
		  pokerLabels.removeAll(selectedPokerLabels);
		
		  //2.从面板中移除
		  for(int i=0;i<selectedPokerLabels.size();i++)
		  {
			   myPanel.remove(selectedPokerLabels.get(i));
		  }
		
		  //3.剩下的扑克列表重新定位
		  for(int i=0;i<pokerLabels.size();i++)
		  {
			    myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			    GameUtil.move(pokerLabels.get(i), 300+30*i, 450);
		  }
		
		  //4.清空选择的扑克牌列表
		  selectedPokerLabels.clear();
		  
		  this.repaint();  
		
	}
	
	
	// 创建鼠标事件监听器类
	class MyMouseEvent implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			// 点击的是抢地主
			if (arg0.getSource().equals(lordLabel1)) {
				// 停止计时器
				countThread.setRun(false);
				isLord = true;
				// 设置抢地主的按钮不可见
				lordLabel1.setVisible(false);
				lordLabel2.setVisible(false);
				timeLabel.setVisible(false);
				
			}
			// 点击的不抢
			if (arg0.getSource().equals(lordLabel2)) {
				// 停止计时器
				countThread.setRun(false);
				isLord = false;
				lordLabel1.setVisible(false);
				lordLabel2.setVisible(false);
				timeLabel.setVisible(false);
				
			}
			
			//点击出牌
			if(arg0.getSource().equals(chupaiJLabel))
			{
				PokerType pokerType=  PokerRule.checkPokerType(selectedPokerLabels);
				//判断是否符合牌型
				if(!pokerType.equals(PokerType.p_error))
				{
					
					System.out.println(prevPlayerid+","+currentPlayer.getId());
				   //符合牌型，判断是不是比上家大 或者上家就是自己
				   if(prevPlayerid==-1 || prevPlayerid==currentPlayer.getId() || PokerRule.isBigger(showOutPokerLabels, selectedPokerLabels))
				   {
					   isOut=true;
					   //计时器停止
					  chuPaiThread.setRun(false);				  
					  chupaiJLabel.setVisible(false);
					  buchuJLabel.setVisible(false);
					  timeLabel.setVisible(false);	
				   }
				   else {
					   JOptionPane.showMessageDialog(null, "请按规则出牌");
					   
				}
				}
				else {
					JOptionPane.showMessageDialog(null, "不符合牌型");
				}
			}
			
			if(arg0.getSource().equals(buchuJLabel))
			{     
				  isOut=false;
				  //计时器停止
				  chuPaiThread.setRun(false);				  
				  chupaiJLabel.setVisible(false);
				  buchuJLabel.setVisible(false);
				  timeLabel.setVisible(false);	
				
			}

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
	
	//创建扑克牌的事件监听器类
	class PokerEvent implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
		    PokerLabel pokerLabel=(PokerLabel)arg0.getSource();
			//如果之前选择过了 则取消选择(设置选中属性为false 位置回到原位，从选择的扑克牌列表中移除)
			if(pokerLabel.isSelected())
			{
				  pokerLabel.setSelected(false); 
				  pokerLabel.setLocation(pokerLabel.getX(), pokerLabel.getY()+30);
				  selectedPokerLabels.remove(pokerLabel);
			}
			//如果之前没有选择 则选中(设置选中属性为true 位置往上移动一点,添加到选择的扑克牌列表中)
			else {
				  pokerLabel.setSelected(true); 
				  pokerLabel.setLocation(pokerLabel.getX(), pokerLabel.getY()-30);
				  selectedPokerLabels.add(pokerLabel);
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
