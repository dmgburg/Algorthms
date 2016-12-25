import java.util.*;

public class Dijkstra {
    private static int distance(ArrayList<Integer>[] adj, ArrayList<Integer>[] cost, int s, int t) {
        long[] dist = new long[adj.length];
        Arrays.fill(dist,Integer.MAX_VALUE);
        Point[] prev = new Point[adj.length];
        dist[s] = 0;
        Queue<Point> queue = new PriorityQueue<>();
        for(int i = 0; i < adj.length; i++){
            queue.add(new Point(Integer.MAX_VALUE,i));
        }
        while (!queue.isEmpty()){
            Point current = queue.poll();
            int indCurr = current.point;
            ArrayList<Integer> adjCurr = adj[indCurr];
            ArrayList<Integer> costCurr = cost[indCurr];
            for (int j = 0; j < adjCurr.size(); j++){
                int thatNode = adjCurr.get(j);
                long newDist = dist[indCurr] + costCurr.get(j);
                if(dist[thatNode] > newDist){
                    dist[thatNode] = newDist;
                    prev[thatNode] = current;
                    Point e = new Point(dist[thatNode], thatNode);
                    queue.remove(e);
                    queue.add(e);
                }
            }
        }
        return  dist[t] >= Integer.MAX_VALUE ? -1 : (int)dist[t];
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point1 = (Point) o;

            return point == point1.point;

        }

        @Override
        public int hashCode() {
            return point;
        }

        @Override
        public int compareTo(Point o) {
            return Long.compare(cost,o.getCost());
        }

        @Override
        public String toString() {
            return "Point{" +
                    "cost=" + cost +
                    ", point=" + point +
                    '}';
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
        int x = scanner.nextInt() - 1;
        int y = scanner.nextInt() - 1;
        System.out.println(distance(adj, cost, x, y));
    }
}

