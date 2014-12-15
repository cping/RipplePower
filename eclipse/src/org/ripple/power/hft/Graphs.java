package org.ripple.power.hft;

import java.util.*;

public class Graphs {

	static final int MAX = 1024, INF = 1 << 28;
	static int[][] graph = new int[MAX][MAX];
	static int[] deg = new int[MAX];
	static int[][] adj = new int[MAX][MAX];
	static int[] distance = new int[MAX];
	static TreeSet<Vertex> vertexSet = new TreeSet<Vertex>();
	static Vertex v, vv;
	static boolean[] inTree = new boolean[MAX];
	static TreeSet<Edge> set = new TreeSet<Edge>(), mst = new TreeSet<Edge>();
	static Iterator<Edge> iter;
	static Edge edge;
	static int[] sorted = new int[MAX];
	static int[] inDegree = new int[MAX];
	static Queue<Integer> queue = new LinkedList<Integer>();
	static int[] match = new int[2 * MAX];
	static int[] cover = new int[2 * MAX];
	static int color = 1;
	static int[] vis = new int[MAX];
	static boolean[] sel = new boolean[2 * MAX];
	static int[] minSlack = new int[MAX];
	static int n;
	static boolean done;
	static int M, N;
	static int[] matchL = new int[MAX];
	static int[] matchR = new int[MAX];
	static boolean[] seen = new boolean[MAX];
	static boolean[][] bgraph = new boolean[MAX][MAX];
	static int[] dfsNumber = new int[MAX], father = new int[MAX];
	static boolean[] articulation = new boolean[MAX];
	static int count;
	static int[][] cap = new int[MAX][MAX];
	static int[][] cost = new int[MAX][MAX];
	static int[][] fnet = new int[MAX][MAX];
	static int[] par = new int[MAX], d = new int[MAX], inq = new int[MAX];
	static int[] pi = new int[MAX];
	static int[] q = new int[MAX], prev = new int[MAX];
	static int qf, qb;
	static Stack<Integer> stack = new Stack<Integer>();
	static int[] id = new int[MAX], pre = new int[MAX], low = new int[MAX],
			ord = new int[MAX];
	static int cnt, sCnt, bCnt;
	static int[] pred = new int[MAX], mins = new int[MAX],
			parent = new int[MAX];
	static int[] adjc = new int[MAX], radjc = new int[MAX];
	static int[][] radj = new int[MAX][MAX];
	static boolean[] visit = new boolean[MAX], cycle = new boolean[MAX];
	static int mine;
	static UnionFind unionFind = new UnionFind(MAX);
	static int[] vLabel = new int[MAX];
	static int top, cur, V;
	static int[] mate = new int[MAX];
	static int[] save = new int[MAX];
	static int[] used = new int[MAX];

	static void reMatch(int x, int y) {
		int m = mate[x];
		mate[x] = y;
		if (mate[m] == x) {
			if (vLabel[x] <= V) {
				mate[m] = vLabel[x];
				reMatch(vLabel[x], m);
			} else {
				int a = 1 + (vLabel[x] - V - 1) / V;
				int b = 1 + (vLabel[x] - V - 1) % V;
				reMatch(a, b);
				reMatch(b, a);
			}
		}
	}

	static void traverse(int x) {
		int i;
		for (i = 1; i <= V; i++)
			save[i] = mate[i];
		reMatch(x, x);
		for (i = 1; i <= V; i++) {
			if (mate[i] != save[i])
				used[i]++;
			mate[i] = save[i];
		}
	}

	static void reLabel(int x, int y) {
		Arrays.fill(used, 1, V + 1, 0);
		traverse(x);
		traverse(y);
		for (int i = 1; i <= V; i++) {
			if (used[i] == 1 && vLabel[i] < 0) {
				vLabel[i] = V + x + (y - 1) * V;
				q[top++] = i;
			}
		}
	}

