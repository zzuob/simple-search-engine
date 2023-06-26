package search;

import java.util.*;
import java.util.stream.IntStream;

public class Search {

    private final String[] entries;
    private final Map<String, List<Integer>> invertedIndex = new HashMap<>();

    private Set<Integer> matches = new HashSet<>();

    public Search(String[] entries) {
        this.entries = entries;
    }

    public void buildInvertedIndex() {
        for (int i = 0; i < entries.length; i++) {
            for (String word : entries[i].split("\\s+")) {
                List<Integer> linesFound = invertedIndex.get(word.toLowerCase());
                if (linesFound == null) {
                    linesFound = new ArrayList<>(); // create a new list of line indexes for the word
                }
                linesFound.add(i); // add the current line no. to the list
                invertedIndex.put(word.toLowerCase(), linesFound); // update the word + list pair
            }
        }
    }

    public void printMatchingLines(String term, Mode mode) {
        updateMatches(term, mode);
        if (matches != null) {
            for (Integer index : matches) {
                System.out.println(entries[index]);
            }
        } else {
            System.out.println("No matching entry found.");
        }
        System.out.println();
    }

    public void updateMatches(String term, Mode mode) {
        // update matches for a given search term and mode
        String[] terms = term.split("\\s+");
        Set<Integer> indexes = new HashSet<>();
        for (String thisTerm : terms) {
            List<Integer> termIndexes = invertedIndex.get(thisTerm.toLowerCase());
            if (termIndexes != null) {
                if (indexes.isEmpty()) { // first iteration with matches found
                    indexes.addAll(termIndexes);
                } else {
                    Set<Integer> newIndexes = new HashSet<>(termIndexes);
                    switch (mode) {
                        case ANY, NONE -> indexes.addAll(newIndexes); // union
                        case ALL -> indexes.retainAll(newIndexes); // intersection
                    }
                }
            }
        }
        if (mode == Mode.NONE) { // find the difference between all indexes and indexes containing search terms
            // i.e. all indexes not containing the search terms
            Set<Integer> allIndexes = new HashSet<>();
            int[] allLines = IntStream.range(0, entries.length).toArray();
            for (int line : allLines) {
                allIndexes.add(line);
            }
            allIndexes.removeAll(indexes);
            indexes = allIndexes;
        }
        matches = indexes;
    }

    public void printAllEntries() {
        System.out.println("=== List of entries ===");
        for (String entry : entries) {
            System.out.println(entry);
        }
    }
}

