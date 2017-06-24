package restConverter;

public class TrySinchro {

	public static void main(String[] args) {
		TrySinchro t = new TrySinchro();
		Player p = new Player("luca","rossi","luro",200);
		Players pl = new Players();
		Thread t1 = new Thread(t.new Sync(1, p, pl)); 
		Thread t2 = new Thread(t.new Sync(4, p, pl));
		t1.run();
		t2.run();
		System.out.println(pl);
		

	}
	
	public class Sync implements Runnable {
		
		private int code;
		private Player p;
		private Players pl;
		
		public Sync(int code, Player p, Players pl){
			this.code = code;
			this.p = p;
			this.pl = pl;
		}

	    @Override
		public void run() {
	    	if (this.code == 1){
				try {
					Thread.sleep(2000);
					pl.deletePlayer(p);
		    	}
		    	catch(IllegalArgumentException ie){
		    		System.out.println(ie.getMessage());
		    	}
		
	    		catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    	else {
	    		pl.addPlayer(p);
	    	}
	    			
	        System.out.println("Thread " + this.code + " has finished!");
	    }

	}
	
	

}
