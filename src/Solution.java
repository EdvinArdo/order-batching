import java.util.ArrayList;

public class Solution {
	private final ArrayList<Integer> solution;
	private Warehouse warehouse;
	private boolean distanceCalculated = false;
	private int distance;

	public Solution(Solution solution) {
		this.solution = new ArrayList<>(solution.solution);
		this.warehouse = solution.warehouse;
		this.distanceCalculated = solution.distanceCalculated;
		this.distance = solution.distance;
	}

	public Solution(Warehouse warehouse, ArrayList<Integer> solution) {
		this.warehouse = warehouse;
		this.solution = solution;
	}

	public int getDistance() {
		if (!distanceCalculated) {
			distanceCalculated = true;
			distance = warehouse.distance(solution);
		}
		return distance;
	}

	public ArrayList<Integer> getSolution() {
		return solution;
	}
}
