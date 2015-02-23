import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

// Graph implementation using the adjacency list approach, with Floyd method
// to find all pair shortest paths.
public class AdjListGraphFloyd {
	// Controlling whether we want to do printing to console, depending on
	// if we are testing the performance or not.
	private static boolean testPerformance = true;

	// Maps from each node to a list of its adjacent nodes/edges.
	private Map<Integer, List<Edge>> adjLists;
	// Number of nodes in the graph, named from 0, 1, ..., size -1.
	private int size;

	public AdjListGraphFloyd(int size) {
		this.size = size;
		adjLists = new HashMap<Integer, List<Edge>>();

		// Loop through each node to init its adjacency list.
		for (int i = 0; i < size; i++) {
			adjLists.put(i, new ArrayList<Edge>());
		}
	}

	public int getSize() {
		return size;
	}

	public void addEdge(int i, int j, int weight) {
		// Validating inputs.
		if (i >= size || j >= size) {
			System.out.println("Error: nodes must be named from 0, 1, ... "
					+ (size - 1));
			return;
		}

		adjLists.get(i).add(new Edge(i, j, weight));
		// Need to keep symmetry.
		adjLists.get(j).add(new Edge(j, i, weight));
	}

	public List<Edge> getAllEdges() {
		List<Edge> allEdges = new ArrayList<Edge>();
		// Add all the edges in the map in to allEdges.
		for (List<Edge> adjEdges : adjLists.values()) {
			allEdges.addAll(adjEdges);
		}
		return allEdges;
	}

	@Override
	public String toString() {
		// Use tree set so that we can get the nodes in order.
		TreeSet<Integer> nodes = new TreeSet<Integer>(adjLists.keySet());

		// Use StringBuilder for efficient string manipulation/mutation.
		StringBuilder sb = new StringBuilder();
		for (Integer node : nodes) {
			sb.append(node + ": " + adjLists.get(node) + "\n");
		}

		return sb.toString();
	}

	// Generate a complete graph with random weights.
	public static AdjListGraphFloyd genRandomGraph(int size) {
		AdjListGraphFloyd graph = new AdjListGraphFloyd(size);

		// Randomly generate the weights and add edges.
		Random randomGenerator = new Random();
		for (int i = 0; i < size; i++) {
			// Due to symmetry, only need to explicitly populate half of the
			// matrix.
			for (int j = 0; j < i; j++) {
				int randomWeight = randomGenerator.nextInt(100);
				graph.addEdge(i, j, randomWeight);
			}
		}
		return graph;
	}

	// Finds and prints out shortest paths between nodes in the given graph
	// using the Floyd method:
	// http://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
	public static void printFloydPaths(AdjListGraphFloyd graph) {
		// let distances indicate the shortest distance between nodes i and j.
		long[][] distances = new long[graph.getSize()][graph.getSize()];
		// let next[i][j] be the first next node in the path from i to j (the
		// "next-hop" of i in the path to j).
		int[][] next = new int[graph.getSize()][graph.getSize()];

		// Initially, nodes are not connected so their distances are set to MAX,
		// except that nodes are connected to themselves with distance 0.
		for (int i = 0; i < graph.getSize(); i++) {
			for (int j = 0; j < graph.getSize(); j++) {
				if (i == j) {
					distances[i][j] = 0;
					next[i][j] = 0;
				} else {
					distances[i][j] = Integer.MAX_VALUE;
					next[i][j] = -1;
				}
			}
		}

		// Base case: if two nodes are already connected, update their
		// distances.
		// Also, if i and j are directly connected, and then the *next node*
		// from i
		// to j is just j.
		List<Edge> edges = graph.getAllEdges();
		for (Edge edge : edges) {
			distances[edge.getNode1()][edge.getNode2()] = edge.getWeight();
			next[edge.getNode1()][edge.getNode2()] = edge.getNode2();
		}

		// Each k is the biggest node name we can use for constructing the
		// shortest paths.
		for (int k = 0; k < graph.getSize(); k++) {
			for (int i = 0; i < graph.getSize(); i++) {
				for (int j = 0; j < graph.getSize(); j++) {
					long newDistance = distances[i][k] + distances[k][j];
					if (newDistance < distances[i][j]) {
						distances[i][j] = newDistance;
						next[i][j] = next[i][k];
					}
				}
			}
		}

		System.out.println("The shortest paths are: \n");
		for (int i = 0; i < graph.getSize(); i++) {
			for (int j = 0; j < graph.getSize(); j++) {
				if (i != j) {
					if (!AdjListGraphFloyd.testPerformance) {
						System.out.println("From " + i + " to " + j + ": "
								+ getPath(i, j, next));
					}
				}
			}
		}
	}

	private static List<Integer> getPath(int source, int dest, int[][] next) {
		List<Integer> path = new ArrayList<Integer>();
		if (next[source][dest] == -1) {
			return path;
		}

		path.add(source);
		while (source != dest) {
			source = next[source][dest];
			path.add(source);
		}

		return path;
	}

	public static void main(String[] args) {
		AdjListGraphFloyd graph = genRandomGraph(11000);

		long startTime = System.currentTimeMillis();
		if (!AdjListGraphFloyd.testPerformance) {
			System.out.println("The adjacency list graph is \n" + graph
					+ "\n\n");
		}
		printFloydPaths(graph);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The runtime is " + totalTime + " milliseconds");

		// Testing results
		// # number of nodes (x1000), time (milliseconds)
		// 1 775
		// 3 17825
		// 5 91259
		// 7 266299
		// 9 525288
		// 11 1017860
		// 15 Too long
	}
}
