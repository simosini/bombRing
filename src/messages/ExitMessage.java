package messages;

import java.net.Socket;

public class ExitMessage extends Message {
	
	private static final long serialVersionUID = -5807158110067786776L;
	private static final int EXIT_PRIORITY = 2;

	public ExitMessage() {
		super(Type.EXITGAME, EXIT_PRIORITY);
	}

	@Override
	public void handleInMessage(Socket sender) {
		System.out.println(this.toString());

	}

	@Override
	public String toString(){
		return "This is an Exit message";
	}
}
