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

public class IncomingMessageHandlerThread implements Runnable {

	private ConnectionData clientConnection;
	
	public IncomingMessageHandlerThread(Socket s) {
	
		initStreams(s);
	}

	private void initStreams(Socket s) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
			outputStream.flush();
			ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
			this.clientConnection = new ConnectionData(s, outputStream, inputStream);
		} catch(IOException e){
			System.err.println("IncomingMessageThread Could not open streams");
			e.printStackTrace();
		}
	}
	
	private ConnectionData getConnectionData() {
		return this.clientConnection;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.getInstance();
		Message message = null;
		ObjectInputStream reader = null;

		try {
			reader = this.getConnectionData().getInputStream();
			while(true){			
				
				message = (Message) reader.readObject();
				//System.out.println("Received :" + message);
				
				//System.out.println("Creating packet!");
				Packets packet = new Packets(message, this.getConnectionData());
				inQueue.add(packet);
				//System.out.println("Packet added correctly to inQueue");
				//System.out.println("Notifying handler");
				synchronized(inQueue){					
					inQueue.notify();
					//System.out.println("Notified");
				}	
				
			}
		} catch (IOException e){
			// do nothing
			System.err.println();
			//System.out.println("Client closed connection. Closing current socket...");
		} catch (ClassNotFoundException e) {
			System.err.println("Error reading message from socket!");
			e.printStackTrace();
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
