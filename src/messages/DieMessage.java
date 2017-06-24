package messages;

/**
 * This is just a local message that is put in the out queue to inform
 * that the current player is dead (because of a bomb or has been eaten)
 * This is never sent through the socket so no need to be serialized.
 * Once the local handler see this message it broadcasts a DEAD message to
 * all the other players.
 * */

public class DieMessage extends InMessage {
	
	private static final int DIE_PRIORITY = 4;

	public DieMessage() {
		super(Type.DIE, DIE_PRIORITY);
		
	}
	
	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}

	@Override
	public String toString() {
		return "This is a Die message";

	}

}
