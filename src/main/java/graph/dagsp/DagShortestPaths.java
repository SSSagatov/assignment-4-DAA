package graph.dagsp;

import java.util.*;

public class DagShortestPaths {

    public static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private final List<List<Edge>> graph;
    private final int n;

    public DagShortestPaths(List<List<Edge>> graph) {
        this.graph = graph;
        this.n = graph.size();
    }

    private List<Integer> topologicalSort() {
        int[] indeg = new int[n];
        for (List<Edge> edges : graph)
            for (Edge e : edges) indeg[e.to]++;
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) q.add(i);
        }
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (Edge e : graph.get(u)) {
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    q.add(e.to);
                }
            }
        }
        if (order.size() != n)
            throw new IllegalStateException("Graph is not DAG");
        return order;
    }

    public int[] shortestPaths(int source) {
        if (n == 0) return new int[0];
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        List<Integer> order = topologicalSort();

        for (int u : order) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Edge e : graph.get(u)) {
                    if (dist[e.to] > dist[u] + e.weight) {
                        dist[e.to] = dist[u] + e.weight;
                    }
                }
            }
        }
        return dist;
    }

    public int[] longestPaths(int source) {
        if (n == 0) return new int[0];
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        dist[source] = 0;

        List<Integer> order = topologicalSort();

        for (int u : order) {
            if (dist[u] != Integer.MIN_VALUE) {
                for (Edge e : graph.get(u)) {
                    if (dist[e.to] < dist[u] + e.weight) {
                        dist[e.to] = dist[u] + e.weight;
                    }
                }
            }
        }
        return dist;
    }
}
