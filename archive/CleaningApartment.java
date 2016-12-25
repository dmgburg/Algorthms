import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

public class CleaningApartment {
    private final InputReader reader;
    private final OutputWriter writer;

    public CleaningApartment(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) {
        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new CleaningApartment(reader, writer).run();
        writer.writer.flush();
    }

    class Edge {
        int from;
        int to;
    }

    class ConvertHampathToSat {
        int numVertices;
        Edge[] edges;

        ConvertHampathToSat(int n, int m) {
            numVertices = n;
            edges = new Edge[m];
            for (int i = 0; i < m; ++i) {
                edges[i] = new Edge();
            }
        }

        void printEquisatisfiableSatFormula() {
            List<String> equasions = new ArrayList<>();
            int[] intEq;
            // Build adj
            Set<Integer>[] adj = new Set[numVertices];
            for (int i = 0; i < numVertices; i++) {
                adj[i] = new HashSet<>();
            }
            for (int i = 0; i < edges.length; i++) {
                Edge edge = edges[i];
                adj[edge.from - 1].add(edge.to - 1);
                adj[edge.to - 1].add(edge.from - 1);
            }

            for (int from = 0; from < numVertices; from++) {
                for (int to = 0; to < numVertices; to++) {
                    if (from == to) continue;
                    if (!adj[from].contains(to)) {
                        for (int stepNumber = 0; stepNumber < numVertices - 1; stepNumber++) {
                            intEq = new int[numVertices * numVertices];
                            intEq[stepNumber * numVertices + from] = -1;
                            intEq[(stepNumber + 1) * numVertices + to] = -1;
                            equasions.add(getEquasion(intEq));
                        }
                    }
                }

            }

            //one step must have exactly one node
            for (int i = 0; i < numVertices; i++) {
                intEq = new int[numVertices * numVertices];
                for (int j = 0; j < numVertices; j++) {
                    intEq[i * numVertices + j] = 1;
                }
                equasions.add(getEquasion(intEq));
                for (int j = 0; j < numVertices; j++) {
                    for (int k = j; k < numVertices; k++) {
                        if (j == k) continue;
                        intEq = new int[numVertices * numVertices];
                        intEq[i * numVertices + j] = -1;
                        intEq[i * numVertices + k] = -1;
                        equasions.add(getEquasion(intEq));
                    }
                }
            }
            //one Node must be used exactly once
            for (int i = 0; i < numVertices; i++) {
                intEq = new int[numVertices * numVertices];
                for (int j = 0; j < numVertices; j++) {
                    intEq[j * numVertices + i] = 1;
                }
                equasions.add(getEquasion(intEq));
                for (int j = 0; j < numVertices; j++) {
                    for (int k = 1; k < numVertices - j; k++) {
                        intEq = new int[numVertices * numVertices];
                        intEq[j * numVertices + i] = -1;
                        intEq[j * numVertices + i + k * numVertices] = -1;
                        equasions.add(getEquasion(intEq));
                    }
                }
            }

            //edges must be connected
            writer.printf(equasions.size() + " " + numVertices*numVertices + "\n");
            for (int i = 0; i < equasions.size(); i++) {
                writer.printf(equasions.get(i) + "\n");
            }
        }

        private String getEquasion(int[] intEq) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < intEq.length; i++) {
                int nodeNum = i + 1;
                if (intEq[i] == -1) {
                    sb.append(-nodeNum).append(" ");
                } else if (intEq[i] == 1) {
                    sb.append(nodeNum).append(" ");
                } else if (intEq[i] == 0) {
                } else {
                    throw new IllegalArgumentException();
                }
            }
            sb.append(0);
            return sb.toString();
        }
    }

    public void run() {
        int n = reader.nextInt();
        int m = reader.nextInt();

        ConvertHampathToSat converter = new ConvertHampathToSat(n, m);
        for (int i = 0; i < m; ++i) {
            converter.edges[i].from = reader.nextInt();
            converter.edges[i].to = reader.nextInt();
        }

        converter.printEquisatisfiableSatFormula();
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