	static int edmonds() {
		int i, x, p, y;
		Arrays.fill(mate, 1, V + 1, 0);
		for (i = 1; i <= V; i++)
			if (mate[i] == 0) {
				Arrays.fill(vLabel, 1, V + 1, -1);
				vLabel[i] = 0;
				cur = top = 0;
				q[top++] = i;
				while (cur < top) {
					x = q[cur++];
					for (p = 0; p < deg[x]; p++) {
						y = adj[x][p];
						if (mate[y] == 0 && i != y) {
							mate[y] = x;
							reMatch(x, y);
							cur = top;
							break;
						}
						if (vLabel[y] >= 0) {
							reLabel(x, y);
							continue;
						}
						if (vLabel[mate[y]] < 0) {
							vLabel[mate[y]] = x;
							q[top++] = mate[y];
						}
					}
				}
			}
		int count = 0;
		for (i = 1; i <= V; i++)
			if (mate[i] > i)
				count++;
		return count;
	}

	static void sCdfs(int w, int N) {
		int i, t, min = low[w] = pre[w] = cnt++;
		stack.push(w);
		for (i = 0; i < deg[w]; i++) {
			t = adj[w][i];
			if (pre[t] == -1)
				sCdfs(t, N);
			min = Math.min(min, low[t]);
		}
		if (min < low[w]) {
			low[w] = min;
			return;
		}
		do {
			id[t = stack.pop()] = sCnt;
			low[t] = N;
		} while (t != w);
		sCnt++;
	}

	static int strongComponentsTarjan(int N) {
		int i;
		cnt = sCnt = 0;
		stack.clear();
		Arrays.fill(id, 0, N, -1);
		Arrays.fill(pre, 0, N, -1);
		Arrays.fill(low, 0, N, -1);
		for (i = N - 1; i >= 0; i--)
			if (pre[i] == -1)
				sCdfs(i, N);
		return sCnt;
	}

	static boolean isStronglyReachable(int u, int v) {
		return id[u] == id[v];
	}

	static boolean[][] transitiveClosure(int N) { 
		int i, j, k, M;
		M = strongComponentsTarjan(N);
		boolean[][] trans = new boolean[N][N];
		boolean[][] strong = new boolean[M][M];
		for (i = 0; i < N; i++)
			for (j = 0; j < deg[i]; j++)
				strong[id[i]][id[adj[i][j]]] = true;
		for (k = 0; k < M; k++)
			for (i = 0; i < M; i++)
				for (j = 0; j < M; j++)
					strong[i][j] |= strong[i][k] && strong[k][j];
		for (i = 0; i < N; i++)
			for (j = 0; j < N; j++)
				trans[i][j] = strong[id[i]][id[j]];
		return trans;
	}

	static boolean[] root = new boolean[MAX];
	static int t;

	static int dfsArt(int x) {
		low[x] = d[x] = t++;
		for (int i = 0; i < N; i++)
			if (bgraph[x][i])
				if (d[i] == -1) {
					low[x] = Math.min(low[x], dfsArt(i));
					adj[x][deg[x]++] = i;
				} else
					low[x] = Math.min(low[x], d[i]);
		return low[x];
	}

	static void findArticulationPoints(boolean[] arts) {
		int i, j;
		Arrays.fill(d, 0, N, -1);
		Arrays.fill(root, 0, N, false);
		Arrays.fill(deg, 0, N, 0);
		t = 0;
		for (i = 0; i < N; i++)
			if (d[i] == -1) {
				dfsArt(i);
				root[i] = true;
			}
		for (i = 0; i < N; i++)
			if (root[i] && deg[i] > 1)
				arts[i] = true;
			else if (!root[i])
				for (j = 0; j < deg[i]; j++)
					if (low[adj[i][j]] >= d[i]) {
						arts[i] = true;
						break;
					}
	}

	static int slack(int i, int j) {
		return cost[i][j] - cover[i] - cover[MAX + j];
	}

	static boolean edge(int i, int j) {
		return slack(i, j) == 0;
	}

