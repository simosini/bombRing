package singletons;

import java.util.PriorityQueue;

import messages.Packets;

/**
 * This singleton represents the out queue, that is the priority
 * queue where all outgoing messages waiting for the token to be handled are
 * kept. Those messages can be put either by the MessageHandler or the
 * UserInputHandler threads.
 */
public class OutQueue {
	
	private PriorityQueue<Packets> outQueue;
	private static OutQueue instance;
		
	private OutQueue(){
		outQueue = new PriorityQueue<>();
	}
	
	/**
	 * singleton
	 */
	
	public static synchronized OutQueue getInstance() {
		if (instance == null)
			instance = new OutQueue();
		return instance;
	}
	
	/**
	 * Add a packet to the queue
	 * @param the packet to be added
	 */
	public synchronized void add(Packets p){
		this.outQueue.add(p);
	}
	
	/**
	 * @return the packet with highest priority or null if the queue is empty
	 */
	public synchronized Packets poll(){
		return this.outQueue.poll();
	}
	
	/**
	 * @return the number of packets inserted in the queue
	 */
	public synchronized int size(){
		return this.outQueue.size();
	}
	
	/**
	 * @return true if the queue is empty
	 */
	public synchronized boolean isEmpty() {
		return this.outQueue.isEmpty();
	}
}
