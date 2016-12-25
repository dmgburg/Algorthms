import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class CircuitDesign {
    private final InputReader reader;
    private final OutputWriter writer;

    public CircuitDesign(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) {
        new Thread(null, new Runnable() {
            public void run() {
                InputReader reader = new InputReader(System.in);
                OutputWriter writer = new OutputWriter(System.out);
                new CircuitDesign(reader, writer).run();
                writer.writer.flush();
            }
        }, "1", 1 << 26).start();
    }

    static class Clause {
        int firstVar;
        int secondVar;

        public Clause() {
        }

        public Clause(int firstVar, int secondVar) {
            this.firstVar = firstVar;
            this.secondVar = secondVar;
        }

        @Override
        public String toString() {
            return "{" + firstVar +
                    ", " + secondVar +
                    '}';
        }
    }

    static class TwoSatisfiability {
        int numVars;
        Clause[] clauses;

        TwoSatisfiability(int n, int m) {
            numVars = n;
            clauses = new Clause[m];
            for (int i = 0; i < m; ++i) {
                clauses[i] = new Clause();
            }
        }

        boolean isSatisfiable(int[] result) {
            List<Integer>[] adj = new List[numVars * 2];
            for (int i = 0; i < numVars * 2; i++) {
                adj[i] = new ArrayList<>();
            }

            for (Clause clause : clauses) {
                adj[getIndex(-clause.firstVar)].add(getIndex(clause.secondVar));
                adj[getIndex(-clause.secondVar)].add(getIndex(clause.firstVar));
            }

            List<List<Integer>> sccList = scc(adj);
            int[] componentNumbers = new int[numVars * 2];
            Arrays.fill(componentNumbers, -1);
            for (int currentComponentNumber = 0; currentComponentNumber < sccList.size(); currentComponentNumber++) {
                List<Integer> component = sccList.get(currentComponentNumber);
                for (Integer node : component) {
                    if (componentNumbers[opposite(node)] == currentComponentNumber) {
                        return false;
                    }
                    componentNumbers[node] = currentComponentNumber;
                }
            }

            List<Integer>[] dagAdj = dagAdj(sccList, componentNumbers, adj);

            List<Integer> dagToposortReversed = topologicalSort(dagAdj);

            for (Integer sccIndex : dagToposortReversed) {
                List<Integer> scc = sccList.get(sccIndex);
                for (Integer node : scc) {
                    if (result[getVarNumber(node)] != 0) {
                        continue;
                    }
                    if (isNegation(node)) {
                        result[getVarNumber(node)] = -1;
                    } else {
                        result[getVarNumber(node)] = 1;
                    }
                }
            }

            return true;
        }

        private boolean isNegation(Integer node) {
            return node % 2 == 1;
        }

        public List<Integer> topologicalSort(List<Integer>[] graph) {
            int n = graph.length;
            boolean[] used = new boolean[n];
            List<Integer> res = new ArrayList<>();
            for (int i = 0; i < n; i++)
                if (!used[i])
                    dfs(graph, used, res, i);
            return res;
        }

        private List<Integer>[] dagAdj(List<List<Integer>> scc, int[] componentNumbers, List<Integer>[] adj) {
            List<Integer>[] result = new List[scc.size()];
            for (int i = 0; i < scc.size(); i++) {
                result[i] = new ArrayList<>();
            }
            for (int i = 0; i < scc.size(); i++) {
                List<Integer> component = scc.get(i);
                for (Integer node : component) {
                    for (Integer targetNode : adj[node]) {
                        result[i].add(componentNumbers[targetNode]);
                    }
                }
            }
            return result;
        }

        private Integer opposite(Integer node) {
            if (isNegation(node)) {
                return node - 1;
            } else {
                return node + 1;
            }
        }

        int getVarNumber(int node) {
            return node / 2;
        }

        int getIndex(int var) {
            if (var > 0) {
                return 2 * var - 2;
            } else {
                return 2 * Math.abs(var) - 1;
            }
        }

        @Override
        public String toString() {
            return "TwoSatisfiability{" +
                    "clauses=" + Arrays.toString(clauses) +
                    '}';
        }
    }

    public static List<List<Integer>> scc(List<Integer>[] graph) {
        int n = graph.length;
        boolean[] used = new boolean[n];
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (!used[i])
                dfs(graph, used, order, i);

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
                dfs(reverseGraph, used, component, u);
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

    public void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();

        TwoSatisfiability twoSat = new TwoSatisfiability(n, m);
        for (int i = 0; i < m; ++i) {
            twoSat.clauses[i].firstVar = reader.nextInt();
            twoSat.clauses[i].secondVar = reader.nextInt();
        }

        int result[] = new int[n];
        if (twoSat.isSatisfiable(result)) {
            writer.printf("SATISFIABLE\n");
            for (int i = 1; i <= n; ++i) {
                if (result[i - 1] == 1) {
                    writer.printf("%d", i);
                } else {
                    writer.printf("%d", -i);
                }
                if (i < n) {
                    writer.printf(" ");
                } else {
                    writer.printf("\n");
                }
            }
        } else {
            writer.printf("UNSATISFIABLE\n");
        }
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }

    static class OutputWriter {
        public PrintWriter writer;

        OutputWriter(OutputStream stream) {
            writer = new PrintWriter(stream);
        }

        public void printf(String format, Object... args) {
            writer.print(String.format(Locale.ENGLISH, format, args));
        }
    }
}
