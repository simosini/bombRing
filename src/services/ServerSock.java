package services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import messages.AckMessage;
import messages.Message;

public class ServerSock {

	public static void main(String[] args) {
		ServerSocket srv = null;
		try{
			srv = new ServerSocket(0);
			System.out.println(srv.getLocalPort());
			while(true){
				Socket serv = srv.accept();
				new Thread(new ServerSock().new Handler(serv)).start();
			}
		}
		catch(IOException e){
			e.printStackTrace();
			
			
		}

	}
	
	public class Handler implements Runnable {
		private Socket mySock;
		
		public Handler(Socket s){
			this.mySock = s;
		}
		
		@Override
		public void run(){
			try{
				ObjectInputStream in = new ObjectInputStream(mySock.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(mySock.getOutputStream());
				while(true){
					Message m = (Message) in.readObject();
					System.out.println(m);
				
					out.writeObject(new AckMessage());
				}
			}
			catch(IOException e){
				System.out.println("client closed socket");
				try {
					mySock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch(ClassNotFoundException ce){
				ce.printStackTrace();
			}
		}
	}
}
