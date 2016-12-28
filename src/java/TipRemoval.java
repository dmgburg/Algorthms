import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TipRemoval {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int requiredReadLength = Integer.valueOf(br.readLine());
        Metric.debug = false;
        List<String> strings = new ArrayList<>();
        String line = br.readLine();
        do {
            strings.add(line);
            line = br.readLine();
        } while (line != null && !line.isEmpty());
        System.out.println(getTips(strings, requiredReadLength));
    }

    public static int getTips(List<String> strings, int requiredReadLength) {
        DeBruhinGraph deBruhinGraph = getDeBruhinGraph(strings, requiredReadLength);
        Set<Integer> tipNodes = new HashSet<>();
        List<List<Integer>> sccs = StronglyConnectedComponents.scc(deBruhinGraph.graph);
        int maxSize = 0;
        for (List<Integer> list : sccs) {
            if (maxSize < list.size()) {
                maxSize = list.size();
            }
        }
        for (List<Integer> list : sccs) {
            if (maxSize != list.size()) {
                tipNodes.addAll(list);
            }
        }
        int result = 0;
        for (int i = 0; i < deBruhinGraph.nodes.size(); i++) {
            DeBruhinGraph.DebruinNode node = deBruhinGraph.nodes.get(i);
            if (tipNodes.contains(i)){
                result += node.directions.size();
            } else{
                for (Integer direction: node.directions.values()){
                    if (tipNodes.contains(direction)){
                        result++;
                    }
                }
            }
        }
        return result;
    }

    static int findNode(List<DeBruhinGraph.DebruinNode> nodes, String prefix) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).prefix.equals(prefix)) {
                return i;
            }
        }
        return -1;
    }

    static DeBruhinGraph getDeBruhinGraph(List<String> strings, int readLength) {
        List<String> reads = new ArrayList<>();
        for (String string : strings) {
            for (int i = 0; i <= string.length() - readLength; i++) {
                reads.add(string.substring(i, i + readLength));
            }
        }
        return DeBruhinGraph.build(reads);
    }
}
