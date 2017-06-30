package threads;

import messages.Packets;

public class OnePlayerHandlerThread implements Runnable {
	
	private Packets packetToSend;
	
	public OnePlayerHandlerThread(Packets p) {
		this.packetToSend = p;
	}

	@Override
	public void run() {
		packetToSend.getMessage().handleOutMessage(null);

	}

}
