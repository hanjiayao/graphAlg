import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Author: Jiayao Han

// Graph implementation using a 1-D array.
// Due to symmetry of an undirected graph, this array essentially
// stores the bottom-left half of the adjacency matrix.
// It also uses Floyd to find all pair shortest paths.
public class ArrayGraphFloyd {
	// Controlling whether we want to do printing to console, depending on
	// if we are testing the performance or not.
	private static boolean testPerformance = true;
	
	// The 1-D array for storing the nodes.
	private Edge[] graphArray;
	// The number of the nodes, named as 0, 1, ... size - 1.
	private int size;
	// The size of the 1-D array
	private int arraySize;

	public ArrayGraphFloyd(int size) {
		// If there are N nodes in the graph, the bottom-left half
		// of the adjacency matrix contains N * (N-1) / 2 nodes, which
		// is the size of the 1-D array.
		arraySize = size * (size - 1) / 2;
		this.size = size;
		graphArray = new Edge[arraySize];
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

		// Only keeps the bottom-left half of the matrix.
		Edge edge;
		if (i > j) {
			edge = new Edge(i, j, weight);
		} else {
			edge = new Edge(j, i, weight);
		}

		// Translate (i, j) into an index in the 1-D array.
		int arrayIndex = i * (i - 1) / 2 + j;
		graphArray[arrayIndex] = edge;
	}

	// Returns all edges, include both (i, j)s and (j, i)s.
	public List<Edge> getAllEdges() {
		List<Edge> allEdges = new ArrayList<Edge>();
		for (Edge edge : graphArray) {
			if (edge == null) {  // there is no edge between the two corresponding nodes.
				continue;
			}
			
			allEdges.add(edge);
			// Since graphArray only stores the bottom-half of the matrix, we
			// need
			// to construct and return the other set of the edges.
			Edge symmetricEdge = new Edge(edge.getNode2(), edge.getNode1(),
					edge.getWeight());
			allEdges.add(symmetricEdge);
		}
		return allEdges;
	}

	// Prints out the graph nicely
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		// After how many elements should we add a line break.
		int lineBreak = 1;
		// How many nodes have we encountered in one row.
		int nodeCount = 0;
		for (int i = 0; i < graphArray.length; i++) {
			sb.append(graphArray[i] + "\t");
			nodeCount++;

			if (nodeCount % lineBreak == 0) {
				nodeCount = 0;
				lineBreak++;
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	// Generate a complete graph with random weights.
	public static ArrayGraphFloyd genRandomGraph(int size) {
		ArrayGraphFloyd graph = new ArrayGraphFloyd(size);

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
	public static void printFloydPaths(ArrayGraphFloyd graph) {
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
					if (!ArrayGraphFloyd.testPerformance) {
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
		ArrayGraphFloyd graph = genRandomGraph(11000);
		long startTime = System.currentTimeMillis();
		if (!ArrayGraphFloyd.testPerformance) {
			System.out.println("The 1-D array graph is \n" + graph + "\n\n");
		}
		printFloydPaths(graph);
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The runtime is " + totalTime + " milliseconds");
		
		// Testing results
		// # number of nodes (x1000), time (milliseconds)
		// 1 793
		// 3 19384
		// 5 98999
		// 7 244671
		// 9 509277
		// 11 927476
	}
}
