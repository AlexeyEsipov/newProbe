package ru.job4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CSVReader {

    public static void handle(ArgsName argsName) throws Exception {
        Path file = Paths.get(argsName.get("path"));
        List<String> fileValues1 = new ArrayList<>();
        List<String> fileValues = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        List<String> text = new ArrayList<>();

        try (var values = new Scanner(file).useDelimiter(System.lineSeparator())) {
            while (values.hasNext()) {
                fileValues1.add(values.next());
            }
        }
        for (String d : fileValues1) {
            try (var v = new Scanner(d).useDelimiter(";")) {
                while (v.hasNext()) {
                    fileValues.add(v.next());
                }
            }
        }
        try (var filterValues = new Scanner(argsName.get("filter")).useDelimiter(",")) {
            while (filterValues.hasNext()) {
                indexes.add(fileValues.indexOf(filterValues.next()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Filter parameters or not exist or are not in line with the source data");
        }

        try (var lines = new Scanner(file).useDelimiter(System.lineSeparator())) {
            while (lines.hasNext()) {
                text.add(lines.next());
            }
        }

        String[][] wordsArray = new String[text.size()][indexes.size()];
        for (int row = 0; row < text.size(); row++) {
            List<String> parsedLine = new ArrayList<>();
            try (var y = new Scanner(text.get(row)).useDelimiter(argsName.get("delimiter"))) {
                while (y.hasNext()) {
                    parsedLine.add(y.next());
                }
                for (int column = 0; column < indexes.size(); column++) {
                    wordsArray[row][column] = parsedLine.get(indexes.get(column));
                }
            }
        }

        StringBuilder collectedArray = new StringBuilder();
        for (String[] string : wordsArray) {
            StringJoiner collectedString = new StringJoiner(";", "", "");
            for (String word : string) {
                collectedString.add(word);
            }
            collectedArray.append(collectedString).append(System.lineSeparator());
        }
        String resl = collectedArray.toString();
        writeOut(argsName, resl);

    }

    public static void checkArgs(ArgsName argsName) {
        if (!argsName.get("path").endsWith(".csv")) {
            throw new IllegalArgumentException("The paths directory has wrong extension or does not exist");
        }
        if (argsName.get("delimiter").length() > 12) {
            throw new IllegalArgumentException("The delimiter should be one type");
        }
    }

    public static void writeOut(ArgsName argsName, String resl) throws Exception {
        if (argsName.get("out").endsWith("stdout")) {
            System.out.println(resl);
        } else {
            try (PrintWriter out = new PrintWriter(
                    new BufferedOutputStream(
                            new FileOutputStream(argsName.get("out"))
                    ))) {
                out.write(resl);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        handle(ArgsName.of(args));
    }

}