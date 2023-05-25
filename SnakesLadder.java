import java.io.*;
import java.util.*;

public class SnakesLadder extends AbstractSnakeLadders {

	int N, M;
	int snakes[];
	int ladders[];
	int optimalM;

	int optimalTo[];
	int optimalFrom[];

	int onlyladders[][];

	public SnakesLadder(String name) throws Exception {
		File file = new File(name);
		BufferedReader br = new BufferedReader(new FileReader(file));
		N = Integer.parseInt(br.readLine());

		M = Integer.parseInt(br.readLine());

		snakes = new int[N];
		ladders = new int[N];
		for (int i = 0; i < N; i++) {
			snakes[i] = -1;
			ladders[i] = -1;
		}

		for (int i = 0; i < M; i++) {
			String e = br.readLine();
			StringTokenizer st = new StringTokenizer(e);
			int source = Integer.parseInt(st.nextToken());
			int destination = Integer.parseInt(st.nextToken());

			if (source < destination) {
				ladders[source] = destination;
			} else {
				snakes[source] = destination;
			}
		}

		optimalM = optimalMovesHelper();
		optimalTo = new int[N];
		optimalFrom = new int[N];
		fill(optimalTo, optimalFrom);

		int totalLadders = 0;
		for (int i = 0; i < N; i++) {
			if (ladders[i] != -1) {
				totalLadders++;
			}
		}
		onlyladders = new int[totalLadders][2];
		int count = 0;
		for (int i = 0; i < N; i++) {
			if (ladders[i] == -1) {
				continue;
			} else {
				onlyladders[count][0] = i;
				onlyladders[count][1] = ladders[i];
				count++;
			}
		}
	}

	private void fill(int[] optimalTo, int[] optimalFrom) {
		optimalTox(optimalTo);
		optimalFromy(optimalFrom);
	}

	private void optimalTox(int optimalTo[]) {
		int[] dist = new int[N + 1];
		Arrays.fill(dist, Integer.MAX_VALUE);

		dist[0] = 0;

		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(0);

		while (!queue.isEmpty()) {
			int curr = queue.poll();

			for (int i = curr + 1; i <= curr + 6 && i < N; i++) {
				int next = i;
				if (snakes[next] != -1 || ladders[next] != -1){
					if (snakes[next] != -1){
						next = snakes[next];
					}else{
						next = ladders[next];
					}
				}

				if (dist[next] > dist[curr] + 1) {
					dist[next] = dist[curr] + 1;
					queue.add(next);
				}
			}
		}

		for (int i = 0; i < N; i++) {
			optimalTo[i] = dist[i];
		}
	}

	private void optimalFromy(int optimalFrom[]) {
		int[] dist = new int[N + 1];
		Arrays.fill(dist, Integer.MAX_VALUE);

		int[] reversedLadders = new int[N];
		int[] reversedSnakes = new int[N];
		Arrays.fill(reversedLadders, -1);
		Arrays.fill(reversedSnakes, -1);

		for (int i = 1; i < N; i++) {
			if (ladders[i] != -1) {
				reversedLadders[N - i] = N - ladders[i];
			}
			if (snakes[i] != -1) {
				reversedSnakes[N - i] = N - snakes[i];
			}
		}

		dist[N] = 0;

		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(N);

		while (!queue.isEmpty()) {
			int curr = queue.poll();

			for (int i = curr - 1; i >= curr - 6 && i > 0; i--) {
				int prev = i;
				if (reversedSnakes[prev] != -1 || reversedLadders[prev] != -1)
					if (reversedSnakes[prev] != -1){
						prev = reversedSnakes[prev];
					}else{
						prev = reversedLadders[prev];
					}

				if (dist[prev] > dist[curr] + 1) {
					dist[prev] = dist[curr] + 1;
					queue.add(prev);
				}
			}
		}

		for (int i = 0; i < N; i++) {
			optimalFrom[i] = dist[i];
		}
	}

	private int optimalMovesHelper(){
		int[] dist = new int[N + 1];
		Arrays.fill(dist, Integer.MAX_VALUE);

		dist[0] = 0;

		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(0);

		while (!queue.isEmpty()) {
			int curr = queue.poll();

			if (curr == N - 1)
				return dist[curr];

			for (int i = curr + 1; i <= curr + 6 && i < N; i++) {
				int next = i;
				if (snakes[next] != -1 || ladders[next] != -1){
					if (snakes[next] != -1){
						next = snakes[next];
					}else{
						next = ladders[next];
					}
				}

				if (dist[next] > dist[curr] + 1) {
					dist[next] = dist[curr] + 1;
					queue.add(next);
				}
			}
		}

		return -1;
	}

	public int OptimalMoves() {
		/*
		 * Complete this function and return the minimum number of moves required to win
		 * the game.
		 */
		return optimalM;
	}

	public int Query(int x, int y) {
		/*
		 * Complete this function and
		 * return +1 if adding a snake/ladder from x to y improves the optimal solution,
		 * else return -1.
		 */
		int beforeAddition = optimalM;
		int afterAddition = optimalTo[x] + optimalFrom[y];
		if (afterAddition < beforeAddition){
			return 1;
		}
		return -1;
	}

	public int[] FindBestNewSnake() {
		int result[] = new int[2];
		result[0] = -1;
		result[1] = -1;
		/*
		 * Complete this function and
		 * return (x, y) i.e the position of snake if adding it increases the optimal
		 * solution by largest value,
		 * if no such snake exists, return (-1, -1)
		 */
		int improved = 0;
		for (int i = 0; i < onlyladders.length; i++) {
			for (int j = i + 1; j < onlyladders.length; j++) {
				int x1 = onlyladders[i][0];
				int y1 = onlyladders[i][1];
				int x2 = onlyladders[j][0];
				int y2 = onlyladders[j][1];

				if (x1 < x2 && x2 < y1 && y1 < y2) {
					int start = y1;
					int end = x2;
					int newmoves = optimalTo[start] + optimalFrom[end];

					if ((optimalM - newmoves) > improved) {
						improved = optimalM - newmoves;
						result[0] = start;
						result[1] = end;
					}
				}
			}
		}
		return result;
	}

}