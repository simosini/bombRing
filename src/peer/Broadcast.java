package peer;

import java.util.ArrayList;
import java.util.List;

import messages.Message;
import threads.SendMessageThread;

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

	public void setClientConnections(List<ConnectionData> clientConnections) {
		this.clientConnections = clientConnections;
	}

	public Message getMsgToSend() {
		return msgToSend;
	}

	public void setMsgToSend(Message msgToSend) {
		this.msgToSend = msgToSend;
	}
	
	public void broadcastMessage() {
		List<Thread> threads = new ArrayList<>();
		this.getPorts().forEach(port -> threads.add(new Thread(new SendMessageThread(port, this.getMsgToSend()))));
		threads.forEach(t -> t.start());
		threads.forEach(t -> {
			try {
				t.join(10 * 1000);
			} catch (InterruptedException e) {
				System.out.println("Error joining threads exiting the game");
				System.exit(-1);
			}
		});
	}

}
