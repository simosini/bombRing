package messages;

import beans.Player;
import peer.ConnectionData;
import services.ExitProcedure;
import singletons.GameLock;
import singletons.Peer;

/** This is the message to inform that the match has finished.
 *  This message will be sent immediately.
 *  When a player receives a Victory message on the server socket,it put it 
 *  on the outQueue with max priority.
 *  As soon as the token is received the exit procedure will start.
 *  The exit procedure in this case is different from the regular one,
 *  cause I don't need to inform other players i'm dead.  */
public class VictoryMessage extends Message {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 1;
	private Player winner;
	
	public VictoryMessage(Player winner) {
		super(Type.VICTORY, VICTORY_PRIORITY);
		this.winner = winner;
	}
	
	/** When the server socket receives this message knows that the match is finished
	 * so it just send an AckMessage */
	@Override
	public boolean handleInMessage(ConnectionData clientConnection) {
		try {
			/** the game is finished */
			Peer.getInstance().setAlive(false);
			
			/** send ackMessage */
			new AckMessage().handleOutMessage(clientConnection);
			
			/** print the winner */
			System.out.println(this.winner.getNickname() + " won the match");
			
			/** exit procedure */
			new ExitProcedure().startGameEndedProcedure();
			
			
			/** exit the game  
			System.out.println("The game is over. Goodbye");
			System.exit(0);*/
			GameLock lock = GameLock.getInstance();
			synchronized (lock) {
				lock.notify();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	/** When I found this on the outQueue means I need to start the exit procedure
	 *  and exit the game. */
	@Override
	public boolean handleOutMessage(ConnectionData clientConnection) {
		try {
			/** start exit procedure */
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
