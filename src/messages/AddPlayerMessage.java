package messages;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import beans.Player;
import peer.Broadcast;
import singletons.Peer;

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
	public void handleInMessage(Socket sender) {
		
		try {
			if (this.checkIsInput()) /** it's an input packet */
				handleInMessage(sender);
			
			else  /** it's an output position packet */
				handleOutMessage(sender);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	private void handleOutMessage(Socket sender) {
		try {
			System.out.println("Handling message. Type: " + this);
			List<Integer> userPorts = Peer.INSTANCE.extractPlayersPorts();
			System.out.println("retrieved user ports");
			this.setInput(true); /** becomes an in packet for the receiver */
			
			if (userPorts.size() != 0) /** check i'm not alone */
				new Broadcast(userPorts, this).broadcastMessage();
			
			System.out.println("Broadcast done");
			final ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
			/** add the player to the map and send him back Ack if still alive */
			if (Peer.INSTANCE.isAlive()){
				Peer.INSTANCE.addNewPlayer(this.getPlayerToAdd());
				System.out.println("Player added to the map");
				out.writeObject(new AckMessage());
				System.out.println("Ack Message sent");
			}
			else{
				System.out.println("I'm dead so sending nack");
				out.writeObject(new Packets(new NackMessage(), null));
			}
			
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}


	private void handleInMessage(Socket sender) {
		try {
			/** add the player and send ack */
			Peer.INSTANCE.addNewPlayer(this.getPlayerToAdd());
			System.out.println("Player added to the map");
			Packets newPacket = new Packets(new AckMessage(), null);
			final ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
			out.writeObject(newPacket);
			System.out.println("Ack Message sent");
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String toString(){
		return "This is an Addplayer message";
	}

}
