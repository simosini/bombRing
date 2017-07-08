package simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MeasureBuffer implements Buffer<Measurement> {

	private LinkedList<Measurement> measureList;
	private static MeasureBuffer instance = null;

	private MeasureBuffer() {
		this.measureList = new LinkedList<>();
	}

	public static synchronized MeasureBuffer getInstance() {
		if (instance == null)
			instance = new MeasureBuffer();
		return instance;
	}

	@Override
	public synchronized void addNewMeasurement(Measurement t) {
		this.measureList.add(t);

	}

	@Override
	public synchronized List<Measurement> readAllAndClean() {
		List<Measurement> measures = new ArrayList<>(this.measureList);
		clearBuffer();
		return measures;
	}

	private void clearBuffer() {
		this.measureList.clear();
	}

	@Override
	public String toString() {
		return "Number of measures: " + this.measureList.size() + ". Measures :" + this.measureList;
	}

}
