package graph.topo;

import java.util.*;

/**
 * Топологическая сортировка Кана.
 */
public class TopoSort {

    private final List<List<Integer>> graph;
    private final int n;

    public TopoSort(List<List<Integer>> graph) {
        this.graph = graph;
        this.n = graph.size();
    }

    public List<Integer> kahnSort() {
        int[] indegree = new int[n];
        for (List<Integer> adj : graph) {
            for (int v : adj) indegree[v]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) queue.add(i);
        }
        List<Integer> topo = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topo.add(u);
            for (int v : graph.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) queue.add(v);
            }
        }
        if (topo.size() != n) throw new IllegalStateException("Graph has cycles");
        return topo;
    }
}
