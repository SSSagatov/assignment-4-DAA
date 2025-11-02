package graph.tests;

import graph.dagsp.DagShortestPaths;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphAlgorithmsTest {

    private static class RandomGraph {
        int n;
        List<List<GraphEdge>> graph;

        static class GraphEdge {
            int to;
            int weight;

            GraphEdge(int to, int weight) {
                this.to = to;
                this.weight = weight;
            }
        }

        RandomGraph(int n, int maxEdgesPerNode, int maxWeight, boolean directed) {
            this.n = n;
            graph = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                graph.add(new ArrayList<>());
                int edgesNum = rand.nextInt(maxEdgesPerNode + 1);
                Set<Integer> used = new HashSet<>();
                for (int j = 0; j < edgesNum; j++) {
                    int to;
                    do {
                        to = rand.nextInt(n);
                    } while (to == i || used.contains(to));
                    used.add(to);
                    int weight = 1 + rand.nextInt(maxWeight);
                    graph.get(i).add(new GraphEdge(to, weight));
                }
            }
        }
    }

    @Test
    public void testRandomGraphsAndLogMetrics() throws IOException {
        int testCount = 50;
        Random rand = new Random();

        try (CSVPrinter printer = new CSVPrinter(new FileWriter("test_results.csv"),
                CSVFormat.DEFAULT.withHeader("TestID", "Nodes", "Edges", "SCCCount", "MaxSCCSize", "TopoOrderSize", "LongestPathLength"))) {

            for (int testId = 1; testId <= testCount; testId++) {
                int n = 5 + rand.nextInt(50);
                int maxEdgesPerNode = Math.min(n - 1, 10);
                int maxWeight = 20;

                RandomGraph rg = new RandomGraph(n, maxEdgesPerNode, maxWeight, true);

                // Convert RandomGraph to format for algorithms
                List<List<Integer>> unweighted = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    List<Integer> adj = new ArrayList<>();
                    for (RandomGraph.GraphEdge e : rg.graph.get(i)) {
                        adj.add(e.to);
                    }
                    unweighted.add(adj);
                }

                // Compute SCC
                SCCFinder sccFinder = new SCCFinder(unweighted);
                List<List<Integer>> sccList = sccFinder.findSCCs();

                // Compute condensation graph
                List<List<Integer>> condensation = sccFinder.buildCondensationGraph();

                // Compute topological order
                TopoSort topoSort = new TopoSort(condensation);
                List<Integer> topoOrder;
                try {
                    topoOrder = topoSort.kahnSort();
                } catch (IllegalStateException ise) {
                    topoOrder = Collections.emptyList(); // in case not DAG
                }

                // Convert for DagShortestPaths
                List<List<DagShortestPaths.Edge>> dagGraph = new ArrayList<>();
                for (List<RandomGraph.GraphEdge> edges : rg.graph) {
                    List<DagShortestPaths.Edge> dEdges = new ArrayList<>();
                    for (RandomGraph.GraphEdge e : edges) {
                        dEdges.add(new DagShortestPaths.Edge(e.to, e.weight));
                    }
                    dagGraph.add(dEdges);
                }

                DagShortestPaths dsp = new DagShortestPaths(dagGraph);
                int source = 0; // always start from 0

                int[] longestDist;
                try {
                    longestDist = dsp.longestPaths(source);
                } catch (Exception e) {
                    longestDist = new int[0];
                }

                int longestPathLength = longestDist.length > 0
                        ? Arrays.stream(longestDist).max().orElse(Integer.MIN_VALUE)
                        : Integer.MIN_VALUE;

                int edgesCount = 0;
                for (List<RandomGraph.GraphEdge> edgeList : rg.graph) {
                    edgesCount += edgeList.size();
                }

                printer.printRecord(testId, n, edgesCount,
                        sccList.size(),
                        sccList.stream().mapToInt(List::size).max().orElse(0),
                        topoOrder.size(),
                        longestPathLength);
            }
        }
    }
}