	static boolean dfs(int i) {
		if (vis[i] == color)
			return false;
		vis[i] = color;
		for (int j = 0; j < n; j++) {
			if (edge(i, j) && match[i] != j
					&& (match[MAX + j] == -1 || dfs(match[MAX + j]))) {
				match[i] = j;
				match[MAX + j] = i;
				return true;
			}
		}
		return false;
	}

	static void dfs2(int i) {
		if (sel[i])
			return;
		sel[i] = true;
		for (int j = 0; j < n; j++) {
			if (edge(i, j) && !sel[MAX + j] && match[i] != j) {
				sel[MAX + j] = true;
				if (match[MAX + j] != -1)
					dfs2(match[MAX + j]);
				else
					done = true;
			} else if (!sel[MAX + j])
				minSlack[j] = Math.min(minSlack[j], slack(i, j));
		}
	}

	static int minCostPerfectMatching(int N) {
		n = N;
		int i, j, delta, matched;
		for (i = 0; i < n; i++) {
			match[i] = -1;
			match[MAX + i] = -1;
		}
		for (i = 0; i < n; i++) {
			match[MAX + i] = -1;
		}
		for (i = 0; i < n; i++) {
			cover[i] = cost[i][0];
			cover[MAX + i] = 0;
			for (j = 1; j < n; j++)
				cover[i] = Math.min(cover[i], cost[i][j]);
		}
		matched = 0;
		while (true) {
			done = false;
			while (!done) {
				done = true;
				for (i = 0; i < n; i++) {
					if (match[i] < 0 && dfs(i)) {
						matched++;
						done = false;
					}
				}
			}
			color++;
			if (matched == n)
				break;
			for (i = 0; i < n; i++)
				sel[i] = false;
			for (i = 0; i < n; i++)
				sel[MAX + i] = false;
			done = false;
			for (i = 0; i < n; i++)
				if (match[i] < 0) {
					dfs2(i);
					break;
				}
			if (done)
				continue;
			for (i = 0; i < n; i++)
				minSlack[i] = INF;
			for (i = 0; i < n; i++) {
				if (!sel[i])
					continue;
				for (j = 0; j < n; j++) {
					if (sel[MAX + j])
						continue;
					minSlack[j] = Math.min(minSlack[j], slack(i, j));
				}
			}
			while (!done) {
				delta = INF;
				for (i = 0; i < n; i++) {
					if (sel[MAX + i])
						continue;
					delta = Math.min(delta, minSlack[i]);
				}
				for (i = 0; i < n; i++) {
					if (sel[i])
						cover[i] += delta;
					if (sel[MAX + i])
						cover[MAX + i] -= delta;
				}
				for (i = 0; i < n; i++) {
					if (sel[MAX + i])
						continue;
					minSlack[i] -= delta;
				}
				for (i = 0; i < n; i++) {
					if (sel[MAX + i])
						continue;
					if (minSlack[i] == 0) {
						sel[MAX + i] = true;
						if (match[MAX + i] == -1) {
							done = true;
							break;
						}
						dfs2(match[MAX + i]);
					}
				}
			}
		}
		int res = 0;
		for (i = 0; i < n; i++)
			res += cost[i][match[i]];
		return res;
	}

