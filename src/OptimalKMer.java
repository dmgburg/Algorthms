import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 25.12.2016.
 */
public class OptimalKMer {
    private static final Metric metric = new Metric();

    public static void main(String[] args) throws IOException {
        Metric.debug = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> strings = new ArrayList<>();
        String line = br.readLine();
        do {
            strings.add(line);
            line = br.readLine();
        } while (line != null && !line.isEmpty());
        System.out.println(getOptimal(strings));
    }

    static int deBruhinGraph(List<String> strings) {
        metric.start("getOptimal");
        int current = getOptimal(strings);
        metric.finish("getOptimal");
        metric.start("buildNodes");
        List<DebruinNode> nodes = new ArrayList<>();
        List<String> reads = new ArrayList<>();
        for (String string : strings) {
            for (int i = 0; i <= string.length() - current; i++) {
                reads.add(string.substring(i, i + current));
            }
        }
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
        metric.start("getPaths");
        CirculationInGraph.GraphWithEdgeDemands circulationGraph = new CirculationInGraph.GraphWithEdgeDemands(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            DebruinNode node = nodes.get(i);
            for (String direction : node.directions.keySet()) {
                Integer index = nodeIndexByPrefix.get(direction);
                circulationGraph.edges.add(new CirculationInGraph.EdgeWithDemand(i, index, 1, nodes.size()));
            }
        }
        metric.log("Edges count: " + circulationGraph.edges.size());
        int[] pathsAmounts = CirculationInGraph.circulation(circulationGraph);
        metric.finish("getPaths");
        return current;
    }

    static int getOptimal(List<String> strings) {
        int top = strings.get(0).length();
        int current = -1;
        kIteration:
        while (top > 0) {
            current = top--;
            metric.start("buildNodes");
            List<DebruinNode> nodes = new ArrayList<>();
            List<String> reads = new ArrayList<>();
            for (String string : strings) {
                for (int i = 0; i <= string.length() - current; i++) {
                    reads.add(string.substring(i, i + current));
                }
            }
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
            List<Integer>[] graph = buildGraph(nodeIndexByPrefix, nodes);
            List<List<Integer>> scc = StronglyConnectedComponents.scc(graph);
            if (scc.size() == 1) {
                break;
            }
        }
        return current;
    }

    private static List<Integer>[] buildGraph(HashMap<String, Integer> nodeIndexByPrefix, List<DebruinNode> nodes) {
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
        return graph;
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