package messages;

import java.net.Socket;

public class VictoryMessage extends Message {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 2;
	
	public VictoryMessage() {
		super(Type.VICTORY, VICTORY_PRIORITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleInMessage(Socket sender) {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Victory message";
	}

}
