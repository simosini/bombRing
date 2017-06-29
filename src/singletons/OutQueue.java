package singletons;

import java.util.PriorityQueue;

import messages.Packets;

public enum OutQueue {
	
	INSTANCE;
	
	private PriorityQueue<Packets> outQueue;
		
	private OutQueue(){
		outQueue = new PriorityQueue<>();
	}
	
	public synchronized void add(Packets p){
		this.outQueue.add(p);
	}
	
	public synchronized Packets poll(){
		return this.outQueue.poll();
	}
	
	public synchronized int size(){
		return this.outQueue.size();
	}

	public synchronized boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.outQueue.isEmpty();
	}
}
