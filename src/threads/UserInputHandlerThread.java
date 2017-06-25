package threads;

import java.io.BufferedReader;
import java.util.PriorityQueue;

import messages.Packets;

/** 
 * As the name says this thread handles move and bomb tossing request from user
 * It builds packets to put in the outQueue and must wait until the requested message
 * has been processed by the messageHandler.
 * Access to the priority queue must be sync. 
 * */

public class UserInputHandlerThread implements Runnable {
	
	private PriorityQueue<Packets> outQueue;
	private BufferedReader userInput;
	
	public UserInputHandlerThread(PriorityQueue<Packets> q, BufferedReader br) {
		this.setOutQueue(q);
		this.setUserInput(br);
	}

	public PriorityQueue<Packets> getOutQueue() {
		return outQueue;
	}

	public void setOutQueue(PriorityQueue<Packets> outQueue) {
		this.outQueue = outQueue;
	}

	public BufferedReader getUserInput() {
		return userInput;
	}

	public void setUserInput(BufferedReader userInput) {
		this.userInput = userInput;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
