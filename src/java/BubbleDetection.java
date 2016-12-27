import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 27.12.2016.
 */
public class BubbleDetection {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        String[] ints = line.split(" ");
        int requiredReadLength = Integer.valueOf(ints[0]);
        int threshold = Integer.valueOf(ints[1]);
        Metric.debug = false;
        List<String> strings = new ArrayList<>();
        line = br.readLine();
        do {
            strings.add(line);
            line = br.readLine();
        } while (line != null && !line.isEmpty());
        System.out.println(getBubbles(strings, requiredReadLength, threshold).size());
    }

    static List<Bubble> getBubbles(List<String> strings, int readLength, int threshold) {
        List<String> reads = new ArrayList<>();
        for (String string : strings) {
            for (int i = 0; i <= string.length() - readLength; i++) {
                reads.add(string.substring(i, i + readLength));
            }
        }
        DeBruhinGraph dbGraph = DeBruhinGraph.build(reads);
        List<Integer>[] graph = dbGraph.graph;
        List<Integer> candidateStart = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            if (graph[i].size() > 1) {
                candidateStart.add(i);
            }
        }
        List<Bubble> bubbles = new ArrayList<>();
        for (Integer start : candidateStart) {
            List<List<Integer>> foundPaths = new ArrayList<>();
            for (Integer direction : graph[start]) {
                List<List<Integer>> lists = dfs(graph, new boolean[graph.length], direction, threshold - 1, start);
                if (lists != null) {
                    foundPaths.addAll(lists);
                }
            }
            for (int i = 0; i < foundPaths.size(); i++) {
                List<Integer> thisPath = foundPaths.get(i);
                iterPaths: for (int j = i; j < foundPaths.size(); j++) {
                    if (i == j){
                        continue;
                    }
                    List<Integer> thatPath = foundPaths.get(j);
                    int lastIndex = thatPath.size() - 1;
                    for (int k = lastIndex; k >= 0; k--) {
                        Integer currNode = thatPath.get(k);
                        if (thisPath.contains(currNode)){
                            if (k == lastIndex){
                                continue iterPaths; // must be disjoint paths
                            }
                            bubbles.add(new Bubble(start,currNode));
                            continue iterPaths;
                        }
                    }
                }
            }
        }
        return bubbles;
    }

    static List<List<Integer>> dfs(List<Integer>[] graph, boolean[] used, Integer direction, int threshold, int start) {
        used[direction] = true;
        List<List<Integer>> paths = new ArrayList<>();
        if (threshold == 0) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            result.get(result.size() - 1).add(direction);
            return result;
        }
        for (Integer node : graph[direction]) {
            if (node == start) {
                continue;
            }
            List<List<Integer>> lists = dfs(graph, used, node, threshold - 1, start);
            if (lists == null) {
                continue;
            }
            paths.addAll(lists);
        }
        for (List<Integer> path : paths) {
            path.add(direction);
        }
        return paths;
    }

    static class Bubble {
        int start;
        int end;

        public Bubble(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}

class DeBruhinGraph {
    private static final Metric metric = new Metric();
    public final List<Integer>[] graph;
    public final List<DebruinNode> nodes;

    private DeBruhinGraph(List<Integer>[] graph, List<DebruinNode> nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }

    public static DeBruhinGraph build(List<String> reads){
        metric.start("buildNodes");
        List<DebruinNode> nodes = new ArrayList<>();
        HashMap<String, Integer> nodeIndexByPrefix = new HashMap<>();
        for (String read : reads) {
            String prefix = read.substring(0, read.length() - 1);
            Integer nodeIndex = nodeIndexByPrefix.get(prefix);
            if (nodeIndex == null) {
                nodeIndex = nodes.size();
                nodes.add(new DebruinNode(prefix));
                nodeIndexByPrefix.put(prefix, nodeIndex);
            }
            nodes.get(nodeIndex).addDirection(read.substring(1));
        }
        metric.finish("buildNodes");
        return buildGraph(nodeIndexByPrefix, nodes);
    }

    private static DeBruhinGraph buildGraph(HashMap<String, Integer> nodeIndexByPrefix, List<DebruinNode> nodes) {
        metric.start("graphConstruction");
        List<Integer>[] graph = new List[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            DebruinNode node = nodes.get(i);
            List<Integer> connections = new ArrayList<>();
            for (String targetPrefix : node.directions.keySet()) {
                Integer targetNode = nodeIndexByPrefix.get(targetPrefix);
                if (targetNode != null) {
                    connections.add(targetNode);
                }
            }
            graph[i] = connections;
        }
        metric.finish("graphConstruction");
        return new DeBruhinGraph(graph, nodes);
    }


    private static class DebruinNode {
        final String prefix;
        final Map<String, Integer> directions = new HashMap<>();
        final List<String> paths = new ArrayList<>();

        DebruinNode(String prefix) {
            this.prefix = prefix;
        }

        public void addDirection(String direction) {
            directions.putIfAbsent(direction, directions.size());
        }

        @Override
        public String toString() {
            return prefix + " -> " + directions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DebruinNode that = (DebruinNode) o;

            return prefix != null ? prefix.equals(that.prefix) : that.prefix == null;

        }

        @Override
        public int hashCode() {
            return prefix != null ? prefix.hashCode() : 0;
        }
    }

}

class Metric {
    public static boolean debug = false;
    private Map<String, Long> startByName = new HashMap<>();

    public void start(String name) {
        if (debug) {
            startByName.put(name, System.currentTimeMillis());
        }
    }

    public void log(String string){
        if (debug) {
            System.out.println(string);
        }
    }

    public void finish(String name) {
        if (debug) {
            long finished = System.currentTimeMillis();
            Long startedAt = startByName.remove(name);
            if (startedAt == null) {
                throw new IllegalStateException(name + " not started");
            }
            System.out.println(name + " took " + (finished - startedAt) + " millis");
        }
    }
}