package messages;

import java.io.Serializable;

/** 
 * Abstract generic class for INPUT messages, that is messages received on
 * the listening server socket. 
 * Every message has a specific code and a priority associated 
 * so as to handle them accordingly.  
 **/

public abstract class InMessage implements Serializable, Message {
	
	private static final long serialVersionUID = 4232448590544129467L;
	private Type codeMessage;
	private int priority;
	
	public InMessage(Type type, int priority){
		this.setCodeMessage(type);
		this.setPriority(priority);
	}
	
	@Override
	public Type getCodeMessage() {
		return codeMessage;
	}
	
	@Override
	public void setCodeMessage(Type codeMessage) {
		this.codeMessage = codeMessage;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	@Override
	public abstract void handleMessage();
}
