import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class BurrowsWheeler {

    /**
     *  Apply Burrows-Wheeler encoding
     */
    public static String encode(String input) {
        StringBuilder result = new StringBuilder();
        // create circ suff arr for it
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(input);

        // make output as last chars of sorted suffixes
        for (int i = 0; i < circularSuffixArray.length(); i++) {
            int index = circularSuffixArray.index(i);
            if (index == 0) {
                result.append(input.charAt(input.length() - 1));
                continue;
            }
            result.append(input.charAt(index - 1));
        }
        // BinaryStdOut must be closed
        return result.toString();
    }

    /**
     *  Apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
     */
    public static String decode(int first, String input) {
        // take first, chars[] from input
        char[] chars = input.toCharArray();
        // construct next[]
        int next[] = new int[chars.length];
        // Algorithm: Brute Force requires O(n^2) =>
        // go through chars, consider chars as key remember positions of chars's in the Queue
        Map<Character, Queue<Integer>> positions = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            if(!positions.containsKey(chars[i]))
                positions.put(chars[i], new ArrayDeque<>());
            positions.get(chars[i]).offer(i);
        }
        // get first chars array
        Arrays.sort(chars);
        // go consistently through sorted firstChars array
        for (int i = 0; i < chars.length; i++) {
            next[i] = positions.get(chars[i]).poll();
        }
        // decode msg
        // for length of the msg
        StringBuilder result = new StringBuilder();
        for (int i = 0, curRow = first; i < chars.length; i++, curRow = next[curRow])
            // go from first to next.
            result.append(chars[curRow]);
        return result.toString();
    }
}