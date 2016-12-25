import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class StockCharts {
    private FastScanner in;
    private PrintWriter out;

    public static void main(String[] args) throws IOException {
        new StockCharts().solve();
    }

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        int[][] stockData = readData();
        int result = minCharts(stockData);
        writeResponse(result);
        out.close();
    }

    private int minCharts(int[][] stockData) {
        int dataCount = stockData.length;
        boolean[][] bipartiteGraph = new boolean[dataCount][];
        for (int j = 0; j < dataCount; j++) {
            bipartiteGraph[j] = new boolean[dataCount];
        }
        for (int i = 0; i < dataCount; i++) {
            for (int j = 0; j < dataCount; j++) {
                bipartiteGraph[i][j] = isLess(stockData[i], stockData[j]);
            }
        }
        return findMatching(bipartiteGraph);
    }

    String bipartToString(boolean[][] bipart) {
        StringBuilder sb = new StringBuilder();
        int dataCount = bipart.length;
        for (int i = 0; i < dataCount; i++) {
            for (int j = 0; j < dataCount; j++) {
                sb.append(" ");
                if (bipart[i][j]) {
                    sb.append(1);
                } else {
                    sb.append(0);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    int[][] readData() throws IOException {
        int numStocks = in.nextInt();
        int numPoints = in.nextInt();
        int[][] stockData = new int[numStocks][numPoints];
        for (int i = 0; i < numStocks; ++i)
            for (int j = 0; j < numPoints; ++j)
                stockData[i][j] = in.nextInt();
        return stockData;
    }


    private int findMatching(boolean[][] bipartiteGraph) {
        // Replace this code with an algorithm that finds the maximum
        // matching correctly in all cases.

        int numLeft = bipartiteGraph.length;
        int numRight = numLeft;
        FlowGraph graph = new FlowGraph(numLeft + numRight + 2);
        for (int i = 1; i <= numLeft; i++) {
            graph.addEdge(0, i, 1);
        }
        for (int i = 1; i <= numLeft; i++) {
            for (int j = 0; j < numRight; j++) {
                if (bipartiteGraph[i - 1][j]) {
                    graph.addEdge(i, numLeft + j + 1, 1);
                }
            }
        }
        int lastVertex = graph.getVertexCount() - 1;
        for (int i = numLeft + 1; i < numLeft + numRight + 1; i++) {
            graph.addEdge(i, lastVertex, 1);
        }

        List<Edge> path;
        while (true) {
            path = bfs(graph, 0, lastVertex);
            if (path.size() == 0) {
                break;
            }
            path.forEach(edge -> graph.addFlow(edge, 1));
        }
        List<Edge> sourceEdges = graph.edges.stream().filter(edge -> edge.to == lastVertex && edge.flow > 0).collect(Collectors.toList());
        return sourceEdges.size();
    }

    private static List<Edge> bfs(FlowGraph graph, int from, int to) {
        int[] dist = new int[graph.getVertexCount()];
        Edge[] prev = new Edge[graph.getVertexCount()];
        Arrays.fill(dist, -1);
        Queue<Integer> queue = new ArrayDeque<>(graph.getVertexCount());
        queue.add(from);
        dist[from] = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            List<Edge> edges = graph.getIds(current).stream()
                    .map(graph::getEdge)
                    .filter(edge -> edge.capacity > edge.flow)
                    .collect(Collectors.toList());
            for (Edge edge : edges) {
                int dest = edge.to;
                if (dist[dest] == -1) {
                    queue.add(dest);
                    dist[dest] = dist[edge.from] + 1;
                    prev[dest] = edge;
                }
            }
        }
        List<Edge> result = new ArrayList<>();
        Edge edge;
        int dest = to;
        do {
            edge = prev[dest];
            if (edge == null) {
                return new ArrayList<>();
            }
            result.add(edge);
            dest = edge.from;
        } while (edge.from != from);

        return result;
    }

    boolean isLess(int[] stock1, int[] stock2) {
        for (int i = 0; i < stock1.length; ++i) {
            if (compare(stock1, stock2, i) <= 0) {
                return false;
            }
        }
        return true;
    }

    int compare(int[] stock1, int[] stock2, int i) {
        return stock1[i] > stock2[i] ? 1 : stock1[i] < stock2[i] ? -1 : 0;
    }

    static class Edge {
        int from, to, capacity, flow;
        final int id;

        public Edge(int from, int to, int capacity, int id) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
            this.id = id;
        }

        @Override
        public String toString() {
            return "{" +
                    +from +
                    "," + to +
                    "," + capacity +
                    "," + flow +
                    "," + id +
                    '}';
        }
    }

    /* This class implements a bit unusual scheme to store the graph edges, in order
     * to retrieve the backward edge for a given edge quickly. */
    static class FlowGraph {
        /* List of all - forward and backward - edges */
        private List<Edge> edges;

        /* These adjacency lists store only indices of edges from the edges list */
        private List<Integer>[] graph;

        public FlowGraph(int n) {
            this.graph = (ArrayList<Integer>[]) new ArrayList[n];
            for (int i = 0; i < n; ++i)
                this.graph[i] = new ArrayList<>();
            this.edges = new ArrayList<>();
        }

        public void addEdge(int from, int to, int capacity) {
            /* Note that we first append a forward edge and then a backward edge,
             * so all forward edges are stored at even indices (starting from 0),
             * whereas backward edges are stored at odd indices. */
            Edge forwardEdge = new Edge(from, to, capacity, edges.size());
            Edge backwardEdge = new Edge(to, from, 0, edges.size() + 1);
            graph[from].add(edges.size());
            edges.add(forwardEdge);
            graph[to].add(edges.size());
            edges.add(backwardEdge);
        }

        public int getVertexCount() {
            return graph.length;
        }

        public int size() {
            return graph.length;
        }

        public List<Integer> getIds(int from) {
            return graph[from];
        }

        public Edge getEdge(int id) {
            return edges.get(id);
        }

        public void addFlow(Edge edge, int flow) {
            /* To get a backward edge for a true forward edge (i.e id is even), we should get id + 1
             * due to the described above scheme. On the other hand, when we have to get a "backward"
             * edge for a backward edge (i.e. get a forward edge for backward - id is odd), id - 1
             * should be taken.
             *
             * It turns out that id ^ 1 works for both cases. Think this through! */
            int id = edge.id;
            edges.get(id).flow += flow;
            edges.get(id ^ 1).flow -= flow;
        }

        public String toShortString() {
            return "FlowGraph{"
                    + edges.stream().filter(edge -> edge.id % 2 == 0).collect(Collectors.toList()) +
                    '}';
        }

        public String toPositiveString() {
            return "FlowGraph{"
                    + edges.stream().filter(edge -> edge.flow > 0).collect(Collectors.toList()) +
                    '}';
        }


        @Override
        public String toString() {
            return "FlowGraph{"
                    + edges +
                    '}';
        }
    }


    private void writeResponse(int result) {
        out.println(result);
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
