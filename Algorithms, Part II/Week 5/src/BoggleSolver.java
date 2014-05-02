import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoggleSolver {
	
	private final static int[] SCORE = {0, 0, 0, 1, 1, 2, 3, 5};
	private TST2<String> dic;
	
	// Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
    	if (dictionary == null) throw new IllegalArgumentException();
    	
    	this.dic = new TST2<String>();
    	StdRandom.shuffle(dictionary);
    	for (String str : dictionary) dic.put(str, str);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
    	if (board == null) throw new IllegalArgumentException();
    	
    	ArrayList<String> result = new ArrayList<String>();
    	for (int row = 0; row < board.rows(); row++) {
    		for (int col = 0; col < board.cols(); col++) {
    			boolean[][] visited = new boolean[board.rows()][board.cols()];
    			traverse(result, "", board, row, col, visited);
    		}
    	}
    	
    	return new HashSet<String>(result);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
    	if (word == null) throw new IllegalArgumentException();
    	if (!dic.contains(word)) return 0;
    	
    	int len = word.length();
    	if (len >= 8) return 11;
    	else return SCORE[len];
    }
    
    private void traverse(List<String> result, String parent, BoggleBoard board, int row, int col, boolean[][] visited) {
    	if (row < 0 || row >= board.rows() || col < 0 || col >= board.cols()) return;
    	if (visited[row][col]) return;
    	
    	char c = board.getLetter(row, col);
    	boolean[][] copy = copy(visited);
    	copy[row][col] = true;
    	
    	String str = parent + board.getLetter(row, col);
    	if (c == 'Q') str = str + "U";
    	
    	if (!dic.prefixMatch(str)) return;
    	if (scoreOf(str) > 0) result.add(str);
    	
    	traverse(result, str, board, row - 1, col - 1, copy);
    	traverse(result, str, board, row + 1, col - 1, copy);
    	traverse(result, str, board, row - 1, col + 1, copy);
    	traverse(result, str, board, row + 1, col + 1, copy);
    	traverse(result, str, board, row - 1, col, copy);
    	traverse(result, str, board, row, col - 1, copy);
    	traverse(result, str, board, row + 1, col, copy);
    	traverse(result, str, board, row, col + 1, copy);
    }
    
    private boolean[][] copy(boolean[][] visited) {
    	boolean[][] copy = new boolean[visited.length][visited[0].length];
    	for (int row = 0; row < visited.length; row++) {
    		for (int col = 0; col < visited[0].length; col++) {
    			copy[row][col] = visited[row][col];
    		}
    	}
    	return copy;
    }
    
    private class TST2<Value> {
        private int N;       // size
        private Node root;   // root of TST

        private class Node {
            private char c;                 // character
            private Node left, mid, right;  // left, middle, and right subtries
            private Value val;              // value associated with string
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        public Value get(String key) {
            if (key == null) throw new NullPointerException();
            if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
            Node x = get(root, key, 0);
            if (x == null) return null;
            return x.val;
        }

        // return subtrie corresponding to given key
        private Node get(Node x, String key, int d) {
            if (key == null) throw new NullPointerException();
            if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
            if (x == null) return null;
            char c = key.charAt(d);
            if      (c < x.c)              return get(x.left,  key, d);
            else if (c > x.c)              return get(x.right, key, d);
            else if (d < key.length() - 1) return get(x.mid,   key, d+1);
            else                           return x;
        }

        public void put(String s, Value val) {
            if (!contains(s)) N++;
            root = put(root, s, val, 0);
        }

        private Node put(Node x, String s, Value val, int d) {
            char c = s.charAt(d);
            if (x == null) {
                x = new Node();
                x.c = c;
            }
            if      (c < x.c)             x.left  = put(x.left,  s, val, d);
            else if (c > x.c)             x.right = put(x.right, s, val, d);
            else if (d < s.length() - 1)  x.mid   = put(x.mid,   s, val, d+1);
            else                          x.val   = val;
            return x;
        }
        
        public boolean prefixMatch(String prefix) {
            Node x = get(root, prefix, 0);
            if (x != null) return true;
            else return false;
        }
    }
    
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
//        BoggleBoard board = new BoggleBoard(100, 100);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
