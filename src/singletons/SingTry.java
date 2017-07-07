package singletons;

import java.util.LinkedList;

public class SingTry {
		
	private LinkedList<Integer> list;
	private static SingTry instance;
		
	private SingTry(){
		list = new LinkedList<>();
	}
	
	public static synchronized SingTry getInstance(){
		if (instance == null){
			instance = new SingTry();
			return instance;
		}
		return instance;
	}
	
	public synchronized void add (int e) {
		this.list.add(e);
	}
	
	public synchronized int size() {
		return this.list.size();
	}
	

}
