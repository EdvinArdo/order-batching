import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Warehouse {

	private static Random rand = new Random(0);

	private int[][] warehouse;
	private int[][] orderAccessPoints;
	private DistanceGraph distanceGraph;
	private int maxOrders;

	public Warehouse(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			int width = Integer.parseInt(reader.readLine());
			int height = Integer.parseInt(reader.readLine());
			maxOrders = Integer.parseInt(reader.readLine());
			warehouse = new int[height][width];

			ArrayList<Integer> orders = new ArrayList<>(maxOrders);
			for (int i = 0; i < maxOrders; i++) {
				orders.add(i);
			}
			Collections.shuffle(orders);
			int order = 0;
			for (int y = 0; y < height; y++) {
				String line = reader.readLine();
				for (int x = 0; x < width; x++) {
					char val = line.charAt(x);

					if (val == '+') {
						warehouse[y][x] = -2;
					} else if (val == 'X') {
						warehouse[y][x] = -3;
					} else if (val == 'O') {
						warehouse[y][x] = orders.get(order++);
					} else if (val == '#') {
						warehouse[y][x] = -1;
					}
				}
			}
		} catch (IOException e) {}

		orderAccessPoints = getWarehouseOrderAccessPoints(warehouse);
		distanceGraph = new DistanceGraph(maxOrders);

		List<Pair<Integer, Integer>> orderPositions = new ArrayList<>(maxOrders);
		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				int order = warehouse[y][x];
				if (order >= 0) {
					orderPositions.add(new Pair<>(x, y));
				}
			}
		}

		for (int order = 0; order < maxOrders; order++) {
			int x = orderPositions.get(order).getKey();
			int y = orderPositions.get(order).getValue();

			generateDistances(x, y, warehouse);
		}
	}

	public Warehouse(int size, int crossAisles) {
		warehouse = new int[size][size];
		maxOrders = generateWarehouse(warehouse, crossAisles);
		orderAccessPoints = getWarehouseOrderAccessPoints(warehouse);
		distanceGraph = new DistanceGraph(maxOrders);

		List<Pair<Integer, Integer>> orderPositions = new ArrayList<>(maxOrders);
		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				int order = warehouse[y][x];
				if (order >= 0) {
					orderPositions.add(new Pair<>(x, y));
				}
			}
		}

		for (int order = 0; order < maxOrders; order++) {
			int x = orderPositions.get(order).getKey();
			int y = orderPositions.get(order).getValue();

			generateDistances(x, y, warehouse);
		}
	}

	private void generateDistances(int x, int y, int[][] warehouse) {
		int order = warehouse[y][x];
		boolean[][] visited = new boolean[warehouse.length][warehouse[0].length];
		Queue<Pair<Integer, Integer>> current = new LinkedList<>();

		//Set access point for shelf, priority in descending order: up, down, left, right
		if (y > 0 && warehouse[y - 1][x] <= -2) current.add(new Pair<>(x, y - 1));
		else if (y < warehouse.length - 1 && warehouse[y + 1][x] <= -2) current.add(new Pair<>(x, y + 1));
		else if (x > 0 && warehouse[y][x - 1] <= -2) current.add(new Pair<>(x - 1, y));
		else if (x < warehouse[0].length - 1 && warehouse[y][x + 1] <= -2) current.add(new Pair<>(x + 1, y));

		generateDistancesRec(order, visited, warehouse, current, 0);
	}

	private void generateDistancesRec(int order, boolean[][] visited, int[][] warehouse, Queue<Pair<Integer, Integer>> current, int distance) {
		int currentSize = current.size();
		if (currentSize == 0) {
			return;
		}

		for (int i = 0; i < currentSize; i++) {
			Pair<Integer, Integer> currentTile = current.remove();
			int x = currentTile.getKey();
			int y = currentTile.getValue();
			visited[y][x] = true;

			if (warehouse[y][x] <= -2) {//path or drop-off
				if (x > 0 && !visited[y][x - 1]) {
					current.add(new Pair<>(x - 1, y));
					visited[y][x - 1] = true;
				}
				if (y > 0 && !visited[y - 1][x]) {
					current.add(new Pair<>(x, y - 1));
					visited[y - 1][x] = true;
				}
				if (x < warehouse[0].length - 1 && !visited[y][x + 1]) {
					current.add(new Pair<>(x + 1, y));
					visited[y][x + 1] = true;
				}
				if (y < warehouse.length - 1 && !visited[y + 1][x]) {
					current.add(new Pair<>(x, y + 1));
					visited[y + 1][x] = true;
				}

				if (warehouse[y][x] == -3) {//drop-off
					distanceGraph.getOrder(order).setDropOffDistance(distance);
				}

				int orderAccessPoint = orderAccessPoints[y][x];
				if (orderAccessPoint >= 0) {
					distanceGraph.getOrder(order).setOrderDistance(orderAccessPoint, distance);
				}
			}
		}

		generateDistancesRec(order, visited, warehouse, current, distance + 1);
	}

	private int[][] getWarehouseOrderAccessPoints(int[][] warehouse) {
		int[][] orderAccessPoints = new int[warehouse.length][warehouse[0].length];

		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				orderAccessPoints[y][x] = -1;
			}
		}

		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				int order = warehouse[y][x];
				if (order >= 0) {//shelf
					if (y > 0 && warehouse[y - 1][x] <= -2) orderAccessPoints[y - 1][x] = order;//up
					else if (y < warehouse.length - 1 && warehouse[y + 1][x] <= -2) orderAccessPoints[y + 1][x] = order;//down
					else if (x > 0 && warehouse[y][x - 1] <= -2) orderAccessPoints[y][x - 1] = order;//left
					else if (x < warehouse[0].length - 1 && warehouse[y][x + 1] <= -2) orderAccessPoints[y][x + 1] = order;//right
				}
			}
		}
		return orderAccessPoints;
	}

	private int[][] copyOf(int[][] array) {
		int height = array.length;
		int width = array[0].length;

		int[][] newArray = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newArray[y][x] = array[y][x];
			}

		}
		return newArray;
	}


	private class DistanceGraph {
		private Node[] orders;

		private DistanceGraph(int orders) {
			this.orders = new Node[orders];
			for (int i = 0; i < orders; i++) {
				this.orders[i] = new Node(orders);
			}
		}

		public Node getOrder(int order) {
			return orders[order];
		}

		private class Node {
			private int[] orderDistances;
			private int dropOffDistance;

			public int getDistance(int order) {
				return orderDistances[order];
			}

			public int getDropOffDistance() {
				return dropOffDistance;
			}

			private Node(int orders) {
				orderDistances = new int[orders];
			}

			void setOrderDistance(int order, int distance) {
				orderDistances[order] = distance;
			}

			void setDropOffDistance(int dropOffDistance) {
				this.dropOffDistance = dropOffDistance;
			}
		}
	}

	//3 st
	//10 lång
	//10/3 -> 3+1 = 4

	// 10%4
	//0 1 2 3 0 1 2 3 0 1

	public int generateWarehouse(int[][] warehouse, int crossAisles) {
		int aisleValue;
		if (crossAisles <= 0) {
			aisleValue = Integer.MAX_VALUE;
		} else {
			aisleValue = ((warehouse[0].length - 2 - crossAisles) / crossAisles);
		}
		int usedSlots = 0;
		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				if (x == 0 && y == 0) {
					warehouse[y][x] = -3;
					usedSlots++;
				} else if (y % 3 == 0) {
					warehouse[y][x] = -2;
					usedSlots++;
				} else if (x % aisleValue == 0) {
					warehouse[y][x] = -2;
					usedSlots++;
				} else if (x == warehouse[0].length - 1) {
					warehouse[y][x] = -2;
					usedSlots++;
				} else {
					warehouse[y][x] = 0;
				}

			}
		}

		int canHold = warehouse.length * warehouse[0].length - usedSlots;
		ArrayList<Integer> items = new ArrayList<>(canHold);
		Stack<Integer> itemStack = new Stack<>();

		for (int i = 0; i < canHold; i++) {
			items.add(i);
		}
		Collections.shuffle(items, rand);
		itemStack.addAll(items);

		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				if (warehouse[y][x] == 0) {
					warehouse[y][x] = itemStack.pop();
				}

			}
		}

		return canHold;
	}

	//cross iles
	//gång var tredje rad
	//orders slumpmässigt

	public int TSP(ArrayList<Integer> orders) {
		ArrayList<ArrayList<Integer>> routes = permutations(orders);
		ArrayList<Integer> routeDistances = new ArrayList<>(routes.size());

		for (ArrayList<Integer> route : routes) {
			routeDistances.add(routeDistance(route));
		}

		return Collections.min(routeDistances);
	}

	public int distance(ArrayList<Integer> solution) {
		int batchAmount = Collections.max(solution) + 1;
		ArrayList<ArrayList<Integer>> batches = new ArrayList<>(batchAmount);
		for (int i = 0; i < batchAmount; i++) {
			batches.add(new ArrayList<>());
		}

		for (int i = 0; i < solution.size(); i++) {
			batches.get(solution.get(i)).add(i);
		}

		int distance = 0;
		for (ArrayList<Integer> batch : batches) {
			distance += TSP(batch);
		}

		return distance;
	}

	public int distance(int order1, int order2) {
		return distanceGraph.getOrder(order1).getDistance(order2);
	}

	private int routeDistance(ArrayList<Integer> route) {
		DistanceGraph.Node order = distanceGraph.getOrder(route.get(0));
		int distance = order.getDropOffDistance();
		for (int i = 1; i < route.size(); i++) {
			order = distanceGraph.getOrder(route.get(i));
			distance += order.getDistance(route.get(i - 1));
		}
		distance += order.getDropOffDistance();
		return distance;
	}

	private ArrayList<ArrayList<Integer>> permutations(ArrayList<Integer> remaining) {
		ArrayList<ArrayList<Integer>> routes = new ArrayList<>();
		if (remaining.size() == 1) {
			ArrayList<Integer> route = new ArrayList<>();
			route.add(remaining.get(0));
			routes.add(route);
		} else {
			for (int i = 0; i < remaining.size(); i++) {
				ArrayList<Integer> newRemaining = new ArrayList<>(remaining);
				newRemaining.remove(i);
				ArrayList<ArrayList<Integer>> subRoutes = permutations(newRemaining);
				for (int j = 0; j < subRoutes.size(); j++) {
					subRoutes.get(j).add(remaining.get(i));
					routes.add(subRoutes.get(j));
				}
			}
		}
		return routes;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		for (int y = 0; y < warehouse.length; y++) {
			for (int x = 0; x < warehouse[0].length; x++) {
				switch (warehouse[y][x]) {
					case -3:
						str.append('X');
						break;
					case -2:
						str.append('+');
						break;
					case -1:
						str.append('#');
						break;
					default:
						str.append(warehouse[y][x]);
						break;
				}
				str.append('\t');
			}
			str.append('\n');
		}

		return str.toString();
	}

	public int getMaxOrders() {
		return maxOrders;
	}
}