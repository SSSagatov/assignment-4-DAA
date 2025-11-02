package graph.scc;

import java.util.*;

public class SCCFinder {
    private final List<List<Integer>> graph;
    private int time;
    private int[] disc, low;
    private boolean[] inStack;
    private Deque<Integer> stack;
    private List<List<Integer>> sccList;

    public SCCFinder(List<List<Integer>> graph) {
        this.graph = graph;
        int n = graph.size();
        disc = new int[n];
        Arrays.fill(disc, -1);
        low = new int[n];
        inStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccList = new ArrayList<>();
        time = 0;
    }

    public List<List<Integer>> findSCCs() {
        for (int i = 0; i < graph.size(); i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }
        return sccList;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        inStack[u] = true;

        for (int v : graph.get(u)) {
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // root of SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int v = stack.pop();
                inStack[v] = false;
                component.add(v);
                if (v == u) break;
            }
            sccList.add(component);
        }
    }

    // конструирует конденсационный граф из SCC
    public List<List<Integer>> buildCondensationGraph() {
        List<List<Integer>> condensation;
        int n = graph.size();
        // Маппинг вершины к компоненте SCC
        int[] sccId = new int[n];
        Arrays.fill(sccId, -1);
        for (int i = 0; i < sccList.size(); i++) {
            for (int v : sccList.get(i)) {
                sccId[v] = i;
            }
        }
        condensation = new ArrayList<>();
        for (int i = 0; i < sccList.size(); i++) condensation.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                int cu = sccId[u], cv = sccId[v];
                if (cu != cv) {
                    condensation.get(cu).add(cv);
                }
            }
        }
        // Можно удалить дубликаты ребер, если надо
        for (List<Integer> edges : condensation) {
            Set<Integer> set = new HashSet<>(edges);
            edges.clear();
            edges.addAll(set);
        }
        return condensation;
    }

    public List<Integer> getSccSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> comp : sccList) {
            sizes.add(comp.size());
        }
        return sizes;
    }
}
