package messages;

import java.io.IOException;
import java.io.ObjectOutputStream;
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

	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			
			final ObjectOutputStream out = clientConnection.getOutputStream();
			OutQueue outQueue = OutQueue.INSTANCE;
			Peer peer = Peer.INSTANCE;
			ConnectionData cd = null;
			
			/** if i'm dead i send a NackMessage */
			if (!peer.isAlive()){
				System.out.println("Sending Nack Message");
				out.writeObject(new NackMessage());
				return true;
			}
			
			/** if i'm alone no need to put the message on the outQueue */
			else if (peer.getCurrentGame().getPlayers().size() == 1){
				/** add player to the map */
				peer.addNewPlayer(this.getPlayer());
				
				/** connect to his server socket */
				cd = this.connectToPlayer(this.getPlayer());
				if (cd == null)
					System.exit(1);
				peer.addConnectedSocket(this.getPlayer().getId(), cd);
				
				/** send a copy of the updated map */
				System.out.println("Player added correctly");
				
				out.writeObject(new MapUpdateMessage(peer.getUserMap()));
				System.out.println("Map Message sent");
				
				/** start token (this happens only if i'm alone)*/
				this.generateToken(cd);
				return true;
			}
			
			/** otherwise create a AddPlayer message and add it to the OutQueue*/
			else {
				System.out.println("Preparing Addplayer message");
				Message m = new AddPlayerMessage(this.getPlayer());
				m.setInput(false);
				
				/**cd is now null cause i want to wait the addPlayer message
				 * handler to do that cause i'm not sure yet the new player
				 * will be added to the game */
				Packets newPacket = new Packets(m,cd);
				
				synchronized (outQueue) {
					outQueue.add(newPacket);
				}				
				System.out.println("Message added to the outQueue");
				return true;
			}
			
		}
		catch(IOException e){
			System.out.println("Error communicating with new adding player!");
			return false;
		}

	}
	
	private void generateToken(ConnectionData cd) {
		try {
			/** send message */
			cd.getOutputStream().writeObject(new TokenMessage());
		}
		catch(IOException e){
			System.out.println("Error creating token");
		}
		
	}
	/**This message is sent by a new player willing to join the game.
	 * Basically he creates a new JoinRing message, and call this method on it.s
	 * This means the connectionData passed id null cause is this method in 
	 * charge of handling this */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			/** retrieve players list */
			Peer peer = Peer.INSTANCE;
			 /** it's a copy of the map given by the rest server */
			Players players = peer.getCurrentGame().getPlayers();
			Player nextPeer = null;
			
			/** communicate with next peer server socket*/
			while(players.size() > 1) {
				nextPeer = peer.getNextPeer(players);
				
				/** connect to it */
				ConnectionData cd = null;
				if ((cd = this.connectToPlayer(nextPeer)) == null){
					System.out.println("Error connecting to server socket, trying another peer");
					players.deletePlayer(nextPeer);
				}
				
				else {
					/** create message and send it */
					cd.getOutputStream().writeObject(new JoinRingMessage(peer.getCurrentPlayer()));
					Message m = (Message) cd.getInputStream().readObject();
					
					/** check answer: can only be mapUpdate or nack */
					if (m instanceof MapUpdateMessage){
						m.handleInMessage(null);
						TreeMap<Integer, Player> updatedMap = ((MapUpdateMessage) m).getUpdatedMap();
						/** add cd to my connections */
						peer.addConnectedSocket(nextPeer.getId(), cd);
						/** connect to all player except myself and nextPeer*/
						updatedMap.remove(peer.getCurrentPlayer());
						updatedMap.remove(nextPeer);
						connectAll(updatedMap);	
						peer.setAlive(true);
						return true;
					}
					players.deletePlayer(nextPeer);
				}
				
			}
			System.out.println("Sorry it was not possible to join the selected game!");
			return false;
			
		} catch (Exception e){
			System.out.println("Could not handle JoinRing outgoing message");
			return false;
			
		}
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
