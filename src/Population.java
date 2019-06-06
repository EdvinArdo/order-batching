import javafx.util.Pair;

import java.util.*;

public class Population {

	private ArrayList<Solution> chromosomes;
	private int populationSize;
	private Warehouse warehouse;
	private ArrayList<Double> probabiltyArray;
	private int eliteOffset;
	private int tournamentSize;
	private double probability;
	private int fittest;
	private Solution bestSolution;

	private ArrayList<Pair<Long, Integer>> bestSolutionsTimes = new ArrayList<>();
	private long startTime;
	private Random random = new Random(System.currentTimeMillis());

	public Population(Warehouse ware) {
		warehouse = ware;
		chromosomes = new ArrayList<>();
		eliteOffset = 2;
		tournamentSize = 4;
		populationSize = 30;
		probability = 0.1;
	}


	//orders, batchsize, tid den får i lång
	//skicka med varuhuset

	public Result solve(int orders, int batchSize, long timeLimit) {
		startTime = System.currentTimeMillis();

		createPopulation(orders, batchSize);
		bestSolution = new Solution(chromosomes.get(0));
		fittest = bestSolution.getDistance();
		bestSolutionsTimes.add(new Pair<>(System.currentTimeMillis() - startTime, fittest));
		int gens = 0;
		while (System.currentTimeMillis() - startTime < timeLimit) {
			gens++;
			selection(eliteOffset, tournamentSize, batchSize, probability);
			int fitness = chromosomes.get(0).getDistance();
			if (fitness < fittest) {
				fittest = fitness;
				bestSolution = new Solution(chromosomes.get(0));
				bestSolutionsTimes.add(new Pair<>(System.currentTimeMillis() - startTime, fittest));
			}
		}
		System.out.println("Generations: " + gens);
		System.out.println("GA solution: " + TestFramework.solToString(bestSolution.getSolution()));
		System.out.println("GA score: " + bestSolution.getDistance());
		System.out.println();

		return new Result(bestSolution.getSolution(), bestSolutionsTimes);
	}


	//Create initial population with random but valid solutions
	public void createPopulation(int amountOfOrders, int batchSize) {
		//create and array matrix
		ArrayList<Solution> pop = new ArrayList<>();  //arrayList matrix
		//pop.add(new ArrayList<>());

		//fill the array matrix with random solutions
		int count = 0;
		for (int i = 0; i < populationSize; i++) {
			pop.add(new Solution(warehouse, new ArrayList<>()));
			for (int j = 0; j < amountOfOrders; j++) {
				if (j % batchSize == 0 && j != 0) {
					count++;
				}
				pop.get(i).getSolution().add(count);
			}
			Collections.shuffle(pop.get(i).getSolution(), random);
			count = 0;
		}
		pop.sort(new solutionCompare());
		chromosomes = pop;
	}


	//{1,1,3,2,2}

	//Select the population for the new generation
	//The eliteOffset determines how many of the top canidiates will be kept for the next generation
	public void selection(int eliteOffset, int tournamentSize, int batchSize, double probability) {
		ArrayList<Solution> newPop = new ArrayList<>();  //arrayList matrix

		for (int i = 0; i < eliteOffset; i++) {
			newPop.add(chromosomes.get(i));
		}

		for (int i = 0; i < eliteOffset; i++) {
			newPop.add(new Solution(warehouse, GA.mutate2(chromosomes.get(i).getSolution(), batchSize)));
		}

		for (int i = eliteOffset * 2; i < chromosomes.size(); i++) {
			Solution parent1 = tournament(tournamentSize);
			Solution parent2 = tournament(tournamentSize);

			Solution newChild = new Solution(warehouse, GA.crossover(parent1.getSolution(), parent2.getSolution()));   //Crossover

			if (random.nextFloat() <= probability) {
				newChild = new Solution(warehouse, GA.rep(batchSize, GA.mutate2(newChild.getSolution(), batchSize)));
			} else {
				newChild = new Solution(warehouse, GA.rep(batchSize, newChild.getSolution()));
			}

			newPop.add(newChild);
		}
		newPop.sort(new solutionCompare());
		chromosomes = newPop;

	}


	//Roulette OLD, not that fast tbh
	//##############################
	public ArrayList<Integer> rouletteBall() {
		double randomNumber = Math.random();
		double currentValue = 0;
		for (int i = 0; i < probabiltyArray.size(); i++) {
			if (randomNumber >= currentValue && randomNumber < probabiltyArray.get(i) + currentValue) {
				return chromosomes.get(i).getSolution();
			}
			currentValue = currentValue + probabiltyArray.get(i);
		}
		return null;
	}

	public void generateRoulette() {
		probabiltyArray = new ArrayList<>();
		double totalFitness = totalFitness(warehouse);
		for (int i = 0; i < chromosomes.size(); i++) {
			double fitness = chromosomes.get(i).getDistance();
			probabiltyArray.add(fitness / totalFitness);
		}
	}
	//#################################


	//Select tournamentSize amount of chromosones and return the one with best fitness
	private Solution tournament(int tournamentSize) {
		ArrayList<Solution> contestants = new ArrayList<>(tournamentSize);
		for (int i = 0; i < tournamentSize; i++) {
			int randomId = random.nextInt(chromosomes.size());
			contestants.add(chromosomes.get(randomId));
		}

		return Collections.min(contestants, new solutionCompare());//fittest chromo
	}

	public double totalFitness(Warehouse warehouse) {
		double totalFitness = 0;
		for (int i = 0; i < populationSize; i++) {
			totalFitness += chromosomes.get(i).getDistance();
		}
		return totalFitness;
	}

	public void printTopInd(Warehouse warehouse) {
		System.out.println("Best individual " + chromosomes.get(0).getDistance());
	}

	public void printWorst(Warehouse warehouse) {
		System.out.println("Worst individual " + chromosomes.get(chromosomes.size() - 1).getDistance());
	}

	public void printBestSolution(Warehouse warehouse) {
		System.out.println(chromosomes.get(0).getSolution());
	}


	public class solutionCompare implements Comparator<Solution> {
		public int compare(Solution p1, Solution p2) {
			return p1.getDistance() - p2.getDistance();
		}
	}


}


