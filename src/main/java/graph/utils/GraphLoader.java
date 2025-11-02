package graph.utils;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphLoader {

    public static List<GraphData> loadGraphs(String filename) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            JsonElement rootElement = gson.fromJson(reader, JsonElement.class);
            List<GraphData> graphs = new ArrayList<>();

            if (rootElement.isJsonObject()) {
                JsonObject root = rootElement.getAsJsonObject();
                if (root.has("graphs") && root.get("graphs").isJsonArray()) {
                    JsonArray graphsArr = root.getAsJsonArray("graphs");
                    for (JsonElement graphElem : graphsArr) {
                        GraphData graphData = gson.fromJson(graphElem, GraphData.class);
                        if (graphData.graph == null) {
                            graphData.graph = new ArrayList<>();
                        }
                        graphs.add(graphData);
                    }
                } else {
                    GraphData singleGraph = gson.fromJson(rootElement, GraphData.class);
                    if (singleGraph.graph == null) {
                        singleGraph.graph = new ArrayList<>();
                    }
                    graphs.add(singleGraph);
                }
            }
            return graphs;
        }
    }

    public static class GraphData {
        public boolean directed;
        public int n;
        public List<List<Edge>> graph;
        public int source;
        public String weight_model;

        public static class Edge {
            public int u;
            public int v;
            public int w;
        }

        public List<List<EdgeSimple>> getAdjacencyList() {
            List<List<EdgeSimple>> adj = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                adj.add(new ArrayList<>());
            }
            if (graph == null) return adj;
            for (int i = 0; i < graph.size(); i++) {
                List<Edge> edges = graph.get(i);
                if (edges == null) continue;
                for (Edge e : edges) {
                    adj.get(i).add(new EdgeSimple(e.v, e.w));
                }
            }
            return adj;
        }
    }

    public static class EdgeSimple {
        public int to;
        public int weight;

        public EdgeSimple(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }
}
