package com.wowowo.thread;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


//������Ϣ���߳�
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
				//�����Ϣ��Ϊnull 
			  if(msg!=null)
			  {
				 System.out.println("��Ϣ�ڷ�����:"+msg);
				  //������Ϣ
			     dataOutputStream.writeUTF(msg);
			     //��Ϣ������� ��Ϣ�������
			     msg=null;			     		    
			  }
			  
			  Thread.sleep(50);  //��ͣ �ȴ�����Ϣ���� 
			
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
