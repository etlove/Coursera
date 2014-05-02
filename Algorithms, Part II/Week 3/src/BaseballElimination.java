import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
	
	private HashMap<String, Integer> teamToIndex;
	private String[] name;
	private MultiHashSet<Integer, String> elimtSet;
	private int win[], loss[], remain[], game[][];
	private boolean eliminated[], smplEliminated[];
	
	/**
	 * create a baseball division from given filename in format specified below.
	 * @param filename
	 */
	public BaseballElimination(String filename) {
		In in = new In(filename);
		int num = in.readInt();
		teamToIndex = new HashMap<String, Integer>(num + 1, 1f);
		name = new String[num];
		elimtSet = new MultiHashSet<Integer, String>(num + 1);
		win = new int[num];
		loss = new int[num];
		remain = new int[num];
		game = new int[num][num];
		eliminated = new boolean[num];
		smplEliminated = new boolean[num];
		
		String tmpName;
		for (int i = 0; i < num; i++) {
			tmpName = in.readString();
			teamToIndex.put(tmpName, i);
			name[i] = tmpName;
			win[i] = in.readInt();
			loss[i] = in.readInt();
			remain[i] = in.readInt();
			
			for (int j = 0; j < num; j++) {
				game[i][j] = in.readInt();
			}
		}
		
		in.close();
		
		for (int i = 0; i < num; i++) quickfindEliminatedByTeam(i);
		for (int i = 0; i < num; i++) findEliminatedByTeam(i);
	}
	/**
	 * 
	 * @param team
	 */
	private void quickfindEliminatedByTeam(int team) {
		for (int i = 0; i < numberOfTeams(); i++) {
			if (i == team) continue;
			if (win[team] + remain[team] < win[i]) {
				eliminated[team] = true;
				smplEliminated[team] = true;
				elimtSet.put(team, name[i]);
//			} else {
//				eliminated[team] = false;
//				smplEliminated[team] = false;
			}
		}
	}
	/**
	 * 
	 * @param team
	 */
	private void findEliminatedByTeam(int team) {
		//
		if (smplEliminated[team]) return;
		//
		int match = numberOfTeams() * (numberOfTeams() - 1) / 2;
		int num = match + numberOfTeams() + 2;
		int node = num - 1;
		//
		FlowNetwork network = new FlowNetwork(num);
		int index = 1;
		for (int i = 0; i < numberOfTeams(); i++) {
			for (int j = i; j < numberOfTeams(); j++) {
				if (i == j || i == team || j == team) continue;
				FlowEdge edge = new FlowEdge(0, index, game[i][j]);
				network.addEdge(edge);
				network.addEdge(new FlowEdge(index, match + i, Double.POSITIVE_INFINITY));
				network.addEdge(new FlowEdge(index, match + j, Double.POSITIVE_INFINITY));
				index++;
			}
		}
		//
		for (int i = 0; i < numberOfTeams(); i++) {
			if (i == team) continue;
			int diff = win[team] + remain[team] - win[i];
			if (diff < 0) diff = 0;
			network.addEdge(new FlowEdge(index + i, node, diff));
		}
		//
		FordFulkerson flow = new FordFulkerson(network, 0, node);
		//
		ArrayList<FlowEdge> edges = new ArrayList<FlowEdge>();
		for (int i = 0; i < network.V(); i++) {
			for (FlowEdge edge : network.adj(i)) {
				if (i == edge.from() && edge.flow() > 0) {
					if (edge.to() == node) edges.add(edge);
				}
			}
		}
		//
		for (FlowEdge edge : edges) {
			if (edge.flow() == edge.capacity()) eliminated[team] = true;
		}
		
		for (int i = 0; i < numberOfTeams(); i++) {
			if (flow.inCut(match + i) && !smplEliminated[i]) {
				elimtSet.put(team, name[i]);
			}
		}
	}
	/**
	 * number of teams.
	 * @return
	 */
	public int numberOfTeams() {
		return teamToIndex.size();
	}
	/**
	 * all teams.
	 * @return
	 */
	public Iterable<String> teams() {
		return teamToIndex.keySet();
	}
	/**
	 * number of wins for given team.
	 * @param team
	 * @return
	 */
	public int wins(String team) {
		int index = getTeamIndex(team);
		return win[index];
	}
	/**
	 * number of losses for given team.
	 * @param team
	 * @return
	 */
	public int losses(String team) {
		int index = getTeamIndex(team);
		return loss[index];
	}
	/**
	 * number of remaining games for given team.
	 * @param team
	 * @return
	 */
	public int remaining(String team) {
		int index = getTeamIndex(team);
		return remain[index];
	}
	/**
	 * number of remaining games between team1 and team2.
	 * @param team1
	 * @param team2
	 * @return
	 */
	public int against(String team1, String team2) {
		int index1 = getTeamIndex(team1);
		int index2 = getTeamIndex(team2);
		return game[index1][index2];
	}
	/**
	 * is given team eliminated?
	 * @param team
	 * @return
	 */
	public boolean isEliminated(String team) {
		int index = getTeamIndex(team);
		return eliminated[index] || smplEliminated[index];
	}
	/**
	 * subset R of teams that eliminates given team; null if not eliminated.
	 * @param team
	 * @return
	 */
	public Iterable<String> certificateOfElimination(String team) {
		int index = getTeamIndex(team);
		if (!eliminated[index]) return null;
		else return elimtSet.get(index);
	}
	/**
	 * Get team index.
	 * @param team
	 * @return team index
	 */
	private int getTeamIndex(String team) {
		Integer index = teamToIndex.get(team);
		if (index == null) throw new IllegalArgumentException();
		else return index;
	}
	/**
	 * 
	 * @author etlove
	 *
	 * @param <K> key
	 * @param <V> value
	 */
	private class MultiHashSet<K, V> {
		
		private HashMap<K, HashSet<V>> map;
		private int capacity;
		private final static float threshold = 1f;
		
		public MultiHashSet(int capacity) {
			this.capacity = capacity;
			this.map = new HashMap<K, HashSet<V>>(capacity, threshold);
		}
		
		public void put(K key, V value) {
			HashSet<V> set = map.get(key);
			if (set == null) {
				set = new HashSet<V>(capacity, threshold);
				map.put(key, set);
			}
			set.add(value);
		}
		
		public Set<V> get(K key) {
			return map.get(key);
		}
	}
	/**
	 * 
	 */
	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
				StdOut.println("}");
			}
			else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}
}
