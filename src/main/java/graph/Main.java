package graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graph.dagsp.DagShortestPaths;
import graph.scc.SCCFinder;
import graph.topo.TopoSort;
import graph.utils.GraphLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String[] categories = {"small", "medium", "large"};
        String baseFolder = "data/";

        Result result = new Result();
        result.results = new ArrayList<>();

        for (String category : categories) {
            String filePath = baseFolder + category + ".json";

            List<GraphLoader.GraphData> graphs = GraphLoader.loadGraphs(filePath);

            int index = 1;
            for (GraphLoader.GraphData gData : graphs) {
                String datasetName = category + index;
                index++;

                if (gData.graph == null) {
                    System.out.println(datasetName + ": graph data is null, skipping.");
                    continue;
                }
                List<List<Integer>> unweighted = new ArrayList<>();
                for (int i = 0; i < gData.n; i++) {
                    List<Integer> adj = new ArrayList<>();
                    if(gData.graph.size() > i && gData.graph.get(i) != null) {
                        for (GraphLoader.GraphData.Edge edge : gData.graph.get(i)) {
                            adj.add(edge.v);
                        }
                    }
                    unweighted.add(adj);
                }

                SCCFinder sccFinder = new SCCFinder(unweighted);
                List<List<Integer>> sccList = sccFinder.findSCCs();
                List<List<Integer>> condensation = sccFinder.buildCondensationGraph();

                TopoSort topoSort = new TopoSort(condensation);
                List<Integer> topoOrder = topoSort.kahnSort();

                List<List<DagShortestPaths.Edge>> dagGraph = convertGraph(gData.graph);
                DagShortestPaths dagSP = new DagShortestPaths(dagGraph);
                int source = (gData.source >= 0 && gData.source < gData.n) ? gData.source : 0;

                int[] shortestDist = dagSP.shortestPaths(source);
                int[] longestDist = dagSP.longestPaths(source);

                int longestPathLength = Integer.MIN_VALUE;
                for (int d : longestDist) {
                    if (d > longestPathLength) longestPathLength = d;
                }

                ResultEntry entry = new ResultEntry();
                entry.datasetName = datasetName;
                entry.sccCount = sccList.size();
                entry.maxSccSize = sccList.stream().mapToInt(List::size).max().orElse(0);
                entry.topoOrderSize = topoOrder.size();
                entry.longestPathLength = longestPathLength;

                result.results.add(entry);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(baseFolder + "result.json")) {
            gson.toJson(result, writer);
        }

        System.out.println("Results written to " + baseFolder + "result.json");
    }

    private static List<List<DagShortestPaths.Edge>> convertGraph(List<List<GraphLoader.GraphData.Edge>> input) {
        List<List<DagShortestPaths.Edge>> newGraph = new ArrayList<>();
        for (List<GraphLoader.GraphData.Edge> edges : input) {
            List<DagShortestPaths.Edge> converted = new ArrayList<>();
            if (edges != null) {
                for (GraphLoader.GraphData.Edge e : edges) {
                    converted.add(new DagShortestPaths.Edge(e.v, e.w));
                }
            }
            newGraph.add(converted);
        }
        return newGraph;
    }

    static class Result {
        List<ResultEntry> results;
    }

    static class ResultEntry {
        String datasetName;
        int sccCount;
        int maxSccSize;
        int topoOrderSize;
        int longestPathLength;
    }
}
