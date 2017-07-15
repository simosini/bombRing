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
				//System.out.println("Consuming inQueue");
				Packets inPacket = inQueue.poll();
				Message inMessage = inPacket.getMessage();
				//System.out.println("-----HANDLING-----");
				//System.out.println(inMessage);
				
				inMessage.handleInMessage(inPacket.getSendingClient());
					
			}
			
			/* Empty outQueue. There could be lower priority messages
			 * Checks if the player is the only one in the game because
			 * in this case there is no need to empty the queue, in fact
			 * whenever a player is alone no messages are ever put in the outQueue 
			 */
			if(peer.getNumberOfPlayers() > 1){
				while (!outQueue.isEmpty()) {
					//System.out.println("Consuming outQueue");
					Packets outPacket = outQueue.poll();
					Message outMessage = outPacket.getMessage();
					
					//System.out.println("-----HANDLING-----");
					//System.out.println(outMessage);
				
					// This is the only message to actually handle. All others can be ignored. 					
					if (outMessage instanceof AddPlayerMessage)
						outMessage.handleOutMessage(outPacket.getSendingClient());
				}
			}
				
			// This is the broadcast. It's not performed in case of victory of one of the players
			if (!isGameEnded){ 
				List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
				//System.out.println("Number of open connections: " + otherPlayers.size());
				//System.out.println("retrieved user sockets");
			
				// start broadcast 
				if (otherPlayers.size() != 0) /** check i'm not alone */
					new Broadcast(otherPlayers, new DeadMessage(peer.getCurrentPlayer())).broadcastMessage();
			
				//System.out.println("Broadcast done");
			}
			
			// tell REST server I'm out 
			//System.out.println("Asking the rest server to delete me from game");
			new ServiceRequester().deletePlayerFromGame(peer.getCurrentGame().getName(), peer.getCurrentPlayer());
			
			// if I was alone the game is finished 
			if (peer.getNumberOfPlayers() == 1){
				GameLock lock = GameLock.getInstance();
				//System.out.println("Exit procedure done!");
				synchronized (lock) {
					// wake main up to gracefully close the game 
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
