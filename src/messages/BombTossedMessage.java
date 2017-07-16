package messages;

import java.util.List;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import peer.Broadcast;
import peer.ConnectionData;
import singletons.OutQueue;
import singletons.Peer;
import threads.BombExplodingThread;

/** 
 * This message  informs all other players that a bomb has just been tossed in
 * a particular area. Once received a peer has 5 seconds to sneak away from there.
 * The behavior of the sender is to broadcast the message, wait for all acks to arrive
 * and finally start the thread in charge of exploding the bomb after 5 seconds. 
 */
public class BombTossedMessage extends Message {

	private static final long serialVersionUID = -8610892414931963536L;
	private static final int BOMB_TOSSED_PRIORITY = 4;
	private String color;


	public BombTossedMessage(String color) {
		super(Type.BOMB_TOSSED, BOMB_TOSSED_PRIORITY);
		this.setColor(color);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	/** 
	 * print message and send back ack of correct reception 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			final Emoji bomb = EmojiManager.getForAlias("bomb");
			System.out.println(bomb.getUnicode() + " A bomb has been tossed in the " + this.color + " zone. You only got 5 seconds to escape!");
			System.out.println("You are currently in the " + Peer.getInstance().getCurrentPosition().getColorZone() + " zone.");
			new AckMessage().handleOutMessage(clientConnection);
		} catch (Exception e) {
			System.err.println("Error handling incoming bomb tossed message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * tell everyone about the bomb and then start exploding bomb thread 
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();
			
			// if dead cannot toss any bomb
			if (peer.isAlive()){
				final OutQueue outQueue = OutQueue.getInstance();
				
				// retrieve connections for the broadcast 
				final List<ConnectionData> clientConnections = peer.getClientConnectionsList();
								
				// broadcast message if not alone
				if (clientConnections.size() != 0) 
					new Broadcast(clientConnections, this).broadcastMessage();
				
				// start bomb exploding thread				
				new Thread(new BombExplodingThread(this.getColor())).start();
				
				// notify the standard in I've finished handling the message 			
				if (peer.getNumberOfPlayers() > 1){
					synchronized (outQueue) {
						outQueue.notify();
					}
				}
				
			}
		} catch (Exception e){
			System.err.println("Error with outgoing bomb tossed message");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is a Bomb tossed message";
	}

}

