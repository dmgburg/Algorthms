import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

public class EulerianCycleProblem {

    static StringTokenizer st;
    static boolean eof;
    static BufferedReader br;

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

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        int numberOfVertices = nextInt();
        int numberOfEdges = nextInt();
        List<Integer>[] graph = new ArrayList[numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int i = 0; i < numberOfEdges; i++) {
            int from = nextInt();
            int to = nextInt();
            graph[from - 1].add(to - 1);
        }
        if(!hasEulerianCycle(graph)){
            System.out.println(0);
            return;
        } else{
            System.out.println(1);
        }
        List<Integer> cycle = eulerCycleDirected(graph,0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cycle.size() -1; i++){
            Integer node = cycle.get(i);
            sb.append(node + 1).append(" ");
        }
        System.out.println(sb.toString());
    }

    private static boolean hasEulerianCycle(List<Integer>[] graph) {
        int[] incomingEdges = new int[graph.length];
        for (int from = 0; from < graph.length; from++) {
            for (Integer to : graph[from]){
                incomingEdges[to]++;
            }
        }
        for (int i = 0; i < graph.length; i++) {
            if (graph[i].size() != incomingEdges[i]){
                return false;
            }
        }
        return true;
    }

    static int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }

    static String nextToken() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (Exception e) {
                eof = true;
                return null;
            }
        }
        return st.nextToken();
    }
}
