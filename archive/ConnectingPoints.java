import java.awt.font.NumericShaper;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ConnectingPoints {

    private static class Connection implements Comparable<Connection> {
        int point1;
        int point2;
        double distance;

        public Connection(int point1, int point2, double distance) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "point1=" + point1 +
                    ", point2=" + point2 +
                    ", distance=" + distance +
                    '}';
        }

        @Override
        public int compareTo(Connection o) {
            return Double.compare(distance, o.distance);
        }
    }

    private static double minimumDistance(int[] x, int[] y, double[][] dist) {
        double result = 0;
        PriorityQueue<Connection> connections = new PriorityQueue<>();
        Set<Integer> connected = new HashSet<>();
        addPoints(0, x.length, dist[0], connections);
        connected.add(0);
        while (connected.size() < x.length) {
            Connection connection = connections.poll();
            if (!connected.contains(connection.point1) || !connected.contains(connection.point2)) {
                if (!connected.contains(connection.point1)) {
                    connected.add(connection.point1);
                    addPoints(connection.point1,x.length,dist[connection.point1],connections);
                }
                if (!connected.contains(connection.point2)) {
                    connected.add(connection.point2);
                    addPoints(connection.point2,x.length,dist[connection.point2],connections);
                }
                result += connection.distance;
            }
        }
        //write your code here
        return result;
    }

    private static void addPoints(int i, int length, double[] doubles, PriorityQueue<Connection> connections) {
        IntStream.range(0, length).filter(it -> it != i).mapToObj(it -> new Connection(i, it, doubles[it])).forEach(connections::add);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = scanner.nextInt();
            y[i] = scanner.nextInt();
        }
        double[][] dist = new double[x.length][y.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = i; j < y.length; j++) {
                if (dist[i][j] != 0) throw new IllegalStateException();
                double dist1 = dist(x[i], y[i], x[j], y[j]);
                dist[i][j] = dist1;
                dist[j][i] = dist1;
            }
        }
        System.out.println(new DecimalFormat("#0.0000000000").format(minimumDistance(x, y, dist)));
    }

    private static double dist(int x1, int y1, int x2, int y2) {
        return sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2));
    }
}

