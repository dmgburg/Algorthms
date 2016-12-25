import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class TrieMatchingExtended implements Runnable {
    private static final char end = '$';

    class Node {
        public static final int Letters = 4;
        public static final int NA = -1;
        public int next[];
        public boolean patternEnd;

        Node() {
            next = new int[Letters];
            Arrays.fill(next, NA);
            patternEnd = false;
        }
    }

    int letterToIndex(char letter) {
        switch (letter) {
            case 'A':
                return 0;
            case 'C':
                return 1;
            case 'G':
                return 2;
            case 'T':
                return 3;
            default:
                assert (false);
                return Node.NA;
        }
    }

    List<Integer> solve(String text, List<String> patterns) {
        Set<Integer> result = new HashSet<>();
        List<Map<Character, Integer>> trie = new ArrayList<>();
        trie.add(new HashMap<>());
        patterns.forEach(pattern -> {
            int current = 0;
            for (int i = 0; i < pattern.length(); i++) {
                char thisChar = pattern.charAt(i);
                Map<Character, Integer> node = trie.get(current);
                if (node.containsKey(thisChar)) {
                    current = node.get(thisChar);
                } else {
                    current = trie.size();
                    node.put(thisChar, current);
                    trie.add(new HashMap<>());
                }
            }
            trie.get(current).put('$', -1);
        });

        for (int i = 0; i < text.length(); i++) {
            int current = 0;
            int j = i;
            while (true) {
                Map<Character, Integer> node = trie.get(current);
                if (node.containsKey(end)) {
                    result.add(i);
                }
                if (j >= text.length()) break;

                char thisChar = text.charAt(j);
                if (node.containsKey(thisChar)) {
                    current = node.get(thisChar);
                    j++;
                    continue;
                } else {
                    break;
                }
            }
        }

        ArrayList<Integer> list = new ArrayList<>(result);
        Collections.sort(list);
        return list;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String text = in.readLine();
            int n = Integer.parseInt(in.readLine());
            List<String> patterns = new ArrayList<String>();
            for (int i = 0; i < n; i++) {
                patterns.add(in.readLine());
            }

            List<Integer> ans = solve(text, patterns);

            for (int j = 0; j < ans.size(); j++) {
                System.out.print("" + ans.get(j));
                System.out.print(j + 1 < ans.size() ? " " : "\n");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Thread(new TrieMatchingExtended()).start();
    }
}
