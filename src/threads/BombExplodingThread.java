package threads;

import messages.BombExplodedMessage;
import messages.Packets;
import singletons.OutQueue;
import singletons.Peer;

/** This thread waits 5 seconds and then put a BombExploded message on the outQueue */
public class BombExplodingThread implements Runnable {
	
	private String colorZone;

	public BombExplodingThread(String color) {
		this.colorZone = color;
	}

	@Override
	public void run() {
		try {
			Packets newPacket = new Packets(new BombExplodedMessage(colorZone), null);
			
			/** wait 5 seconds */
			Thread.sleep(5000);
			
			if (Peer.INSTANCE.getNumberOfPlayers() > 1) { 
				OutQueue.INSTANCE.add(newPacket);
				//System.out.println("BombPacket put on the outQueue");
			}
			else {
				Thread t = new Thread(new OnePlayerHandlerThread(newPacket));
				//System.out.println("One Player handler started");
				t.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
