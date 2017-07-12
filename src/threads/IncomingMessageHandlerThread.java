package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import beans.Player;
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
	private Player connectedPlayer;
	
	public IncomingMessageHandlerThread(Socket s) {
	
		initStreams(s);
	}

	private void initStreams(Socket s) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
			outputStream.flush();
			ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
			this.clientConnection = new ConnectionData(s, outputStream, inputStream);
			Player connectedPlayer = (Player) inputStream.readObject();
			this.setConnectedPlayer(connectedPlayer);
			
		} catch(IOException e){
			System.err.println("IncomingMessageThread could not open streams");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Couldn't retrieve player!");
			e.printStackTrace();
		}
	}
	
	private Player getConnectedPlayer() {
		return connectedPlayer;
	}

	private void setConnectedPlayer(Player connectedPlayer) {
		this.connectedPlayer = connectedPlayer;
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
			
			System.out.print(this.getConnectedPlayer().getNickname() + " just left the game. Disconnecting from him...");
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
			System.out.println("Done!");
		}
		
		
				
	}

}
