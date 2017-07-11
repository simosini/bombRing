package messages;

import java.util.List;

import beans.Player;
import beans.Players;
import peer.Broadcast;
import peer.Cell;
import peer.ConnectionData;
import singletons.Peer;
import singletons.PositionList;

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
	
	/** broadcast to other players informing of a new user */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.getInstance();
			//System.out.println("Handling addPlayer out");
			
			/** if i'm still alive i.e. no bomb killed me in the meantime */
			if (peer.isAlive()){
				
				/** retrieve connections to other serverSockets. The new player is not here yet */
				List<ConnectionData> otherPlayers  = peer.getClientConnectionsList();
				//System.out.println("retrieved user sockets");
								
				/** clear PositionList and add current player*/
				PositionList.getInstance().clearList();
				PositionList.getInstance().addCell(new Cell(peer.getCurrentPosition()));
				//System.out.println("Initialized positions list (only mine): " + PositionList.getInstance().getPlayerPositions());
				
				/** start broadcast */
				if (otherPlayers.size() != 0) /** check i'm not alone */
					new Broadcast(otherPlayers, this).broadcastMessage();
				
				//System.out.println("Broadcast done");
				
				/** compute a free position to assign */
				//System.out.println("Busy positions: " + PositionList.getInstance().getPlayerPositions());
				Cell newCell = PositionList.getInstance().computeNewPosition();
				//System.out.println("New position assigned: " + newCell);
				
				/** add the player to the map and connect to him */
				peer.addNewPlayer(this.getPlayerToAdd());
				//System.out.println("Player added to the map!");
				
				ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
				if (cd ==null){
					System.err.println("Error connecting to socket");
					System.exit(1);
				}
				peer.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
				//System.out.println("Connection added correctly");
				
				/** send MapUpdate Message */				
				new MapUpdateMessage(peer.getUserMap(), newCell).handleOutMessage(clientConnection);
				//System.out.println("Updated map sent");
			}
			
			else {
				/** send nack message with a copy of the updated map */
				//System.out.println("I'm dead so sending nack");
				Players players = peer.getCurrentGame().getPlayers();
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

	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			Peer peer = Peer.getInstance();
			/** add the player */
			peer.addNewPlayer(this.getPlayerToAdd());
			//System.out.println("Player added to the map");
			
			/** connect to him */
			ConnectionData cd = this.connectToPlayer(this.getPlayerToAdd());
			if (cd == null){
				System.err.println("Error connecting to socket");
				System.exit(1);
			}
			
			peer.addConnectedSocket(this.getPlayerToAdd().getId(), cd);
			//System.out.println("Connection added correctly");
			
			/** send ack */
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
