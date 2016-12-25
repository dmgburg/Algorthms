import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

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
