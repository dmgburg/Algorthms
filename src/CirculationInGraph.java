import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

class CirculationInGraph {
    private static FastScanner in;
    private static final Metric metric = new Metric();

    public static void main(String[] args) throws IOException {
        in = new FastScanner();

        int vertex_count = in.nextInt();
        int edge_count = in.nextInt();
        GraphWithEdgeDemands graph = new GraphWithEdgeDemands(vertex_count);

        for (int i = 0; i < edge_count; ++i) {
            int from = in.nextInt() - 1;
            int to = in.nextInt() - 1;
            int minUsed = in.nextInt();
            int capacity = in.nextInt();
            graph.edges.add(new EdgeWithDemand(from, to, minUsed, capacity));
        }

        int[] flow = circulation(graph);
        if (flow == null){
            System.out.println("NO");
        } else {
            System.out.println("YES");
            for (int i = 0; i < flow.length; i++) {
                System.out.println(flow[i]);
            }
        }
    }

    static int[] circulation(GraphWithEdgeDemands graph) {
        MaxFlowEdmondsKarp.FlowGraph ekgraph = reduceToEdmondKarp(graph);
        int maxFlow = MaxFlowEdmondsKarp.maxFlow(ekgraph, ekgraph.getVertexCount() - 2, ekgraph.getVertexCount() - 1);
        int sumDemand = 0;
        for (EdgeWithDemand edgeWithDemand : graph.edges) {
            sumDemand += edgeWithDemand.demand;
        }
        boolean isFeasible = sumDemand == maxFlow;
        int[] flow = new int[graph.edges.size()];
        if (isFeasible) {
            for (int i = 0; i < graph.edges.size(); i++) {
                EdgeWithDemand edgeWithDemand = graph.edges.get(i);
                flow[i] = ekgraph.getEdge(edgeWithDemand.edmondkarpIndex).flow + ekgraph.getEdge(edgeWithDemand.edmondKarpSourceIndex).flow;
            }
            return flow;
        } else {
            return null;
        }

    }

    private static MaxFlowEdmondsKarp.FlowGraph reduceToEdmondKarp(GraphWithEdgeDemands graph) {
        metric.start("reduction");
        MaxFlowEdmondsKarp.FlowGraph ekGraph = new MaxFlowEdmondsKarp.FlowGraph(graph.vertexCount + 2);
        //vertex calculation rule:
        // for original vertex vertex number = ek-vertex number
        // for first artifitial vertex vertex number = Noriginal + 2*edge number + 1
        // for second artifitial vertex vertex number = Noriginal + 2*edge number  + 2
        // sink is graph.vertexCount + 2 * graph.edges.size()
        // source is graph.vertexCount + 2 * graph.edges.size() + 1

        for (int i = 0; i < graph.edges.size(); i++) {
            EdgeWithDemand edgeWithDemand = graph.edges.get(i);
            edgeWithDemand.edmondkarpIndex = ekGraph.getNextEdgeNumber();
            ekGraph.addEdge(edgeWithDemand.from, edgeWithDemand.to, edgeWithDemand.capacity - edgeWithDemand.demand);
        }
        int source = graph.vertexCount;
        int sink = graph.vertexCount + 1;
        for (int i = 0; i < graph.edges.size(); i++) {
            EdgeWithDemand edgeWithDemand = graph.edges.get(i);
            ekGraph.addEdge(edgeWithDemand.from, sink, edgeWithDemand.demand);

            edgeWithDemand.edmondKarpSourceIndex = ekGraph.getNextEdgeNumber();
            ekGraph.addEdge(source, edgeWithDemand.to, edgeWithDemand.demand);
        }
        metric.finish("reduction");
        return ekGraph;
    }

    static class GraphWithEdgeDemands {
        int vertexCount;
        List<EdgeWithDemand> edges = new ArrayList<>();

        public GraphWithEdgeDemands(int vertexCount) {
            this.vertexCount = vertexCount;
        }
    }

    static class EdgeWithDemand {
        int from;
        int to;
        int demand;
        int capacity;

        int edmondkarpIndex;
        int edmondKarpSourceIndex;

        public EdgeWithDemand(int from, int to, int demand, int capacity) {
            this.from = from;
            this.to = to;
            this.demand = demand;
            this.capacity = capacity;
        }


        @Override
        public String toString() {
            return "EdgeWithDemand{" +
                    "from=" + from +
                    ", to=" + to +
                    ", demand=" + demand +
                    ", capacity=" + capacity +
                    ", edmondkarpIndex=" + edmondkarpIndex +
                    ", edmondKarpSourceIndex=" + edmondKarpSourceIndex +
                    '}';
        }
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }

}
