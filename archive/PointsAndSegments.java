import java.util.Arrays;
import java.util.Scanner;

public class PointsAndSegments {

    static int[] fastCountSegments(int[] starts, int[] ends, int[] points) {
        int[] cnt = new int[points.length];
        Segment[] segmentsStart = new Segment[starts.length];

        for (int i = 0; i< starts.length; i++){
            segmentsStart[i] = new Segment(starts[i],ends[i],i);
        }
        Segment[] segmentsEnd = Arrays.copyOf(segmentsStart,segmentsStart.length);
        Arrays.sort(segmentsStart, (o1, o2) -> o1.start - o2.start);
        Arrays.sort(segmentsEnd, (o1, o2) -> o1.end - o2.end);
        for (int i = 0; i < segmentsStart.length; i++){
            segmentsStart[i].startsPosition = i;
            segmentsEnd[i].endsPosition = i;
        }
        Segment bluff = new Segment(0,0,-1);
        for (int i = 0; i < points.length ; i++){
            bluff.start = points[i];
            bluff.end = points[i];
            int positionStart = Arrays.binarySearch(segmentsStart, bluff,(o1, o2) -> o1.start - o2.start);
            int positionEnd = Arrays.binarySearch(segmentsEnd, bluff,(o1, o2) -> o1.end - o2.end);
            positionStart = purge(segmentsStart,positionStart,true);
            positionEnd = purge(segmentsEnd,positionEnd, false);
            if(positionStart < segmentsEnd.length - positionEnd){
                for(int j = 0; j <= positionStart; j++){
                    if(segmentsStart[j].endsPosition >= positionEnd) {
                        cnt[i]++;
                    }
                }
            } else {
                for(int j = positionEnd; j < segmentsEnd.length; j++){
                    if(segmentsEnd[j].startsPosition <= positionStart) {
                        cnt[i]++;
                    }
                }
            }
        }
        return cnt;
    }

    private static int purge(Segment[] sortedArray, int position, boolean left) {
        int result;
        if(position < 0){
            if(left) {
                result = -position - 2;
            } else {
                result = - position - 1;
            }
        } else {
            result = position;
        }
        if(result < 0 || result >= sortedArray.length){
            return result;
        }
        if(left) {
            while (result + 1 < sortedArray.length && sortedArray[result + 1].start == sortedArray[result].start) {
                result++;
            }
            return result;
        } else {
            while (result - 1 >=0 && sortedArray[result - 1].end == sortedArray[result].end) {
                result--;
            }
            return result;
        }
    }

    static class Segment{
        int start;
        int end;
        int position;
        int startsPosition;
        int endsPosition;

        public Segment(int start, int end, int position) {
            this.start = start;
            this.end = end;
            this.position = position;
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    static int[] naiveCountSegments(int[] starts, int[] ends, int[] points) {
        int[] cnt = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < starts.length; j++) {
                if (starts[j] <= points[i] && points[i] <= ends[j]) {
                    cnt[i]++;
                }
            }
        }
        return cnt;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n, m;
        n = scanner.nextInt();
        m = scanner.nextInt();
        int[] starts = new int[n];
        int[] ends = new int[n];
        int[] points = new int[m];
        for (int i = 0; i < n; i++) {
            starts[i] = scanner.nextInt();
            ends[i] = scanner.nextInt();
        }
        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }
        //use fastCountSegments
        int[] cnt = fastCountSegments(starts, ends, points);
        for (int x : cnt) {
            System.out.print(x + " ");
        }
    }
}

