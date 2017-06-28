package peer;

import java.util.ArrayList;
import java.util.List;

import messages.Message;
import threads.SendMessagesThread;

public class Broadcast {
	
	private List<Integer> ports;
	private Message msgToSend;
	
	public Broadcast(List<Integer> ports, Message m) {
		this.setPorts(new ArrayList<>(ports));
		this.setMsgToSend(m);
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	public Message getMsgToSend() {
		return msgToSend;
	}

	public void setMsgToSend(Message msgToSend) {
		this.msgToSend = msgToSend;
	}
	
	public void broadcastMessage() {
		List<Thread> threads = new ArrayList<>();
		this.getPorts().forEach(port -> threads.add(new Thread(new SendMessagesThread(port, this.getMsgToSend()))));
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