	static boolean dijkstraSparse(int n, int s, int t) {
		int qs, i, j, k, tmp, v;
		Arrays.fill(d, 0, n, INF);
		Arrays.fill(par, 0, n, -1);
		Arrays.fill(inq, 0, n, -1);
		d[s] = qs = 0;
		inq[q[qs++] = s] = 0;
		par[s] = n;
		while (qs != 0) {
			int u = q[0];
			inq[u] = -1;
			q[0] = q[--qs];
			if (qs != 0)
				inq[q[0]] = 0;
			for (i = 0, j = 2 * i + 1; j < qs; i = j, j = 2 * i + 1) {
				if (j + 1 < qs && d[q[j + 1]] < d[q[j]])
					j++;
				if (d[q[j]] >= d[q[i]])
					break;
				tmp = q[i];
				q[i] = q[j];
				q[j] = tmp;
				tmp = inq[q[i]];
				inq[q[i]] = inq[q[j]];
				inq[q[j]] = tmp;
			}
			for (k = 0, v = adj[u][k]; k < deg[u]; v = adj[u][++k]) {
				if (fnet[v][u] != 0 && d[v] > pot(u, v) - cost[v][u])
					d[v] = pot(u, v) - cost[v][par[v] = u];
				if (fnet[u][v] < cap[u][v] && d[v] > pot(u, v) + cost[u][v])
					d[v] = pot(u, v) + cost[par[v] = u][v];
				if (par[v] == u) {
					if (inq[v] < 0) {
						inq[q[qs] = v] = qs;
						qs++;
					}
					for (i = inq[v], j = (i - 1) / 2; d[q[i]] < d[q[j]]; i = j, j = (i - 1) / 2) {
						tmp = q[i];
						q[i] = q[j];
						q[j] = tmp;
						tmp = inq[q[i]];
						inq[q[i]] = inq[q[j]];
						inq[q[j]] = tmp;
					}
				}
			}
		}
		for (i = 0; i < n; i++)
			if (pi[i] < INF)
				pi[i] += d[i];
		return par[t] >= 0;
	}

	static int maxFlowMinCostSparse(int n, int s, int t) {
		int i, j;
		Arrays.fill(deg, 0, n, 0);
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				if (cap[i][j] != 0 || cap[j][i] != 0)
					adj[i][deg[i]++] = j;
		for (i = 0; i < n; i++)
			Arrays.fill(fnet[i], 0, n, 0);
		Arrays.fill(pi, 0, n, 0);
		int flow = 0;
		while (dijkstraSparse(n, s, t)) {
			int bot = INF;
			for (int v = t, u = par[v]; v != s; u = par[v = u])
				bot = Math.min(bot, fnet[v][u] != 0 ? fnet[v][u]
						: (cap[u][v] - fnet[u][v]));
			for (int v = t, u = par[v]; v != s; u = par[v = u])
				if (fnet[v][u] != 0) {
					fnet[v][u] -= bot;
				} else {
					fnet[u][v] += bot;
				}
			flow += bot;
		}
		return flow;
	}

	static int pot(int u, int v) {
		return d[u] + pi[u] - pi[v];
	}

	boolean dijkstra(int n, int s, int t) {
		int i;
		Arrays.fill(d, 0, n, INF);
		Arrays.fill(par, 0, n, -1);
		d[s] = 0;
		par[s] = -n - 1;
		for (;;) {
			int u = -1, bestD = INF;
			for (i = 0; i < n; i++)
				if (par[i] < 0 && d[i] < bestD)
					bestD = d[u = i];
			if (bestD == INF)
				break;
			par[u] = -par[u] - 1;
			for (i = 0; i < deg[u]; i++) {
				int v = adj[u][i];
				if (par[v] >= 0)
					continue;
				if (fnet[v][u] > 0 && d[v] > pot(u, v) - cost[v][u]) {
					d[v] = pot(u, v) - cost[v][u];
					par[v] = -u - 1;
				}
				if (fnet[u][v] < cap[u][v] && d[v] > pot(u, v) + cost[u][v]) {
					d[v] = pot(u, v) + cost[u][v];
					par[v] = -u - 1;
				}
			}
		}
		for (i = 0; i < n; i++)
			if (pi[i] < INF)
				pi[i] += d[i];
		return par[t] >= 0;
	}


