import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TestPhagWithErrors {
    private static String string = "ACGT";

    @Test
    public void test(){
        Random random = new Random(100);
        while(true) {
            testOne(random);
        }
    }


    private void testOne(Random random) {
        int genomeLength = 5386;
        System.out.println("Genome length " + genomeLength);
//        int readLength = genomeLength / 2;
        int readLength = 100;
        System.out.println("Read length " + readLength);
        int readsAmount = 1618;
//        int readsAmount = genomeLength * 1500 /readLength;
        System.out.println("Reads amount" + readsAmount);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < genomeLength; i++) {
            int character = random.nextInt(string.length());
            stringBuilder.append(string.charAt(character));
        }
        String genome = stringBuilder.toString();
        List<String> reads = new ArrayList<>();
        for (int i = 0; i < readsAmount; i++) {
            int startIndex = random.nextInt(genomeLength);
            String read;
            if(startIndex + readLength <= genomeLength){
                read = genome.substring(startIndex, startIndex + readLength);

            } else {
                read = genome.substring(startIndex) + genome.substring(0, readLength - (genomeLength - startIndex));
            }
            char[] chars = read.toCharArray();
            int place = random.nextInt(chars.length);
            char character = chars[place];
            char newChar;
            do {
                newChar = string.charAt(random.nextInt(string.length()));
            } while (newChar == character);
            chars[place] = newChar;

            reads.add(new String(chars));
        }
        for (String read : reads){
            assert read.length() == readLength;
        }
        ArrayList<String> parts = new ArrayList<>(new HashSet<>(reads));
        System.out.println("Unique parts: " + parts.size());
        String solution = new PhagWithErrors(12, 2, true).solve(parts);
        Assert.assertEquals(genome.length(),solution.length());
        Assert.assertTrue((genome+genome).contains(solution));
    }


    @Test
    public void test1(){
        System.out.println(BurrowsWheeler.encode("AASDFADGFSAFDGSDFGSDFHGHDFGHKLSDFJKDFJLGHKDHFGHLASDFHASDLKFHAKLDSFHAKLSDHFKLADHFKLASDHFLASHFLAKSDHFLAKSHDFKLAKSHDF"));
    }
}
