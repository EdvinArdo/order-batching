import javafx.util.Pair;

import java.util.ArrayList;

public class Result {
	ArrayList<Integer> solution;
	ArrayList<Pair<Long, Integer>> bestSolutionsTimes;

	public Result(ArrayList<Integer> solution, ArrayList<Pair<Long, Integer>> bestSolutionsTimes) {
		this.solution = solution;
		this.bestSolutionsTimes = bestSolutionsTimes;
	}
}