	int maxFlowMinCost(int n, int s, int t) {
		Arrays.fill(deg, 0);
		int i, j, v, bot, u;
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				if (cap[i][j] > 0 || cap[j][i] > 0)
					adj[i][deg[i]++] = j;
		for (i = 0; i < n; i++)
			Arrays.fill(fnet[i], 0, n, 0);
		Arrays.fill(pi, 0, n, 0);
		int flow = 0;
		while (dijkstra(n, s, t)) {
			bot = INF;
			for (v = t, u = par[v]; v != s; u = par[v = u])
				bot = Math.min(bot, fnet[v][u] > 0 ? fnet[v][u]
						: (cap[u][v] - fnet[u][v]));
			for (v = t, u = par[v]; v != s; u = par[v = u])
				if (fnet[v][u] > 0) {
					fnet[v][u] -= bot;
				} else {
					fnet[u][v] += bot;
				}
			flow += bot;
		}
		return flow;
	}

	static int fordFulkerson(int n, int s, int t) {
		int flow = 0;
		int i;
		for (i = 0; i < n; i++)
			Arrays.fill(fnet[i], 0, n, 0);
		while (true) {
			Arrays.fill(prev, 0, n, -1);
			qf = qb = 0;
			prev[q[qb++] = s] = -2;
			while (qb > qf && prev[t] == -1)
				for (int u = q[qf++], v = 0; v < n; v++)
					if (prev[v] == -1 && fnet[u][v] - fnet[v][u] < cap[u][v])
						prev[q[qb++] = v] = u;
			if (prev[t] == -1)
				break;
			int bot = 0x7FFFFFFF;
			for (int v = t, u = prev[v]; u >= 0; v = u, u = prev[v])
				bot = Math.min(bot, cap[u][v] - fnet[u][v] + fnet[v][u]);
			for (int v = t, u = prev[v]; u >= 0; v = u, u = prev[v])
				fnet[u][v] += bot;
			flow += bot;
		}
		return flow;
	}

	static int dinic(int n, int s, int t) {
		int flow = 0;
		int i, j, u, v, z, bot;
		Arrays.fill(deg, 0, n, 0);
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				if (cap[i][j] != 0 || cap[j][i] != 0)
					adj[i][deg[i]++] = j;
		while (true) {
			Arrays.fill(prev, 0, n, -1);
			qf = qb = 0;
			prev[q[qb++] = s] = -2;
			while (qb > qf && prev[t] == -1)
				for (u = q[qf++], i = 0; i < deg[u]; i++)
					if (prev[v = adj[u][i]] == -1 && cap[u][v] != 0)
						prev[q[qb++] = v] = u;
			if (prev[t] == -1)
				break;
			for (z = 0; z < n; z++)
				if (cap[z][t] != 0 && prev[z] != -1) {
					bot = cap[z][t];
					for (v = z, u = prev[v]; u >= 0; v = u, u = prev[v])
						bot = Math.min(bot, cap[u][v]);
					if (bot == 0)
						continue;
					cap[z][t] -= bot;
					cap[t][z] += bot;
					for (v = z, u = prev[v]; u >= 0; v = u, u = prev[v]) {
						cap[u][v] -= bot;
						cap[v][u] += bot;
					}
					flow += bot;
				}
		}
		return flow;
	}

	static int[] BellmanFord(Edge[] edges, int s, int E, int N) {
		int[] distance = new int[N];
		int i, j, d;
		Arrays.fill(distance, 0, N, INF);
		distance[s] = 0;
		for (i = 0; i <= N; i++)
			for (j = 0; j < E; j++)
				if (distance[edges[j].a] != INF) {
					d = distance[edges[j].a] + edges[j].cost;
					if (d < distance[edges[j].b]) {
						distance[edges[j].b] = d;
						if (i == N)
							System.out.println("Ciclo xD");
					}
				}
		return distance;
	}

