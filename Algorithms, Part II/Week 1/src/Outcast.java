/**
 * 
 * @author etlove
 */
public class Outcast {
	
	private final WordNet wordNet;
	/**
	 * constructor takes a WordNet object.
	 * @param wordnet
	 */
	public Outcast(WordNet wordnet) {
		this.wordNet = wordnet;
	}
	/**
	 * given an array of WordNet nouns, return an outcast.
	 * @param nouns
	 * @return
	 */
	public String outcast(String[] nouns) {
		String outcase = null;
		int max = 0;
		
		for (String str1 : nouns) {
			int distance = 0;
			for (String str2 : nouns) {
				if (!str1.equals(str2)) {
					distance = distance + this.wordNet.distance(str1, str2);
				}
			}
			
			if (max < distance) {
				max = distance;
				outcase = str1;
			}
		}
		
		return outcase;
	}
	/**
	 * for unit testing of this class (such as the one below).
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
