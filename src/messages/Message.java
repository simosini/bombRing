package messages;

import java.io.Serializable;

/** 
 * Abstract generic class for messages. Every message has a specific code and 
 * and a priority associated so as to handle them accordingly.  
 **/

public abstract class Message implements Serializable{
	
	private static final long serialVersionUID = 4232448590544129467L;
	private Type codeMessage;
	private int priority;
	
	public Message(Type type, int priority){
		this.setCodeMessage(type);
		this.setPriority(priority);
	}
	
	/**
	 * @return the codeMessage
	 */
	public Type getCodeMessage() {
		return codeMessage;
	}
	/**
	 * @param codeMessage the codeMessage to set
	 */
	public void setCodeMessage(Type codeMessage) {
		this.codeMessage = codeMessage;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	abstract void handleMessage();
}
