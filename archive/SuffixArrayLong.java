import java.util.*;
import java.io.*;

public class SuffixArrayLong {
    private enum Char {
        $, A, C, G, T;


        public static Char valueOf(char c) {
            switch (c) {
                case 'A':
                    return A;
                case 'C':
                    return C;
                case 'G':
                    return G;
                case 'T':
                    return T;
                case '$':
                    return $;
                default:
                    throw new IllegalArgumentException("Unknown char: " + c);
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

    public class Suffix implements Comparable {
        String suffix;
        int start;

        Suffix(String suffix, int start) {
            this.suffix = suffix;
            this.start = start;
        }

        @Override
        public int compareTo(Object o) {
            Suffix other = (Suffix) o;
            return suffix.compareTo(other.suffix);
        }
    }

    // Build suffix array of the string text and
    // return an int[] result of the same length as the text
    // such that the value result[i] is the index (0-based)
    // in text where the i-th lexicographically smallest
    // suffix of text starts.
    public int[] computeSuffixArray(String text) {
        int[] result = new int[text.length()];
        OrderClass orderClass = getOrderClassInit(text);
        int l = 1;
        while (l < text.length()){
            orderClass = doubleSort(text,l,orderClass);
            l *= 2;
        }
        return orderClass.order;
    }

    private OrderClass doubleSort(String text, int l, OrderClass orderClass) {
        int[] classEq = orderClass.classEq;
        int[] order = orderClass.order;
        int[] count = new int[text.length()];
        int[] newOrder =  new int[text.length()];
        for(int i = 0; i < text.length(); i++){
            count[classEq[i]] = count[classEq[i]] + 1;
        }
        for(int i = 1; i < text.length(); i++){
            count[i] = count[i] + count[i-1];
        }
        for(int i = text.length() - 1; i >= 0; i--){
            int start = (order[i] - l + text.length()) % text.length();
            int cl = classEq[start];
            count[cl] = count[cl] - 1;
            newOrder[count[cl]] = start;
        }
        int[] newClass = new int[text.length()];
        newClass[newOrder[0]] = 0;
        for(int i = 1; i < text.length(); i ++){
            int cur = newOrder[i];
            int prev  = newOrder[i-1];
            int mid = cur + l;
            int midPrev = (prev + l) % text.length();
            if(classEq[cur] != classEq[prev] || classEq[mid] != classEq[midPrev]){
                newClass[cur] = newClass[prev] + 1;
            } else {
                newClass[cur] = newClass[prev];
            }
        }
        return new OrderClass(newOrder, newClass);
    }

    private OrderClass getOrderClassInit(String text) {
        int[] order = new int[text.length()];
        int alphabetLength = 5;
        int[] count = new int[alphabetLength];
        for (int i = 0; i < text.length(); i++) {
            int charOrd = getOrdinal(text, i);
            count[charOrd] = count[charOrd] + 1;
        }
        for (int i = 1; i < alphabetLength; i++) {
            count[i] = count[i] + count[i - 1];
        }
        for (int i = text.length() - 1; i >= 0; i--) {
            int c = getOrdinal(text, i);
            count[c] = count[c] - 1;
            order[count[c]] = i;
        }
        int[] eqClass = new int[text.length()];
        eqClass[order[0]] = 0;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(order[i]) != text.charAt(order[i - 1])) {
                eqClass[order[i]] = eqClass[order[i - 1]] + 1;
            } else {
                eqClass[order[i]] = eqClass[order[i - 1]];
            }
        }
        return new OrderClass(order,eqClass);
    }

    private int getOrdinal(String text, int i) {
        return Char.valueOf(text.charAt(i)).ordinal();
    }

    static public void main(String[] args) throws IOException {
        new SuffixArrayLong().run();
    }

    public void print(int[] x) {
        for (int a : x) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public void run() throws IOException {
        FastScanner scanner = new FastScanner();
        String text = scanner.next();
        int[] suffix_array = computeSuffixArray(text);
        print(suffix_array);
    }

    private class OrderClass {
        final int[] order;
        final int[] classEq;

        public OrderClass(int[] order, int[] eqClass) {
            this.order = order;
            this.classEq = eqClass;
        }
    }
}
