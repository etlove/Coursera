/**
 * Coursera Programming Assignment<br/>
 * Algorithm Part 1 - Week 4.
 */
/**
 * @author etlove
 */
public class Solver {
	/**
	 * Result Node
	 */
	private BoardNode result;
	/**
	 * Find a solution to the initial board (using the A* algorithm).
	 * @param initial
	 */
	public Solver(Board initial) {
		if (initial.isGoal()) {
			result = new BoardNode(initial, null);
		} else {
			result = solve(initial);
		}
	}
	/**
	 * Check if the initial board solvable?
	 * @return true if solvable else false
	 */
	public boolean isSolvable() {
		return result != null;
	}
	/**
	 * Min number of moves to solve initial board; -1 if no solution.
	 * @return min number of moves if solvable else -1
	 */
	public int moves() {
		if (result != null) {
			return result.moves;
		} else {
			return -1;
		}
	}
	/**
	 * Sequence of boards in a shortest solution; null if no solution.
	 * @return solution
	 */
	public Iterable<Board> solution() {
		if (result == null) return null;
		// Create the iterator
		Stack<Board> stack = new Stack<Board>();
		BoardNode node = result;
		do {
			stack.push(node.board);
		} while ((node = node.parent) != null);
		return stack;
	}
	/**
	 * Attempt to solve the puzzle.
	 * @param board
	 * @param twin
	 * @return result
	 */
	private BoardNode solve(Board board) {
		MinPQ<BoardNode> mQueue = new MinPQ<BoardNode>();
		MinPQ<BoardNode> tQueue = new MinPQ<BoardNode>();
		mQueue.insert(new BoardNode(board, null));
		tQueue.insert(new BoardNode(board.twin(), null));
		// Try to find the goal of board itself and it's twin
		BoardNode mGoal = null;
		BoardNode tGoal = null;
		while (true) {
			mGoal = move(mQueue);
			tGoal = move(tQueue);
			if (mGoal.board.isGoal()) return mGoal;
			if (tGoal.board.isGoal()) return null;
		}
	}
	/**
	 * Implementation of A* algorithm.
	 * @param queue
	 * @return
	 */
	private BoardNode move(MinPQ<BoardNode> queue) {
		BoardNode node = queue.delMin();
		for (Board neighbor : node.board.neighbors()) {
			if (node.parent == null || neighbor.equals(node.parent.board)) {
				queue.insert(new BoardNode(neighbor, node));
			}
		}
		return node;
	}
	/**
	 * 
	 * @author prokiller
	 */
	private class BoardNode implements Comparable<BoardNode> {
		/**
		 * Priority.
		 */
		private int priority;
		/**
		 * Moves toward goal.
		 */
		private int moves;
		/**
		 * Link to parent node.
		 */
		private BoardNode parent;
		/**
		 * Reference to the board.
		 */
		private Board board;
		/**
		 * 
		 * @param board
		 * @param parent
		 */
		private BoardNode(Board board, BoardNode parent) {
			if (parent == null) {
				moves = 0;
			} else {
				this.parent = parent;
				moves = parent.moves + 1;
			}
			this.board = board;
			priority = board.manhattan() + moves;
		}
		/**
		 * Comparison based on priority.
		 */
		@Override
		public int compareTo(BoardNode that) {
			return this.priority - that.priority;
		}
	}
	/**
	 * Solve a slider puzzle (code given).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create initial board from file
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
