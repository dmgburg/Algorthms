import java.util.Random;
import java.util.Scanner;

public class LCM {
    private static long naive(int a, int b) {
        long result = a * b;
        long current = result;
        while (current >= a && current >= b) {
            if (current % a == 0 && current % b == 0 && current < result) {
                result = current;
            }
            current--;
        }
        return result;
    }

    private static long lcm(int a, int b) {
        return (long) a * b/gcd(a,b);
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

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();

        System.out.println(lcm(a, b));
    }

//    @Test1
    public void test() {
        Random random = new Random();
        for (;;) {
            int a = random.nextInt(20000);
            int b = random.nextInt(20000);
            System.out.println(a + " " + b);
            long naive = naive(a,b);
            long fast = lcm(a, b);
            long gcd = gcd(a, b);
            System.out.println(naive + " " + fast + " " + gcd);
            if (naive - fast != 0) throw new IllegalStateException();
        }
    }
}
