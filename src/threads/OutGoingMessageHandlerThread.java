package threads;

import beans.Player;
import messages.Packets;
import messages.TokenMessage;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;

/** 
 * This Thread is in charge of retrieving the packet with highest priority
 * from the outQueue and handle it. It can only be called by the MessageHandlerThread
 * whenever it receives a token */

public class OutGoingMessageHandlerThread implements Runnable {
	
	private TokenMessage token;
	

	public OutGoingMessageHandlerThread(TokenMessage token) {
		this.setToken(token);
	}

	private TokenMessage getToken() {
		return token;
	}

	private void setToken(TokenMessage token) {
		this.token = token;
	}

	@Override
	public void run() {
		OutQueue outQueue = OutQueue.INSTANCE;
		try {
			/** get first message out the outQueue and handle it */
			synchronized (outQueue) {
				if (!outQueue.isEmpty()){
					
					Packets packet = outQueue.poll();
					System.out.println("OutGoingHandler got packet from outQueue");
					packet.getMessage().handleOutMessage(packet.getSendingClient());
					
				}
			}
			
			/** Pass the token now */			
			Player nextPeer = Peer.INSTANCE.getNextPeer(Peer.INSTANCE.getCurrentGame().getPlayers());
			if (nextPeer != null){ //it's null only if i'm alone in the game
				ConnectionData peerConnection = Peer.INSTANCE.getClientConnectionById(nextPeer.getId());
				System.out.println("OutgoingHandler done, passing the token to port " + peerConnection.getClientSocket().getPort());
				this.getToken().handleOutMessage(peerConnection);
			}
			
		} catch (Exception e){
			System.out.println("Error handling outgoing message");
		}
	}

}
