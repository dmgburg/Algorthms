import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CoveringSegments {

    public static Integer[] optimalPoints(Segment[] segments) {
        ArrayList<Segment.Point> list = new ArrayList<>(segments.length * 2);
        ArrayList<Segment> started = new ArrayList<>(segments.length);
        ArrayList<Segment> covered = new ArrayList<>(segments.length);
        Set<Integer> points = new HashSet<>(segments.length);

        for (Segment segment : segments){
            list.add(segment.start);
            list.add(segment.end);
        }
        list.stream().sorted().forEach(point->{
            if(point.isEnd()){
                if(!started.isEmpty() && !covered.contains(point.getSegment())) {
                    covered.addAll(started);
                    started.clear();
                    points.add(point.coord);
                }
            } else {
                started.add(point.getSegment());
            }
        });
        return points.toArray(new Integer[points.size()]);
    }

    public static class Segment{
        Point start, end;

        Segment(int start, int end) {
            this.start = new Point(start,Type.START);
            this.end = new Point(end,Type.END);
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "start=" + start.coord +
                    ", end=" + end.coord +
                    '}';
        }

        class Point implements Comparable<Point>{
            int coord;
            Type type;

            Point(int coord, Type type) {
                this.coord = coord;
                this.type = type;
            }

            boolean isEnd(){
                return type == Type.END;
            }

            Segment getSegment(){
                return Segment.this;
            }

            @Override
            public String toString() {
                return "Point{" +
                        "coord=" + coord +
                        ", type=" + type +
                        '}';
            }

            @Override
            public int compareTo(Point o) {
                if(coord == o.coord){
                    if(type == o.type){
                        return 1;
                    } else if (isEnd() && !o.isEnd()){
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return coord - o.coord;
            }
        }

        enum Type{
            START,END
        }
    }

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        Segment[] segments = new Segment[n];
        for (int i = 0; i < n; i++) {
            int start, end;
            start = scanner.nextInt();
            end = scanner.nextInt();
            segments[i] = new Segment(start, end);
        }
        Integer[] points = optimalPoints(segments);
        System.out.println(points.length);
        for (int point : points) {
            System.out.print(point + " ");
        }
    }
}
 
