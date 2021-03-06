package threads;

import messages.Packets;

/** 
 * This is a special thread used only when the player is alone in
 * the game. It's called from the standard in Thread 
 */
public class OnePlayerHandlerThread implements Runnable {
	
	private Packets packetToSend;
	
	public OnePlayerHandlerThread(final Packets p) {
		this.packetToSend = p;
	}
	
	/**
	 * handle the message given
	 */
	@Override
	public void run() {
		packetToSend.getMessage().handleOutMessage(null);
		
	}

}
