package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
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

	private Queue<Packets> inQueue; /** where to put new packets */
	private Thread handler; /** needed to check it's waiting */
	private Socket sender;
	
	public ReceivedMessagesHandlerThread(Queue<Packets> q, Thread t, Socket s){
		this.setInQueue(q);
		this.setHandler(t);
		this.setSender(s);
	}
	
	public Queue<Packets> getInQueue(){
		return this.inQueue;
	}
	
	public void setInQueue(Queue<Packets> q) {
		this.inQueue = q;		
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

	@Override
	public void run() {
		Message message = null;
		Socket sender = this.getSender();
		try {			
			ObjectInputStream reader = new ObjectInputStream(sender.getInputStream());
			message = (Message) reader.readObject();
			System.out.println("Recieved :" + message);
			
		}
		catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		synchronized(inQueue){
			System.out.println("State :");
			while(getHandler().getState() != Thread.State.WAITING){
				System.out.println("The handler is busy. I wait ");
				try {
					inQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/** Handler is now ready to be notified */
			System.out.println("Creating packet!");
			Packets packet = new Packets(message, sender);
			inQueue.add(packet);
			//System.out.println("Produced " + packet);
			/** might not be the handler but it's not a problem */
			System.out.println("Notifying handler");
			inQueue.notify();
			System.out.println("Notified");
		}			
	}

}
