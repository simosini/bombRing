package messages;

import java.util.List;

import beans.Player;
import beans.Players;
import peer.Broadcast;
import peer.Cell;
import peer.ConnectionData;
import singletons.Peer;
import singletons.PositionList;

/** 
 * This Message is sent broadcast to all other players to 
 * inform them of a new player willing to join the game. 
 */

public class AddPlayerMessage extends Message {

	private static final long serialVersionUID = 4448703639215851985L;
	private static final int ADD_PRIORITY = 3;
	private Player playerToAdd;
	
	public AddPlayerMessage() { }

	public AddPlayerMessage(Player p) {
		super(Type.ADDPLAYER, ADD_PRIORITY);
		this.setPlayerToAdd(p);
	}

	public Player getPlayerToAdd() {
		return playerToAdd;
	}
	
	/** 
	 * only called by the constructor 
	 */
	private void setPlayerToAdd(Player playerToAdd) {
		this.playerToAdd = playerToAdd;
	}
	
	/** 
	 * If the current peer it's alive broadcasts other players informing of a new user.
	 * Otherwise tells the new player it cannot fulfill the request
	 * sending him a Nack message. 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();
						
			// if i'm still alive i.e. no bomb killed me in the meantime 
			if (peer.isAlive()){
				
				// retrieve connections to other serverSockets. The new player is not here yet 
				final List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
								
				/* 
				 * Clear PositionList and add current player. 
				 * The list could contain positions of a previous insertion.
				 */ 
				PositionList.getInstance().clearList();
				PositionList.getInstance().addCell(new Cell(peer.getCurrentPosition()));
				
				// now start broadcast if i'm not the only player of the game
				if (otherPlayers.size() != 0) {
					
					new Broadcast(otherPlayers, this).broadcastMessage();
				}
				
				// compute a free position to assign 
				final Cell newCell = PositionList.getInstance().computeNewPosition();
				
				// add the player to the map and connect to him 
				peer.addNewPlayer(this.getPlayerToAdd());
				
				final ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
				if (cd ==null){
					System.err.println("Error connecting to socket");
					System.exit(1);
				}
				
				peer.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
				
				// send MapUpdate Message with updated map and new position				
				new MapUpdateMessage(peer.getUserMap(), newCell).handleOutMessage(clientConnection);
			}
			
			else {
				
				// in case i'm dead send nack message with a copy of the updated map 
				final Players players = peer.getCurrentGame().getPlayers();
				players.addPlayer(this.getPlayerToAdd());
				new NackMessage(players).handleOutMessage(clientConnection);
			}
			
		}
		catch (Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * When received the peer adds the new player to its map and tries to
	 * connect to its server socket. Eventually send back an ack.
	 **/
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();
			
			// add the player to the map 
			peer.addNewPlayer(this.getPlayerToAdd());
			
			// connect to its server socket 
			final ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
			if (cd == null){
				System.err.println("Error connecting to socket");
				System.exit(1);
			}
			
			// add the socket to the list of open sockets
			peer.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
			
			// send back ack 
			new AckPosition(peer.getCurrentPosition()).handleOutMessage(clientConnection);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "This is an Addplayer message";
	}

}
