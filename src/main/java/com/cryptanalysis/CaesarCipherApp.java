package com.cryptanalysis;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CaesarCipherApp {
    private static final String ENCRYPTION_INFO_FILE = "encryption_info.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Caesar Cipher cryptanalysis tool!");
        System.out.println("Choose a mode:");
        System.out.println("1: Encryption/Decryption");
        System.out.println("2: Brute force cryptanalysis");
        System.out.println("3: Statistical cryptanalysis");

        int mode = scanner.nextInt();

        switch (mode) {
            case 1:
                handleEncryptionDecryption(scanner);
                break;

            case 2:
                handleBruteForceCryptanalysis(scanner);
                break;

            case 3:
                handleStatisticalCryptanalysis(scanner);
                break;

            default:
                System.out.println("Invalid mode. Please select a valid mode (1, 2, or 3).");
        }

        scanner.close();
    }

    private static void handleEncryptionDecryption(Scanner scanner) {
        int mode;
        String inputText;
        String resultText;
        int key;

        System.out.println("Do you want to set a custom substitution alphabet? (yes/no)");
        scanner.nextLine();
        String useCustomAlphabet = scanner.nextLine().toLowerCase();

        CaesarCipher cipher = CaesarCipher.getInstance();

        if (useCustomAlphabet.equals("yes")) {
            System.out.println("Enter the custom substitution alphabet:");
            String customAlphabet = scanner.nextLine();
            cipher.setSubstitutionAlphabet(customAlphabet);
        }

        System.out.println("Choose a mode:");
        System.out.println("1: Encryption");
        System.out.println("2: Decryption");
        mode = Integer.parseInt(scanner.nextLine());

        if (mode == 1) {
            System.out.println("Enter the text to encrypt:");
            inputText = scanner.nextLine();
            key = getInputKey(scanner);
            resultText = cipher.encrypt(inputText, key);
            System.out.println("Encrypted text: " + resultText);
            saveEncryptionInfo(inputText, resultText, key);
        } else if (mode == 2) {
            System.out.println("Enter the text to decrypt:");
            inputText = scanner.nextLine();
            key = getInputKey(scanner);
            resultText = cipher.decrypt(inputText, key);
            System.out.println("Decrypted text: " + resultText);
        } else {
            System.out.println("Invalid mode. Please select 1 for encryption or 2 for decryption.");
        }
    }

    private static int getInputKey(Scanner scanner) {
        int key = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("Enter the cryptographic key (an integer):");
            try {
                key = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer key.");
                scanner.nextLine();
            }
        }

        return key;
    }

    private static void saveEncryptionInfo(String originalText, String resultText, int key) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENCRYPTION_INFO_FILE, true))) {
            writer.println("Action: " + "Encrypted");
            writer.println("Original Text: " + originalText);
            writer.println("Result Text: " + resultText);
            writer.println("Key: " + key);
        } catch (IOException e) {
            System.err.println("Error: Could not save encryption info to file.");
        }
    }

    private static void handleBruteForceCryptanalysis(Scanner scanner) {
        System.out.println("Enter the ciphertext to analyze:");
        scanner.nextLine();
        String ciphertext = scanner.nextLine();

        CaesarCipher cipher = CaesarCipher.getInstance();
        cipher.bruteForceDecrypt(ciphertext);
    }

    private static void handleStatisticalCryptanalysis(Scanner scanner) {
        System.out.println("Enter the path to the ciphertext file:");
        scanner.nextLine();
        String ciphertextFilePath = scanner.nextLine();

        System.out.println("Enter the path to the plaintext file for statistical analysis:");
        String plaintextFilePath = scanner.nextLine();

        String ciphertext = readFile(ciphertextFilePath);
        String statisticsText = readFile(plaintextFilePath);

        CaesarCipher cipher = CaesarCipher.getInstance();
        cipher.statisticalDecrypt(ciphertext, statisticsText);
    }

    private static String readFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            StringBuilder content = new StringBuilder();

            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
            }

            scanner.close();
            return content.toString();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + filePath);

            return "";
        }
    }
}