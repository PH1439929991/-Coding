package com.wowowo.view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/
public class MyPanel extends JPanel{

	public MyPanel()
	{
		this.setLayout(null);  //如果需要用到setLocation() setBounds() 就需要设置布局为null
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		//super.paintComponent(arg0);
		
		
		Image image=new ImageIcon("images/bg/bg1.png").getImage();
		
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		
		
	}
	
	   	  
	

}
