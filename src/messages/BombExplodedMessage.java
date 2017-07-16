package messages;

import java.util.List;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.OutQueue;
import singletons.Peer;

/**
 * When this message is sent it tells everyone that the bomb has exploded. Upon
 * receipt a peer must check if the bomb actually cause its death and behaves
 * accordingly.
 */
public class BombExplodedMessage extends Message {

	private static final long serialVersionUID = 493773639985449364L;
	private static final int BOMB_EXPLODED_PRIORITY = 3;
	private Player tosser;
	private String color;

	public BombExplodedMessage(String color, Player tosser) {
		super(Type.BOMB_EXPLODED, BOMB_EXPLODED_PRIORITY);
		this.setColor(color);
		this.setTosser(tosser);
	}

	/**
	 * setters and getters
	 */
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Player getTosser() {
		return tosser;
	}

	private void setTosser(final Player tosser) {
		this.tosser = tosser;
	}

	/**
	 * If received a peer must check if it's dead or still alive
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();
			final OutQueue outQueue = OutQueue.getInstance();
			final Emoji emoji = EmojiManager.getForAlias("see_no_evil");
			final String myColorZone = peer.getCurrentPosition().getColorZone();
			final String bombColor = this.getColor();

			// check position and if i'm still alive
			if (peer.isAlive() && bombColor.equalsIgnoreCase(myColorZone)) {

				System.out.println(emoji.getUnicode() + " Shame... A bomb tossed by " + this.getTosser().getNickname()
						+ " just killed you! " + emoji.getUnicode());

				// build killed message and call handler
				final Player myself = peer.getCurrentPlayer();
				final KilledMessage km = new KilledMessage(myself, this.getTosser(), true);
				km.handleOutMessage(clientConnection);

				// create dead message
				final Packets packet = new Packets(new DeadMessage(myself), null);

				// put message on the outQueue
				outQueue.add(packet);

			} else { // just send ack
				final Emoji em = EmojiManager.getForAlias("v");
				
				// if another bomb killed me no need to print this message
				if (peer.isAlive()) {
					System.out.println(em.getUnicode() + " A bomb has exploded in the " + this.getColor()
							+ " zone. Thank God you are safe and sound!! " + em.getUnicode());
				}
				new AckMessage().handleOutMessage(clientConnection);

			}

		} catch (Exception e) {
			System.err.println("Error with incoming position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Send this message to all other player to inform the bomb has indeed
	 * exploded. Before broadcasting that, check the bomb did not killed me.
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			final Peer peer = Peer.getInstance();
			final Emoji emoji = EmojiManager.getForAlias("bomb");

			/* if i'm dead before the bomb count down finished thr bomb dies with me
			 * i.e. it does not explode
			 */
			if (peer.isAlive()) {
				System.out.println(emoji.getUnicode() + " Bomb exploded in the " + this.getColor()
						+ " zone. Informing other players...");

				// retrieve connections for the broadcast
				final List<ConnectionData> clientConnections = peer.getClientConnectionsList();

				// now check the bomb did not killed me. Needed in case i killed someone else
				if (this.getColor().equalsIgnoreCase(peer.getCurrentPosition().getColorZone()))
					peer.setAlive(false);

				// broadcast bomb exploded message if not alone
				if (clientConnections.size() != 0) 
					new Broadcast(clientConnections, this).broadcastMessage();

				System.out.println("done!");

				// this happens if the bomb killed me
				if (!peer.isAlive()) {
					System.out.println("Congratulations!! You just committed suicide!");
					Thread.sleep(2000); // a little retard for debugging

					// now die gracefully
					new ExitProcedure().startRegularProcedure(false);
				}

			}
		} catch (Exception e) {
			System.err.println("Error with outgoing position message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "This is a Bomb exploded message";
	}

}
