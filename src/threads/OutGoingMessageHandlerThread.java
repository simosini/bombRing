package threads;

import beans.Player;
import messages.Packets;
import messages.TokenMessage;
import peer.ConnectionData;
import singletons.GameLock;
import singletons.OutQueue;
import singletons.Peer;

/** 
 * This Thread is in charge of retrieving the packet with highest priority
 * from the outQueue and handle it. It can only be called by the MessageHandlerThread
 * whenever it receives the token. So it's started only if the token is active. 
 */

public class OutGoingMessageHandlerThread implements Runnable {
	
	private TokenMessage token;
	

	public OutGoingMessageHandlerThread(final TokenMessage token) {
		this.setToken(token);
	}

	private TokenMessage getToken() {
		return token;
	}

	private void setToken(TokenMessage token) {
		this.token = token;
	}
	
	/**
	 * handles packets of the out Queue
	 */
	@Override
	public void run() {
		final OutQueue outQueue = OutQueue.getInstance();
		final Peer peer = Peer.getInstance();
		
		try {
			// get first message out the outQueue and handle it 
			if (!outQueue.isEmpty()){
					
				final Packets packet = outQueue.poll();
				packet.getMessage().handleOutMessage(packet.getSendingClient());
					
			}
		
			// Pass the token now 			
			final Player nextPeer = peer.getNextPeer(peer.getCurrentGame().getPlayers());
			
			//it's null only if i'm alone in the game
			if (nextPeer != null){ 
				final ConnectionData peerConnection = peer.getClientConnectionById(nextPeer.getId());
				this.getToken().handleOutMessage(peerConnection);
			}
			
			// After passing the token if i'm dead exit 
			if (!peer.isAlive()) {	
				final GameLock lock = GameLock.getInstance();
				synchronized (lock) {
					// wake up main to end the game
					lock.notify();
				}
			}
			
		} catch (Exception e){
			System.err.println("Error handling outgoing message");
			e.printStackTrace();
		}
	}

}
