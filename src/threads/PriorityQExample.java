package threads;

import java.util.PriorityQueue;

import messages.AckMessage;
import messages.BombExplodedMessage;
import messages.Packets;
import messages.PositionMessage;

public class PriorityQExample {

	public static void main(String[] args) {
		Packets p1 = new Packets(new AckMessage(), null);
		Packets p2 = new Packets(new BombExplodedMessage("red"), null);
		Packets p3 = new Packets(new PositionMessage(1, 1), null);
		PriorityQueue<Packets> q = new PriorityQueue<>();
		q.add(p1);
		q.add(p2);
		q.add(p3);
		while(!q.isEmpty()){
			System.out.println(q.remove());
		}

	}

}
