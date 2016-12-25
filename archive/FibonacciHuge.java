import java.math.BigInteger;
import java.util.Scanner;

public class FibonacciHuge {
    private static BigInteger naive(long number, long modulo) {
        return calc_fib(number).mod(BigInteger.valueOf(modulo));
    }

    public static BigInteger calc_fib(long n) {
        BigInteger i1 = BigInteger.ZERO;
        BigInteger i2 = BigInteger.ONE;
        BigInteger reg = BigInteger.ZERO;
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        for (int j = 2; j <= n; j++) {
            reg = i1.add(i2);
            i1 = i2;
            i2 = reg;
        }
        return reg;
    }

    private static BigInteger getFibonacciHuge(long number, long modulo) {
        long peisano = peisano(modulo);
        return naive(number % peisano, modulo);
    }

    public static long peisano(long m) {
        int iter = 1;
        long c1 = 1;
        long c2 = 1;
        long reg = 2;
        while (c1 != 0 || c2 != 1) {
            iter++;
            reg = (c1 + c2) % m;
            c1 = c2;
            c2 = reg;
        }
        return iter;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long n = scanner.nextLong();
        long m = scanner.nextLong();
        System.out.println(getFibonacciHuge(n, m));
    }

//    @Test1
//    public void test() {
//        Random random = new Random(31);
//        for (int i = 0; ; i++) {
//            int m = random.nextInt(20000) + 2;
//            int n = random.nextInt(100) + 1;
//            long naive = naive(n, m);
//            long fast = getFibonacciHuge(n, m);
//            System.out.println(i+":" + naive + "=" + fast);
//            if (naive - fast != 0) throw new IllegalStateException(n + " " + m + " " + naive + " " + fast);
//        }
//    }
//
//    @Test1
//    public void test2() {
//        for (int m = 2; m <= 144; m++) {
//            System.out.println(getFibonacciHuge(1000, 100));
//            System.out.println(naive(1000, 100));
//        }
//    }
}

