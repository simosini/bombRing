package messages;

public class TokenMessage extends Message {
	
	private static final long serialVersionUID = -2487582210405295762L;
	private static final int TOKEN_PRIORITY = 1;
	
	public TokenMessage() {
		super(Type.TOKEN, TOKEN_PRIORITY);
	}
	
	@Override
	public void handleMessage(){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Token message";
	}

}
