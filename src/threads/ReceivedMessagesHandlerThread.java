package threads;

import java.net.Socket;
import java.util.Queue;

import messages.Message;
import messages.Packets;

/** 
 * This handler takes care of the messages received on the server Socket.
 * It's started by the server and creates a packet to put on the in Queue.
 * In order to do that must make sure the MessageHandler is ready to process
 * it otherwise waits until it's ready. It does not handle any message.
 * */

public class ReceivedMessagesHandlerThread implements Runnable {

	private Queue<Packets> queue; /** where to put new packets */
	private Thread handler; /** needed to check it's waiting */
	private Socket sender;
	private Message incomingMessage;
	
	public ReceivedMessagesHandlerThread(Queue<Packets> q, Thread t, Socket s, Message m){
		this.setQueue(q);
		this.setHandler(t);
		this.setSender(s);
		this.setIncomingMessage(m);
	}
	
	public Queue<Packets> getQueue(){
		return this.queue;
	}
	
	public void setQueue(Queue<Packets> q) {
		this.queue = q;		
	}

	public Thread getHandler(){
		return this.handler;
	}
	
	public void setHandler(Thread t){
		this.handler = t;
	}
	
	public Socket getSender() {
		return sender;
	}

	public void setSender(Socket sender) {
		this.sender = sender;
	}

	public Message getIncomingMessage() {
		return incomingMessage;
	}

	public void setIncomingMessage(Message incomingMessage) {
		this.incomingMessage = incomingMessage;
	}

	@Override
	public void run() {
		synchronized(queue){
			while(getHandler().getState() != Thread.State.WAITING){
				//System.out.println("The producer is busy. I wait ");
				try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/** Handler is now ready to be notified */
			Packets packet = new Packets(this.getIncomingMessage(), this.getSender());
			queue.add(packet);
			//System.out.println("Produced " + packet);
			/** might not be the handler but it's not a problem */
			queue.notify();
		}			
	}

}
