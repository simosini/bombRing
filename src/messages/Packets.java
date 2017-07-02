package messages;

import peer.ConnectionData;

/**
 * a packet is a message with the client socket that sent it.
 */

public class Packets implements Comparable<Packets>{
	
	private Message message;
	private ConnectionData sendingClient; /** the client to which answer */
	
	public Packets(Message m, ConnectionData cd){
		this.setMessage(m);
		this.setSendingClient(cd);
	}

	public ConnectionData getSendingClient() {
		return this.sendingClient;
	}

	public void setSendingClient(ConnectionData sender) {
		this.sendingClient = sender;
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
