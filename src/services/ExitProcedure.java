package services;

import java.net.ServerSocket;
import java.util.List;

import messages.AddPlayerMessage;
import messages.DeadMessage;
import messages.Message;
import messages.Packets;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.GameLock;
import singletons.InQueue;
import singletons.OutQueue;
import singletons.Peer;

/**
 * This class offers 2 public methods to be used to gracefully leave a game.
 * The boolean taken as argument states if the game is finished or not.
 * In case is finished no broadcast will be made.
 * This can only happen in case of a victory message. 
 */
public class ExitProcedure {
	
	ServerSocket srvSocket; // the main socket to close 
	

	public ExitProcedure() {
		
	}
	/**
	 * Allows a peer to gracefully leave the application.
	 * @param a boolean value to indicate if the game is finished
	 */
	public void startRegularProcedure(boolean isGameEnded){
		
		final Peer peer = Peer.getInstance();
		final InQueue inQueue = InQueue.getInstance();
		final OutQueue outQueue = OutQueue.getInstance();
		
		try {
			
			// first  close the main server socket 
			peer.closeServerSocket();
			
			// Empty inQueue. There should be no need to do that though 
			while (!inQueue.isEmpty()) {
				
				// retrieve message contained in the packet
				Packets inPacket = inQueue.poll();
				Message inMessage = inPacket.getMessage();
				
				// handle it
				inMessage.handleInMessage(inPacket.getSendingClient());
					
			}
			
			/* Empty outQueue. There could be lower priority messages
			 * Checks if the player is the only one in the game because
			 * in this case there is no need to empty the queue, in fact
			 * whenever a player is alone no messages are ever put in the outQueue 
			 */
			if(peer.getNumberOfPlayers() > 1){
				while (!outQueue.isEmpty()) {
					
					Packets outPacket = outQueue.poll();
					Message outMessage = outPacket.getMessage();
					
					// This is the only message to actually handle. All others can be ignored. 					
					if (outMessage instanceof AddPlayerMessage)
						outMessage.handleOutMessage(outPacket.getSendingClient());
				}
			}
				
			// This is the broadcast. It's not performed in case of victory of one of the players
			if (!isGameEnded){ 
				final List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
				
				// start broadcast if i'm not alone 
				if (otherPlayers.size() != 0) 
					new Broadcast(otherPlayers, new DeadMessage(peer.getCurrentPlayer())).broadcastMessage();
			
			}
			
			// tell REST server I'm out 
			new ServiceRequester().deletePlayerFromGame(peer.getCurrentGame().getName(), peer.getCurrentPlayer());
			
			// if I was alone the game is finished wake up main thread 
			if (peer.getNumberOfPlayers() == 1){
				final GameLock lock = GameLock.getInstance();
				synchronized (lock) {
					
					// wake main thread up to gracefully close the game 
					lock.notify();
				}
			}
			
		} catch (Exception e){
			System.err.println("Error during exit procedure!");
			e.printStackTrace();
		}
	}
	/**
	 * The procedure to call in case of victory. This avoids the broadcast of a dead message 
	 * to all other players of the game.
	 */
	public void startGameEndedProcedure(){
		this.startRegularProcedure(true);
	}
}
