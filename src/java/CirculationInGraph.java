import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

// can be redused to
//http://www.win.tue.nl/~nikhil/courses/2013/2WO08/scribenotes26febv02.pdf
public class CirculationInGraph {
    private static FastScanner in;

    public static void main(String[] args) throws IOException {
        in = new FastScanner();

        MaxFlowEdmondsKarp.FlowGraph graph = readGraph();
        System.out.println(circulation(graph));
    }

    private static boolean circulation(MaxFlowEdmondsKarp.FlowGraph graph) {
        List<MaxFlowEdmondsKarp.FlowGraph> circles =findCircles(graph);
        return false;
    }

    private static List<MaxFlowEdmondsKarp.FlowGraph> findCircles(MaxFlowEdmondsKarp.FlowGraph graph) {
        List<Integer>[] rawGraph = graph.getGraph();
        List<List<Integer>> sccs = StronglyConnectedComponents.scc(rawGraph);
        List<List<Integer>> cicles = new ArrayList<>();
        for (List<Integer> scc: sccs){
            int start = scc.get(0);
            dfs(rawGraph,new boolean[graph.size()], new LinkedList<>(),cicles,start);
        }
        return null;
    }

    static void dfs(List<Integer>[] graph, boolean[] used, LinkedList<Integer> stack, List<List<Integer>> cicles, int start) {
        used[start] = true;
        for (int v : graph[start]) {
            if (!used[v]) {
                stack.push(v);
                dfs(graph, used, stack, cicles, v);
            } else {
                cicles.add(new ArrayList<>(stack));
            }
            stack.pop();
        }
    }

    static MaxFlowEdmondsKarp.FlowGraph readGraph() throws IOException {
        int vertex_count = in.nextInt();
        int edge_count = in.nextInt();
        MaxFlowEdmondsKarp.FlowGraph graph = new MaxFlowEdmondsKarp.FlowGraph(vertex_count);

        for (int i = 0; i < edge_count; ++i) {
            int from = in.nextInt() - 1;
            int to = in.nextInt() - 1;
            int minUsed = in.nextInt();
            int capacity = in.nextInt();
            graph.addEdge(from, to, minUsed, capacity);
        }
        return graph;
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
