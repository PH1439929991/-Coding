package com.wowowo.util;

import com.wowowo.model.PokerLabel;


/**
* author:呆萌老师
* qq:2398779723
* weixin:it_daimeng
*/

public class GameUtil {

	public static void move(PokerLabel pokerLabel,int x,int y)
	{
		 pokerLabel.setLocation(x, y);
		 
		 try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void move2(PokerLabel pokerLabel,int x,int y)
	{
		 pokerLabel.setLocation(x, y);
		 
		
	}
	
	
}
