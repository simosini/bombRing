package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.Message;
import messages.Packets;
import peer.ConnectionData;
import singletons.InQueue;

/** 
 * This handler takes care of the messages received on the server Socket.
 * It's started by the server and creates a packet to put on the inQueue.
 * It stays alive and connected with the same client.
 * It does not handle any message just put them on the inQueue and notify 
 * the handler.
 * */

public class ReceivedMessagesHandlerThread implements Runnable {

	private ConnectionData clientConnection;
	
	public ReceivedMessagesHandlerThread(Socket s) throws IOException{
	
		ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
		ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
		this.clientConnection = new ConnectionData(s, outputStream, inputStream);
	}
	
	private ConnectionData getConnectionData() {
		return this.clientConnection;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.INSTANCE;
		Message message = null;
		ObjectInputStream reader = null;

		try {
			reader = this.getConnectionData().getInputStream();
			while(true){			
				
				message = (Message) reader.readObject();
				System.out.println("Received :" + message);
				synchronized(inQueue){
					
					System.out.println("Creating packet!");
					Packets packet = new Packets(message, this.getConnectionData());
					inQueue.add(packet);
					System.out.println("Packet added correctly to inQueue");
					System.out.println("Notifying handler");
					inQueue.notify();
					System.out.println("Notified");
				}	
				
			}
		} catch (IOException e){
			System.out.println("Client closed connection. Closing current socket...");
		} catch (ClassNotFoundException ce) {
			System.out.println("Error reading message");
		} finally {
			Socket s = null;
			try{
				if ((s = this.getConnectionData().getClientSocket()) != null)
					s.close();
			} catch(IOException ie){
				ie.printStackTrace();
			}
		}
		
		
				
	}

}
