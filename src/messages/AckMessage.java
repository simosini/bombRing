package messages;

import java.io.Serializable;

public class AckMessage extends Message implements Serializable{
	
	private static final long serialVersionUID = -2461861844306614558L;
	private static final int ACK_PRIORITY = 1;

	public AckMessage() {
		super(Type.ACK, ACK_PRIORITY);
	}
	
	@Override
	public void handleMessage(){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is an Ack message";
	}

}
