package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Mode {ALL, ANY, NONE}
public class Main {

    public static String readLineFromInput() {
        Scanner scan = new Scanner(System.in);
        if (scan.hasNextLine()) {
            return scan.nextLine();
        }
        return "";
    }

    public static int getNumberFromInput() {
        String number = readLineFromInput().trim();
        int lines;
        if (number.matches("\\d+")) {
            lines = Integer.parseInt(number);
        } else {
            lines = 0;
        }
        return lines;
    }

    public static String[] getMultiLinesFromInput(int numberOfLines) {
        String[] lines = new String[numberOfLines];
        for (int i = 0; i < numberOfLines; i++) {
            lines[i] = readLineFromInput();
        }
        return lines;
    }

    public static void searchForTerm(Search search) {
        boolean invalidMode = true;
        Mode strategy = null;
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        while (invalidMode) {
            String input = readLineFromInput().toUpperCase();
            try {
               strategy = Mode.valueOf(input);
               invalidMode = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Enter a valid strategy: ALL, ANY, NONE");
            }
        }
        System.out.println("Enter the search term:");
        String term = readLineFromInput();
        search.printMatchingLines(term, strategy);
    }

    @Deprecated
    public static void repeatSearches(Search search) {
        System.out.println("Enter the number of search queries:");
        int queries = getNumberFromInput();
        for (int i = 0; i < queries; i++) {
            searchForTerm(search);
        }
    }

    public static String[] getLinesFromFile(String path) {
        // return all lines in a file as a String array
        File file = new File(path);
        List<String> lines = new ArrayList<>();
        try (Scanner scan = new Scanner(file)){
            while (scan.hasNext()) {
                lines.add(scan.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found at"+file.getPath());
            System.exit(1);
        }
        return lines.toArray(new String[0]);
    }
    public static void main(String[] args) {
        String[] entries = new String[0];
        if (args.length == 0) { // get entries from user input if called with no args
            System.out.println("Enter the number of entries:");
            int lines = getNumberFromInput();
            System.out.println("Enter all entries:");
            entries = getMultiLinesFromInput(lines);
        } else {
            boolean isArgsValid = false; // validate args
            if (args.length == 2) {
                if ("--data".equals(args[0])) {
                    entries = getLinesFromFile(args[1]);
                    isArgsValid = true;
                }
            }
            if (!isArgsValid) { // end program if args are invalid
                System.out.println("Unknown arguments, supported args are: --data FILENAME");
                System.exit(1);
            }
        } 
        Search search = new Search(entries);
        search.buildInvertedIndex();
        int choice = -1;
        while (choice != 0) {
            System.out.println("""
                    === Menu ===
                    1. Find an entry
                    2. Print all entries
                    0. Exit""");
            choice = getNumberFromInput();
            System.out.println();
            switch (choice) {
                case 1 -> searchForTerm(search);
                case 2 -> search.printAllEntries();
                case 0 -> System.out.println("Bye!");
                default -> System.out.println("Incorrect option! Try again.");
            }
            System.out.println();
        }
    }
}