	static int[] dijkstra2(int s, int N) { 
		final int NN = N * N;
		int qs, t;
		int[] d = new int[NN], q = new int[NN], inq = new int[NN], prev = new int[NN], deg = new int[NN];
		Arrays.fill(d, INF);
		Arrays.fill(inq, -1);
		Arrays.fill(prev, -1);
		d[s] = qs = 0;
		inq[q[qs++] = s] = 0;
		prev[s] = -2;
		while (qs > 0) {
			int u = q[0];
			inq[u] = -1;
			q[0] = q[--qs];
			if (qs > 0)
				inq[q[0]] = 0;
			for (int i = 0, j = 2 * i + 1; j < qs; i = j, j = 2 * i + 1) {
				if (j + 1 < qs && d[q[j + 1]] < d[q[j]])
					j++;
				if (d[q[j]] >= d[q[i]])
					break;
				t = q[i];
				q[i] = q[j];
				q[j] = t;
				t = inq[q[i]];
				inq[q[i]] = inq[q[j]];
				inq[q[j]] = t;
			}
			for (int k = 0, v = adj[u][k]; k < deg[u]; v = adj[u][++k]) {
				int newd = d[u] + graph[u][v];
				if (newd < d[v]) {
					d[v] = newd;
					prev[v] = u;
					if (inq[v] < 0) {
						inq[q[qs] = v] = qs;
						qs++;
					}
					for (int i = inq[v], j = (i - 1) / 2; d[q[i]] < d[q[j]]; i = j, j = (i - 1) / 2) {
						t = q[i];
						q[i] = q[j];
						q[j] = t;
						t = inq[q[i]];
						inq[q[i]] = inq[q[j]];
						inq[q[j]] = t;
					}
				}
			}
		}
		return d;
	}

	static int bipartiteMatching() {
		Arrays.fill(matchL, 0, M, -1);
		Arrays.fill(matchR, 0, N, -1);
		int cnt = 0;
		for (int i = 0; i < M; i++) {
			Arrays.fill(seen, false);
			if (bpm(i))
				cnt++;
		}
		return cnt;
	}

	static boolean bpm(int u) {
		for (int v = 0; v < N; v++)
			if (bgraph[u][v] && !seen[v]) {
				seen[v] = true;
				if (matchR[v] < 0 || bpm(matchR[v])) {
					matchL[u] = v;
					matchR[v] = u;
					return true;
				}
			}
		return false;
	}

	static int[][] memo = new int[N][1 << N];

	static int tsp(int v, int mask) {
		if (mask == 0)
			return 0;
		if (memo[v][mask] < INF)
			return memo[v][mask];
		int min = INF;
		for (int i = 0; i < N; i++)
			if ((mask & (1 << i)) != 0)
				min = Math.min(min, tsp(i, mask - (1 << i)) + graph[v][i]);
		return memo[v][mask] = min;
	}

	static void dijkstraShort(int s, int N) {
		int i, j, w;
		vertexSet.clear();
		for (i = 0; i < N; i++)
			distance[i] = graph[s][i];
		distance[s] = 0;
		for (i = 0; i < N; i++)
			if (distance[i] < INF)
				vertexSet.add(new Vertex(i, distance[i]));
		while (!vertexSet.isEmpty()) {
			v = vertexSet.iterator().next();
			vertexSet.remove(v);
			for (j = 0; j < N; j++) {
				w = distance[v.v] + graph[v.v][j];
				if (w < distance[j]) {
					vv = new Vertex(j, distance[j]);
					vertexSet.remove(vv);
					distance[j] = vv.priority = w;
					vertexSet.add(vv);
				}
			}
		}
	}

	static void dijkstra(int s, int N) {
		int j, w;
		boolean[] inTree = new boolean[N];
		TreeSet<Vertex> set = new TreeSet<Vertex>();
		Vertex v, vv;
		for (int i = 0; i < N; i++)
			if (graph[s][i] < INF)
				set.add(new Vertex(i, graph[s][i]));
		while (!set.isEmpty()) {
			v = set.iterator().next();
			set.remove(v);
			inTree[v.v] = true;
			for (j = 0; j < N; j++) {
				w = graph[v.v][j];
				if (graph[s][j] > graph[s][v.v] + w) {
					if (!inTree[j]) {
						vv = new Vertex(j, graph[s][j]);
						set.remove(vv);
						vv.priority = graph[s][v.v] + w;
						set.add(vv);
					}
					graph[s][j] = graph[s][v.v] + w;
				}
			}
		}
	}

