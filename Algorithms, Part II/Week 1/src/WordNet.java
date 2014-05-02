import java.util.HashMap;
import java.util.HashSet;

/**
 * @author etlove
 */
public class WordNet {
	
	private final SAP sap;
	private final HashMap<Integer, String> idToSynset;
	private final HashMap<String, HashSet<Integer>> nounToIds;
	/**
	 * constructor takes the name of the two input files
	 * @param synsets
	 * @param hypernyms
	 */
	public WordNet(String synsets, String hypernyms) {
		idToSynset = new HashMap<Integer, String>();
		nounToIds = new HashMap<String, HashSet<Integer>>();
		
		importSynsets(synsets);
		sap = new SAP(importHypernyms(hypernyms));
	}
	/**
	 * 
	 * @param synsets
	 */
	private void importSynsets(String synsets) {
		In in = new In(synsets);
		String[] strs = null;
		HashSet<Integer> idSet;
		
		while (in.hasNextLine()) {
			strs = in.readLine().split(",");
			int id = Integer.parseInt(strs[0]);
			idToSynset.put(id, strs[1]);
			
			for (String noun : strs[1].split(" ")) {
				idSet = nounToIds.get(noun);
				
				if (idSet == null) {
					idSet = new HashSet<Integer>();
					idSet.add(id);
					nounToIds.put(noun, idSet);
				} else {
					idSet.add(id);
				}
			}
			
		}
	}
	/**
	 * 
	 * @param hypernyms
	 * @return
	 */
	private Digraph importHypernyms(String hypernyms) {
		Digraph graph = new Digraph(idToSynset.size());
		In in = new In(hypernyms);
		String[] strs = null;
		
		while (in.hasNextLine()) {
			strs = in.readLine().split(",");
			int id = Integer.parseInt(strs[0]);
			
			for (int i = 1; i < strs.length; i++) {
				graph.addEdge(id, Integer.parseInt(strs[i]));
			}
		}
		
		checkGraph(graph);
		
		return graph;
	}
	/**
	 * 
	 * @param graph
	 */
	private void checkGraph(Digraph graph) {
		DirectedCycle cycle = new DirectedCycle(graph);
		
		if (cycle.hasCycle()) {
			throw new IllegalArgumentException("DiGraph has a cycle.");
		}
		
		int roots = 0;
		
		for (int i = 0; i < graph.V(); i++) {
			if (!graph.adj(i).iterator().hasNext()) {
				roots++;
			}
		}
		
		if (roots != 1) {
			throw new IllegalArgumentException("Not a rooted DAG.");
		}
	}
	/**
	 * the set of nouns (no duplicates), returned as an Iterable.
	 * @return
	 */
	public Iterable<String> nouns() {
		return nounToIds.keySet();
	}
	/**
	 * is the word a WordNet noun?
	 * @param word
	 * @return
	 */
	public boolean isNoun(String word) {
		return nounToIds.containsKey(word);
	}
	/**
	 * distance between nounA and nounB (defined below).
	 * @param nounA
	 * @param nounB
	 * @return
	 */
	public int distance(String nounA, String nounB) {
		checkNoun(nounA, nounB);
		
		return sap.length(nounToIds.get(nounA), nounToIds.get(nounB));
	}
	/**
	 * a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	 * in a shortest ancestral path (defined below).
	 * @param nounA
	 * @param nounB
	 * @return
	 */
	public String sap(String nounA, String nounB) {
		checkNoun(nounA, nounB);
		
		return idToSynset.get(sap.ancestor(nounToIds.get(nounA), nounToIds.get(nounB)));
	}
	/**
	 * 
	 * @param strings
	 */
	private void checkNoun(String... strings) {
		for (String word : strings) {
			if (!isNoun(word)) {
				throw new IllegalArgumentException();
			}
		}
	}
	/**
	 * for unit testing of this class
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
