import java.io.*;
import java.util.*;

public class Sorting {
    private static Random random = new Random();

    static int[] partition3(int[] a, int l, int r) {
        int x = a[l];
        int lessEnd = l;
        int eqEnd = l;

        for (int i = l + 1; i <= r; i++) {
            if (a[i] < x) {
                eqEnd++;
                lessEnd++;
                if(eqEnd != i) {
                    swap(a, eqEnd, lessEnd);
                }
                swap(a, lessEnd, i);
            } else if (a[i] == x){
                eqEnd++;
                swap(a, eqEnd, i);
            }
        }
        swap(a, lessEnd, l);
        return new int[]{lessEnd, eqEnd};
    }

    private static void swap(int[] a, int startMore, int i) {
        int t = a[i];
        a[i] = a[startMore];
        a[startMore] = t;
    }

    private static int partition2(int[] a, int l, int r) {
        int x = a[l];
        int j = l;
        for (int i = l + 1; i <= r; i++) {
            if (a[i] <= x) {
                j++;
                swap(a, j, i);
            }
        }
        swap(a, j, l);
        return j;
    }

    static void randomizedQuickSort3(int[] a, int l, int r) {
        if (l >= r) {
            return;
        }
        int k = random.nextInt(r - l + 1) + l;
        swap(a, k, l);
        //use partition3
        int[] m = partition3(a, l, r);
        randomizedQuickSort3(a, l, m[0] - 1);
        randomizedQuickSort3(a, m[1] + 1, r);
    }

    static void randomizedQuickSort2(int[] a, int l, int r) {
        if (l >= r) {
            return;
        }
        int k = random.nextInt(r - l + 1) + l;
        swap(a, k, l);
        //use partition3
        int m = partition2(a, l, r);
        randomizedQuickSort2(a, l, m - 1);
        randomizedQuickSort2(a, m + 1, r);
    }

    public static void main(String[] args) {
        FastScanner scanner = new FastScanner(System.in);
        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }
        randomizedQuickSort3(a, 0, n - 1);
        for (int i = 0; i < n; i++) {
            System.out.print(a[i] + " ");
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
}

