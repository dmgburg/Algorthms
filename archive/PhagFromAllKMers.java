import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static java.util.Arrays.copyOf;

public class PhagFromAllKMers {
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
        System.out.println(getCircullarString(strings));
    }

    static String getCircullarString(List<String> strings) {
        metric.start("buildNodes");
        List<DebruinNode> nodes = new ArrayList<>();
        HashMap<String, Integer> nodeIndexByPrefix = new HashMap<>();
        for (String read : strings) {
            String prefix = read.substring(0, read.length() - 1);
            Integer nodeIndex = nodeIndexByPrefix.get(prefix);
            if (nodeIndex == null) {
                nodeIndex = nodes.size();
                nodes.add(new DebruinNode(prefix));
                nodeIndexByPrefix.put(prefix, nodeIndex);
            }
            nodes.get(nodeIndex).paths.add(read.substring(1));
        }
        metric.finish("buildNodes");
        List<Integer>[] graph = buildGraph(nodeIndexByPrefix,nodes);
        List<Integer> eulerian = EulerianCicle.eulerCycleDirected(graph, 0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < eulerian.size() - 1; i++) {
            int node = eulerian.get(i);
            sb.append(nodes.get(node).prefix.charAt(0));
        }
        return sb.toString();
    }

    private static List<Integer>[] buildGraph(HashMap<String, Integer> nodeIndexByPrefix, List<DebruinNode> nodes) {
        metric.start("graphConstruction");
        List<Integer>[] graph = new List[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            DebruinNode node = nodes.get(i);
            List<Integer> connections = new ArrayList<>();
            for (String targetPrefix : node.paths){
                connections.add(nodeIndexByPrefix.get(targetPrefix));
            }
            graph[i] = connections;
        }
        metric.finish("graphConstruction");
        return graph;
    }

    private static class DebruinNode {
        final String prefix;
        final List<String> paths = new ArrayList<>();

        DebruinNode(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return prefix + " -> " + paths;
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

    public void log(String text) {
        if (debug) {
            System.out.println(text);
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

class EulerianCicle {
    public static List<Integer> eulerCycleDirected(List<Integer>[] graph, int v) {
        int[] curEdge = new int[graph.length];
        List<Integer> res = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        stack.add(v);
        while (!stack.isEmpty()) {
            v = stack.pop();
            while (curEdge[v] < graph[v].size()) {
                stack.push(v);
                v = graph[v].get(curEdge[v]++);
            }
            res.add(v);
        }
        Collections.reverse(res);
        return res;
    }
}