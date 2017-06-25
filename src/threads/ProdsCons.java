package threads;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProdsCons {

	public static void main(String[] args) {
		Queue<Integer> buffer = new LinkedList<>();
		ProdsCons pc = new ProdsCons();
		ConsThread ct = pc.new ConsThread(buffer, "cons"); /** 1 consumer */
		Thread t1 = new Thread(ct);
		t1.start();
		for (int i = 0; i < 5; i++)
			new Thread(pc.new ProdThread(buffer, t1, ("prod"+i))).start();	
		
	}

	private class ProdThread implements Runnable{
		
		private Queue<Integer> queue;
		private Thread consumer; /** needed to check it's waiting*/
		private String name;
		
		private ProdThread(Queue<Integer> q, Thread t, String name){
			this.queue = q;
			this.consumer = t;
			this.name = name;
		}
		
		private Thread getConsumer(){
			return this.consumer;
		}
		@Override
		public void run() {
			//while(true){
				synchronized(queue){
					while(getConsumer().getState() != Thread.State.WAITING){
						System.out.println("The producer is busy. I wait " + this.name);
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					/** Consumer is now ready to be notified */
					int i = new Random().nextInt(100);
					queue.add(i);
					System.out.println(this.name + " produced " + i);
					queue.notify();
				}
			//}			
		}
		
	}
	
	private class ConsThread implements Runnable {
		
		private Queue<Integer> queue;
		private String name;
		
		private ConsThread(Queue<Integer> q, String name) {
			this.queue = q;
			this.name = name;
		}
		
		@Override
		public void run() {
			while(true){
				synchronized (queue) {
					/*try{
						Thread.sleep(2000);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}*/
					while(queue.isEmpty()){
						System.out.println("The queue is empty. " + this.name + " waiting!");
						try{
							queue.wait();
						}
						catch(InterruptedException e){
							e.printStackTrace();
						}
					}
					/** consumes all items before releasing the lock on queue */
					while(!queue.isEmpty()){
						System.out.println("Consuming : " + queue.remove());
					}
					
					queue.notify();
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			
			}
		
		}
	}
}





	


