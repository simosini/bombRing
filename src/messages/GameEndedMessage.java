package messages;

public class GameEndedMessage extends Message {

	private static final long serialVersionUID = 6647186110663744043L;
	private static final int END_PRIORITY = 6;  // max priority 

	public GameEndedMessage() {
		super(Type.GAME_ENDED, END_PRIORITY);
		
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is the End message";
	}

}
