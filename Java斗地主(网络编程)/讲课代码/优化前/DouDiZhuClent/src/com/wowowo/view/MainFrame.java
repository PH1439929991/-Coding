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
* author:������ʦ
* qq:2398779723
* weixin:it_daimeng
*/
public class MainFrame extends JFrame {

	public MyPanel myPanel;

	public String uname;

	public Socket socket;

	public SendThread sendThread; // ������Ϣ���߳�

	public ReceiveThread receiveThread;// ������Ϣ���߳�

	public Player currentPlayer; // ��ŵ�ǰ��Ҷ���

	public List<PokerLabel> pokerLabels = new ArrayList<PokerLabel>(); // ����˿˱�ǩ�б�

	public JLabel lordLabel1; // ��������ǩ

	public JLabel lordLabel2; // ����

	public JLabel timeLabel; // ��ʱ����ǩ

	public CountThread countThread; // ���������߳�

	public boolean isLord; // �Ƿ��ǵ���

	public JLabel msgLabel; // �����Ϣ
	
	public JLabel lordIconLabel; //����ͼ��
	
	public JLabel chupaiJLabel;//���Ʊ�ǩ
	
	public JLabel buchuJLabel; //�����Ʊ�ǩ
	
	public ChuPaiThread chuPaiThread; //���ƶ�ʱ���߳�
	
	public List<PokerLabel> selectedPokerLabels= new ArrayList<PokerLabel>();//���ѡ�е��˿��б�
	
	public List<PokerLabel> showOutPokerLabels=new ArrayList<PokerLabel>(); //��ŵ�ǰ���Ƶ��б�
	
	public boolean isOut;  //ѡ����ǳ��ƻ��ǲ�����
	
	public int prevPlayerid=-1; //��һ�����Ƶ����id
	

	public MainFrame(String uname, Socket socket) {
		this.uname = uname;
		this.socket = socket;
		// ���ô��ڵ�����

		this.setSize(1200, 700);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ���mypanel
		myPanel = new MyPanel();
		myPanel.setBounds(0, 0, 1200, 700);
		this.add(myPanel);		
		
		//��ʼ��������Ϣ
		init();

		// ��������Ϣ���߳�
		sendThread = new SendThread(socket, uname);
		sendThread.start();

		// ����������Ϣ���߳�
		receiveThread = new ReceiveThread(socket, this);
		receiveThread.start();

	}
	
