package ru.job4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CSVReader {

    public static String handle(ArgsName argsName) throws Exception {
        Path file = Paths.get(argsName.get("path"));
        List<String> fileValues = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        List<String> text = new ArrayList<>();

        try (var values = new Scanner(file).useDelimiter(";|\\r\\n")) {
            while (values.hasNext()) {
                fileValues.add(values.next());
            }
        }

        try (var filterValues = new Scanner(argsName.get("filter")).useDelimiter(",")) {
            while (filterValues.hasNext()) {
                indexes.add(fileValues.indexOf(filterValues.next()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Filter parameters or not exist or not in line with the source data");
        }

        try (var lines = new Scanner(file).useDelimiter(System.lineSeparator())) {
            while (lines.hasNext()) {
                text.add(lines.next());
            }
        }

        String[][] wordsArray = new String[text.size()][indexes.size()];
        for (int row = 0; row < text.size(); row++) {
            List<String> parsedLine = new ArrayList<>();
            var y = new Scanner(text.get(row)).useDelimiter(";");
            while (y.hasNext()) {
                parsedLine.add(y.next());
            }
            for (int column = 0; column < indexes.size(); column++) {
                wordsArray[row][column] = parsedLine.get(indexes.get(column));
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
        String rsl = collectedArray.toString();

        try (PrintWriter out = new PrintWriter(
                new BufferedOutputStream(
                        new FileOutputStream(argsName.get("out"))
                ))) {
            out.write(rsl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rsl;
    }

    public static void checkArgs(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "In the root folder has no four arguments. Usage ROOT_FOLDER");
        }
        if (!args[0].endsWith(".csv")) {
            throw new IllegalArgumentException(
                    "The paths directory has wrong extension or does not exist");
        }
        if (args[1].length() > 12) {
            throw new IllegalArgumentException("The delimiter should be one type");
        }
        if (!args[2].endsWith(".csv")) {
            throw new IllegalArgumentException("The source directory has wrong extension");
        }
    }

    public static void writeOut(ArgsName argsName) throws Exception {
        System.out.println(handle(argsName));
    }

    public static void main(String[] args) throws Exception {
        checkArgs(args);
        ArgsName rsl = ArgsName.of(args);
        handle(rsl);
        writeOut(rsl);
    }
}