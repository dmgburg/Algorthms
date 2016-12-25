import java.util.Arrays;

public class CircularSuffixArray {

    private String input;

    private Integer[] index;

    /**
     * Constructs circular suffix array of input String s
     *
     * @param s Input String
     */
    public CircularSuffixArray(String s) {
        if (s == null || s.equals(""))
            throw new java.lang.IllegalArgumentException("Can't get suffix array for empty string!");
        input = s;
        index = new Integer[length()];
        for (int i = 0; i < index.length; i++)
            index[i] = i;

        // algorithm: not to store strings; just compare them using number of shifts
        Arrays.sort(index, (first, second) -> {
            // get start indexes of chars to compare
            int firstIndex = first;
            int secondIndex = second;
            // for all characters
            for (int i = 0; i < input.length(); i++) {
                // if out of the last char then start from beginning
                if (firstIndex > input.length() - 1)
                    firstIndex = 0;
                if (secondIndex > input.length() - 1)
                    secondIndex = 0;
                // if first string > second
                if (input.charAt(firstIndex) < input.charAt(secondIndex))
                    return -1;
                else if (input.charAt(firstIndex) > input.charAt(secondIndex))
                    return 1;
                // watch next chars
                firstIndex++;
                secondIndex++;
            }
            // equal strings
            return 0;
        });
    }

    /**
     * Length of circular array string
     */
    public int length() {
        return input.length();
    }

    /**
     * Returns ordinal row (index) in the original suffix of ith sorted suffix
     *
     * @param i Ordinal number of sorted index
     */
    public int index(int i) {
        return index[i];
    }
}
