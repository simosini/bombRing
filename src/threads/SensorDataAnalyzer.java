package threads;

import java.util.List;
import java.util.OptionalDouble;

import peer.Bomb;
import simulator.MeasureBuffer;
import simulator.Measurement;
import singletons.BombQueue;

public class SensorDataAnalyzer implements Runnable {
	
	private static final double ALPHA = 0.5;
	private static final double THRESHOLD = 0.25;
	
	private double currentEMA = 0.;
	private volatile boolean stop = false;
	
	public SensorDataAnalyzer() {}
	
	public void stopAnalyzer(){
		stop = true;
	}

	private double getCurrentEMA() {
		return currentEMA;
	}

	private void setCurrentEMA(double currentEMA) {
		this.currentEMA = currentEMA;
	}

	@Override
	public void run() {
		BombQueue bombQueue = BombQueue.getInstance();
		MeasureBuffer dataBuffer = MeasureBuffer.getInstance();
		
		while (!stop) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/** retrieve data */
			List<Measurement> data = dataBuffer.readAllAndClean();
			
			/** compute next EMA */
			double nextEMA = computeNextEMA(data);
			
			/** add bomb to the queue if needed */
			Bomb bomb = null;
			if ((bomb = getNextBomb(nextEMA)) != null)
				bombQueue.addBomb(bomb);
			
			this.setCurrentEMA(nextEMA);
			
			
			
		}

	}

	private Bomb getNextBomb(double nextEMA) {
		Bomb newBomb = null;
		double currentEMA = this.currentEMA;
		if ((nextEMA - currentEMA) / nextEMA > THRESHOLD) {
			int val = (int)nextEMA % 4;
			switch (val) {
			case 0:
				newBomb = new Bomb("green");
				break;
			case 1:
				newBomb = new Bomb("red");
				break;
			case 2:
				newBomb = new Bomb("blue");
				break;
			default:
				newBomb = new Bomb("Yellow");
				break;
			}
		}
		return newBomb;
	}

	private double computeNextEMA(List<Measurement> data) {
		OptionalDouble average = data
	            .stream()
	            .mapToDouble(el -> el.getValue())
	            .average(); 
		double nextEMA = (average.getAsDouble() - this.getCurrentEMA()) * ALPHA;
		return this.getCurrentEMA() + nextEMA;

	}

}
