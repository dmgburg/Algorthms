import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class NegativeCycle {
    private static int negativeCycle(ArrayList<Integer>[] adj, ArrayList<Integer>[] cost) {
        long[] dist = new long[adj.length];
        Arrays.fill(dist,Integer.MAX_VALUE);
        Point[] prev = new Point[adj.length];
        dist[0] = 0;
        for (int i = 0; i < adj.length; i++) {
           improve(dist,prev,adj, cost);
        }
        return  improve(dist,prev,adj,cost) ? 1 : 0;
    }

    private static boolean improve(long[] dist, Point[] prev, ArrayList<Integer>[] adj, ArrayList<Integer>[] cost) {
        boolean improved = false;
        for (int currNode = 0; currNode < adj.length; currNode++){
            List<Integer> currAdj = adj[currNode];
            List<Integer> currCost = cost[currNode];
            for (int j = 0; j < currAdj.size(); j++) {
                int thatNode = currAdj.get(j);
                if (dist[thatNode] > dist[currNode] + currCost.get(j)){
                    dist[thatNode] = dist[currNode] + currCost.get(j);
                    improved = true;
                }
            }
        }
        return improved;
    }

    static class Point implements Comparable<Point>{
        long cost;
        int point;

        public Point(long cost, int point) {
            this.cost = cost;
            this.point = point;
        }

        public long getCost() {
            return cost;
        }

        public int getPoint() {
            return point;
        }


        @Override
        public int compareTo(Point o) {
            return Long.compare(cost, o.getCost());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        ArrayList<Integer>[] adj = (ArrayList<Integer>[])new ArrayList[n];
        ArrayList<Integer>[] cost = (ArrayList<Integer>[])new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<Integer>();
            cost[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < m; i++) {
            int x, y, w;
            x = scanner.nextInt();
            y = scanner.nextInt();
            w = scanner.nextInt();
            adj[x - 1].add(y - 1);
            cost[x - 1].add(w);
        }
        System.out.println(negativeCycle(adj, cost));
    }
}

