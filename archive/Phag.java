import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Phag {

    private static int wrongTurn = 0;

    int shareRequired = 1;
    boolean debug = false;

    public Phag(int shareRequired, boolean debug) {
        this.debug = debug;
        this.shareRequired = shareRequired;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> parts = new ArrayList<>(1500);
        String line;
        do {
            line = br.readLine();
            if (line != null && line.length() > 0) {
                parts.add(line);
            }
        } while (line != null && line.length() > 0);
        System.out.println(new Phag(12, false).solve(parts));
    }

    public String solve(List<String> parts) {
        Metric metric = new Metric();
        if (debug) {
            metric.start("unify");
        }
        parts = new ArrayList<>(new HashSet<>(parts));
        if (debug) {
            metric.finish("unify");
        }
        if (debug) {
            metric.start("lengthInCommon");
        }
        int[][] lengthInCommon = getLengthInCommonMatrix(parts);
        if (debug) {
            metric.finish("lengthInCommon");
            metric.start("buildGraph");
        }
        PriorityQueue<Path>[] adj = buildGraph(lengthInCommon);
        if (debug) {
            metric.finish("buildGraph");
        }
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < adj.length; i++) {
            if (debug) {
                metric.start("dfs");
            }
            dfs(adj, new HashSet<>(), res, i);
            if (debug) {
                metric.finish("dfs");
            }
            if (res.size() == adj.length) {
                break;
            } else if (res.size() != 0) {
                throw new IllegalStateException();
            }
        }

        return reconstruct(lengthInCommon, parts, res);
    }

    private String reconstruct(int[][] lengthInCommon, List<String> parts, ArrayList<Integer> res) {
        Metric metric = new Metric();
        if (debug) {
            metric.start("reconstruct");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(parts.get(res.get(res.size() - 1)));
        for (int i = res.size() - 1; i > 0; i--) {
            int node = res.get(i);
            int nextNode = res.get(i - 1);
            sb.append(parts.get(nextNode).substring(lengthInCommon[node][nextNode]));
        }
        String sequence = findSequence(sb.toString());
        if (debug) {
            metric.finish("reconstruct");
        }
        return sequence;
    }

    private String findSequence(String text) {
        int[] prefixFunc = calcPrefix(text);
        return text.substring(0, text.length() - prefixFunc[prefixFunc.length - 1]);
    }


    private PriorityQueue<Path>[] buildGraph(int[][] lengthInCommon) {
        PriorityQueue<Path>[] result = new PriorityQueue[lengthInCommon.length];
        for (int i = 0; i < lengthInCommon.length; i++) {
            PriorityQueue<Path> curr = new PriorityQueue<>();
            for (int j = 0; j < lengthInCommon.length; j++) {
                if (lengthInCommon[i][j] > shareRequired) {
                    curr.add(new Path(j, lengthInCommon[i][j]));
                }
                result[i] = curr;
            }
        }

        return result;
    }

    void dfs(PriorityQueue<Path>[] graph, Set<Integer> visited, List<Integer> res, int u) {
        visited.add(u);
        for (Path v : graph[u]) {
            if (!visited.contains(v.to)) {
                dfs(graph, visited, res, v.to);
            }
        }
        if (visited.size() == graph.length) {
            res.add(u);
        } else {
            if (debug) {
                if (wrongTurn % 100 == 0) {
                    System.out.println("Wrong turn " + wrongTurn);
                }
            }
            wrongTurn++;
            visited.remove(u);
        }
    }

    private int[][] getLengthInCommonMatrix(List<String> parts) {
        int[][] lengthInCommon = new int[parts.size()][];
        for (int i = 0; i < parts.size(); i++) {
            int[] currLengthInCommon = new int[parts.size()];

            for (int j = 0; j < parts.size(); j++) {
                if (i == j) {
                    currLengthInCommon[j] = 0;
                } else {
                    currLengthInCommon[j] = findCommon(parts.get(j), parts.get(i));
                }
            }
            lengthInCommon[i] = currLengthInCommon;
        }
        return lengthInCommon;
    }

    private int findCommon(String pattern, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append(pattern).append("$").append(text);
        String concat = sb.toString();
        int[] prefix = calcPrefix(concat);
        return prefix[prefix.length - 1];
    }

    private int[] calcPrefix(String kmp) {
        int[] prefixFunc = new int[kmp.length()];
        int current = 0;
        for (int i = 0; i < kmp.length(); i++) {
            while (current != 0 && kmp.charAt(current) != kmp.charAt(i)) {
                current = prefixFunc[current - 1];
            }
            if (kmp.charAt(current) == kmp.charAt(i) && current != i) {
                current++;
            }
            prefixFunc[i] = current;
        }
        return prefixFunc;

    }

    private static class Path implements Comparable<Path> {
        public int to;
        public int weight;

        public Path(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Path o) {
            // reversed order
            return Integer.compare(o.weight, weight);
        }

        @Override
        public String toString() {
            return "Path{" +
                    "to=" + to +
                    ", weight=" + weight +
                    '}';
        }
    }
}

class Metric {
    private Map<String, Long> startByName = new HashMap<>();

    public void start(String name) {
        startByName.put(name, System.currentTimeMillis());
    }

    public void finish(String name) {
        long finished = System.currentTimeMillis();
        Long startedAt = startByName.remove(name);
        if (startedAt == null) {
            throw new IllegalStateException(name + " not started");
        }
        System.out.println(name + " took " + (finished - startedAt) + " millis");
    }
}
