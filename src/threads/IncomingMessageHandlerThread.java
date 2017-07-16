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
 * It stays alive and connected with the same client during the whole game.
 * It does not handle any message just put them on the inQueue and notify 
 * the handler.
 */

public class IncomingMessageHandlerThread implements Runnable {

	private ConnectionData clientConnection;
	private Player connectedPlayer;
	
	public IncomingMessageHandlerThread(Socket s) {
	
		initStreams(s);
	}
	
	/**
	 * initializes the streams of the connection with the client 
	 * connected to the main server socket 
	 * @param the connected socket, that is the server side of the peer
	 */
	private void initStreams(Socket s) {
		try {
			final ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
			outputStream.flush();
			final ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
			this.clientConnection = new ConnectionData(s, outputStream, inputStream);
			final Player connectedPlayer = (Player) inputStream.readObject();
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
	
	/**
	 * The server thread receives the message and put it on the incoming
	 * messages queue. Once done it notifies the message handler which is
	 * in charge of emptying the queue.
	 */
	@Override
	public void run() {
		final InQueue inQueue = InQueue.getInstance();
		Message message = null;
		ObjectInputStream reader = null;

		try {
			reader = this.getConnectionData().getInputStream();
			while(true){			
				
				message = (Message) reader.readObject();
				
				Packets packet = new Packets(message, this.getConnectionData());
				inQueue.add(packet);
				
				// notify message handler thread
				synchronized(inQueue){					
					inQueue.notify();
				}	
				
			}
		} catch (IOException e){
			
			// when the connected player closes its client socket this socket can be closed as well 
			System.out.print(this.getConnectedPlayer().getNickname() + " just left the game. Disconnecting socket...");
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
