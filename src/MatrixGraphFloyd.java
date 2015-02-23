import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Implementing the undirected graph using adjacency matrix, using Floyd
// to find shortest paths.
public class MatrixGraphFloyd {
	// Controlling whether we want to do printing to console, depending on
	// if we are testing the performance or not.
	private static boolean testPerformance = true;
	
	// Adjacency matrix for the graph
	private long[][] matrix;
	// Number of nodes in the graph.
	private int size;
	// Assume/use Integer.MAX_VALUE as the weight for indicating unconnected
	// nodes.
	private final static int MAX = Integer.MAX_VALUE;

	// n is the number of nodes in the graph. They will be named 0, 1, ... n-1.
	public MatrixGraphFloyd(int n) {
		size = n;
		matrix = new long[size][size];

		// Init the matrix.
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j) {
					// Nodes are connected to themselves.
					matrix[i][j] = 0;
				} else {
					// Set all the weights to be MAX to assume nodes are not
					// connected.
					matrix[i][j] = MAX;
				}
			}
		}
	}

	public int getSize() {
		return size;
	}

	public void addEdge(int node1, int node2, int weight) {
		// Validating inputs.
		if (node1 >= size || node2 >= size) {
			System.out.println("Error: nodes must be named from 0, 1, ... "
					+ (size - 1));
			return;
		}

		// Since it is undirected graph, need to keep symmetry.
		matrix[node1][node2] = weight;
		matrix[node2][node1] = weight;
	}

	// Gets all edges in the graph. Both (x, y, weight) and (y, x, weight)
	// will be returned.
	public List<Edge> getAllEdges() {
		List<Edge> edges = new ArrayList<Edge>();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (matrix[i][j] != MAX) {  // indicate i and j have an edge.
					edges.add(new Edge(i, j, matrix[i][j]));
				}
			}
		}

		return edges;
	}

	// Prints out the graph nicely
	@Override
	public String toString() {
		return Utils.printMatrix(matrix, size);
	}

	// Generate a complete graph with random weights.
	public static MatrixGraphFloyd genRandomCompleteGraph(int size) {
		MatrixGraphFloyd graph = new MatrixGraphFloyd(size);

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
	public static void printFloydPaths(MatrixGraphFloyd graph) {
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
					distances[i][j] = MAX;
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

		// System.out.println("The shortest distances are \n" +
		// Utils.printMatrix(distances, graph.getSize()) + "\n\n");

		System.out.println("The shortest paths are: \n");
		for (int i = 0; i < graph.getSize(); i++) {
			for (int j = 0; j < graph.getSize(); j++) {
				if (i != j) {
					if (!MatrixGraphFloyd.testPerformance) {
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
		MatrixGraphFloyd graph = genRandomCompleteGraph(10);
		long startTime = System.currentTimeMillis();
		if (!MatrixGraphFloyd.testPerformance) {
			System.out.println("The matrix graph is \n" + graph + "\n\n");
		}
		printFloydPaths(graph);
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The runtime is " + totalTime + " milliseconds");
	}
}
