package peer;

import java.util.ArrayList;
import java.util.List;

import messages.Message;
import threads.SendMessageThread;

/**
 * An object of this class is used to broadcast a message to all other current
 * active players of a game. 
 */
public class Broadcast {
	
	private List<ConnectionData> clientConnections;
	private Message msgToSend;
	
	public Broadcast(List<ConnectionData> clientConnections, Message m) {
		this.setClientConnections(new ArrayList<>(clientConnections));
		this.setMsgToSend(m);
	}

	public List<ConnectionData> getClientConnections() {
		return this.clientConnections;
	}

	private void setClientConnections(List<ConnectionData> clientConnections) {
		this.clientConnections = clientConnections;
	}

	public Message getMsgToSend() {
		return msgToSend;
	}

	public void setMsgToSend(Message msgToSend) {
		this.msgToSend = msgToSend;
	}
	
	/**
	 * send the message to all peers inserted in the client connection list.
	 */
	public void broadcastMessage() {
		List<Thread> threads = new ArrayList<>();
		
		// start threads
		this.getClientConnections().forEach(conn -> threads.add(new Thread(new SendMessageThread(conn, this.getMsgToSend()))));
		threads.forEach(t -> t.start());
		
		// wait for threads to be done
		threads.forEach(t -> {
			try {
				// don't want to wait for ever...
				t.join(10 * 1000);
			} catch (InterruptedException e) {
				System.err.println("Error joining threads exiting the game");
				e.printStackTrace();
				System.exit(-1);
			}
		});
		
		//System.out.println("Threads done!");
	}

}
