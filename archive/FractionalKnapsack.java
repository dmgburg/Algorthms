import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class FractionalKnapsack {
    public static double getOptimalValue(double capacity, Item[] items) {
        Arrays.sort(items, Collections.reverseOrder());
        double totalWeight = 0;
        double totalValue = 0;
        for (int i = 0; i < items.length; i++) {
            Item thisItem = items[i];
            if (totalWeight + thisItem.weight <= capacity) {
                totalValue += thisItem.value;
                totalWeight += thisItem.weight;
            } else {
                double partition = (capacity - totalWeight)/thisItem.weight;
                totalValue += thisItem.value * partition;
                return totalValue;
            }
            if (totalWeight == capacity) {
                break;
            } else if (totalWeight > capacity) {
                throw new RuntimeException();
            }
        }
        return totalValue;
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int capacity = scanner.nextInt();
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(scanner.nextInt(), scanner.nextInt());
        }
        DecimalFormat format = new DecimalFormat("###.#####");
        System.out.println(format.format(getOptimalValue(capacity, items)));
    }

    static class Item implements Comparable<Item> {
        double weight;
        double value;

        public Item(int value, int weight) {
            this.value = value;
            this.weight = weight;
        }

        public static Item item(int value, int weight){return new Item(value,weight);}

        public double fraction() {
            return (double) value / weight;
        }

        @Override
        public int compareTo(Item o) {
            double r = this.fraction() - o.fraction();
            if (r > 0) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return "Item{" +
                    "weight=" + weight +
                    ", value=" + value +
                    '}';
        }
    }

    public double naive(double capacity, Item[] items, double value) {
        if(items.length == 0){
            return value;
        }
        double[] values = new double[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i].weight < capacity) {
                values[i] =  naive(capacity - items[i].weight,removeItem(items,i),value + items[i].value);
            } else if(items[i].weight == capacity){
                values[i] =  value + items[i].value;
            } else {
                values[i] =  value + items[i].value * capacity / items[i].weight;
            }
        }
        Arrays.sort(values);
        return values[items.length - 1];
    }

    private Item[] removeItem(Item[] items, int i){
        ArrayList<Item> list = new ArrayList<>();
        list.addAll(Arrays.asList(items));
        list.remove(i);
        return list.toArray(new Item[list.size()]);
    }


} 
