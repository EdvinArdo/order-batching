import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestFramework {
	final int WARMUP_RUNS = 0;
	final int TEST_RUNS = 5;

	ArrayList<ArrayList<Pair<Long, Integer>>> bestSolutionsTimesSA = new ArrayList<>(TEST_RUNS);
	ArrayList<ArrayList<Pair<Long, Integer>>> bestSolutionsTimesGA = new ArrayList<>(TEST_RUNS);

	public void writeStatistics(String fileName, ArrayList<ArrayList<Integer>> resultsList) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);

			for (ArrayList<Integer> results : resultsList) {//median
				fileWriter.write(String.valueOf(Statistics.median(results)) + " ");
			}
			fileWriter.write('\n');
			for (ArrayList<Integer> results : resultsList) {//std dev
				fileWriter.write(String.valueOf(Statistics.standardDeviation(results)) + " ");
			}

			fileWriter.close();
		} catch (IOException e) {
		}
	}

	public void writeMean(String fileName, ArrayList<ArrayList<Integer>> resultsList) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);

			for (ArrayList<Integer> results : resultsList) {//median
				fileWriter.write(String.valueOf(Statistics.mean(results)) + " ");
			}
			fileWriter.write('\n');

			fileWriter.close();
		} catch (IOException e) {
		}
	}

	public TestFramework() {
		Warehouse warehouseOrdered = new Warehouse("warehouseOrdered.txt");
		Warehouse warehouseIrregular = new Warehouse("warehouseIrregular.txt");
		Warehouse warehouseWeird = new Warehouse("warehouseWeird.txt");
		Warehouse warehouseLargeOrdered = new Warehouse("warehouseLargeOrdered.txt");
		Warehouse warehouseLargeIrregular = new Warehouse("warehouseLargeIrregular.txt");
		Warehouse warehouseLargeWeird = new Warehouse("warehouseLargeWeird.txt");
		ArrayList<TestParameters> tests = new ArrayList<>();


		double smallWarehouseCDR = 0.99999;
		double largeWarehouseCDR = 0.9999;
		tests.add(new TestParameters(warehouseOrdered, 3, warehouseOrdered.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseIrregular, 3, warehouseIrregular.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseWeird, 3, warehouseWeird.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseOrdered, 4, warehouseOrdered.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseIrregular, 4, warehouseIrregular.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseWeird, 4, warehouseWeird.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseOrdered, 5, warehouseOrdered.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseIrregular, 5, warehouseIrregular.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseWeird, 5, warehouseWeird.getMaxOrders(), smallWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeOrdered, 3, warehouseLargeOrdered.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeIrregular, 3, warehouseLargeIrregular.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeWeird, 3, warehouseLargeWeird.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeOrdered, 4, warehouseLargeOrdered.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeIrregular, 4, warehouseLargeIrregular.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeWeird, 4, warehouseLargeWeird.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeOrdered, 5, warehouseLargeOrdered.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeIrregular, 5, warehouseLargeIrregular.getMaxOrders(), largeWarehouseCDR));
		tests.add(new TestParameters(warehouseLargeWeird, 5, warehouseLargeWeird.getMaxOrders(), largeWarehouseCDR));

		ArrayList<ArrayList<Integer>> resultsListSA = new ArrayList<>(tests.size());
		ArrayList<ArrayList<Integer>> resultsListGA = new ArrayList<>(tests.size());

		for (TestParameters test : tests) {
			System.out.println(test.warehouse);
			System.out.println("---------- Test ----------");
			System.out.println("Batch size: " + test.batchSize);
			System.out.println("Order amount: " + test.orderAmount + "\n");
			Pair<ArrayList<Integer>, ArrayList<Integer>> results = results(test);
			resultsListSA.add(results.getKey());
			resultsListGA.add(results.getValue());

			writeBestSolutionsTimes("bestSolutionsTimesSA-" + test.batchSize+ "-" + test.orderAmount + ".txt", bestSolutionsTimesSA);
			writeBestSolutionsTimes("bestSolutionsTimesGA-" + test.batchSize+ "-" + test.orderAmount + ".txt", bestSolutionsTimesGA);
		}

		writeStatistics("statisticsSA.txt", resultsListSA);
		writeStatistics("statisticsGA.txt", resultsListGA);
		//writeMean("meanSA.txt", resultsListSA);
		//writeMean("meanGA.txt", resultsListGA);
		//writeMean("meanDiff.txt", listDifference(resultsListSA, resultsListGA));
		writeResultList("res-3-small.txt", resultsListSA.subList(0, 3), resultsListGA.subList(0, 3));
		writeResultList("res-4-small.txt", resultsListSA.subList(3, 6), resultsListGA.subList(3, 6));
		writeResultList("res-5-small.txt", resultsListSA.subList(6, 9), resultsListGA.subList(6, 9));
		writeResultList("res-3-big.txt", resultsListSA.subList(9, 12), resultsListGA.subList(9, 12));
		writeResultList("res-4-big.txt", resultsListSA.subList(12, 15), resultsListGA.subList(12, 15));
		writeResultList("res-5-big.txt", resultsListSA.subList(15, 18), resultsListGA.subList(15, 18));
	}

	public void writeResultList(String fileName, List<ArrayList<Integer>> resultsSA, List<ArrayList<Integer>> resultsGA) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);

			for (int i = 0; i < resultsSA.size(); i++) {
				for (int j = 0; j < resultsSA.get(i).size(); j++) {
					fileWriter.write(String.valueOf(resultsSA.get(i).get(j)));
					fileWriter.write(" ");
				}
				fileWriter.write("\n");
				for (int j = 0; j < resultsGA.get(i).size(); j++) {
					fileWriter.write(String.valueOf(resultsGA.get(i).get(j)));
					fileWriter.write(" ");
				}
				fileWriter.write("\n");
			}

			fileWriter.close();
		} catch (IOException e) {
		}
	}

	public void writeBestSolutionsTimes(String fileName, ArrayList<ArrayList<Pair<Long, Integer>>> bestSolutionsTimes) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);

			for (Pair<Long, Integer> solutionTimePair : bestSolutionsTimes.get(0)) {
				fileWriter.write(String.valueOf(solutionTimePair.getKey() + " "));
			}
			fileWriter.write('\n');
			for (Pair<Long, Integer> solutionTimePair : bestSolutionsTimes.get(0)) {
				fileWriter.write(String.valueOf(solutionTimePair.getValue() + " "));
			}
			fileWriter.write('\n');

			fileWriter.close();
		} catch (IOException e) {
		}
	}

	private ArrayList<Integer> difference(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		ArrayList<Integer> res = new ArrayList<>(list1.size());
		for (int i = 0; i < list1.size(); i++) {
			res.add(list2.get(i) - list1.get(i));
		}
		return res;
	}

	private ArrayList<ArrayList<Integer>> listDifference(ArrayList<ArrayList<Integer>> list1, ArrayList<ArrayList<Integer>> list2) {
		ArrayList<ArrayList<Integer>> res = new ArrayList<>(list1.size());
		for (int i = 0; i < list1.size(); i++) {
			res.add(difference(list1.get(i), list2.get(i)));
		}
		return res;
	}

	private Pair<ArrayList<Integer>, ArrayList<Integer>> results(TestParameters test) {
		SA sa = new SA(test.SACooldownRate);
		ArrayList<Integer> solutionsDistSA = new ArrayList<>(TEST_RUNS);
		ArrayList<Integer> solutionsDistGA = new ArrayList<>(TEST_RUNS);
		for (int i = 0; i < TEST_RUNS + WARMUP_RUNS; i++) {
			bestSolutionsTimesSA.clear();
			bestSolutionsTimesGA.clear();
			long t0 = System.currentTimeMillis();
			Result solutionSA = sa.solve(test.getWarehouse(), test.getOrderAmount(), test.getBatchSize());
			long t1 = System.currentTimeMillis();
			bestSolutionsTimesSA.add(solutionSA.bestSolutionsTimes);

			Population pop = new Population(test.getWarehouse());
			long T0 = System.currentTimeMillis();
			Result solutionGA = pop.solve(test.getOrderAmount(), test.getBatchSize(), t1 - t0);
			long T1 = System.currentTimeMillis();

			System.out.println("SA time: " + (t1 - t0));
			System.out.println("GA time: " + (T1 - T0));
			System.out.println("-------");

			if (!verifySolution(solutionSA.solution, test.orderAmount, test.batchSize)) System.out.println("SA solution is invalid");
			if (!verifySolution(solutionGA.solution, test.orderAmount, test.batchSize)) System.out.println("GA solution is invalid");

			bestSolutionsTimesGA.add(solutionGA.bestSolutionsTimes);

			int distSA = test.getWarehouse().distance(solutionSA.solution);
			int distGA = test.getWarehouse().distance(solutionGA.solution);
			if (i >= WARMUP_RUNS) {
				solutionsDistSA.add(distSA);
				solutionsDistGA.add(distGA);
			}
		}

		return new Pair<>(solutionsDistSA, solutionsDistGA);
	}

	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();
		TestFramework tf = new TestFramework();
		long t1 = System.currentTimeMillis();
		System.out.println("Total time elapsed:" + (t1 - t0));
	}

	private boolean verifySolution(ArrayList<Integer> solution, int orders, int batchSize) {
		int batchAmount = (int) Math.ceil((float) orders / batchSize);
		if (solution.size() != orders) return false;//verify solution size
		int[] batches = new int[batchAmount];
		//count orders in each batch
		for (int i = 0; i < solution.size(); i++) {
			if (solution.get(i) < 0 || solution.get(i) >= batchAmount) return false;//verify order is member of a valid batch
			batches[solution.get(i)]++;
		}
		//verify each batch has at most batchSize orders
		for (int i = 0; i < batches.length; i++) {
			if (batches[i] > batchSize) {
				return false;
			}
		}
		return true;
	}

	class TestParameters {
		private Warehouse warehouse;
		private int batchSize;
		private int orderAmount;
		private double SACooldownRate;

		public TestParameters(Warehouse warehouse, int batchSize, int orderAmount, double SACooldownRate) {
			this.warehouse = warehouse;
			this.batchSize = batchSize;
			this.orderAmount = orderAmount;
			this.SACooldownRate = SACooldownRate;
		}

		public Warehouse getWarehouse() {
			return warehouse;
		}

		public int getBatchSize() {
			return batchSize;
		}

		public int getOrderAmount() {
			return orderAmount;
		}
	}

	public static String solToString(ArrayList<Integer> solution) {
		StringBuilder str = new StringBuilder();
		for (int val : solution) {
			str.append(val);
			str.append(' ');
		}
		return str.toString();
	}
}
