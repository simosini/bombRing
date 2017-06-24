package messages;

import java.io.Serializable;

public interface Message extends Serializable {

	/**
	 * @return the codeMessage
	 */
	Type getCodeMessage();

	/**
	 * @param codeMessage the codeMessage to set
	 */
	void setCodeMessage(Type codeMessage);

	/**
	 * @return the priority
	 */
	int getPriority();

	/**
	 * @param priority the priority to set
	 */
	void setPriority(int priority);
	
	void handleMessage();

}