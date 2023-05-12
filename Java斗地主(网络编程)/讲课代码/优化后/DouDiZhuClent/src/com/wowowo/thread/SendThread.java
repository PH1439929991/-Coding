package com.wowowo.thread;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


//发送消息的线程
public class SendThread extends Thread{

	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	private Socket socket;
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	private boolean isRun=true;	
	
	public boolean isRun() {
		return isRun;
	}
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	public SendThread(Socket socket)
	{
		this.socket=socket;
	}
	public SendThread(Socket socket,String msg)
	{
		this.socket=socket;
		this.msg=msg;
	}
	public SendThread()
	{
	}
	public void run()
	{
		DataOutputStream dataOutputStream;
		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			while(true)
			{
				if(isRun==false)
					break;
				//如果消息不为null 
			  if(msg!=null)
			  {
				 System.out.println("消息在发送中:"+msg);
				  //发送消息
			     dataOutputStream.writeUTF(msg);
			     //消息发送完毕 消息内容清空
			     msg=null;			     		    
			  }
			  
			  Thread.sleep(50);  //暂停 等待新消息进来 
			
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}
	
}
