package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);
        Scanner toPrintOut = new Scanner(file);
        Scanner reader = new Scanner(System.in);

        String line;

        int countWord = 0;
        int sentenceCount = 0;
        int characterCount = 0;
        int paragraphCount = 1;
        int whitespaceCount = 0;
        int averageWordsInSentence = 0;
        int syllablesCount = 0;
        int polysyllables = 0;

        // Reading line by line from the
        // file until a null is returned
        try (scanner) {
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                if (line.equals("")) {
                    paragraphCount++;
                } else {
                    characterCount += line.replaceAll("\\s+", "").length();

                    // \\s+ is the space delimiter in java
                    String[] wordList = line.split("\\s+");
                    for (String word : wordList) {
                        int syllablesInOneWord = countWithRegex(word);
                        syllablesCount += syllablesInOneWord;
                        if (syllablesInOneWord > 2) {
                            polysyllables++;
                        }
                    }

                    countWord += wordList.length;
                    whitespaceCount += countWord - 1;

                    // [!?.:]+ is the sentence delimiter in java
                    String[] sentenceList = line.split("[!?.:]+");

                    sentenceCount += sentenceList.length;
                    averageWordsInSentence = averageWordInSent(sentenceList);
                }
            }
        }

        // STAGE 3/4
        double automatedReadabilityIndex = 0;
        double firstDivision = (double) characterCount / countWord;
        double secondDivision = (double) countWord / sentenceCount;
        automatedReadabilityIndex = Math.round((4.71 * firstDivision + 0.5 * secondDivision - 21.43) * 100.0 ) / 100.0;

        // STAGE 4/4
        //Flesch–Kincaid readability tests
        double fleschKincaidReadabilityTests = 0;
        double firstDivisionFleschKincaidReadabilityTests = (double) countWord / sentenceCount;
        double secondDivisionfleschKincaidReadabilityTests = (double) syllablesCount / countWord;
        fleschKincaidReadabilityTests = Math.round((0.39 * firstDivisionFleschKincaidReadabilityTests + 11.8 * secondDivisionfleschKincaidReadabilityTests - 15.59) * 100.0 ) / 100.0;

        //SMOG index
        double smogIndex = 0;
        double radix = Math.sqrt(polysyllables * ((double) 30 / sentenceCount));
        smogIndex = Math.round((radix + 3.1291) * 100.0) / 100.0;

        //Coleman–Liau index
        double colemanLiauIndex = 0;
        double l = (double) characterCount / ((double) countWord / 100);
        double s = (double) sentenceCount / ((double) countWord / 100);
        colemanLiauIndex = Math.round((0.0588 * l - 0.296 * s - 15.8) * 100.0) / 100.0;

        //Printing out results
        String answer = "";
        System.out.println("The text is:\n");
        try (toPrintOut) {
            while (toPrintOut.hasNext()) {
                System.out.println(toPrintOut.nextLine());
            }
        }

        System.out.println("\nWords: " + countWord + "\nSentences: " + sentenceCount +
            "\nCharacters: " + characterCount + "\nSyllables: " + syllablesCount +
            "\nPolysyllables: " + polysyllables +
            "\nEnter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        try (reader) {
            answer = reader.nextLine();
        }

        //Printing out Indexes
        int ariAgeForReading = ageForReading(automatedReadabilityIndex);
        int fkAgeForReading = ageForReading(fleschKincaidReadabilityTests);
        int smogAgeForReading = ageForReading(smogIndex);
        int clAgeForReading = ageForReading(colemanLiauIndex);

        double averageAgeForReading = (double) (ariAgeForReading + fkAgeForReading + smogAgeForReading + clAgeForReading) / 4;
        switch (answer) {
            case "ARI":
                System.out.println("Automated Readability Index: " + automatedReadabilityIndex +
                    " (about " + ariAgeForReading + " year olds).");
                break;
            case "FK":
                System.out.println("Flesch–Kincaid readability tests: " + fleschKincaidReadabilityTests +
                    " (about " + fkAgeForReading + " year olds).");
                break;
            case "SMOG":
                System.out.println("Simple Measure of Gobbledygook: " + smogIndex +
                    " (about " + smogAgeForReading + " year olds).");
                break;
            case "CL":
                System.out.println("Coleman–Liau index: " + colemanLiauIndex +
                    " (about " + clAgeForReading + " year olds).");
                break;
            case "all":
                System.out.println("\nAutomated Readability Index: " + automatedReadabilityIndex +
                    " (about " + ariAgeForReading + " year olds).\n" +
                    "Flesch–Kincaid readability tests: " + fleschKincaidReadabilityTests +
                    " (about " + fkAgeForReading + " year olds).\n" +
                    "Simple Measure of Gobbledygook: " + smogIndex +
                    " (about " + smogAgeForReading + " year olds).\n" +
                    "Coleman–Liau index: " + colemanLiauIndex +
                    " (about " + clAgeForReading + " year olds).\n" + "\n" +
                    "This text should be understood in average by " + averageAgeForReading + " year olds.");
        }
    }

    public static int averageWordInSent(String[] sentenceList) {
        int[] wordsQuantity = new int[sentenceList.length];
        int sum = 0;
        double average = 0.0;

        for (int i = 0; i < sentenceList.length; i++) {
            String[] wordList = sentenceList[i].split("\\s+");

            wordsQuantity[i] = wordList.length;
        }

        for (int i = 0; i < wordsQuantity.length; i++) {
            sum = sum + wordsQuantity[i];
        }
        average = (double) sum / wordsQuantity.length;
        return (int) Math.round(average);
    }

    public static int ageForReading(double ReadabilityIndex) {

        int[] ages = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 24, 24, 24, 24, 24, 24, 24};
        return ages[((int) Math.ceil(ReadabilityIndex) - 1)];

    }

    private static int countWithRegex(String word) {
        String i = "(?i)[aiouy][aeiouy]*|e[aeiouy]*(?!d?\\b)";
        Matcher m = Pattern.compile(i).matcher(word);
        int count = 0;

        while (m.find()) {
            count++;
        }

        // return at least 1
        return Math.max(count, 1);
    }
}
