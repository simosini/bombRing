package messages;

import java.io.Serializable;

public class VictoryMessage extends InMessage implements Serializable {

	private static final long serialVersionUID = 4714778184603472137L;
	private static final int VICTORY_PRIORITY = 0;
	
	public VictoryMessage() {
		super(Type.VICTORY, VICTORY_PRIORITY);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a Victory message";
	}

}
