import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static javax.swing.UIManager.put;

public class BWMatching {
    private enum Char {
        $(0), A(1), C(2), G(3), T(4);

        int number;

        Char(int number) {
            this.number = number;
        }

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

    // Preprocess the Burrows-Wheeler Transform bwt of some text
    // and compute as a result:
    //   * starts - for each character C in bwt, starts[C] is the first position
    //       of this character in the sorted array of
    //       all characters of the text.
    //   * occ_count_before - for each character C in bwt and each position P in bwt,
    //       occ_count_before[C][P] is the number of occurrences of character C in bwt
    //       from position 0 to position P inclusive.
    private static void PreprocessBWT(String bwt, Map<Character, Integer> starts, Map<Character, int[]> occ_counts_before) {
        int[][] calcCharNumbers = calcCharNumbers(bwt);
        occ_counts_before.put('A', calcCharNumbers[getNumber('A')]);
        occ_counts_before.put('C', calcCharNumbers[getNumber('C')]);
        occ_counts_before.put('G', calcCharNumbers[getNumber('G')]);
        occ_counts_before.put('T', calcCharNumbers[getNumber('T')]);
        occ_counts_before.put('$', calcCharNumbers[getNumber('$')]);
        getStartingIndex(occ_counts_before, starts);
    }

    static Map<Character, Integer> getStartingIndex(Map<Character, int[]> occ_counts_before, Map<Character, Integer> starts) {
        int prev = 0;
        starts.put('$', prev);
        prev = getLast(occ_counts_before.get('$'));
        starts.put('A', prev);
        prev += getLast(occ_counts_before.get('A'));
        starts.put('C', prev);
        prev += getLast(occ_counts_before.get('C'));
        starts.put('G', prev);
        prev += getLast(occ_counts_before.get('G'));
        starts.put('T', prev);
        prev += getLast(occ_counts_before.get('T'));
        return starts;
    }

    private static Integer getLast(int[] arr) {
        return arr[arr.length - 1];
    }

    private static int getNumber(char found) {
        return Char.valueOf(found).number;
    }

    private static int[][] calcCharNumbers(String bwt) {
        int[][] charNumbers = new int[5][bwt.length()];
        charNumbers[Char.A.number][0] = 0;
        charNumbers[Char.C.number][0] = 0;
        charNumbers[Char.G.number][0] = 0;
        charNumbers[Char.T.number][0] = 0;
        charNumbers[Char.$.number][0] = 0;
        charNumbers[getNumber(bwt.charAt(0))][0] = 1;
        for (int i = 1; i < bwt.length(); i++) {
            charNumbers[Char.A.number][i] = charNumbers[Char.A.number][i - 1];
            charNumbers[Char.C.number][i] = charNumbers[Char.C.number][i - 1];
            charNumbers[Char.G.number][i] = charNumbers[Char.G.number][i - 1];
            charNumbers[Char.T.number][i] = charNumbers[Char.T.number][i - 1];
            charNumbers[Char.$.number][i] = charNumbers[Char.$.number][i - 1];
            charNumbers[getNumber(bwt.charAt(i))][i] = charNumbers[getNumber(bwt.charAt(i))][i] + 1;
        }
        return charNumbers;
    }

    // Compute the number of occurrences of string pattern in the text
    // given only Burrows-Wheeler Transform bwt of the text and additional
    // information we get from the preprocessing stage - starts and occ_counts_before.
    static int CountOccurrences(String pattern, String bwt, Map<Character, Integer> starts, Map<Character, int[]> occ_counts_before) {
        int top = 0;
        int bot = bwt.length() - 1;
        int patternPointer = pattern.length() - 1;
        while (top <= bot) {
            if (patternPointer < 0) {
                return bot - top + 1;
            }
            char symbol = pattern.charAt(patternPointer);
            int first = -1;
            int last = -1;
            for (int i = top; i <= bot; i++) {
                if (bwt.charAt(i) == symbol) {
                    first = i;
                    break;
                }
            }
            for (int i = bot; i >= top; i--) {
                if (bwt.charAt(i) == symbol) {
                    last = i;
                    break;
                }
            }

            if (first == -1) {
                return 0;
            }
            top = starts.get(symbol) + occ_counts_before.get(symbol)[first] - 1;
            bot = starts.get(symbol) + occ_counts_before.get(symbol)[last] - 1;
            patternPointer--;
        }
        throw new IllegalStateException();
    }

    static public void main(String[] args) throws IOException {
        new BWMatching().run();
    }

    public static void print(int[] x) {
        for (int a : x) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public void run() throws IOException {
        FastScanner scanner = new FastScanner();
        String bwt = scanner.next();
        // Start of each character in the sorted list of characters of bwt,
        // see the description in the comment about function PreprocessBWT
        Map<Character, Integer> starts = new HashMap<Character, Integer>();
        // Occurrence counts for each character and each position in bwt,
        // see the description in the comment about function PreprocessBWT
        Map<Character, int[]> occ_counts_before = new HashMap<Character, int[]>();
        // Preprocess the BurrowsWheeler once to get starts and occ_count_before.
        // For each pattern, we will then use these precomputed values and
        // spend only O(|pattern|) to find all occurrences of the pattern
        // in the text instead of O(|pattern| + |text|).
        long start = System.currentTimeMillis();
        PreprocessBWT(bwt, starts, occ_counts_before);
        long preprocess = System.currentTimeMillis();
        int patternCount = scanner.nextInt();
        String[] patterns = new String[patternCount];
        int[] result = new int[patternCount];
        for (int i = 0; i < patternCount; ++i) {
            patterns[i] = scanner.next();
            result[i] = CountOccurrences(patterns[i], bwt, starts, occ_counts_before);
        }
        print(result);
    }

    public static void run(String bwt, String... patterns) throws IOException {
        // Start of each character in the sorted list of characters of bwt,
        // see the description in the comment about function PreprocessBWT
        Map<Character, Integer> starts = new HashMap<Character, Integer>();
        // Occurrence counts for each character and each position in bwt,
        // see the description in the comment about function PreprocessBWT
        Map<Character, int[]> occ_counts_before = new HashMap<Character, int[]>();
        // Preprocess the BurrowsWheeler once to get starts and occ_count_before.
        // For each pattern, we will then use these precomputed values and
        // spend only O(|pattern|) to find all occurrences of the pattern
        // in the text instead of O(|pattern| + |text|).
        long start = System.currentTimeMillis();
        PreprocessBWT(bwt, starts, occ_counts_before);
        long preprocess = System.currentTimeMillis();
        System.out.println("preprocess: " + (preprocess - start));
        int patternCount = patterns.length;
        int[] result = new int[patternCount];
        for (int i = 0; i < patternCount; ++i) {
            start = System.currentTimeMillis();
            result[i] = CountOccurrences(patterns[i], bwt, starts, occ_counts_before);
            System.out.println("count 1: " + (System.currentTimeMillis() - start));
        }
        System.out.println("count: " + (System.currentTimeMillis() - preprocess));
        print(result);
    }
}
