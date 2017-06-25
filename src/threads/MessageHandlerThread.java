package threads;

import java.util.PriorityQueue;
import java.util.Queue;

import messages.Message;
import messages.Packets;

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

	private Queue<Packets> inQueue;
	private PriorityQueue<Packets> outQueue;

	public MessageHandlerThread(Queue<Packets> inQueue) {
		this.setInQueue(inQueue);
	}

	public Queue<Packets> getInQueue() {
		return this.inQueue;
	}

	public void setInQueue(Queue<Packets> queue) {
		this.inQueue = queue;
	}

	public PriorityQueue<Packets> getOutQueue() {
		return this.outQueue;
	}

	public void setOutQueue(PriorityQueue<Packets> queue) {
		this.outQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
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
				}
				/** consumes all items before releasing the lock on queue */
				while (!inQueue.isEmpty()) {
					Packets newPacket = inQueue.remove();
					Message inMessage = newPacket.getMessage();
					inMessage.handleMessage(newPacket.getSendingSocket(), this.getOutQueue());
				}

				inQueue.notify();
				try {
					inQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}

}
