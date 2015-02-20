public class Edge {
	private int node1;
	private int node2;
	private long weight;

	public Edge(int i, int j, long weight) {
		super();
		this.node1 = i;
		this.node2 = j;
		this.weight = weight;
	}

	public int getNode1() {
		return node1;
	}

	public void setNode1(int node1) {
		this.node1 = node1;
	}

	public int getNode2() {
		return node2;
	}

	public void setNode2(int node2) {
		this.node2 = node2;
	}

	public long getWeight() {
		return weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "(" + node1 + ", " + node2 + ", " + weight + ")"; 
	}
}
