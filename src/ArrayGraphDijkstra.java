import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

// Graph implementation using a 1-D array.
// Due to symmetry of an undirected graph, this array essentially
// stores the bottom-left half of the adjacency matrix.
// It also uses Dijkstra to find all pair shortest paths.
public class ArrayGraphDijkstra {
	// The 1-D array for storing the nodes.
	private Edge[] graphArray;
	// The number of the nodes, named as 0, 1, ... size - 1.
	private int size;
	// The size of the 1-D array
	private int arraySize;

	public ArrayGraphDijkstra(int size) {
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

	// Returns a list of outgoing edges from node i.
	// Only (i, j)s will be returned, but (j, i)s will not.
	public List<Edge> getOutgoingEdges(int node) {
		List<Edge> outgoingEdges = new ArrayList<Edge>();
		for (Edge edge : graphArray) {
			if (edge == null) { // there is no edge between the two
								// corresponding nodes.
				continue;
			}

			if (edge.getNode1() == node) {
				outgoingEdges.add(edge);
			}
			// Due to symmetry, only need to explicitly populate half of the
			// matrix.
			if (edge.getNode2() == node) {
				outgoingEdges.add(new Edge(node, edge.getNode1(), edge.getWeight()));
			}
		}
		return outgoingEdges;
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
	public static ArrayGraphDijkstra genRandomCompleteGraph(int size) {
		ArrayGraphDijkstra graph = new ArrayGraphDijkstra(size);

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
	public static void printDijkstraPaths(ArrayGraphDijkstra g, int start) {
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
		System.out.println(sb);
	}

	public static void main(String[] args) {
		ArrayGraphDijkstra graph = genRandomCompleteGraph(10);
		System.out.println("The 1-D array graph is \n" + graph + "\n\n");
		printDijkstraPaths(graph, 0);
	}
}
