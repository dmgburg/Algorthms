import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class InverseBWT {
    private enum Char{
        $(0),A(1),C(2),G(3),T(4);

        int number;

        Char (int number){
            this.number = number;
        }

        public static Char valueOf(char c){
            switch (c){
                case 'A': return A;
                case 'C': return C;
                case 'G': return G;
                case 'T': return T;
                case '$': return $;
                default: throw new IllegalArgumentException("Unknown char: " + c);
            }
        }
    }

    class FastScanner {
        StringTokenizer tok = new StringTokenizer("");
        BufferedReader in;

        FastScanner() {
            in = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() throws IOException {
            while (!tok.hasMoreElements())
                tok = new StringTokenizer(in.readLine());
            return tok.nextToken();
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }

    String inverseBWT(String bwtS) {
        char [] result = new char[bwtS.length()];
        char[] bwt = bwtS.toCharArray();
        int[][] charNumbers = calcCharNumbers(bwt);
        int[] count = charNumbers[charNumbers.length-1];
        char[] sorted = bwtS.toCharArray();
        Arrays.sort(sorted);

        int currentSorted = 0;
        for (int i = 0; i < bwtS.length() -1; i++){
            char found = bwt[currentSorted];
            result[bwt.length - i - 2] = found;
            int foundCharCount = charNumbers[currentSorted][getNumber(found)];
            currentSorted = getStartingIndex(found, count) + foundCharCount - 1;
        }

        result[bwt.length - 1] = '$';
        return new String(result);
    }

    private int getNumber(char found) {
        return Char.valueOf(found).number;
    }

    int getStartingIndex(char c, int[] counts){
        int result = 0;
        for (int i = 0; i < getNumber(c); i++){
            result += counts[i];
        }
        return result;
    }

    private int[][] calcCharNumbers(char[] bwt) {
        int[][] charNumbers = new int[bwt.length][5];
        charNumbers[0][Char.A.number] = 0;
        charNumbers[0][Char.C.number] = 0;
        charNumbers[0][Char.G.number] = 0;
        charNumbers[0][Char.T.number] = 0;
        charNumbers[0][Char.$.number] = 0;
        charNumbers[0][getNumber(bwt[0])] = 1;
        for(int i = 1; i< bwt.length; i++){
            charNumbers[i][Char.A.number] = charNumbers[i-1][Char.A.number];
            charNumbers[i][Char.C.number] = charNumbers[i-1][Char.C.number];
            charNumbers[i][Char.G.number] = charNumbers[i-1][Char.G.number];
            charNumbers[i][Char.T.number] = charNumbers[i-1][Char.T.number];
            charNumbers[i][Char.$.number] = charNumbers[i-1][Char.$.number];
            charNumbers[i][getNumber(bwt[i])] = charNumbers[i][getNumber(bwt[i])] + 1;
        }
        return charNumbers;
    }

    static public void main(String[] args) throws IOException {
        new InverseBWT().run();
    }

    public void run() throws IOException {
        FastScanner scanner = new FastScanner();
        String bwt = scanner.next();
        System.out.println(inverseBWT(bwt));
    }
}
