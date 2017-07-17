package messages;

import java.util.TreeMap;

import beans.Player;
import beans.Players;
import peer.Cell;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;
import singletons.PositionList;

/**
 * This message is sent by a new player who wants to be added to a current active game. 
 * Once received the message is put by the handler in the outQueue as a addPlayer packet. 
 * The behavior of the sender is :
 * 1- sends the packet
 * 2- blocks until an ack is received
 * 3- starts playing
 * In case of problems of adding the player to the ring a Nack message is sent, in
 * which case the sender tries to contact a different player. If no player left
 * the new player is sent back to the previous menu to choose a different game. 
 */
public class JoinRingMessage extends Message {

	private static final long serialVersionUID = -7568751517974737661L;
	private static final int JOIN_PRIORITY = 5;
	private Player player; // the player to add to the ring 

	public JoinRingMessage(){ }
	
	public JoinRingMessage(Player p) {
		super(Type.JOINRING, JOIN_PRIORITY);
		this.setPlayer(p);
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/** 
	 * this message is received from a player who wants to join the game.
	 * The behavior of the receiver is as follows:
	 * 1- if it's alone (and alive) it just add the new player to the ring and starts the token
	 * 2- otherwise it builds a new AddPlayer message and add it to the outQueue waiting for the
	 * 	  token to handle it. 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			
			final OutQueue outQueue = OutQueue.getInstance();
			final Peer peer = Peer.getInstance();
			ConnectionData cd = null;
			
			// if i'm dead i send a NackMessage with an up to date copy of the map 
			if (!peer.isAlive()){
				Players players = peer.getCurrentGame().getPlayers();
				players.addPlayer(this.getPlayer());
				new NackMessage(players).handleOutMessage(clientConnection);
			}
			
			// if i'm alone no need to put the message on the outQueue 
			else if (peer.getNumberOfPlayers() == 1){
				
				// add player to the map 
				peer.addNewPlayer(this.getPlayer());
				
				// connect to his server socket 
				cd = this.connectToPlayer(this.getPlayer());
				if (cd == null){
					System.exit(1);
				}
				
				peer.addConnectedSocket(this.getPlayer().getId(), cd);
	
				// clear list in case of previous use. 
				PositionList.getInstance().clearList();
				
				// add my position
				PositionList.getInstance().addCell(peer.getCurrentPosition());
				
				// compute position for the new player
				Cell newPosition = PositionList.getInstance().computeNewPosition();
				
				// send a copy of the updated map and position 
				new MapUpdateMessage(peer.getUserMap(), newPosition).handleOutMessage(clientConnection);
				
				// start token (this happens only if i'm alone)
				this.generateToken(cd);
			}
			
			// otherwise create a AddPlayer message and add it to the OutQueue
			else {
				
				final Message m = new AddPlayerMessage(this.getPlayer());
								
				/*
				 * wait to connect to this player cause I want the addPlayer message
				 * handler to do that. In fact I'm not sure yet the new player
				 * will be added to the game. AddPlayer handler will add
				 * the player and connect to him if everything go smooth 
				 */
				
				final Packets newPacket = new Packets(m, clientConnection);
				
				// put packet on the outQueue 
				outQueue.add(newPacket);
			}
			
		}
		catch(Exception e){
			System.err.println("Error communicating with new adding player!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void generateToken(ConnectionData cd) {
		
		// send token 
		new TokenMessage().handleOutMessage(cd);
	}
		
	
	/**
	 * This message is sent by a new player willing to join the game.
	 * Basically he creates a new JoinRing message, and call this method on it.
	 * This means the connectionData passed is null cause is this method in 
	 * charge of handling this 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();			

			// it's a copy of the map given by the rest server
			Players players = peer.getCurrentGame().getPlayers();
			
			Player nextPeer = null;
			
			// communicate with next peer server socket
			while(players.size() > 1) {
				
				nextPeer = peer.getNextPeer(players);
				
				// connect to it 
				ConnectionData cd = null;
				if ((cd = this.connectToPlayer(nextPeer)) == null){
					System.out.println("Trying another peer...");
					players.deletePlayer(nextPeer);
				}
				
				else {
					
					// create message and send it 
					cd.getOutputStream().writeBytes(createJsonMessage(new JoinRingMessage(peer.getCurrentPlayer())) + "\n");
					
					// wait for the answer 
					final Message answer = readJsonMessage(cd.getInputStream().readLine());					
					
					// check answer: can only be mapUpdate or Nack 
					if (answer instanceof MapUpdateMessage){
						answer.handleInMessage(null);
						TreeMap<Integer, Player> updatedMap = ((MapUpdateMessage) answer).getUpdatedMap();
						
						// add cd to my connections 
						peer.addConnectedSocket(nextPeer.getId(), cd);
						
						// connect to all player except myself and nextPeer 
						updatedMap.remove(peer.getCurrentPlayer().getId());
						updatedMap.remove(nextPeer.getId());
						connectAll(updatedMap);	
						
						// new player is now in the game 
						System.out.println(nextPeer.getNickname() + " correctly inserted you in the ring");
						peer.setAlive(true);
						return true;
					}
					
					//in case of problems update current map and try next player 
					players = ((NackMessage) answer).getPlayers();
					players.deletePlayer(nextPeer);
				}
				
			}
			
			// I get here only if there's no other player available 
			System.out.println("Sorry it was not possible to join the selected game!");
			
			
		} catch (Exception e){
			System.err.println("Could not handle JoinRing outgoing message");
			e.printStackTrace();
						
		}
		return false;
	}
	
	/**
	 * This method opens a connections to all server socket of the 
	 * players in the map passed as argument.
	 */
	private void connectAll(TreeMap<Integer, Player> updatedMap) {
		for (Player p : updatedMap.values()){
			final ConnectionData cd = this.connectToPlayer(p);
			if (cd != null)
				Peer.getInstance().addConnectedSocket(p.getId(), cd);
		}
		
	}

	@Override
	public String toString(){
		return "This is a Join message";
	}

	

}
