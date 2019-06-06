/**
 * Genetic Algorithm
 */

import javafx.util.Pair;
import java.util.Random;
import java.util.*;


public class GA {
private static Random random = new Random(System.currentTimeMillis());


    //Crossover, will combine part of chromosones from both mother and father
    public static ArrayList<Integer> crossover(ArrayList<Integer> father, ArrayList<Integer> mother) {
        ArrayList<Integer> child = new ArrayList<>();

        int crossPoint = random.nextInt(father.size() - 1);  //Find a random crossover point

        for (int i = 0; i < father.size(); i++) {
            if (i < crossPoint) {
                child.add(father.get(i));
            } else {
                child.add(mother.get(i));
            }
        }
        return child;
    }


    //Mutates a chromosone
    public static ArrayList<Integer> mutate(ArrayList<Integer> chromosone, double probability, int batchSize) {
        int numberOfBatches = 1 + (chromosone.size() - 1) / batchSize;
        for (int i = 0; i < chromosone.size(); i++) {
            if (random.nextFloat() <= probability) {
                chromosone.set(i, random.nextInt(numberOfBatches));
            }
        }
        return chromosone;
    }


    public static ArrayList<Integer> mutate2(ArrayList<Integer> chromosone, int batchSize) {
        if (random.nextFloat() < 0.9) {
            // swap
            int order1 = random.nextInt(chromosone.size());
            int order2 = random.nextInt(chromosone.size());
            Collections.swap(chromosone, order1, order2);
        } else {
            // transfer
            int batchAmount = 1 + (chromosone.size() - 1) / batchSize;
            ArrayList<ArrayList<Integer>> batches = new ArrayList<>(batchAmount);

            for (int i = 0; i < batchAmount; i++) {
                batches.add(new ArrayList<>());
            }

            for (int i = 0; i < chromosone.size(); i++) {
                int batch = chromosone.get(i);
                batches.get(batch).add(i);
            }

            for (int batch = 0; batch < batches.size(); batch++) {
                if (batches.get(batch).size() < batchSize) {
                    int randOrderIndex = random.nextInt(chromosone.size());
                    chromosone.set(randOrderIndex, batch);
                    break;
                }
            }
        }
        return chromosone;
    }


    public static ArrayList<Integer> rep(int batchSize, ArrayList<Integer> chrome) {
        ArrayList<Pair<Integer, ArrayList<Integer>>> batches = new ArrayList<>();
        int batchAmount = 1 + (chrome.size() - 1) / batchSize;
        for (int batch = 0; batch < batchAmount; batch++) {//crate batches
            Pair<Integer, ArrayList<Integer>> pair = new Pair<>(batch, new ArrayList<>());
            batches.add(pair);
        }

        for (int order = 0; order < chrome.size(); order++) {//count orders in each batch
            int batch = chrome.get(order);
            batches.get(batch).getValue().add(order);
        }

        Collections.sort(batches, new Comparator<Pair<Integer, ArrayList<Integer>>>() {
            @Override
            public int compare(Pair<Integer, ArrayList<Integer>> o1, Pair<Integer, ArrayList<Integer>> o2) {
                return o1.getValue().size() - o2.getValue().size();
            }
        });

        int right = batches.size() - 1;
        int left = 0;

        while (batches.get(right).getValue().size() > batchSize) {
            int order = batches.get(right).getValue().remove(0);
            chrome.set(order, batches.get(left).getKey());
            batches.get(left).getValue().add(order);
            if (batches.get(right).getValue().size() <= batchSize) {
                right--;
            }
            if (batches.get(left).getValue().size() >= batchSize) {
                left++;
            }
        }

        return chrome;
    }


    //Old and bad versions of repair

    //repair a chromosone
    public static ArrayList<Integer> repair(int batchSize, ArrayList<Integer> chrome) {
        ArrayList<Integer> repairedArray = chrome;
        ArrayList<Integer> itemsInBatch = new ArrayList<>();
        int numberOfBatches = 1 + (chrome.size() - 1) / batchSize;
        Stack<Integer> batchesWithFew = new Stack<>();
        //Finds amount of items in each batch
        int count = 0;
        for (int i = 0; i < numberOfBatches; i++) {
            for (int j = 0; j < chrome.size(); j++) {
                if (chrome.get(j) == i) {
                    count++;
                }
            }
            if (count < batchSize) {
                batchesWithFew.push(i);
            }
            itemsInBatch.add(count);
            count = 0;
        }
        //Replaces items to correct batch
        for (int i = 0; i < numberOfBatches; i++) {
            if (itemsInBatch.get(i) > batchSize) {  //finds the batches with more items then allowed
                int remaining = 0;
                while (true) {
                    if (batchesWithFew.empty()) {
                        break;
                    }
                    int batchWithLess = batchesWithFew.pop();
                    int canHold = batchSize - itemsInBatch.get(batchWithLess);


                    //i finns inte med i repairedArray
                    remaining = itemsInBatch.get(i) - remaining - canHold;
                    for (int m = 0; m < canHold; m++) {
                        repairedArray.set(repairedArray.indexOf(i), batchWithLess); //switch items in batch
                    }
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
        return repairedArray;
    }


    public static ArrayList<Integer> findBatchesWithFew(ArrayList<Integer> chrome, int batchSize) {
        int numberOfBatches = 1 + (chrome.size() - 1) / batchSize;
        ArrayList<Integer> itemsInStack = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < numberOfBatches; i++) {
            for (int j = 0; j < chrome.size(); j++) {
                if (chrome.get(j) == i) {
                    counter++;
                }
            }
            itemsInStack.add(counter);
            counter = 0;
        }
        return itemsInStack;
    }


    public static ArrayList<Integer> repOld(int batchSize, ArrayList<Integer> chrome) {
        ArrayList<Integer> newArray = chrome;
        int numberOfBatches = 1 + (chrome.size() - 1) / batchSize;
        ArrayList<Integer> itemsInBatches = findBatchesWithFew(chrome, batchSize);
        for (int i = 0; i < itemsInBatches.size(); i++) {   //gå igenom alla batches
            if (itemsInBatches.get(i) > batchSize) {    //Om batchen är större än tillåtet
                for (int j = 0; j < itemsInBatches.size(); j++) {      //för alla batches
                    if (itemsInBatches.get(j) < batchSize) {        //om batchen är mindre
                        for (int x = 0; x < batchSize - itemsInBatches.get(j); x++) {  //loopa så många gånger som krävs
                            if (itemsInBatches.get(i) > batchSize) {
                                newArray.set(newArray.indexOf(i), j);
                                itemsInBatches.set(j, itemsInBatches.get(j) + 1);
                                itemsInBatches.set(i, itemsInBatches.get(i) - 1);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return newArray;
    }

}