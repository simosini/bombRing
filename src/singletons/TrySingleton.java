package singletons;

import simulator.MeasureBuffer;
import threads.AccelerometerSimulator;
import threads.SensorDataAnalyzer;

public class TrySingleton {

	public static void main(String[] args) throws InterruptedException{
		
		AccelerometerSimulator as = new AccelerometerSimulator(MeasureBuffer.getInstance()); 
		SensorDataAnalyzer sda = new SensorDataAnalyzer();
		
		
		Thread t1 = new Thread(as);
		Thread t2 = new Thread(sda);
		t1.start();
		
		t2.start();
		
		Thread.sleep(10000);
		as.stopMeGently();
		sda.stopAnalyzer();
		
		System.out.println(BombQueue.getInstance().getBombQueue());
		
	}

}

	
