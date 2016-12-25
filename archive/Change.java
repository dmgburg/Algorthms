import java.util.Scanner;

public class Change {
    private static int getChange(int n) {
        int dec = n / 10;
        int fives = n % 10 / 5;
        int ones = n % 10 % 5;
        return dec + fives + ones;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        System.out.println(getChange(n));

    }
}

