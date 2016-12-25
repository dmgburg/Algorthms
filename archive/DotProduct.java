import java.util.Arrays;
import java.util.Scanner;

public class DotProduct {
    public static long minDotProduct(int[] a, int[] b) {
        long result = 0;
        Arrays.sort(a);
        Arrays.sort(b);
        for (int i = 0; i < a.length; i++) {
            result += (long)a[i] * b[b.length - i - 1];
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }
        int[] b = new int[n];
        for (int i = 0; i < n; i++) {
            b[i] = scanner.nextInt();
        }
        System.out.println(minDotProduct(a, b));
    }

    public static long naive(int[] a, int[] b) {
        if (a.length == 1) {
            return (long)a[0] * b[0];
        }
        long min = Long.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                long product = (long)a[i] * b[j] + naive(removeItem(a, i), removeItem(b, j));
                if (product < min) {
                    min = product;
                }
            }
        }
        return min;
    }

    private static int[] removeItem(int[] items, int i) {
        int[] result = new int[items.length - 1];
        System.arraycopy(items, 0, result, 0, i);
        System.arraycopy(items, i + 1, result, i, result.length - i);
        return result;
    }
}

