package threads;

import messages.Message;
import messages.Packets;
import singletons.InQueue;
import singletons.OutQueue;
import singletons.Peer;

/**
 * This thread (the handler) is in charge of handling incoming and outcoming
 * messages. The protocol consists on the following rules: 1. the handler waits
 * on the in Queue until a message arrives. 2. When a message arrives a server
 * thread checks if the handler is available by verifying it's in waiting state,
 * if so put the message on the queue and notify the handler. Otherwise (means
 * the handler is working) it waits and will be awaken by the handler when it's
 * done. 3. In case of concurrent arrive of messages the handler will empty the
 * queue. Even though 99.99% of the time there will be only one message on the
 * queue. 4. The message is taken from the inQueue and processed. If the
 * handling is a simple operation such as ACK, the handler sends it and
 * terminates, otherwise it creates a new message to be put on the outQueue. 5.
 * Messages on the outQueue are processed only with the token and one at the
 * time according the to priority indicated.
 */

public class MessageHandlerThread implements Runnable {

	private volatile boolean stop = false; // to stop the thread when the game
											// is finished

	public MessageHandlerThread() { }


	public synchronized void stopThread() {
		this.stop = true;
	}

	private synchronized boolean stop() {
		return this.stop;
	}

	@Override
	public void run() {
		InQueue inQueue = InQueue.INSTANCE;
		OutQueue outQueue = OutQueue.INSTANCE;
		while (!stop()) {
			synchronized (inQueue) {
				/*
				 * try { all comments for debugging purpose only
				 * Thread.sleep(2000); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */
				
				while (inQueue.isEmpty()) {
					// System.out.println("The queue is empty. Waiting!");
					try {
						inQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/** check if the player is the only one in the game */
					System.out.println("Im awake");
					if (Peer.INSTANCE.getCurrentGame().getPlayers().size() == 1) break;
				}
				/**
				 * if you are alone take first message on the outQueue and
				 * eventually notify the standard in thread. No token needed
				 */
				if (Peer.INSTANCE.getCurrentGame().getPlayers().size() == 1 && !outQueue.isEmpty()) {
					Packets outPacket = null;
					
					synchronized (outQueue) {
						System.out.println("retrieving packet from outq");
						outPacket = outQueue.poll(); /** only first packet */
					}
					if (outPacket != null) {
						System.out.println("Starting handling");
						Message outMessage = outPacket.getMessage();
						outMessage.handleMessage(outPacket.getSendingSocket());

					}
					else
						System.out.println("No packet to retrieve!");
				}
				/** consumes all items before releasing the lock on queue */
				while (!inQueue.isEmpty()) {
					System.out.println("Consuming inQueue");
					Packets inPacket = inQueue.poll();
					Message inMessage = inPacket.getMessage();
					inMessage.handleMessage(inPacket.getSendingSocket());
				}

				inQueue.notify();
				
			}

		}
	}

}
