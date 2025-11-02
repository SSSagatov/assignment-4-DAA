package graph.topo;

import java.util.*;

public class TopoSort {
    private final List<List<Integer>> graph;
    private final int n;

    public TopoSort(List<List<Integer>> graph) {
        this.graph = graph;
        this.n = graph.size();
    }

    // Кahn's algorithm для топологической сортировки в DAG
    public List<Integer> kahnSort() {
        int[] inDegree = new int[n];
        for (List<Integer> adj : graph) {
            for (int v : adj) inDegree[v]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) queue.add(i);
        }
        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            for (int v : graph.get(u)) {
                if (--inDegree[v] == 0) queue.add(v);
            }
        }
        if (topoOrder.size() != n)
            throw new IllegalStateException("Graph is not a DAG");
        return topoOrder;
    }
}
