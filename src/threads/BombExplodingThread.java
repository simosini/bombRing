package threads;

import messages.BombExplodedMessage;
import messages.Packets;
import singletons.OutQueue;
import singletons.Peer;

/** 
 * This thread waits 5 seconds and then put a BombExploded message on the outQueue 
 */
public class BombExplodingThread implements Runnable {
	
	private String colorZone;

	public BombExplodingThread(final String color) {
		this.colorZone = color;
	}
	
	/**
	 * Wait 5 seconds and then put a BombExploded message on the out queue 
	 * to be handled when the token arrives
	 */
	@Override
	public void run() {
		try {
			final Packets newPacket = new Packets(new BombExplodedMessage(colorZone, Peer.getInstance().getCurrentPlayer()), null);
			
			// wait 5 seconds 
			Thread.sleep(5000);
			
			// put the packet only if I'm not alone i.e. the token is active
			if (Peer.getInstance().getNumberOfPlayers() > 1) 
				OutQueue.getInstance().add(newPacket);
			
			// else start the one player thread
			else {
				Thread t = new Thread(new OnePlayerHandlerThread(newPacket));
				t.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
