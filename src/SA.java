import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SA {
	private final double cooldownRate;
	private int initTemp = 3000;
	private int endTemp = 10;
	private double k = 0.0003;

	private Warehouse warehouse;
	private double temperature;
	private ArrayList<Integer> bestSolution;
	private int bestSolutionDist;
	private ArrayList<Integer> currentSolution;
	private int currentSolutionDist;
	private Random random = new Random(System.nanoTime());

	private int orders;
	private int batchAmount;
	private int batchSize;

	private ArrayList<Pair<Long, Integer>> bestSolutionsTimes = new ArrayList<>();
	long startTime;

	public SA(double cooldownRate) {
		this.cooldownRate = cooldownRate;
	}
	
	private void tick() {
		ArrayList<Integer> neighbor = getNeighbor();
		int neighborDist = warehouse.distance(neighbor);
		if (neighborDist <= currentSolutionDist) {
			currentSolution = neighbor;
			currentSolutionDist = neighborDist;
			if (neighborDist < bestSolutionDist) {
				bestSolution = neighbor;
				bestSolutionDist = neighborDist;
				bestSolutionsTimes.add(new Pair<>(System.currentTimeMillis() - startTime, bestSolutionDist));
			}
		} else {
			int distDiff = currentSolutionDist - neighborDist;
			double neighborLikelihood = acceptance(distDiff);
			if (random.nextDouble() < neighborLikelihood) {
				currentSolution = neighbor;
				currentSolutionDist = neighborDist;
			}
		}

		temperature *= cooldownRate;
	}

	private void initSolution() {
		currentSolution = new ArrayList<>(orders);
		for (int order = 0; order < orders; order++) {
			currentSolution.add(order % batchAmount);
		}
		Collections.shuffle(currentSolution, random);
	}

	private ArrayList<Integer> getNeighbor() {
		ArrayList<Integer> neighbor = new ArrayList<>(orders);
		neighbor.addAll(currentSolution);
		if (random.nextFloat() < 0.9) {
			// swap
			int order1 = random.nextInt(orders);
			int order2 = random.nextInt(orders);
			Collections.swap(neighbor, order1, order2);
		} else {
			// transfer
			ArrayList<ArrayList<Integer>> batches = new ArrayList<>(batchAmount);

			for (int i = 0; i < batchAmount; i++) {
				batches.add(new ArrayList<>());
			}

			for (int i = 0; i < orders; i++) {
				int batch = currentSolution.get(i);
				batches.get(batch).add(i);
			}

			for (int batch = 0; batch < batches.size(); batch++) {
				if (batches.get(batch).size() < batchSize) {
					int randOrderIndex = random.nextInt(neighbor.size());
					neighbor.set(randOrderIndex, batch);
					break;
				}
			}
		}

		return neighbor;
	}

	//returns the likelihood the neighbor should be chosen, a value between 0 and 1, depending on distance to neighbor and temperature
	private double acceptance(int distDiff) {
		return Math.exp(distDiff / (k * temperature));
	}

	public Result solve(Warehouse warehouse, int orders, int batchSize) {
		startTime = System.currentTimeMillis();
		bestSolutionsTimes.clear();
		this.warehouse = warehouse;
		this.orders = orders;
		this.batchSize = batchSize;
		this.batchAmount = (int) Math.ceil((double) orders / batchSize);
		temperature = initTemp;
		initSolution();
		currentSolutionDist = warehouse.distance(currentSolution);
		bestSolution = currentSolution;
		bestSolutionDist = currentSolutionDist;
		bestSolutionsTimes.add(new Pair<>(System.currentTimeMillis() - startTime, bestSolutionDist));
		int iterations = 0;
		while (temperature > endTemp) {
			tick();
			iterations++;
		}
		System.out.println("Iterations: " + iterations);
		System.out.println("SA solution: " + TestFramework.solToString(bestSolution));
		System.out.println("SA score: " + warehouse.distance(bestSolution));
		System.out.println();
		return new Result(bestSolution, bestSolutionsTimes);
	}
}