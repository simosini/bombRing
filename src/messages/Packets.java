package messages;

import java.net.Socket;

/**
 * a packet is a message with the client socket that sent it.
 */

public class Packets implements Comparable<Packets>{
	
	private Message message;
	private Socket sendingSocket;
	
	public Packets(Message m, Socket s){
		this.setMessage(m);
		this.setSendingSocket(s);
	}

	public Socket getSendingSocket() {
		return sendingSocket;
	}

	public void setSendingSocket(Socket sendingSocket) {
		this.sendingSocket = sendingSocket;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	@Override
	public String toString(){
		return this.getMessage().toString();
	}
	
	/** needed for the priority queue */
	@Override
	public int compareTo(Packets other) {
		return Integer.compare(this.getMessage().getPriority(), 
							  other.getMessage().getPriority());
	}
}
