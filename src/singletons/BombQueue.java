package singletons;

import java.util.LinkedList;

import peer.Bomb;

/**
 * This is the queue where bombs are saved once created by the SensorDataAnalyzer
 */
public class BombQueue {
	
	private LinkedList<Bomb> bombQueue;
	private static BombQueue instance = null;

	private BombQueue() {
		this.bombQueue = new LinkedList<>();
	}
	
	public static synchronized BombQueue getInstance() {
		if (instance == null)
			instance = new BombQueue();
		return instance;
	}
	
	public synchronized LinkedList<Bomb> getBombQueue() {
		return new LinkedList<>(bombQueue);
	}
	
	/**
	 * Adds a bomb to the queue
	 * @param the bomb to be added
	 */
	public synchronized void addBomb(Bomb b){
		this.bombQueue.add(b);
	}
	
	/**
	 * @return the first bomb on the queue or null if the queue is empty
	 * After this operation the bomb is removed from the queue
	 */
	public synchronized Bomb removeBomb() {
		return this.bombQueue.poll();
	}
	
	/**
	 * @return the first bomb on the queue or null if the queue is empty
	 * After this operation the bomb is NOT removed from the queue
	 */
	public synchronized Bomb peekBomb() {
		return this.bombQueue.peek();
	}
	

}
