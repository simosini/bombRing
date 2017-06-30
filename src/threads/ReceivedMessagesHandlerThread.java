package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import messages.Message;
import messages.Packets;
import singletons.InQueue;

/** 
 * This handler takes care of the messages received on the server Socket.
 * It's started by the server and creates a packet to put on the inQueue.
 * It stays alive and connected with the same client.
 * It does not handle any message just put them on the inQueue and notify 
 * the handler.
 * */

public class ReceivedMessagesHandlerThread implements Runnable {

	private Socket sender;
	
	public ReceivedMessagesHandlerThread(Socket s){
	
		this.setSender(s);
	}
	
	private Socket getSender() {
		return sender;
	}

	private void setSender(Socket sender) {
		this.sender = sender;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.INSTANCE;
		Message message = null;
		ObjectInputStream reader = null;

		try {
			reader = new ObjectInputStream(this.getSender().getInputStream());
			while(true){			
				
				message = (Message) reader.readObject();
				System.out.println("Received :" + message);
				synchronized(inQueue){
					
					System.out.println("Creating packet!");
					Packets packet = new Packets(message, sender);
					inQueue.add(packet);
					System.out.println("Packet added correctly to inQueue");
					System.out.println("Notifying handler");
					inQueue.notify();
					System.out.println("Notified");
				}	
				
			}
		} catch (IOException e){
			System.out.println("Client closed connection. Socket closed");
		} catch (ClassNotFoundException ce) {
			System.out.println("Error reading message");
		} finally {
			try{
				sender.close();
			} catch(IOException ie){
				ie.printStackTrace();
			}
		}
		
		
				
	}

}
