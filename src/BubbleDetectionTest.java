import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static edu.uci.ics.jung.samples.SimpleGraphDraw.getGraph;

/**
 * Created by Denis on 27.12.2016.
 */
public class BubbleDetectionTest {
    private static String string = "ACGT";

    @Test
    public void test() {
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
        BubbleDetection.dfs(graph, new boolean[graph.length], 8, 3, 0);
    }

    @Test
    public void test1() {
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
        BubbleDetection.getBubbles(graph, 4, 4);
    }


    @Test
    public void test2() {
        List<String> graph = new ArrayList<>();
        graph.add("AAAG");
        graph.add("AAGG");
        graph.add("AGGT");
        graph.add("GGTT");
        graph.add("AAAT");
        graph.add("AATG");
        graph.add("ATGT");
        graph.add("TGTT");
        graph.add("AAAC");
        graph.add("AACG");
        graph.add("ACGT");
        graph.add("CGTT");
        graph.add("GTTA");
        graph.add("TTAA");
        graph.add("TAAA");
        BubbleDetection.getBubbles(graph, 4, 4);
    }

    @Test
    public void testRandom() {
        Random random = new Random(100);
        while (true) {
            testOne(random);
        }
    }

    private void testOne(Random random) {
        int genomeLength = 10;
        System.out.println("Genome length " + genomeLength);
//        int readLength = genomeLength / 2;
        int readLength = genomeLength / 2;
        System.out.println("Read length " + readLength);
//        int readsAmount = 1618;
//        int readsAmount = genomeLength * 150 / readLength;
        int readsAmount = genomeLength;
        System.out.println("Reads amount " + readsAmount);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < genomeLength; i++) {
            int character = random.nextInt(string.length());
            stringBuilder.append(string.charAt(character));
        }
        String genome = stringBuilder.toString();
        List<String> reads = new ArrayList<>();
        for (int i = 0; i < readsAmount-1; i++) {
            int startIndex = i;
            String read;
            if (startIndex + readLength <= genomeLength) {
                read = genome.substring(startIndex, startIndex + readLength);

            } else {
                read = genome.substring(startIndex) + genome.substring(0, readLength - (genomeLength - startIndex));
            }
            reads.add(new String(read));
        }

        int optimalRead = OptimalKMer.getOptimal(reads);
        showGraph(BubbleDetection.getDeBruhinGraph(reads, optimalRead));
        int bubbleCount = 1;
        for (int i = 0; i < bubbleCount; i++) {
            int readIndex = random.nextInt(readsAmount);
            char[] chars = reads.get(readIndex).toCharArray();
            int place = random.nextInt(chars.length);
            char character = chars[place];
            char newChar;
            do {
                newChar = string.charAt(random.nextInt(string.length()));
            } while (newChar == character);
            chars[place] = newChar;
            reads.add(new String(chars));
        }

        for (String read : reads) {
            assert read.length() == readLength;
        }
        showGraph(BubbleDetection.getDeBruhinGraph(reads, optimalRead));
        if (StronglyConnectedComponents.scc(BubbleDetection.getDeBruhinGraph(reads, optimalRead).graph).size() == 1){
            List<BubbleDetection.Bubble> bubbles = BubbleDetection.getBubbles(reads, optimalRead, optimalRead);
            Assert.assertEquals(bubbleCount, bubbles.size());
            return;
        }
    }

    private void showGraph(DeBruhinGraph deBruhinGraph){
        List<Integer>[] graph = deBruhinGraph.graph;
        JFrame jf = new JFrame();
        Graph<String,Integer> g = new SparseMultigraph<>();
        for (int i = 0; i < graph.length; i++) {
            g.addVertex(deBruhinGraph.nodes.get(i).prefix);
        }
        int index = 0;
        for (int i = 0; i < graph.length; i++) {
            for (Integer node : graph[i]){
                g.addEdge(index++,deBruhinGraph.nodes.get(i).prefix,deBruhinGraph.nodes.get(node).prefix, EdgeType.DIRECTED);
            }
        }

        Layout<String,Integer> layout = new KKLayout<>(g);
        VisualizationViewer vv = new VisualizationViewer(layout);
        final DefaultModalGraphMouse<Integer,Number> graphMouse = new DefaultModalGraphMouse<Integer,Number>();
        vv.setGraphMouse(graphMouse);
        Transformer<Integer,String> stringer = e -> g.getEndpoints(e).getSecond().substring(g.getEndpoints(e).getSecond().length() - 1);
        vv.getRenderContext().setEdgeLabelTransformer(stringer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        jf.getContentPane().add(panel);
        jf.pack();
        jf.setVisible(true);
    }
}

