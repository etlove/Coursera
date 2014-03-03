/**
 * @author etlove
 */
public class Board {
	/**
	 * Blocks
	 */
	private int[][] blocks;
	/**
	 * Dimension
	 */
	private int dim;
	/**
	 * Construct a board from an N-by-N array of blocks. where blocks[i][j] =
	 * block in row i, column j
	 * @param blocks input of block
	 */
	public Board(int[][] blocks) {
		this.blocks = blocks.clone();
		dim = blocks.length;
	}
	/**
	 * Board dimension N.
	 * @return dimension of board
	 */
	public int dimension() {
		return dim;
	}
	/**
	 * Number of blocks out of place.
	 * @return Number of block(s) out of place.
	 */
	public int hamming() {
		int count = 0;
		int expect = 1;
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				if (blocks[row][col] != expect++) {
					count++;
				}
			}
		}
		// skip empty block and return
		return count - 1;
	}
	/**
	 * Sum of Manhattan distances between blocks and goal.
	 * @return
	 */
	public int manhattan() {
		int sum = 0;
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				int value = blocks[row][col];
				// skip empty block
				if (value == 0) continue;
				// identify current coordinate
				int curRow = (value - 1) / dim;
				int curCol = (value - 1) % dim;
				// calculate the distance
				sum += Math.abs(row - curRow) + Math.abs(col - curCol);
			}
		}
		return sum;
	}
	/**
	 * Check if this board is the goal board.
	 * @return true if goal board; otherwise false
	 */
	public boolean isGoal() {
		if (blocks[dim - 1][dim - 1] != 0) return false;
		int expect = 1;
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				// skip the trailing empty block
				if (row == dim - 1 && col == dim - 1) continue;
				if (blocks[row][col] != expect++) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * A board obtained by exchanging two adjacent blocks in the same row.
	 * @return a board
	 */
	public Board twin() {
		if (dim <= 1) return new Board(blocks);
		// look for two consecutive non-empty blocks
		int row = 0;
		int col = 0;
		match:
		for (row = 0; row < dim; row++) {
			int last = 0;
			for (col = 0; col < dim; col++) {
				if (blocks[row][col] != 0 && last != 0) {
					break match;
				} else {
					last = blocks[row][col];
				}
			}
		}
		// exchange value with non-empty block in the same row
		return exchange(row, col, 0, -1);
	}
	/**
	 * Does this board equal y?
	 * @return true if equal else false
	 */
	public boolean equals(Object y) {
		if (y == this) return true;
		if (y == null) return false;
		if (y instanceof Board) {
			Board board = (Board) y;
			// check if dimension size matches
			if (board.dim != this.dim) return false;
			// check each value
			for (int row = 0; row < dim; row++) {
				for (int col = 0; col < dim; col++) {
					if (this.blocks[row][col] != board.blocks[row][col]) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
	/**
	 * All neighboring boards.
	 * @return all neighboring boards
	 */
	public Iterable<Board> neighbors() {
		Queue<Board> queue = new Queue<Board>();
		// look for empty block
		int row = 0;
		int col = 0;
		match:
		for (row = 0; row < dim; row++) {
			for (col = 0; col < dim; col++) {
				if (blocks[row][col] == 0) {
					break match;
				}
			}
		}
		// Enqueue all possible neighbors
		if (row > 0) {
			queue.enqueue(exchange(row, col, -1, 0));
		}
		if (row < dim - 1) {
			queue.enqueue(exchange(row, col, 1, 0));
		}
		if (col > 0) {
			queue.enqueue(exchange(row, col, 0, -1));
		}
		if (col < dim - 1) {
			queue.enqueue(exchange(row, col, 0, 1));
		}
		
		return queue;
	}
	/**
	 * 
	 * @param row index of row
	 * @param col index of column
	 * @param dRow movement of row
	 * @param dCol movement of column
	 * @return exchanged result as Board object
	 */
	private Board exchange(int row, int col, int dRow, int dCol) {
		int[][] copy = blocks.clone();
		int temp = copy[row][col];
		copy[row][col] = copy[row + dRow][col + dCol];
		copy[row + dRow][col + dCol] = temp;
		return new Board(copy);
	}
	/**
	 * String representation of the board.
	 * @return string representation of the board
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(dim + "\n");
		for (int row = 0; row < dim; row++) {
			for (int col = 0; col < dim; col++) {
				s.append(String.format("%2d ", blocks[row][col]));
			}
			s.append("\n");
		}
		return s.toString();
	}
}
