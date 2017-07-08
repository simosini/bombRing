package services;

import java.net.ServerSocket;
import java.util.List;

import messages.AddPlayerMessage;
import messages.DeadMessage;
import messages.Message;
import messages.Packets;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.InQueue;
import singletons.OutQueue;
import singletons.Peer;

public class ExitProcedure {
	
	ServerSocket srvSocket; /** the main socket to close */
	

	public ExitProcedure() {
		
	}
	
	public void startRegularProcedure(boolean isGameEnded){
		System.out.println("Starting exit procedure");
		Peer peer = Peer.INSTANCE;
		InQueue inQueue = InQueue.INSTANCE;
		OutQueue outQueue = OutQueue.INSTANCE;
		
		try {
			/** close server socket */
			System.out.println("Closing main server socket");
			peer.closeServerSocket();
			
			/** Empty inQueue. There should be no need to do that though */
			while (!inQueue.isEmpty()) {
				System.out.println("Consuming inQueue");
				Packets inPacket = inQueue.poll();
				Message inMessage = inPacket.getMessage();
				System.out.println("-----HANDLING-----");
				System.out.println(inMessage);
				
				inMessage.handleInMessage(inPacket.getSendingClient());
					
			}
			
			/** Empty outQueue. There could be lower priority messages */
			if(peer.getNumberOfPlayers() > 1){
				while (!outQueue.isEmpty()) {
					System.out.println("Consuming outQueue");
					Packets outPacket = outQueue.poll();
					Message outMessage = outPacket.getMessage();
					
					System.out.println("-----HANDLING-----");
					System.out.println(outMessage);
				
					/** This is the only message to actually handle. All others
					 *  can be ignored. */
					if (outMessage instanceof AddPlayerMessage)
						outMessage.handleOutMessage(outPacket.getSendingClient());
				}
			}
				
			/** Tell everyone else i'm dead if needed */
			if (!isGameEnded){ // do it only in case of ExitMessage
				List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
				System.out.println("Number of open connections: " + otherPlayers.size());
				System.out.println("retrieved user sockets");
			
				/** start broadcast */
				if (otherPlayers.size() != 0) /** check i'm not alone */
					new Broadcast(otherPlayers, new DeadMessage(peer.getCurrentPlayer())).broadcastMessage();
			
				System.out.println("Broadcast done");
			}
			/** tell REST server I'm out */
			System.out.println("Asking the rest server to delete me from game");
			new ServiceRequester().deletePlayerFromGame(peer.getCurrentGame().getName(), peer.getCurrentPlayer());
			
			/** if I was alone the game is finished */
			if (Peer.INSTANCE.getNumberOfPlayers() == 1){
				System.out.println("Exit procedure done!");
				System.exit(0);
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void startGameEndedProcedure(){
		this.startRegularProcedure(true);
	}
}
