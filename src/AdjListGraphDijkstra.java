import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

// Graph implementation using the adjacency list approach, with dijkstra
// to find shortest paths.
public class AdjListGraphDijkstra {
	// Controlling whether we want to do printing to console, depending on
	// if we are testing the performance or not.
	private static boolean testPerformance = true;
	
	// Maps from each node to a list of its adjacent nodes/edges.
	private Map<Integer, List<Edge>> adjLists;
	// Number of nodes in the graph, named from 0, 1, ..., size -1.
	private int size;

	public AdjListGraphDijkstra(int size) {
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

	// Returns a list of outgoing edges from node i.
	// Only (i, j)s will be returned, but (j, i)s will not.
	public List<Edge> getOutgoingEdges(int node) {
		if (node >= size) {
			System.out
					.println("Node must be in range 0, 1, ..., " + (size - 1));
			return null;
		}
		return adjLists.get(node);
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
	public static AdjListGraphDijkstra genRandomCompleteGraph(int size) {
		AdjListGraphDijkstra graph = new AdjListGraphDijkstra(size);

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

	// print out the shortest paths using Dijkstra starting from start to all
	// nodes.
	public static void printDijkstraPaths(AdjListGraphDijkstra g, int start) {
		// nodeDistances keeps the list of all nodes and their
		// currently-known-shortest
		// distance to the starting node. Initially, we know the shortest
		// distance from
		// the start node to itself is 0, and from all other nodes to the start
		// node as
		// infinity (set to Integer.MAX_VALUE)
		//
		// We use a priority queue so that the nodes will be sorted by their
		// distances.
		PriorityQueue<NodeDistance> nodeDistances = new PriorityQueue<NodeDistance>();
		// distances keeps track of each node and their shortest distance to the
		// start node.
		// This seems to have redundant information with nodeDistances, as both
		// store the node
		// and their corresponding shortest distance to the start node. However,
		// as shown below,
		// nodeDistances will be mutated during the compuation (entries will be
		// removed from it
		// until it is empty), hence, distances serves as a permanent place to
		// record the final
		// shortest distance to each node.
		HashMap<Integer, Integer> distances = new HashMap<Integer, Integer>();

		for (int node = 0; node < g.getSize(); node++) {
			if (node == start) {
				nodeDistances.add(new NodeDistance(start, 0));
				distances.put(start, 0);
			} else {
				nodeDistances.add(new NodeDistance(node, Integer.MAX_VALUE));
				distances.put(node, Integer.MAX_VALUE);
			}
		}

		// Initialize the hashmap to hold the parent of each intermediate node
		// (for outprint). The key is each node in the graph, and its
		// corresponding
		// value is the parent node (previous node) in the shortest path from
		// the start node
		// to the current node. For example, an entry (5, 3) means that in
		// shortest path from
		// the start to node "5", the previous node (parent) of node "5" is node
		// "3". As another
		// example, if the parents contains (5, 3), (3, 4), (4, start). Then we
		// can construct
		// the shortest path from start to node "5" is start - 4 - 3 - 5.
		HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();

		// Repeatedly extrat out and examine the node currently closest to the
		// start node,
		// until there are no nodes left in the priority queue (nodeDistances).
		while (!nodeDistances.isEmpty()) {
			// get the node currently closest to the start node.
			NodeDistance cur = nodeDistances.poll();
			int currentNode = cur.getNode();

			// Get neighbors of this current node. See if any neighbor will find
			// a shorter
			// path than what they currently have using the current node as an
			// intermediate node
			List<Edge> outgoingEdges = g.getOutgoingEdges(currentNode);
			// Examining each neighor (edge).
			for (Edge e : outgoingEdges) {
				int neighbor = e.getNode2();

				// newDist is the distance of the path that uses the current
				// node
				// as an intermediate node to its neighbor.
				long newDist = cur.getDistance() + e.getWeight();
				long oldDist = distances.get(neighbor);

				if (newDist < oldDist) {
					parents.put(neighbor, currentNode);

					// update the priority queue by removing the entry and then
					// re-add it
					// to cause the priority queue to re-sort with the new
					// distance.
					//
					// Note that we can remove and add the NodeDistance object
					// from nodeDistances
					// because we have implemented/customized equals() method in
					// nodeDistance class.
					//
					// Also note that the priority queue will do the correct
					// sorting because we have
					// implemented/customized the Comparable method for the
					// nodeDistance class.
					nodeDistances.remove(new NodeDistance(neighbor, oldDist));
					nodeDistances.add(new NodeDistance(neighbor, newDist));

					// update the actual distance
					distances.put(neighbor, (int) newDist);
				}
			}
		}

		System.out.println("The shortest paths from " + start
				+ " to all nodes are:\n");
		// Now lets reconstruct the paths from the parents data structure.
		// We find the path for each node ('finish') in the graph.
		for (int finish = 0; finish < g.getSize(); finish++) {
			reconstructAndPrintPath(start, finish, parents);
		}

	}

	private static void reconstructAndPrintPath(int start, int finish,
			HashMap<Integer, Integer> parents) {
		StringBuffer sb = new StringBuffer();
		sb.append("from " + start + " to " + finish + ": ");

		// Use linkedList so that we can easily insert element at front of the
		// list.
		List<Integer> path = new LinkedList<Integer>();
		if (finish == start) {
			path.add(start);
		} else {
			// reconstruct the path backwards by always inserting at the head of
			// the list.
			path.add(0, finish);
			Integer prevNode = parents.get(finish);
			while (prevNode.intValue() != start) {
				path.add(0, prevNode);
				prevNode = parents.get(prevNode);
			}
			path.add(0, start);
		}

		sb.append(path);
		if (!AdjListGraphDijkstra.testPerformance) {
			System.out.println(sb);
		}
	}

	public static void main(String[] args) {
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		// The memory already allocated without running our algorithm or generating data structure.
		long baseMem = totalMem - freeMem;
		
		AdjListGraphDijkstra graph = genRandomCompleteGraph(30000);
		freeMem = Runtime.getRuntime().freeMemory();
		System.out.println("Usage (MB) by data structure: " + (totalMem - freeMem - baseMem) / 1000000);
		
		long startTime = System.currentTimeMillis();
		if (!AdjListGraphDijkstra.testPerformance) {
			System.out.println("The adjacency list graph is \n" + graph + "\n\n");
		}
		printDijkstraPaths(graph, 0);
		freeMem = Runtime.getRuntime().freeMemory();
		System.out.println("Usage (MB) by data structure and algorithm: " + (totalMem - freeMem - baseMem) / 1000000);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The runtime is " + totalTime + " milliseconds");
		
		// Testing results
		// # number of nodes (x1000), time (milliseconds)
		// 1 117
		// 3 539
		// 5 1139
		// 7 2148
		// 9 4516
		// 15 8750
		// 20 41436
		// 30 Too long
		
		// Storage testing results
		// # number of nodes (x1000), usage for DS, usage for DS + algorithm
		// 1 58 75
		// 3 362 400
		// 5 1012 1067
		// 7 1998 2003
		// 9 3478 3496
		// 15 8373 9274
		// 20 14943 14981
	}
}