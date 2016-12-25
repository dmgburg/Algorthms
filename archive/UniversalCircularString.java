import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class UniversalCircularString {
    private static final Metric metric = new Metric();

    public static void main(String[] args) throws IOException {
        Metric.debug = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int k = Integer.valueOf(br.readLine());
        int maxInt = (int) Math.round(Math.pow(2, k)) - 1;
        List<Integer> ints = new ArrayList<>(maxInt);
        for (int i = 0; i <= maxInt; i++) {
            ints.add(i);
        }
        System.out.println(getCircullarString(ints, k));
    }

    private static String getCircullarString(List<Integer> ints, int bitLength) {
        List<Integer>[] graph = buildGraph(ints, bitLength);
        List<Integer> eulerian = EulerianCicle.eulerCycleDirected(graph, 0);
        StringBuilder sb = new StringBuilder();
        for (Integer node : eulerian) {
            String string = Integer.toBinaryString(ints.get(node));
            sb.append(string.charAt(string.length() - 1));
        }
        return sb.toString();
    }

    private static List<Integer>[] buildGraph(List<Integer> ints, int bitLength) {
        List<Integer>[] graph = new List[ints.size()];
        metric.start("graphConstruction");
        for (int i = 0; i < ints.size(); i++) {
            List<Integer> connections = new ArrayList<>();
            for (int j = 0; j < ints.size(); j++) {
                int unsetFirstBit = i  & ~(1 << (bitLength-1));
                int lastBitSet = (j & 1) == 0 ? unsetFirstBit << 1 : (unsetFirstBit << 1) | 1;
                if (i != j && lastBitSet == j) {
                    connections.add(j);
                }
            }
            graph[i] = connections;
        }
        metric.finish("graphConstruction");
        return graph;
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

class Metric {
    public static boolean debug = false;
    private Map<String, Long> startByName = new HashMap<>();

    public void start(String name) {
        if (debug) {
            startByName.put(name, System.currentTimeMillis());
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