	//���ڳ�ʼ��
	public void init()
	{

		//������Ϣ��
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
		// 1.��ʾ������ҵ�����

		// 2.��ʾ��ǰ��ҵ��˿��б�
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getName().equals(uname)) {
				currentPlayer = players.get(i);
			}
		}

		List<Poker> pokers = currentPlayer.getPokers();

		for (int i = 0; i < pokers.size(); i++) {
			// �����˿˱�ǩ
			Poker poker = pokers.get(i);

			PokerLabel pokerLabel = new PokerLabel(poker.getId(),
					poker.getName(), poker.getNum());
			pokerLabel.turnUp(); // ��ʾ����ͼ
			// ��ӵ������
			this.myPanel.add(pokerLabel);

			this.pokerLabels.add(pokerLabel);

			// ��̬����ʾ����
			this.myPanel.setComponentZOrder(pokerLabel, 0);
			// һ��һ�ŵ���ʾ����
			GameUtil.move(pokerLabel, 300 + 30 * i, 450);

		}

		// ���˿��б�����
		Collections.sort(pokerLabels);

		// �����ƶ�λ��
		for (int i = 0; i < pokerLabels.size(); i++) {
			this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			GameUtil.move(pokerLabels.get(i), 300 + 30 * i, 450);
		}

		//System.out.println(currentPlayer);

		if (currentPlayer.getId() == 0) {
			getLord(); // ������
		}

	}

	//������
	public void getLord() {
		// ��ʾ�������İ�ť �� ��ʱ����ť
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

		//��ʾ��ʱ����ͼ��
		this.timeLabel.setVisible(true);
		
		this.setVisible(true);
		// �ػ�
		this.repaint();

		// ������ʱ�����߳�
		countThread = new CountThread(10, this);
		countThread.start();

	}
	
	//��ʾ���Ƶı�ǩ
	public void showChuPaiJabel()
	{
		// ��ʾ���ƺͲ����Ƶİ�ť �� ��ʱ����ť
		chupaiJLabel.setVisible(true);
		buchuJLabel.setVisible(true);
		timeLabel.setVisible(true);
		
		this.repaint();
		
		chuPaiThread=new ChuPaiThread(30, this);
		chuPaiThread.start();		
		
	}

	// ��ʾ��Ϣ(���� �� ����)
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

	// ��ӵ�����
	public void addLordPokers(List<Poker> lordPokers) {
		for (int i = 0; i < lordPokers.size(); i++) {
			Poker poker = lordPokers.get(i);
			
			PokerLabel pokerLabel = new PokerLabel(poker.getId(),
					
			poker.getName(), poker.getNum());
			
			pokerLabel.turnUp(); // ��ʾ����ͼ			

			this.pokerLabels.add(pokerLabel);

		
		}
		
		Collections.sort(pokerLabels);
		
		for(int i=0;i<pokerLabels.size();i++)
		{
			// ��ӵ������
			this.myPanel.add(pokerLabels.get(i));
			// ��̬����ʾ����
			this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			// һ��һ�ŵ���ʾ����
			GameUtil.move(pokerLabels.get(i), 300 + 30 * i, 450);

		}
		
		currentPlayer.getPokers().addAll(lordPokers);
	}	
	
	//��ʾ����ͼ��
	public void showLordIcon(int playerid)
	{
		//��������ͼ�����
		 lordIconLabel=new JLabel();
		 lordIconLabel.setIcon(new ImageIcon("images/bg/dizhu.png"));
		 lordIconLabel.setSize(60, 89);
		
		
		//�������id��ʾ�������λ��
		 //����Լ��ǵ���
		 if(playerid==currentPlayer.getId())
		 {
			 lordIconLabel.setLocation(200, 450);
		 }
		 else if(playerid+1==currentPlayer.getId() || playerid-2==currentPlayer.getId()) //�ϼ�  //  2 0    0 1   1 2
		 {
			 lordIconLabel.setLocation(200, 100);
		 }
		 else { // �¼�
			 lordIconLabel.setLocation(950, 100);
		}
		 
		 //��ӵ���ͼ�굽�����
		 this.myPanel.add(lordIconLabel);
		 
		 this.repaint();  //�ػ�
		
		 
	}
	
	//���˿�����ӵ����¼�
	public void addClickEventToPoker()
	{
		  for(int i=0;i<pokerLabels.size();i++)
		  {
			  pokerLabels.get(i).addMouseListener(new PokerEvent());
		  }
		
	}
	
	//��ʾ���Ƶ��б�
	public void showOutPokerList(int playerid,List<Poker> outPokers)
	{
		 //�Ӵ������Ƴ�֮ǰ�ĳ��Ƶ��б�
		  for(int i=0;i<showOutPokerLabels.size();i++)
		  {
			     myPanel.remove(showOutPokerLabels.get(i));
		  }		
		
		  //���֮ǰ���Ƶ��б�
		  showOutPokerLabels.clear();
		 
		  //��ʾ��ǰ���Ƶ��б�
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
		  
		  this.repaint() ; //�����ػ�
		
		
		
	}
	
	//����  �������ƴӵ�ǰ��ҵ��˿��б����Ƴ�
	public void removeOutPokerFromPokerList()
	{
		  //1.�ӵ�ǰ��ҵ��˿��б����Ƴ�
		  pokerLabels.removeAll(selectedPokerLabels);
		
		  //2.��������Ƴ�
		  for(int i=0;i<selectedPokerLabels.size();i++)
		  {
			   myPanel.remove(selectedPokerLabels.get(i));
		  }
		
		  //3.ʣ�µ��˿��б����¶�λ
		  for(int i=0;i<pokerLabels.size();i++)
		  {
			    myPanel.setComponentZOrder(pokerLabels.get(i), 0);
			    GameUtil.move(pokerLabels.get(i), 300+30*i, 450);
		  }
		
		  //4.���ѡ����˿����б�
		  selectedPokerLabels.clear();
		  
		  this.repaint();  
		
	}
	
	
	// ��������¼���������
	class MyMouseEvent implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			// �������������
			if (arg0.getSource().equals(lordLabel1)) {
				// ֹͣ��ʱ��
				countThread.setRun(false);
				isLord = true;
				// �����������İ�ť���ɼ�
				lordLabel1.setVisible(false);
				lordLabel2.setVisible(false);
				timeLabel.setVisible(false);
				
			}
			// ����Ĳ���
			if (arg0.getSource().equals(lordLabel2)) {
				// ֹͣ��ʱ��
				countThread.setRun(false);
				isLord = false;
				lordLabel1.setVisible(false);
				lordLabel2.setVisible(false);
				timeLabel.setVisible(false);
				
			}
			
			//�������
			if(arg0.getSource().equals(chupaiJLabel))
			{
				PokerType pokerType=  PokerRule.checkPokerType(selectedPokerLabels);
				//�ж��Ƿ��������
				if(!pokerType.equals(PokerType.p_error))
				{
					
					System.out.println(prevPlayerid+","+currentPlayer.getId());
				   //�������ͣ��ж��ǲ��Ǳ��ϼҴ� �����ϼҾ����Լ�
				   if(prevPlayerid==-1 || prevPlayerid==currentPlayer.getId() || PokerRule.isBigger(showOutPokerLabels, selectedPokerLabels))
				   {
					   isOut=true;
					   //��ʱ��ֹͣ
					  chuPaiThread.setRun(false);				  
					  chupaiJLabel.setVisible(false);
					  buchuJLabel.setVisible(false);
					  timeLabel.setVisible(false);	
				   }
				   else {
					   JOptionPane.showMessageDialog(null, "�밴�������");
					   
				}
				}
				else {
					JOptionPane.showMessageDialog(null, "����������");
				}
			}
			
			if(arg0.getSource().equals(buchuJLabel))
			{     
				  isOut=false;
				  //��ʱ��ֹͣ
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
	
	//�����˿��Ƶ��¼���������
	class PokerEvent implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
		    PokerLabel pokerLabel=(PokerLabel)arg0.getSource();
			//���֮ǰѡ����� ��ȡ��ѡ��(����ѡ������Ϊfalse λ�ûص�ԭλ����ѡ����˿����б����Ƴ�)
			if(pokerLabel.isSelected())
			{
				  pokerLabel.setSelected(false); 
				  pokerLabel.setLocation(pokerLabel.getX(), pokerLabel.getY()+30);
				  selectedPokerLabels.remove(pokerLabel);
			}
			//���֮ǰû��ѡ�� ��ѡ��(����ѡ������Ϊtrue λ�������ƶ�һ��,��ӵ�ѡ����˿����б���)
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
