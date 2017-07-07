package singletons;

public class TrySingleton {

	public static void main(String[] args) throws InterruptedException{
		TrySingleton ts = new TrySingleton();
		
		Thread t1 = new Thread(ts.new runner(1));
		Thread t2 = new Thread(ts.new runner(2));
		t1.start();
		Thread.sleep(1000);
		t2.start();
	}
	
	public class runner implements Runnable {
		
		private int side;
		
		public runner(int i){
			this.side = i;
		}

		@Override
		public void run() {
			//OutQueue q = OutQueue.INSTANCE;
			SingTry q = SingTry.getInstance();
			System.out.println("Thread-" + side + " " + q.size());
			if (side == 1){
				try{
					Thread.sleep(2000);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			//Message m = new AckMessage();
			//q.add(new Packets(m,null));
			q.add(side);
			System.out.println("Thread-" + side + " " + q.size());
			
		}
		
	}

}

	
