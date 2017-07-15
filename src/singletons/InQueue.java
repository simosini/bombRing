package singletons;

import java.util.LinkedList;

import messages.Packets;

/**
 * This is the queue for the incoming packets, that is packets received on the server socket.
 */
public class InQueue {
	
	private LinkedList<Packets> inQueue;
	private static InQueue instance = null;
		
	private InQueue(){
		inQueue = new LinkedList<>();
	}
	
	public static synchronized InQueue getInstance(){
		if (instance == null)
			instance = new InQueue();
		return instance;
	}
	
	/**
	 * Adds a packet to the queue
	 * @param the packet to be added
	 */
	public synchronized void add (Packets p){
		this.inQueue.add(p);
	}
	
	/**
	 * @return the first packet in the queue or null if the queue is empty
	 */
	public synchronized Packets poll(){
		return this.inQueue.poll();
	}
	
	/**
	 * @return the size of the queue
	 */
	public synchronized int size(){
		return this.inQueue.size();
	}

	/**
	 * @return true if the queue is empty
	 */
	public synchronized boolean isEmpty() {
		return this.inQueue.isEmpty();
	}
}
