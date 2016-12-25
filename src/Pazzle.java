import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Denis on 23.12.2016.
 */
public class Pazzle {
    private static final Pattern pattern = Pattern.compile("\\(([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)");
    private static final String BLACK = "black";

    private static class Part {
        private String top;
        private String left;
        private String bottom;
        private String right;
        int position = -1;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            return formatter.format("(%s,%s,%s,%s)", top, left, bottom, right).toString();
        }

        public Part(String top, String left, String bottom, String right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Part> parts = new ArrayList<>();
        String line = br.readLine();
        do {
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            parts.add(new Part(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4)));
            line = br.readLine();
        } while (line != null && !line.isEmpty());
        Part[][] result = solvePazzle(parts);
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            Part[] row = result[i];
            for (int j = 0; j < row.length - 1; j++) {
                sb.append(row[j]).append(";");
            }
            sb.append(row[row.length -1]).append("\n");
        }
        System.out.println(sb.toString());
    }

    private static Part[][] solvePazzle(List<Part> parts) {
        Map<String, List<Part>> partsByTop = new HashMap<>();
        Map<String, List<Part>> partsByLeft = new HashMap<>();
        Map<String, List<Part>> partsByBot = new HashMap<>();
        Map<String, List<Part>> partsByRight = new HashMap<>();
        for (Part part : parts) {
            getList(partsByBot, part.bottom).add(part);
            getList(partsByTop, part.top).add(part);
            getList(partsByLeft, part.left).add(part);
            getList(partsByRight, part.right).add(part);
        }
        int dimention = (int) Math.round(Math.sqrt(parts.size()));
        Part[][] init = new Part[dimention][];
        for (int i = 0; i < dimention; i++) {
            init[i] = new Part[dimention];
        }
        return solveInner(partsByTop, partsByLeft, partsByRight, partsByBot, init, 0, 0);
    }

    private static Part[][] solveInner(Map<String, List<Part>> partsByTop, Map<String, List<Part>> partsByLeft, Map<String, List<Part>> partsByRight, Map<String, List<Part>> partsByBot, Part[][] currentState,final int currRow, final int currColumn) {
        List<Part> feasibleParts = getFeasibleParts(partsByTop, partsByLeft, partsByBot, partsByRight,  currentState, currRow, currColumn);
        if (feasibleParts.size() == 0){
            currentState[currRow][currColumn] = null;
            return null;
        }
        if (currColumn == currentState.length - 1 && currRow == currentState.length - 1){
                currentState[currRow][currColumn] = feasibleParts.get(0);
                return currentState;
        }
        for (Part part : feasibleParts){
            int newRow = currColumn == currentState.length -1 ? currRow + 1 : currRow;
            int newColumn = currColumn == currentState.length -1 ? 0 : currColumn + 1;
            currentState[currRow][currColumn] = part;
            Part[][] parts = solveInner(partsByTop, partsByLeft, partsByRight, partsByBot, currentState, newRow, newColumn);
            if (parts != null){
                return parts;
            }
        }
        currentState[currRow][currColumn] = null;
        return null;
    }

    private static List<Part> getFeasibleParts(Map<String, List<Part>> partsByTop, Map<String, List<Part>> partsByLeft, Map<String, List<Part>> partsByBot, Map<String, List<Part>> partsByRight, Part[][] currentState, int currRow, int currColumn) {
        int dimention = currentState.length;
        String top = currRow == 0 ? BLACK : currentState[currRow - 1][currColumn].bottom;
        String left = currColumn == 0 ? BLACK : currentState[currRow][currColumn - 1].right;
        String bot = currRow == dimention - 1 ? BLACK : null;
        String right = currColumn == dimention - 1 ? BLACK : null;
        List<Part> intersected = partsByTop.get(top);
        intersected = intersection(intersected,partsByLeft.get(left));
        if (bot != null) {
            intersected = intersection(intersected, partsByBot.get(bot));
        }
        if (right != null) {
            intersected = intersection(intersected, partsByRight.get(right));
        }
        return intersected;
    }

    private static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    private static List<Part> getList(Map<String, List<Part>> partsByBot, String color) {
        partsByBot.putIfAbsent(color, new ArrayList<>());
        return partsByBot.get(color);
    }
}