	static void floydWarshall(int N) {
		int i, j, k;
		for (k = 0; k < N; k++)
			for (i = 0; i < N; i++)
				for (j = 0; j < N; j++)
					graph[i][j] = Math.min(graph[i][j], graph[i][k]
							+ graph[k][j]);
	}

	static void fWTransitiveClosure(int N) {
		int i, j, k;
		for (k = 0; k < N; k++)
			for (i = 0; i < N; i++)
				for (j = 0; j < N; j++)
					bgraph[i][j] |= bgraph[i][k] && bgraph[k][j];
	}

	static boolean dfsDMST(int r, int u) {
		if (visit[u])
			return r == u;
		visit[u] = true;
		int v;
		for (int i = 0; i < adjc[u]; i++) {
			v = adj[u][i];
			if (dfsDMST(r, v)) {
				mine = Math.min(mine, mins[v]);
				cycle[u] = true;
				return true;
			}
		}
		return false;
	}

	static void dfsDMST(int u) {
		if (visit[u])
			return;
		visit[u] = true;
		for (int i = 0; i < adjc[u]; i++)
			dfsDMST(adj[u][i]);
	}

	static int dmst(int root, int N) {
		int i, j, k, w, inc, pw, nc, mc, u, v, pu, pv, pj;
		boolean hc, first;
		Arrays.fill(visit, 0, N, false);
		for (i = 0; i < N; i++)
			unionFind.makeSet(i);
		dfsDMST(root);
		for (i = 0; i < N; i++)
			if (!visit[i])
				return INF;
		first = true;
		mc = 0;
		while (true) {
			Arrays.fill(adjc, 0, N, 0);
			Arrays.fill(pred, 0, N, -1);
			Arrays.fill(mins, 0, N, INF);
			for (i = 0; i < N; i++)
				if (i != root)
					for (j = 0; j < radjc[i]; j++) {
						u = radj[i][j];
						v = i;
						pu = unionFind.findSet(u);
						pv = unionFind.findSet(v);
						if (pu == pv)
							continue;
						if (cost[u][v] < mins[pv]) {
							mins[pv] = cost[u][v];
							pred[pv] = pu;
						}
					}
			for (i = 0; i < N; i++)
				if (pred[i] >= 0) {
					k = pred[i];
					adj[k][adjc[k]++] = i;
					if (first)
						mc += mins[i];
				}
			first = false;
			Arrays.fill(cycle, 0, N, false);
			Arrays.fill(visit, 0, N, false);
			inc = mine = INF;
			hc = false;
			for (i = 0; i < N; i++)
				if (!visit[i])
					if (hc = dfsDMST(i, i)) {
						for (j = 0; j < N; j++) {
							pj = unionFind.findSet(j);
							if (!cycle[pj])
								continue;
							for (k = 0; k < radjc[j]; k++) {
								w = radj[j][k];
								pw = unionFind.findSet(w);
								if (cycle[pw])
									continue;
								nc = cost[w][j] + mine - mins[pj];
								if (nc - mine < inc)
									inc = nc - mine;
								cost[w][j] = nc;
							}
						}
						for (j = 0; j < N; j++)
							if (cycle[j])
								unionFind.unionSet(i, j);
						break;
					}
			if (!hc)
				return mc;
			else
				mc += inc;
		}
	}

	static int prim(int N) {
		int i, j, cost = 0;
		set.clear();
		mst.clear();
		set.add(new Edge(-1, 0, 0)); 
		for (i = 0; i < N; i++) {
			iter = set.iterator();
			do {
				if (!iter.hasNext())
					return INF;
				edge = iter.next();
				iter.remove();
			} while (inTree[edge.b]);
			mst.add(edge);
			cost += edge.cost;
			inTree[edge.b] = true;
			for (j = 0; j < N; j++)
				if (!inTree[j] && graph[edge.b][j] < INF)
					set.add(new Edge(edge.b, j, graph[edge.b][j]));
		}
		return cost;
	}

