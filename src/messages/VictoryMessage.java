package messages;

import beans.Player;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.GameLock;
import singletons.Peer;

/** 
 * This is the message to inform that the match has finished.
 * This message will be sent immediately, without passing the token forward.
 * When a player receives a Victory message on the server socket, it leaves
 * the game immediately and the exit procedure is started.
 * The exit procedure in this case is different from the regular one,
 * cause I don't need to inform other players i'm dead.  
 */
public class VictoryMessage extends Message {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 1;
	private Player winner;
	
	public VictoryMessage(Player winner) {
		super(Type.VICTORY, VICTORY_PRIORITY);
		this.winner = winner;
	}
	
	/** 
	 * When the server socket receives this message knows that the match is finished
	 * so it just sends an AckMessage and starts the exit procedure. 
	 */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			// the game is finished 
			Peer.getInstance().setAlive(false);
			
			// send ackMessage 
			new AckMessage().handleOutMessage(clientConnection);
			
			// print the winner 
			System.out.println(this.winner.getNickname() + " won the match");
			
			// start exit procedure 
			new ExitProcedure().startGameEndedProcedure();
			
			// leave the application gracefully. The notify will unblock the main thread
			final GameLock lock = GameLock.getInstance();
			synchronized (lock) {
				lock.notify();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	/** 
	 * This message is sent to inform all other player that the game is finished.
	 * The exit procedure will take care of the broadcast and to gracefully leave the game.
	 */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			
			// start exit procedure 
			new ExitProcedure().startGameEndedProcedure();
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return "This is a Victory message";
	}

}
