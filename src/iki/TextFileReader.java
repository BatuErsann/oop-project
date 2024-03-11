package iki;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static void main(String[] args) {
        String filePath = "brands.txt"; // Dosya adını ve yolunu kendi dosyanıza uygun şekilde değiştirin
        List<String> fileLines = readLines(filePath);

        System.out.println("Dosya İçeriği:");
        for (String line : fileLines) {
            System.out.println(line);
        }
    }
}
