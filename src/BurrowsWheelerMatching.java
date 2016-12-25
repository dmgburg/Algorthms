import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class BurrowsWheelerMatching {
    private String ALPHABET = "$ACGT";

    public BurrowsWheelerMatching() {
    }

    public BurrowsWheelerMatching(String ALPHABET) {
        this.ALPHABET = ALPHABET;
    }

    // Preprocess the Burrows-Wheeler Transform bwt of some text
    // and compute as a result:
    //   * starts - for each character C in bwt, starts[C] is the first position
    //       of this character in the sorted array of
    //       all characters of the text.
    //   * occ_count_before - for each character C in bwt and each position P in bwt,
    //       occ_count_before[C][P] is the number of occurrences of character C in bwt
    //       from position 0 to position P inclusive.
    private void PreprocessBWT(String bwt, Map<Character, Integer> starts, Map<Character, int[]> occ_counts_before) {
        int[][] calcCharNumbers = calcCharNumbers(bwt);
        for (char character : ALPHABET.toCharArray()) {
            occ_counts_before.put(character, calcCharNumbers[getNumber(character)]);
        }
        getStartingIndex(occ_counts_before, starts);
    }

    private Map<Character, Integer> getStartingIndex(Map<Character, int[]> occ_counts_before, Map<Character, Integer> starts) {
        int prev = 0;
        for (char character : ALPHABET.toCharArray()) {
            starts.put(character, prev);
            prev += getLast(occ_counts_before.get(character));
        }
        return starts;
    }

    private static Integer getLast(int[] arr) {
        return arr[arr.length - 1];
    }

    private int getNumber(char found) {
        return ALPHABET.indexOf(found);
    }

    private int[][] calcCharNumbers(String bwt) {
        int[][] charNumbers = new int[5][bwt.length()];
        for (char character : ALPHABET.toCharArray()) {
            charNumbers[getNumber(character)][0] = 0;
        }
        charNumbers[getNumber(bwt.charAt(0))][0] = 1;
        for (int i = 1; i < bwt.length(); i++) {
            for (char character : ALPHABET.toCharArray()) {
                charNumbers[getNumber(character)][i] = charNumbers[getNumber(character)][i - 1];
            }
            charNumbers[getNumber(bwt.charAt(i))][i] = charNumbers[getNumber(bwt.charAt(i))][i] + 1;
        }
        return charNumbers;
    }

    // Compute the number of occurrences of string pattern in the text
    // given only Burrows-Wheeler Transform bwt of the text and additional
    // information we get from the preprocessing stage - starts and occ_counts_before.
    private static List<PartialMatch> findMatches(String pattern, String bwt, Map<Character, Integer> starts, Map<Character, int[]> occ_counts_before, int minRequiredMatch) {
        List<PartialMatch> result = new ArrayList<>();
        int mathcedLength = 0;
        int top = 0;
        int bot = bwt.length() - 1;
        int patternPointer = pattern.length() - 1;
        while (top <= bot) {
            if (patternPointer < 0) {
                for (int i = top; i <= bot; i++) {
                    result.add(new PartialMatch(i, mathcedLength));
                }
                return result;
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

            if (mathcedLength >= minRequiredMatch) {
                if (first == -1){
                    for (int i = top; i < bot; i++) {
                        result.add(new PartialMatch(i, mathcedLength));
                    }
                } else {
                    for (int i = top; i < first; i++) {
                        result.add(new PartialMatch(i, mathcedLength));
                    }
                    for (int i = last; i < bot; i++) {
                        result.add(new PartialMatch(i, mathcedLength));
                    }
                }
            }

            if (first == -1) {
                return result;
            }
            top = starts.get(symbol) + occ_counts_before.get(symbol)[first] - 1;
            bot = starts.get(symbol) + occ_counts_before.get(symbol)[last] - 1;
            patternPointer--;
            mathcedLength++;
        }
        throw new IllegalStateException();
    }

    public static void print(int[] x) {
        for (int a : x) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public List<PartialMatch> partialSuffixMatch(String bwt, String pattern, int minRequiredMatch) {
        Map<Character, Integer> starts = new HashMap<Character, Integer>();
        Map<Character, int[]> occ_counts_before = new HashMap<Character, int[]>();
        // Preprocess the BurrowsWheeler once to get starts and occ_count_before.
        // For each pattern, we will then use these precomputed values and
        // spend only O(|pattern|) to find all occurrences of the pattern
        // in the text instead of O(|pattern| + |text|).
        PreprocessBWT(bwt, starts, occ_counts_before);
        List<PartialMatch> result = findMatches(pattern, bwt, starts, occ_counts_before, minRequiredMatch);
        return result;
    }

    public List<PartialMatch> match(String bwt, String pattern) {
        Map<Character, Integer> starts = new HashMap<Character, Integer>();
        Map<Character, int[]> occ_counts_before = new HashMap<Character, int[]>();
        // Preprocess the BurrowsWheeler once to get starts and occ_count_before.
        // For each pattern, we will then use these precomputed values and
        // spend only O(|pattern|) to find all occurrences of the pattern
        // in the text instead of O(|pattern| + |text|).
        PreprocessBWT(bwt, starts, occ_counts_before);
        List<PartialMatch> result = findMatches(pattern, bwt, starts, occ_counts_before, pattern.length());
        return result;
    }

    public static class PartialMatch{
        public int position;
        public int mathchedLength;

        public PartialMatch(int position, int mathchedLength) {
            this.position = position;
            this.mathchedLength = mathchedLength;
        }

        @Override
        public String toString() {
            return "PartialMatch{" +
                    "position=" + position +
                    ", mathchedLength=" + mathchedLength +
                    '}';
        }
    }

    public static void main(String[] args) {
        String text = "AGGTCACTCGGAGGTACT$";
        CircularSuffixArray csa = new CircularSuffixArray(text);
        List<PartialMatch> partialMatches = new BurrowsWheelerMatching().partialSuffixMatch(BurrowsWheeler.encode(text),"AGGTC",1);
        for (PartialMatch match : partialMatches){
            System.out.println("place: " + match.position);
            System.out.println("index: " + csa.index(match.position));
            System.out.println("length: " + match.mathchedLength);
            System.out.println("String: " + text.substring(csa.index(match.position),csa.index(match.position) + match.mathchedLength));
            System.out.println();
        }
    }
}
