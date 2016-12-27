import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Denis on 27.12.2016.
 */
public class BubbleDetectionTest {
    private static String string = "ACGT";

    @Test
    public void test(){
        List<Integer>[] graph = new List[12];
        graph[0] = new ArrayList<>();
        graph[0].add(1);
        graph[0].add(5);
        graph[0].add(8);

        graph[1] = new ArrayList<>();
        graph[1].add(2);
        graph[2] = new ArrayList<>();
        graph[2].add(3);
        graph[3] = new ArrayList<>();
        graph[3].add(4);
        graph[3].add(7);
        graph[4] = new ArrayList<>();
        graph[4].add(12);
        graph[5] = new ArrayList<>();
        graph[5].add(6);
        graph[6] = new ArrayList<>();
        graph[6].add(7);
        graph[7] = new ArrayList<>();
        graph[7].add(4);
        graph[8] = new ArrayList<>();
        graph[8].add(9);
        graph[9] = new ArrayList<>();
        graph[9].add(10);
        graph[10] = new ArrayList<>();
        graph[10].add(0);
        graph[11] = new ArrayList<>();
        graph[11].add(0);
        BubbleDetection.dfs(graph,new boolean[graph.length],8,3,0);
    }

    @Test
    public void test1(){
        List<String> graph = new ArrayList<>();
        graph.add("AAAG");
        graph.add("AAGG");
        graph.add("AGGA");
        graph.add("GGAG");
        graph.add("GAGT");
        graph.add("AGTT");
        graph.add("AGGT");
        graph.add("GGTT");
        graph.add("AAAC");
        graph.add("AACG");
        graph.add("ACGT");
        graph.add("CGTT");
        graph.add("GTTA");
        graph.add("TTAA");
        graph.add("TAAA");
        BubbleDetection.getBubbles(graph,4,4);
    }
}