	static void kruskal(int N) {
		ArrayList<Edge> mst = new ArrayList<Edge>();
		ArrayList<TreeSet<Integer>> forest = new ArrayList<TreeSet<Integer>>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		int i, j, ta, tb;
		TreeSet<Integer> tree;
		Edge e;
		for (i = 0; i < N; i++) {
			tree = new TreeSet<Integer>();
			tree.add(i);
			forest.add(tree);
			for (j = i + 1; j < N; j++)
				edges.add(new Edge(i, j, graph[i][j]));
		}
		Collections.sort(edges);
		for (i = 0; i < edges.size(); i++) {
			e = edges.get(i);
			ta = tb = -1;
			for (j = 0; j < forest.size() && (ta < 0 || tb < 0); j++) {
				if (forest.get(j).contains(e.a))
					ta = j;
				if (forest.get(j).contains(e.b))
					tb = j;
			}
			if (ta != tb) {
				mst.add(e);
				forest.get(ta).addAll(forest.get(tb));
				forest.remove(tb);
			}
		}
	}

	static void searchBridge(int v, int w) {
		int i, t;
		low[w] = ord[w] = cnt++;
		for (i = 0; i < deg[w]; i++) {
			t = adj[w][i];
			if (ord[t] == -1) {
				searchBridge(w, t);
				low[w] = Math.min(low[w], low[t]);
				if (low[t] == ord[t])
					bCnt++; 
			} else if (t != v)
				low[w] = Math.min(low[w], ord[t]);
		}
	}

	static int bridges(int N) {
		int v;
		bCnt = cnt = 0;
		Arrays.fill(ord, 0, N, -1);
		Arrays.fill(low, 0, N, -1);
		for (v = 0; v < N; v++)
			if (ord[v] == -1)
				searchBridge(v, v);
		return bCnt + 1;
	}

	static int tour(int v) {
		while (true) {
			if (deg[v] == 0)
				break;
			v = adj[v][--deg[v]];
		}
		return v;
	}

	static String eulerPath(int N, int v) {
		Stack<Integer> stack = new Stack<Integer>();
		String path = "";
		while (tour(v) == v && !stack.isEmpty()) {
			v = stack.pop();
			path += "-" + v;
		}
		return path;
	}

	static boolean topologicalSort(int N) {
		int i, j, v;
		for (i = 0; i < N; i++)
			for (j = 0; j < N; j++)
				if (bgraph[i][j])
					inDegree[j]++;
		for (i = 0; i < N; i++)
			if (inDegree[i] == 0)
				queue.add(i);
		for (j = 0; j < N; j++) {
			if (queue.isEmpty())
				return false;
			sorted[j] = v = queue.poll();
			for (i = 0; i < N; i++)
				if (bgraph[v][i] && --inDegree[i] == 0)
					queue.offer(i);
		}
		return true;
	}
}

class Edge implements Comparable<Edge> {
	int a, b, cost;

	Edge(int _a, int _b, int _cost) {
		a = _a;
		b = _b;
		cost = _cost;
	}

	public String toString() {
		return "[" + a + "-" + b + "=" + cost + "]";
	}

	public int compareTo(Edge e) {
		if (cost != e.cost)
			return cost - e.cost;
		if (a != e.a)
			return a - e.a;
		return b - e.b;
	}

	public boolean equals(Edge e) {
		return e.a == a && e.b == b;
	}
}

class Vertex implements Comparable<Vertex> {
	int v, priority;

	Vertex(int _v, int _priority) {
		v = _v;
		priority = _priority;
	}

	public int compareTo(Vertex x) {
		if (priority != x.priority)
			return priority - x.priority;
		return v - x.v;
	}

	public boolean equals(Vertex x) {
		return v == x.v;
	}
}
