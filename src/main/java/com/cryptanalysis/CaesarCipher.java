package com.cryptanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CaesarCipher {
    private static CaesarCipher instance = null;
    private static Set<String> commonWords;
    private String substitutionAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; // Default English alphabet

    private CaesarCipher() {
        commonWords = loadCommonWords();
    }

    public static CaesarCipher getInstance() {
        if (instance == null) {
            instance = new CaesarCipher();
        }

        return instance;
    }

    public void setSubstitutionAlphabet(String alphabet) {
        this.substitutionAlphabet = alphabet;
    }

    public String encrypt(String plaintext, int key) {
        StringBuilder encryptedText = new StringBuilder();

        for (char c : plaintext.toCharArray()) {
            int index = substitutionAlphabet.indexOf(c);
            if (index != -1) {
                int newIndex = (index + key) % substitutionAlphabet.length();
                encryptedText.append(substitutionAlphabet.charAt(newIndex));
            } else {
                encryptedText.append(c);
            }
        }

        return encryptedText.toString();
    }

    public String decrypt(String ciphertext, int key) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : ciphertext.toCharArray()) {
            int index = substitutionAlphabet.indexOf(c);
            if (index != -1) {
                int newIndex = (index - key + substitutionAlphabet.length()) % substitutionAlphabet.length();
                decryptedText.append(substitutionAlphabet.charAt(newIndex));
            } else {
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    public void bruteForceDecrypt(String ciphertext) {
        System.out.println("Brute force cryptanalysis:");
        for (int key = 1; key < substitutionAlphabet.length(); key++) {
            String decryptedText = decrypt(ciphertext, key);
            System.out.println("Key " + key + ": " + decryptedText);

            if (isMeaningful(decryptedText)) {
                System.out.println("Decryption successful with Key " + key + ": " + decryptedText);
                break;
            }
        }
    }

    private static Set<String> loadCommonWords() {
        Set<String> commonWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("common_words.txt"))) {
            while (scanner.hasNextLine()) {
                commonWords.add(scanner.nextLine().trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Common words file not found - " + "common_words.txt");
        }

        return commonWords;
    }

    private boolean isMeaningful(String text) {
        int wordCount = 0;
        for (String word : commonWords) {
            if (text.contains(word)) {
                wordCount++;
            }
        }
        boolean correctStructure = text.matches("[A-Z].*[.!?]");

        return wordCount >= 5 && correctStructure;
    }

    public void statisticalDecrypt(String ciphertext, String statisticsText) {
        Map<Character, Integer> ciphertextFrequency = calculateCharacterFrequency(ciphertext);
        Map<Character, Integer> statisticsFrequency = calculateCharacterFrequency(statisticsText);

        // Assuming most frequent characters map to each other
        char mostFrequentStatisticsChar = findMostFrequentCharacter(statisticsFrequency);
        char mostFrequentCiphertextChar = findMostFrequentCharacter(ciphertextFrequency);

        int key = (mostFrequentCiphertextChar - mostFrequentStatisticsChar + substitutionAlphabet.length()) % substitutionAlphabet.length();

        // Try decrypting with this key
        String decryptedText = decrypt(ciphertext, key);
        if (!isMeaningful(decryptedText)) {
            // If the result is not meaningful, iterate through other possible keys
            for (int possibleKey = 0; possibleKey < substitutionAlphabet.length(); possibleKey++) {
                decryptedText = decrypt(ciphertext, possibleKey);
                if (isMeaningful(decryptedText)) {
                    key = possibleKey;
                    break;
                }
            }
        }

        printDecryptionResult(key, decryptedText);
    }

    private void printDecryptionResult(int key, String decryptedText) {
        System.out.println("Statistical cryptanalysis:");
        System.out.println("Key: " + key);
        System.out.println("Decrypted text: " + decryptedText);
    }

    private Map<Character, Integer> calculateCharacterFrequency(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        return frequencyMap;
    }

    private char findMostFrequentCharacter(Map<Character, Integer> frequencyMap) {
        char mostFrequentChar = ' ';
        int maxFrequency = 0;

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                mostFrequentChar = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }

        return mostFrequentChar;
    }
}
