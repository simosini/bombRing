package messages;

import java.util.TreeMap;

import beans.Player;
import beans.Players;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;

/**
 * this packet is sent by a new player who wants to  be added to a current active game 
 * This means that it can only be a incoming packet, in fact
 * once received to the message is put by the handler in the outQueue as a 
 * addPlayer packet. The behavior of the sender is :
 * 1- send the packet
 * 2- block until an ack is received
 * 3- starts playing
 * In case of problems of adding the player to the ring a Nack message is sent, in
 * which case the sender tries to contact a different player. If no player left
 * the new player is sent back to the previous menu to choose a different game. 
 * */
public class JoinRingMessage extends Message {

	private static final long serialVersionUID = -7568751517974737661L;
	private static final int JOIN_PRIORITY = 5;
	private Player player; /** the player to add to the ring */

	
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
	
	/** this message is received from a player who wants to join the game */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			System.out.println("InHandling started!!");
			OutQueue outQueue = OutQueue.INSTANCE;
			Peer peer = Peer.INSTANCE;
			ConnectionData cd = null;
			
			/** if i'm dead i send a NackMessage */
			if (!peer.isAlive()){
				System.out.println("Sending Nack Message");
				new NackMessage().handleOutMessage(clientConnection);
			}
			
			/** if i'm alone no need to put the message on the outQueue */
			else if (peer.getCurrentGame().getPlayers().size() == 1){
				/** add player to the map */
				System.out.println("adding player to map");
				System.out.println(this.getPlayer());
				peer.addNewPlayer(this.getPlayer());
				
				System.out.println("Connecting to new player server socket");
				/** connect to his server socket */
				cd = this.connectToPlayer(this.getPlayer());
				if (cd == null){
					System.exit(1);
				}
				System.out.println("Adding player connection");	
				peer.addConnectedSocket(this.getPlayer().getId(), cd);
				
				/** send a copy of the updated map */
				System.out.println("Player added correctly");
				
				new MapUpdateMessage(peer.getUserMap()).handleOutMessage(clientConnection);
				System.out.println("Map Message sent");
				
				/** start token (this happens only if i'm alone)*/
				System.out.println("generating token");
				this.generateToken(cd);
			}
			
			/** otherwise create a AddPlayer message and add it to the OutQueue*/
			else {
				System.out.println("Preparing Addplayer message");
				Message m = new AddPlayerMessage(this.getPlayer());
				m.setInput(false);
				
				/**cd is now null cause i want to wait the addPlayer message
				 * handler to do that cause i'm not sure yet the new player
				 * will be added to the game. AddPlayer handler will add
				 * the player and connect to him if everything go smooth */
				Packets newPacket = new Packets(m,clientConnection);
				
				synchronized (outQueue) {
					outQueue.add(newPacket);
				}				
				System.out.println("Message added to the outQueue");
			}
			
		}
		catch(Exception e){
			System.err.println("Error communicating with new adding player!");
			return false;
		}
		return true;
	}
	
	private void generateToken(ConnectionData cd) {
		/** send message */
		new TokenMessage().handleOutMessage(cd);
	}
		
	
	/**This message is sent by a new player willing to join the game.
	 * Basically he creates a new JoinRing message, and call this method on it.
	 * This means the connectionData passed is null cause is this method in 
	 * charge of handling this */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			System.out.println("Start handling ---- " + this);
			/** retrieve players list */
			Peer peer = Peer.INSTANCE;
			

			 /** it's a copy of the map given by the rest server */
			Players players = peer.getCurrentGame().getPlayers();
			Player nextPeer = null;
			System.out.println("Current players: " + players);
			
			/** communicate with next peer server socket*/
			while(players.size() > 1) {
				nextPeer = peer.getNextPeer(players);
				System.out.println("next player " + nextPeer);
				
				/** connect to it */
				ConnectionData cd = null;
				System.out.println("Connecting to him");
				if ((cd = this.connectToPlayer(nextPeer)) == null){
					System.out.println("Error connecting to server socket, trying another peer");
					players.deletePlayer(nextPeer);
				}
				
				else {
					/** create message and send it */
					System.out.println("sending message to port " + nextPeer.getPort());
					cd.getOutputStream().writeObject(new JoinRingMessage(peer.getCurrentPlayer()));
					System.out.println("message sent!");
					
					/** wait for the answer */
					Message m = (Message) cd.getInputStream().readObject();
					System.out.println("received answer :" + m);
					
					/** check answer: can only be mapUpdate or nack */
					if (m instanceof MapUpdateMessage){
						m.handleInMessage(null);
						TreeMap<Integer, Player> updatedMap = ((MapUpdateMessage) m).getUpdatedMap();
						
						/** add cd to my connections */
						peer.addConnectedSocket(nextPeer.getId(), cd);
						System.out.println("new connection added");
						
						/** connect to all player except myself and nextPeer */
						updatedMap.remove(peer.getCurrentPlayer().getId());
						updatedMap.remove(nextPeer.getId());
						connectAll(updatedMap);	
						System.out.println("Connected to all players");
						
						/** new Player is in the game */
						System.out.println("Im in the game!!!");
						peer.setAlive(true);
						return true;
					}
					
					/** in case of problems try next player */
					players.deletePlayer(nextPeer);
				}
				
			}
			
			/** I get here if there's no other player available */
			System.out.println("Sorry it was not possible to join the selected game!");
			
			
		} catch (Exception e){
			System.out.println("Could not handle JoinRing outgoing message");
			e.printStackTrace();
						
		}
		return false;
	}
	
	private void connectAll(TreeMap<Integer, Player> updatedMap) {
		for (Player p : updatedMap.values()){
			ConnectionData cd = this.connectToPlayer(p);
			if (cd != null)
				Peer.INSTANCE.addConnectedSocket(p.getId(), cd);
		}
		
	}

	@Override
	public String toString(){
		return "This is a Join message";
	}

	

}
