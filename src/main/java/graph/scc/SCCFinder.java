package graph.scc;

import java.util.*;

/**
 * Алгоритм Тарьяна для нахождения сильносвязных компонент.
 */
public class SCCFinder {
    private final List<List<Integer>> graph;
    private final int n;

    private int time = 0;
    private int[] disc;
    private int[] low;
    private boolean[] stackMember;
    private Deque<Integer> stack;
    private List<List<Integer>> sccs;

    public SCCFinder(List<List<Integer>> graph) {
        this.graph = graph;
        this.n = graph.size();
    }

    public List<List<Integer>> findSCCs() {
        time = 0;
        disc = new int[n];
        low = new int[n];
        stackMember = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();

        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }
        return sccs;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        stackMember[u] = true;

        for (int v : graph.get(u)) {
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (stackMember[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                stackMember[w] = false;
                scc.add(w);
            } while (w != u);
            sccs.add(scc);
        }
    }

    public List<List<Integer>> buildCondensationGraph() {
        List<List<Integer>> sccList = findSCCs();
        int compCount = sccList.size();
        int[] compId = new int[n];
        for (int i = 0; i < compCount; i++) {
            for (int v : sccList.get(i)) {
                compId[v] = i;
            }
        }
        Set<Integer>[] adj = new HashSet[compCount];
        for (int i = 0; i < compCount; i++) adj[i] = new HashSet<>();

        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                if (compId[u] != compId[v]) {
                    adj[compId[u]].add(compId[v]);
                }
            }
        }
        List<List<Integer>> condensation = new ArrayList<>();
        for (Set<Integer> set : adj) {
            condensation.add(new ArrayList<>(set));
        }
        return condensation;
    }
}
