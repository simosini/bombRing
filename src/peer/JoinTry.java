package peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JoinTry {

	public static void main(String[] args) throws IOException {
		JoinTry mainThread = new JoinTry();
		Thread t1 = new Thread(mainThread.new JoinThreads(1));
		Thread t2 = new Thread(mainThread.new JoinThreads(2));
		Thread t3 = new Thread(mainThread.new JoinThreads(3));
		List<Thread> threads = new ArrayList<>();
		threads.add(t1);
		threads.add(t2);
		threads.add(t3);
		threads.forEach(el -> el.start());
		threads.forEach(el -> {
			try {
				el.join();
				System.out.println("Thread collected!");
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		});
		System.out.println("Finished!!!");
		
	}
	
	public class JoinThreads implements Runnable {
		
		private int code;
		
		public JoinThreads(int code){
			this.code = code;
		}

	    @Override
		public void run() {
	    	if (this.code == 1)
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		
	        System.out.println("Thread " + this.code + " has finished!");
	    }

	}

}
