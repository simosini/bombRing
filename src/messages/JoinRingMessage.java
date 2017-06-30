package messages;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import beans.Player;
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
	public void handleInMessage(Socket sender) {
		try {
			
			final ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
			OutQueue outQueue = OutQueue.INSTANCE;
			/** if i'm dead i send a NackMessage*/
			if (!Peer.INSTANCE.isAlive()){
				System.out.println("Sending Nack Message");
				out.writeObject(new NackMessage());
			}
			/** if i'm alone no need to put the message on the outQueue */
			else if (Peer.INSTANCE.getCurrentGame().getPlayers().size() == 1){
				Peer.INSTANCE.addNewPlayer(this.getPlayer());
				System.out.println("Player added to the map");
				out.writeObject(new AckMessage());
				System.out.println("Ack Message sent");
				this.generateToken();
			}
			/** create a AddPlayer message and add it to the OutQueue*/
			else {
				System.out.println("Preparing Addplayer message");
				Message m = new AddPlayerMessage(this.getPlayer());
				m.setInput(false);
				Packets newPacket = new Packets(m,sender);
				synchronized (outQueue) {
					outQueue.add(newPacket);
				}				
				System.out.println("Message added to the outQueue");
			}
			/** cannot close stream otherwise i close the socket */
		}
		catch(IOException e){
			System.out.println("Error communicating with new adding player!");
		}

	}
	
	private void generateToken() {
		try {
			Socket cli = null;
			/** if i was alone i got to create the token and start the ring */
			if (Peer.INSTANCE.getCurrentGame().retrievePlayersNumber() == 2){
				List<Integer> ports = Peer.INSTANCE.extractPlayersPorts();
				System.out.println(ports);
				for (int port : ports) { /** fake for there only one player */
					cli = new Socket("localhost", port);
					ObjectOutputStream out = new ObjectOutputStream(cli.getOutputStream());
					/** send message */
					out.writeObject(new TokenMessage());
				}
				Thread.sleep(500);
				cli.close();
			}
		}
		catch(IOException | InterruptedException e){
			System.out.println("Error creating token");
		}
		
	}
	
	@Override
	public String toString(){
		return "This is a Join message";
	}

}
