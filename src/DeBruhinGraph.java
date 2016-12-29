import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 27.12.2016.
 */
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
            String targetPrefix = read.substring(1);
            Integer targetNodeIndex = nodeIndexByPrefix.get(targetPrefix);
            if (targetNodeIndex == null) {
                targetNodeIndex = nodes.size();
                nodes.add(new DebruinNode(targetPrefix));
                nodeIndexByPrefix.put(targetPrefix, targetNodeIndex);
            }
            nodes.get(nodeIndex).addDirection(targetPrefix, targetNodeIndex);
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


    static class DebruinNode {
        final String prefix;
        final Map<String, Integer> directions = new HashMap<>();
        final List<String> paths = new ArrayList<>();

        DebruinNode(String prefix) {
            this.prefix = prefix;
        }

        public void addDirection(String direction, int targetIndex) {
            directions.putIfAbsent(direction, targetIndex);
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