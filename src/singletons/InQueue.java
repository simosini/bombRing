package singletons;

import java.util.LinkedList;

import messages.Packets;

public enum InQueue {
	
	INSTANCE;
	
	private LinkedList<Packets> inQueue;
		
	private InQueue(){
		inQueue = new LinkedList<>();
	}
	
	public synchronized void add (Packets p){
		this.inQueue.add(p);
	}
	
	public synchronized Packets poll(){
		return this.inQueue.poll();
	}
	
	public synchronized int size(){
		return this.inQueue.size();
	}

	public synchronized boolean isEmpty() {
		return this.inQueue.isEmpty();
	}
}
