import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Denis on 26.12.2016.
 */
public class TestOptimalKmer {
    private static String string = "ACGT";

    @Test
    public void test(){
        Metric.debug = true;
        Random random = new Random(100);
        while(true) {
            testOne(random);
        }
    }


    private void testOne(Random random) {
        int genomeLength = 1500000;
        System.out.println("Genome length " + genomeLength);
        int readLength = 10;
        int readsAmount = genomeLength;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < genomeLength; i++) {
            int character = random.nextInt(string.length());
            stringBuilder.append(string.charAt(character));
        }
        String genome = stringBuilder.toString();
        List<String> reads = new ArrayList<>();
        for (int i = 0; i < readsAmount; i++) {
            String read;
            if(i + readLength < genomeLength){
                read = genome.substring(i, i + readLength);

            } else {
                read = genome.substring(i) + genome.substring(0, readLength - (genomeLength - i));
            }
            reads.add(read);
        }
        Collections.shuffle(reads,random);
        int optimal = OptimalKMer.deBruhinGraph(reads);
        Assert.assertEquals(optimal, readLength);
    }
}
