package singletons;

import java.util.LinkedList;

import peer.Bomb;

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
	
	public synchronized void addBomb(Bomb b){
		this.bombQueue.add(b);
	}
	
	public synchronized Bomb removeBomb() {
		return this.bombQueue.poll();
	}
	
	public synchronized Bomb peekBomb() {
		return this.bombQueue.peek();
	}
	

}
