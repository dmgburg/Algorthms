import com.sun.javafx.geom.Edge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

class MaxFlowEdmondsKarp {
    private static final Metric metric = new Metric();

    public static int maxFlow(FlowGraph graph, int from, int to) {
        metric.start("maxFlow");
        int flow = 0;
        List<Edge> path;
        metric.log(graph.size() + " - " + graph.getNextEdgeNumber());
        while (true) {
            path = bfs(graph, from, to);
            if (path.size() == 0) {
                break;
            }
            int maxFlow = path.stream().mapToInt((edge) -> edge.capacity - edge.flow).min().getAsInt();
            path.forEach(edge -> graph.addFlow(edge, maxFlow));
            flow += maxFlow;
        }
        metric.finish("maxFlow");
        return flow;
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

        public FlowGraph(int vertexCount) {
            this.graph = (ArrayList<Integer>[]) new ArrayList[vertexCount];
            for (int i = 0; i < vertexCount; ++i)
                this.graph[i] = new ArrayList<>();
            this.edges = new ArrayList<>();
        }

        public void addEdge(int from, int to, int capacity) {
            /* Note that we first append a forward edge and then a backward edge,
             * so all forward edges are stored at even indices (starting from 0),
             * whereas backward edges are stored at odd indices. */
            Edge forwardEdge = new Edge(from, to, capacity, getNextEdgeNumber());
            Edge backwardEdge = new Edge(to, from, 0, getNextEdgeNumber() + 1);
            graph[from].add(getNextEdgeNumber());
            edges.add(forwardEdge);
            graph[to].add(getNextEdgeNumber());
            edges.add(backwardEdge);
        }

        public List<Edge> getEdges() {
            return edges;
        }

        public int getNextEdgeNumber() {
            return edges.size();
        }

        public int getVertexCount() {
            return graph.length;
        }

        public int size() {
            return graph.length;
        }

        public List<Integer>[] getGraph() {
            return graph;
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

        @Override
        public String toString() {
            return "FlowGraph{"
                    + edges +
                    '}';
        }
    }


}
