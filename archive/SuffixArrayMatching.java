import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SuffixArrayMatching {
    class fastscanner {
        StringTokenizer tok = new StringTokenizer("");
        BufferedReader in;

        fastscanner() {
            in = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() throws IOException {
            while (!tok.hasMoreElements())
                tok = new StringTokenizer(in.readLine());
            return tok.nextToken();
        }

        int nextint() throws IOException {
            return Integer.parseInt(next());
        }
    }

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

    private int getOrdinal(String text, int i) {
        return Char.valueOf(text.charAt(i)).ordinal();
    }

    public int[] computeSuffixArray(String text) {
        OrderClass orderClass = getOrderClassInit(text);
        int l = 1;
        while (l < text.length()) {
            orderClass = doubleSort(text, l, orderClass);
            l *= 2;
        }
        return orderClass.order;
    }

    private OrderClass doubleSort(String text, int l, OrderClass orderClass) {
        int[] classEq = orderClass.classEq;
        int[] order = orderClass.order;
        int[] count = new int[text.length()];
        int[] newOrder = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            count[classEq[i]] = count[classEq[i]] + 1;
        }
        for (int i = 1; i < text.length(); i++) {
            count[i] = count[i] + count[i - 1];
        }
        for (int i = text.length() - 1; i >= 0; i--) {
            int start = (order[i] - l + text.length()) % text.length();
            int cl = classEq[start];
            count[cl] = count[cl] - 1;
            newOrder[count[cl]] = start;
        }
        int[] newClass = new int[text.length()];
        newClass[newOrder[0]] = 0;
        for (int i = 1; i < text.length(); i++) {
            int cur = newOrder[i];
            int prev = newOrder[i - 1];
            int mid = cur + l;
            int midPrev = (prev + l) % text.length();
            if (classEq[cur] != classEq[prev] || classEq[mid] != classEq[midPrev]) {
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
        return new OrderClass(order, eqClass);
    }

    public List<Integer> findOccurrences(String pattern, String text, int[] suffixArray) {
        List<Integer> result = new ArrayList<>();

        int start = 0;
        int end = text.length() - 1;
        for (int j = 0; j < pattern.length(); j++) {
            for (int i = start; i < text.length(); i++) {
                if (pattern.charAt(j) == text.charAt(suffixArray[i] + j)) {
                    start = i;
                    break;
                } else if(i == end){
                    return new ArrayList<>();
                }
            }
            for (int i = end; i >= 0; i--) {
                if (pattern.charAt(j) == text.charAt(suffixArray[i] + j)) {
                    end = i;
                    break;
                } else if(i == start){
                    return new ArrayList<>();
                }
            }
        }
        for(int i = start; i <= end; i++){
            result.add(suffixArray[i]);
        }

        return result;
    }

    static public void main(String[] args) throws IOException {
        new SuffixArrayMatching().run();
    }

    public void print(boolean[] x) {
        for (int i = 0; i < x.length; ++i) {
            if (x[i]) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    public void run() throws IOException {
        fastscanner scanner = new fastscanner();
        String text = scanner.next() + "$";
        int[] suffixArray = computeSuffixArray(text);
        int patternCount = scanner.nextint();
        boolean[] occurs = new boolean[text.length()];
        for (int patternIndex = 0; patternIndex < patternCount; ++patternIndex) {
            String pattern = scanner.next();
            List<Integer> occurrences = findOccurrences(pattern, text, suffixArray);
            for (int x : occurrences) {
                occurs[x] = true;
            }
        }
        print(occurs);
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
