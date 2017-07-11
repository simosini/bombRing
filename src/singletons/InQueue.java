package singletons;

import java.util.LinkedList;

import messages.Packets;

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
