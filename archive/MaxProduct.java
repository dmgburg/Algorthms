import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by Denis on 26.06.2016.
 */
public class MaxProduct {
    static long getMaxPairwiseProduct(long[] numbers) {
        int n = numbers.length;
        int maxIndex1 = 0;
        int maxIndex2 = -1;
        for (int i = 1; i < n; i++) {
            if (numbers[i] >= numbers[maxIndex1]) {
                maxIndex2 = maxIndex1;
                maxIndex1 = i;
            } else if (maxIndex2 == -1) {
                maxIndex2 = i;
            } else if (numbers[i] >= numbers[maxIndex2]) {
                maxIndex2 = i;
            }
        }
        return numbers[maxIndex1] * numbers[maxIndex2];
    }

    public static void main(String[] args) {
        FastScanner scanner = new FastScanner(System.in);
        int n = scanner.nextInt();
        long[] numbers = new long[n];
        for (int i = 0; i < n; i++) {
            numbers[i] = scanner.nextInt();
        }
        System.out.println(getMaxPairwiseProduct(numbers));
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
