import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MajorityElement {
    static int getMajorityElement(int[] a, int left, int right) {
        if (left == right) {
            return a[left];
        }
        int leftMaj = getMajorityElement(a, left, (left + right) / 2);
        int rightMaj = getMajorityElement(a, (left + right) / 2 + 1, right);
        if (leftMaj == rightMaj) {
            return leftMaj;
        } else if (leftMaj >= 0 && countValues(a, left, right, leftMaj) > (right - left + 1) / 2) {
            return leftMaj;
        } else if (rightMaj >= 0 && countValues(a, left, right, rightMaj) > (right - left + 1) / 2) {
            return rightMaj;
        }
        return -1;
    }

    static long countValues(int[] a, int left, int right, int leftMaj) {
        return Arrays.stream(a, left, right + 1).filter(i -> i == leftMaj).count();
    }

    public static void main(String[] args) {
        FastScanner scanner = new FastScanner(System.in);
        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }
        if (getMajorityElement(a, 0, a.length - 1) != -1) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }

    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(InputStream stream) {
            try {
                br = new BufferedReader(new InputStreamReader(stream));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    static Integer naive(int[] a) {
        IntStream stream = Arrays.stream(a);
        Map.Entry<Integer, Long> maj = Collections.max(stream.boxed()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting())).entrySet(), Map.Entry.comparingByValue());
        if (maj.getValue() > a.length / 2) {
            return maj.getKey();
        } else {
            return -1;
        }
    }
}

