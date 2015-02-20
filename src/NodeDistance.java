// A helper class for dijkstra that represents the distance from a source to the
// specified node.
// This class implements the comparable interface, so when being used in a priority queue,
// the class instances will be sorted by their distances.
public class NodeDistance implements Comparable<NodeDistance> {
	private int node; // the current node
	private long distance; // distance from a source node to this current node

	public NodeDistance(int node, long oldDist) {
		this.node = node;
		this.distance = oldDist;
	}

	public int getNode() {
		return node;
	}

	public long getDistance() {
		return distance;
	}

	public int compareTo(NodeDistance o) {
		if (distance > o.distance)
			return 1;
		else if (distance < o.distance)
			return -1;
		else
			return (o.node == node ? 0 : 1);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long result = 1;
		result = prime * result + distance;
		result = prime * result + node;

		return (int)result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		NodeDistance other = (NodeDistance) obj;
		if (distance != other.distance)
			return false;

		if (node != other.node)
			return false;
		
		return true;
	}

}
