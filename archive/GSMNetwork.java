import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class GSMNetwork {
    private final InputReader reader;
    private final OutputWriter writer;

    public GSMNetwork(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) {
        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new GSMNetwork(reader, writer).run();
        writer.writer.flush();
    }

    class Edge {
        int from;
        int to;
    }

    class ConvertGSMNetworkProblemToSat {
        int colors = 3;
        int numVertices;
        Edge[] edges;

        ConvertGSMNetworkProblemToSat(int n, int m) {
            numVertices = n;
            edges = new Edge[m];
            for (int i = 0; i < m; ++i) {
                edges[i] = new Edge();
            }
        }


        void printEquisatisfiableSatFormula() {
            List<String> equasions = new ArrayList<>();
            int[] intEq;
            //all towers must at least 1 color
            for (int i = 0; i < numVertices; i++) {
                intEq = new int[numVertices * colors];
                for (int j = 0; j < colors; j++) {
                    intEq[3 * i + j] = 1;
                }
                equasions.add(getEquasion(intEq));
                for (int j = 0; j < colors; j++) {
                    intEq = new int[numVertices * colors];
                    for (int k = 0; k < colors; k++) {
                        if (k == j) {
                            intEq[colors * i + k] = 0;
                        } else {
                            intEq[colors * i + k] = -1;
                        }
                    }
                    equasions.add(getEquasion(intEq));
                }
            }
            // colors of towers connected by edge must be different
            for (int i = 0; i < edges.length; i++) {
                Edge edge = edges[i];
                for (int j = 0; j < colors; j++) {
                    intEq = new int[numVertices * colors];
                    intEq[(edge.to - 1) * colors + j] = -1;
                    intEq[(edge.from - 1) * colors + j] = -1;
                    equasions.add(getEquasion(intEq));
                }

            }
            writer.printf(equasions.size() + " " + numVertices*colors + "\n");
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

        ConvertGSMNetworkProblemToSat converter = new ConvertGSMNetworkProblemToSat(n, m);
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
