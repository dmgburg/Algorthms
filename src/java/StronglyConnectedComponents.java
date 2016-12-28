import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Denis on 25.12.2016.
 */
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
            if (currentIterator.hasNext()){
                int v = currentIterator.next();
                if (!used[v]){
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
