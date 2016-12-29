import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TipRemoval {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int requiredReadLength = Integer.valueOf(br.readLine());
        Metric.debug = false;
        List<String> strings = new ArrayList<>();
        String line = br.readLine();
        do {
            strings.add(line);
            line = br.readLine();
        } while (line != null && !line.isEmpty());
        System.out.println(getTipsIterative(strings, requiredReadLength));
    }

    public static int getTips(List<String> strings, int requiredReadLength) {
        DeBruhinGraph deBruhinGraph = getDeBruhinGraph(strings, requiredReadLength);
        Set<Integer> tipNodes = new HashSet<>();
        List<Integer>[] graph = deBruhinGraph.buildGraph();
        List<List<Integer>> sccs = StronglyConnectedComponents.scc(graph);
//        int maxSize = 0;
//        for (List<Integer> list : sccs) {
//            if (maxSize < list.size()) {
//                maxSize = list.size();
//            }
//        }
//        for (List<Integer> list : sccs) {
//            if (maxSize != list.size()) {
//                tipNodes.addAll(list);
//            }
//        }
        for (List<Integer> list : sccs) {
            if (list.size() == 1) {
                tipNodes.addAll(list);
            }
        }
        int result = 0;
        for (int i = 0; i < deBruhinGraph.nodes.size(); i++) {
            DeBruhinGraph.DebruinNode node = deBruhinGraph.nodes.get(i);
            if (tipNodes.contains(i)) {
                result += node.directions.size();
            } else {
                for (Integer direction : node.directions.values()) {
                    if (tipNodes.contains(direction)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }


    //No idea what's wrong. Let's wait mb there is an issue in grader
    static int getTipsIterative(List<String> strings, int requiredReadLength){
        DeBruhinGraph deBruhinGraph = getDeBruhinGraph(strings, requiredReadLength);
        int result = 0;
        boolean removed;
        do {
            removed = false;
            for (DeBruhinGraph.DebruinNode node : deBruhinGraph.nodes){
                if (node.incoming.size() == 0 && node.directions.size() >0){
                    for (String direction : node.directions.keySet()){
                        deBruhinGraph.removeEdge(node.prefix,direction);
                        removed = true;
                        result++;
                    }
                }
                if (node.incoming.size() > 0 && node.directions.size() == 0){
                    for (Integer sourceIndex : node.incoming){
                        deBruhinGraph.removeEdge(deBruhinGraph.nodes.get(sourceIndex).prefix,node.prefix);
                        removed = true;
                        result++;
                    }
                }
            }
        } while (removed);
        return result;
    }

    static DeBruhinGraph getDeBruhinGraph(List<String> strings, int readLength) {
        List<String> reads = new ArrayList<>();
        for (String string : strings) {
            for (int i = 0; i <= string.length() - readLength; i++) {
                reads.add(string.substring(i, i + readLength));
            }
        }
        return DeBruhinGraph.build(reads);
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

    public void log(String string) {
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

class DeBruhinGraph {
    private static final Metric metric = new Metric();
    public final List<DebruinNode> nodes;
    public final HashMap<String, Integer> nodeIndexByPrefix;


    private DeBruhinGraph(List<DebruinNode> nodes, HashMap<String, Integer> nodeIndexByPrefix) {
        this.nodes = nodes;
        this.nodeIndexByPrefix = nodeIndexByPrefix;
    }

    public static DeBruhinGraph build(List<String> reads) {
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
            String targetPrefix = read.substring(1);
            Integer targetNodeIndex = nodeIndexByPrefix.get(targetPrefix);
            if (targetNodeIndex == null) {
                targetNodeIndex = nodes.size();
                nodes.add(new DebruinNode(targetPrefix));
                nodeIndexByPrefix.put(targetPrefix, targetNodeIndex);
            }
            nodes.get(targetNodeIndex).addIncoming(nodeIndex);
            nodes.get(nodeIndex).addDirection(targetPrefix, targetNodeIndex);
        }
        metric.finish("buildNodes");
        return new DeBruhinGraph(nodes, nodeIndexByPrefix);
    }

    List<Integer>[] buildGraph() {
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

    public void removeEdge(String source, String direction) {
        int sourceIndex = nodeIndexByPrefix.get(source);
        int directionIndex = nodeIndexByPrefix.get(direction);
        DebruinNode directionNode = nodes.get(directionIndex);
        directionNode.incoming.remove(sourceIndex);
        DebruinNode sourceNode = nodes.get(sourceIndex);
        sourceNode.directions.remove(direction);
    }


    static class DebruinNode {
        final String prefix;
        final Map<String, Integer> directions = new HashMap<>();
        final Set<Integer> incoming = new HashSet<>();
        final List<String> paths = new ArrayList<>();

        DebruinNode(String prefix) {
            this.prefix = prefix;
        }

        public void addIncoming(int sourceIndex){
            incoming.add(sourceIndex);
        }

        public void addDirection(String direction, int targetIndex) {
            directions.putIfAbsent(direction, targetIndex);
        }

        @Override
        public String toString() {
            return prefix + " -> " + directions + " incoming: " + incoming;
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

class StronglyConnectedComponents {
    public static List<List<Integer>> scc(List<Integer>[] graph) {
        int n = graph.length;
        boolean[] used = new boolean[n];
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (!used[i])
                dfsNoRecurcion(graph, used, order, i);

        List<Integer>[] reverseGraph = new List[n];
        for (int i = 0; i < n; i++)
            reverseGraph[i] = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j : graph[i])
                reverseGraph[j].add(i);

        List<List<Integer>> components = new ArrayList<>();
        Arrays.fill(used, false);
        Collections.reverse(order);

        for (int u : order)
            if (!used[u]) {
                List<Integer> component = new ArrayList<>();
                dfsNoRecurcion(reverseGraph, used, component, u);
                components.add(component);
            }

        return components;
    }


    static void dfs(List<Integer>[] graph, boolean[] used, List<Integer> res, int u) {
        used[u] = true;
        for (int v : graph[u])
            if (!used[v])
                dfs(graph, used, res, v);
        res.add(u);
    }

    static void dfsNoRecurcion(List<Integer>[] graph, boolean[] used, List<Integer> res, int start) {
        LinkedList<Integer> stackOfNodes = new LinkedList<>();
        LinkedList<Iterator<Integer>> stackOfIterators = new LinkedList<>();
        stackOfNodes.push(start);
        stackOfIterators.push(graph[start].iterator());
        while (!stackOfNodes.isEmpty()) {
            int current = stackOfNodes.peek();
            Iterator<Integer> currentIterator = stackOfIterators.peek();
            used[current] = true;
            if (currentIterator.hasNext()) {
                int v = currentIterator.next();
                if (!used[v]) {
                    stackOfNodes.push(v);
                    stackOfIterators.push(graph[v].iterator());
                }
            } else {
                res.add(current);
                stackOfNodes.pop();
                stackOfIterators.pop();
            }
        }
    }
}
