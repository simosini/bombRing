package messages;

import java.util.List;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import singletons.Peer;

/** this Message is sent broadcast to all other players to 
 *  inform them of a new player willing to join the game */

public class AddPlayerMessage extends Message {

	private static final long serialVersionUID = 4448703639215851985L;
	private static final int ADD_PRIORITY = 3;
	private Player playerToAdd;

	public AddPlayerMessage(Player p) {
		super(Type.ADDPLAYER, ADD_PRIORITY);
		this.setPlayerToAdd(p);
	}

	public Player getPlayerToAdd() {
		return playerToAdd;
	}

	public void setPlayerToAdd(Player playerToAdd) {
		this.playerToAdd = playerToAdd;
	}
	
	
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.INSTANCE;
			System.out.println("Handling message. Type: " + this);
			
			/** if i'm still alive i.e. no bomb killed me in the meantime */
			if (peer.isAlive()){
				
				/** retrieve connections to other serverSockets. The new player is not here yet */
				List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
				System.out.println("retrieved user sockets");
				this.setInput(true); /** becomes an in packet for the receivers */
				
				/** start broadcast */
				if (otherPlayers.size() != 0) /** check i'm not alone */
					new Broadcast(otherPlayers, this).broadcastMessage();
				
				System.out.println("Broadcast done");
				
				/** add the player to the map and connect to him */
				peer.addNewPlayer(this.getPlayerToAdd());
				System.out.println("Player added to the map!");
				
				ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
				if (cd ==null){
					System.err.println("Error connecting to socket");
					System.exit(1);
				}
				peer.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
				System.out.println("Connection added correctly");
				
				/** send MapUpdate Message */				
				new MapUpdateMessage(peer.getUserMap()).handleOutMessage(clientConnection);
				System.out.println("Updated map sent");
			}
			
			else {
				/** send nack message */
				System.out.println("I'm dead so sending nack");
				new NackMessage().handleOutMessage(clientConnection);
			}
			
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			/** add the player */
			Peer.INSTANCE.addNewPlayer(this.getPlayerToAdd());
			System.out.println("Player added to the map");
			
			/** connect to him */
			ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
			if (cd ==null){
				System.err.println("Error connecting to socket");
				System.exit(1);
			}
			Peer.INSTANCE.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
			System.out.println("Connection added correctly");
			
			/** send ack */
			new AckMessage().handleOutMessage(clientConnection);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "This is an Addplayer message";
	}

}
