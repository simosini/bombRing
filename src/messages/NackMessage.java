package messages;

import java.io.Serializable;

public class NackMessage extends InMessage implements Serializable {

	private static final long serialVersionUID = -1036121250705826323L;
	private static final int NACK_PRIORITY = 2;
	

	public NackMessage() {
		super(Type.NACK, NACK_PRIORITY);

	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());

	}
	
	@Override
	public String toString(){
		return "This is a Nack message";
	}

}
