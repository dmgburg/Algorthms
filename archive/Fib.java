//import org.junit.Test;

import java.util.Scanner;

public class Fib {
    public static long naive(int n) {
        if (n <= 1)
            return n;

        return naive(n - 1) + naive(n - 2);
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();

        System.out.println(calc_fib(n));
    }

    public static long calc_fib(int n) {
        long i1 = 0;
        long i2 = 1;
        long reg = 0;
        if (n <= 1) {
            return n;
        }
        for (int j = 2; j <= n; j++) {
            reg = i1 + i2;
            i1 = i2;
            i2 = reg;
        }
        return reg;
    }

//    @Test1
    public void test(){
        for (int i =0 ; i<= 45; i++){
            if(Fib.naive(i)-Fib.calc_fib(i) != 0) throw new IllegalStateException(""+i);
            System.out.println(Fib.calc_fib(i));
        }
    }
}

