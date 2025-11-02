package graph.dagsp;

import java.util.*;

public class DagShortestPaths {
    private final List<List<Edge>> graph;
    private final int n;

    // Edge with weight (or duration if chosen)
    public static class Edge {
        public int to;
        public int weight;
        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public DagShortestPaths(List<List<Edge>> graph) {
        this.graph = graph;
        this.n = graph.size();
    }

    // Находит топологический порядок (вспомогательно)
    private List<Integer> topologicalOrder() {
        int[] inDegree = new int[n];
        for (List<Edge> adj : graph) for (Edge e : adj) inDegree[e.to]++;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (inDegree[i] == 0) queue.add(i);
        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (Edge e : graph.get(u)) {
                if (--inDegree[e.to] == 0) queue.add(e.to);
            }
        }
        if (order.size() != n) throw new IllegalStateException("Graph is not DAG");
        return order;
    }

    // Кратчайшие пути от источника
    public int[] shortestPaths(int source) {
        List<Integer> order = topologicalOrder();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        for (int u : order) {
            if (dist[u] == Integer.MAX_VALUE) continue;
            for (Edge e : graph.get(u)) {
                if (dist[e.to] > dist[u] + e.weight) {
                    dist[e.to] = dist[u] + e.weight;
                }
            }
        }
        return dist;
    }

    // Длиннейший путь в DAG (критический путь)
    public int[] longestPaths(int source) {
        List<Integer> order = topologicalOrder();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        dist[source] = 0;
        for (int u : order) {
            if (dist[u] == Integer.MIN_VALUE) continue;
            for (Edge e : graph.get(u)) {
                if (dist[e.to] < dist[u] + e.weight) {
                    dist[e.to] = dist[u] + e.weight;
                }
            }
        }
        return dist;
    }

    // Восстановление одного оптимального пути по массиву расстояний
    public List<Integer> reconstructLongestPath(int source, int target, int[] dist) {
        List<Integer> path = new ArrayList<>();
        path.add(target);
        int current = target;
        // Ищем предка по условиям максимальной длины
        while (current != source) {
            boolean found = false;
            for (int u = 0; u < n; u++) {
                for (Edge e : graph.get(u)) {
                    if (e.to == current && dist[current] == dist[u] + e.weight) {
                        path.add(u);
                        current = u;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) throw new IllegalStateException("Path reconstruction failed");
        }
        Collections.reverse(path);
        return path;
    }
}
