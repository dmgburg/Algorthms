import java.util.Random;
import java.util.Scanner;

public class GCD {
    private static int naive(int a, int b) {
        int current_gcd = 1;
        for (int d = 2; d <= a && d <= b; ++d) {
            if (a % d == 0 && b % d == 0) {
                if (d > current_gcd) {
                    current_gcd = d;
                }
            }
        }

        return current_gcd;
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();

        System.out.println(gcd(a, b));
    }

    public static long gcd(int a, int b) {
        if (b > a) {
            return gcd(b, a);
        }
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }


//    @Test1
    public void test() {
        Random random = new Random();
        for (int i = 0; i <= 4500000; i++) {
            int a = random.nextInt(2000000);
            int b = random.nextInt(2000000);
            System.out.println(a + " " + b);
            if (naive(a,b) - gcd(a, b) != 0) throw new IllegalStateException();
        }
    }
}
