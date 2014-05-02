import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author etlove
 */
public class SAP {
	
	private final Digraph graph;
	/**
	 * constructor takes a digraph (not necessarily a DAG)
	 * @param G
	 */
	public SAP(Digraph G) {
		this.graph = new Digraph(G);
	}
	/**
	 * length of shortest ancestral path between v and w; -1 if no such path
	 * @param v
	 * @param w
	 * @return
	 */
	public int length(int v, int w) {
		verify(v, w);
		
		BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(graph, w);
		
		int ancestor = ancestor(v, w);
		int length;
		
		if (ancestor == -1) length = -1;
		else length = vBfs.distTo(ancestor) + wBfs.distTo(ancestor);
		
		return length;
	}
	/**
	 * a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	 * @param v
	 * @param w
	 * @return
	 */
	public int ancestor(int v, int w) {
		verify(v, w);
		
		BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(graph, w);
		
		int ancestor = -1;
		int minPath = Integer.MAX_VALUE;
		Deque<Integer> ancestors = new ArrayDeque<Integer>();
		
		for (int i = 0; i < graph.V(); i++) {
			if (vBfs.hasPathTo(i) && wBfs.hasPathTo(i)) {
				ancestors.push(i);
			}
		}
		
		for (int i : ancestors) {
			int path = vBfs.distTo(i) + wBfs.distTo(i);
			if (minPath > path) {
				minPath = path;
				ancestor = i;
			}
		}
		
		return ancestor;
	}
	/**
	 * length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	 * @param v
	 * @param w
	 * @return
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		verify(v, w);
		
		BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(graph, w);
		
		int ancestor = ancestor(v, w);
		int length;
		
		if (ancestor == -1) length = -1;
		else length = vBfs.distTo(ancestor) + wBfs.distTo(ancestor);
		
		return length;
	}
	/**
	 * a common ancestor that participates in shortest ancestral path; -1 if no such path
	 * @param v
	 * @param w
	 * @return
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		verify(v, w);
		
		BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(graph, v);
		BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(graph, w);
		
		int ancestor = -1;
		int minPath = Integer.MAX_VALUE;
		Deque<Integer> ancestors = new ArrayDeque<Integer>();
		
		for (int i = 0; i < graph.V(); i++) {
			if (vBfs.hasPathTo(i) && wBfs.hasPathTo(i)) {
				ancestors.push(i);
			}
		}
		
		for (int i : ancestors) {
			int path = vBfs.distTo(i) + wBfs.distTo(i);
			if (minPath > path) {
				minPath = path;
				ancestor = i;
			}
		}
		
		return ancestor;
	}
	/**
	 * check input.
	 * @param values
	 */
	private void verify(int value) {
		if (value < 0 || value >= graph.V()) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	private void verify(int a, int b) {
		verify(a);
		verify(b);
	}
	/**
	 * 
	 * @param values
	 */
	private void verify(Iterable<Integer> values1, Iterable<Integer> values2) {
		for (int v : values1) {
			verify(v);
		}
		for (int v : values2) {
			verify(v);
		}
	}
	/**
	 * for unit testing of this class (such as the one below)
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
