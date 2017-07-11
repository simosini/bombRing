package singletons;

import java.util.PriorityQueue;

import messages.Packets;

public class OutQueue {
	
	private PriorityQueue<Packets> outQueue;
	private static OutQueue instance;
		
	private OutQueue(){
		outQueue = new PriorityQueue<>();
	}
	
	public static synchronized OutQueue getInstance() {
		if (instance == null)
			instance = new OutQueue();
		return instance;
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
