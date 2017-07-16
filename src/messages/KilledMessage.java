package messages;

import java.util.List;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import beans.Player;
import peer.Broadcast;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.Peer;

/**
 * This message is sent to a peer to inform that it killed the current player.
 * This could either be caused by a bomb exploded or a position overlapping. The
 * receiving peer must check it's still alive in order to collect points.
 */
public class KilledMessage extends Message {

	private static final long serialVersionUID = 184832401348782267L;
	private static final int KILLED_PRIORITY = 5;
	private Player killedPlayer;
	private Player killer;
	private boolean killedByBomb;

	public KilledMessage() {
	}

	public KilledMessage(Player killed, Player killer, boolean killedByBomb) {
		super(Type.KILLED, KILLED_PRIORITY);
		this.setKilledPlayer(killed);
		this.setKiller(killer);
		this.setKilledByBomb(killedByBomb);

	}

	public Player getKilledPlayer() {
		return killedPlayer;

	}

	public void setKilledPlayer(Player killedPlayer) {
		this.killedPlayer = killedPlayer;

	}

	public Player getKiller() {
		return killer;
	}

	private void setKiller(Player killer) {
		this.killer = killer;
	}
	
	private void setKilledByBomb(boolean killedByBomb) {
		this.killedByBomb = killedByBomb;
	}

	/**
	 * I receive this if I killed someone else
	 */
	@Override
	public boolean handleInMessage(ConnectionData cd) {
		try {
			final Peer peer = Peer.getInstance();
			final int targetScore = peer.getCurrentGame().getScoreNeeded();
			final Emoji emoji = EmojiManager.getForAlias("v");

			System.out.println("You just killed " + this.getKilledPlayer().getNickname());

			// add points if i'm alive and check victory
			if (peer.isAlive() && peer.getCurrentScore() < targetScore) {

				// set new score
				peer.incrementCurrentScore();

				// check victory
				if (peer.getCurrentScore() == targetScore) {
					System.out.println(
							emoji.getUnicode() + " Congratulations! You just won the game!! " + emoji.getUnicode());

					// game is finished set alive false
					peer.setAlive(false);

					// retrieve connections to other serverSockets
					final List<ConnectionData> otherPlayers = peer.getClientConnectionsList();

					// waits for position broadcast to be done
					Thread.sleep(500);

					// tell every player the game is finished. I cannot be
					// alone.
					new Broadcast(otherPlayers, new VictoryMessage(peer.getCurrentPlayer())).broadcastMessage();

					// everybody else is dead so exit the game
					new ExitProcedure().startGameEndedProcedure();
					System.out.println("The game is over. Goodbye");
					System.exit(0);

				}

			}

		} catch (Exception e) {
			System.err.println("Error handling incoming killed message");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Tell the sender peer he just killed me
	 */
	@Override
	public boolean handleOutMessage(ConnectionData cd) {
		try {
			final Emoji emoji = EmojiManager.getForAlias("see_no_evil");
			if(!this.killedByBomb)
				System.out.println(emoji.getUnicode() + " Unfortunately " + this.getKiller().getNickname()
					+ " has just killed you! " + emoji.getUnicode());
			Peer.getInstance().setAlive(false); // i'm dead

			// send killed message
			cd.getOutputStream().writeObject(this);

		} catch (Exception e) {
			System.err.println("Error handling killedMessage out");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "This is a Killed message";
	}

}
