package threads;

import java.util.List;
import java.util.OptionalDouble;

import peer.Bomb;
import simulator.MeasureBuffer;
import simulator.Measurement;
import singletons.BombQueue;

/**
 * This thread is in charge of monitoring data coming sensors 
 * in order to detect possible outliers.
 */
public class SensorDataAnalyzer implements Runnable {
	
	private static final double ALPHA = 0.5;
	private static final double THRESHOLD = 0.15;
	
	private double currentEMA = 0.;
	private volatile boolean stop = false;
	
	public SensorDataAnalyzer() {}
	
	public synchronized void stopAnalyzer(){
		stop = true;
	}

	private double getCurrentEMA() {
		return currentEMA;
	}

	private void setCurrentEMA(double currentEMA) {
		this.currentEMA = currentEMA;
	}
	
	/**
	 * periodically check the presence of outliers. When those are detected a new bomb is
	 * put on the queue. The color depends on the value of the new EMA
	 */
	@Override
	public void run() {
		final BombQueue bombQueue = BombQueue.getInstance();
		final MeasureBuffer dataBuffer = MeasureBuffer.getInstance();
		
		while (!stop) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// retrieve data 
			List<Measurement> data = dataBuffer.readAllAndClean();
			
			// compute next EMA 
			double nextEMA = computeNextEMA(data);
			
			// add bomb to the queue if needed 
			Bomb bomb = null;
			if ((bomb = getNextBomb(nextEMA)) != null)
				bombQueue.addBomb(bomb);
			
			// set new EMA
			this.setCurrentEMA(nextEMA);
			
			
			
		}

	}
	
	/**
	 * Creates a new bomb if the new EMA is different from the previous one
	 * more than the threshold
	 * @param next calculated EMA  
	 * @return A bomb if an outlier is detected null otherwise
	 */
	private Bomb getNextBomb(double nextEMA) {
		Bomb newBomb = null;
		final double currentEMA = this.currentEMA;
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
				newBomb = new Bomb("yellow");
				break;
			}
		}
		return newBomb;
	}
	/**
	 * Compute next EMA from the values retrieved from list of measurements
	 * @param data from sensors
	 * @return next EMA
	 */
	private double computeNextEMA(List<Measurement> data) {
		
		// compute average
		final OptionalDouble average = data
	            						.stream()
	            						.mapToDouble(el -> el.getValue())
	            						.average();
		
		double nextEMA = (average.getAsDouble() - this.getCurrentEMA()) * ALPHA;
		return this.getCurrentEMA() + nextEMA;

	}

}
