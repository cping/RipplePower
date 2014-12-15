package org.ripple.power.hft;

public class UnionFind {
	int[] p, rank;

	public UnionFind(int size) {
		p = new int[size];
		rank = new int[size];
	}

	void makeSet(int x) {
		p[x] = x;
		rank[x] = 0;
	}

	void link(int x, int y) {
		if (rank[x] > rank[y])
			p[y] = x;
		else {
			p[x] = y;
			if (rank[x] == rank[y])
				rank[y]++;
		}
	}

	int findSet(int x) {
		if (x != p[x])
			p[x] = findSet(p[x]);
		return p[x];
	}

	void unionSet(int x, int y) {
		link(findSet(x), findSet(y));
	}
}
