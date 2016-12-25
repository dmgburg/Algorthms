import java.util.Scanner;

public class FibonacciLastDigit {
    public static long getFibonacciLastDigit(int n) {
        long i1 = 0;
        long i2 = 1;
        long reg = 0;
        if (n <= 1) {
            return n;
        }
        for (int j = 2; j <= n; j++) {
            reg = i1 + i2;
            i1 = i2 % 10;
            i2 = reg % 10;
        }
        return reg % 10;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        long c = getFibonacciLastDigit(n);
        System.out.println(c);
    }

//    @Test1
//    public void test(){
//        long fib;
//        for (int i =0 ; i<= 450; i++){
//            fib = Fib.calc_fib(i);
//            System.out.println(fib);
//            if(getFibonacciLastDigit(i) - fib % 10  != 0) throw new IllegalStateException(""+i);
//        }
//    }
}

