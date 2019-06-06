import java.util.ArrayList;
import java.util.Collections;

public class Statistics {
	public static double mean(ArrayList<Integer> list) {
		int sum = 0;
		for (int val : list) {
			sum += val;
		}
		return (double) sum / list.size();
	}

	public static double standardDeviation(ArrayList<Integer> list) {
		int sum = 0;
		double mean = mean(list);
		for (int val : list) {
			sum += Math.pow(val - mean, 2);
		}
		return Math.sqrt((double) sum / list.size());
	}

	public static double median(ArrayList<Integer> list) {
		Collections.sort(list);
		if (list.size() % 2 == 0) {
			return (list.get(list.size() / 2 - 1) + list.get(list.size() / 2)) / 2;//Average of the two most median values
		} else {
			return list.get(list.size() / 2);
		}
	}
}
