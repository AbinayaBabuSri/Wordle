package com.wordlegame.app;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Wordle {

	private static String filename = "../../../../resources/Words.txt";
	private static int lengthOfWord = 5;
	private static int noOfGuesses = 5;
	private static String redColor = "\033[0;31m";
	private static String greenColor = "\033[0;32m";
	private static String yellowColor = "\033[0;33m";
	private static String resetColor = "\033[0m";

	public static void main(String[] args) throws Exception {
		try {
			List<String> wordsFromFile = getWordsFromFile();

			if (wordsFromFile.isEmpty()) {
				System.out.println("Word list is empty. Please check the file and then try again");
				return;
			}

			Scanner scanner = new Scanner(System.in);

			boolean isGameOngoing = true;

			System.out.println("Welcome to Wordle!!!");
	        System.out.println("Here's how to play:");
	        System.out.println("1. You will have "+noOfGuesses+ " attempts to guess a "+lengthOfWord+"-letter word.");
	        System.out.println("2. After each guess, you'll receive feedback:");
	        System.out.println("   - Letters in the correct position will be shown in Green.");
	        System.out.println("   - Letters that are correct but in the wrong position will be shown in Yellow.");
	        System.out.println("   - Letters that are not in the word will be shown in Red.");
	        System.out.println("3. Good luck!");
	        
			while (isGameOngoing) {
				isGameOngoing = startGame(scanner, isGameOngoing, wordsFromFile);
			}

			scanner.close();
			return;
		} catch (Exception e) {
			System.out.println("Exception occurred while loading the game : " + e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	private static boolean startGame(Scanner scanner, boolean isGameOngoing, List<String> wordsFromFile) {
		String correctWord = wordsFromFile.get(new Random().nextInt(wordsFromFile.size())).toUpperCase();

		for (int i = 1; i <= noOfGuesses; i++) {
			System.out.println("Guess No - " + i + " : ");
			String guessedWord = getGuessedWord(scanner);

			if (correctWord.equals(guessedWord)) {

				System.out.println("Congratulations!!! You have guessed the word correctly");
				System.out
						.println("Do you want to try the game again? (Enter 'Y' to continue or 'N' to exit the game) ");
				return restartGame(scanner, isGameOngoing);

			} else {

				String result = getFeedback(correctWord, guessedWord);
				System.out.println("You guessed : " + result);
				if (i == noOfGuesses) {
					System.out.println("You lost the game and exceeded maximum guesses.");
					System.out.println("The correct word is " + correctWord);
					System.out.println(
							"Do you want to try the game again? (Enter 'Y' to continue or 'N' to exit the game) ");
					return restartGame(scanner, isGameOngoing);
				}
			}

		}
		return isGameOngoing;
	}

	private static boolean restartGame(Scanner scanner, boolean isGameOngoing) {

		String continueGame = scanner.nextLine().trim().toUpperCase();
		if (continueGame.equalsIgnoreCase("N") || continueGame.equalsIgnoreCase("Y")) {
			if (continueGame.equalsIgnoreCase("N")) {
				isGameOngoing = false;
				System.out.println("Thanks for playing the game. See you again!!!");
			} else {
				isGameOngoing = true;
			}
		} else {
			System.out.println("Enter 'Y' to continue or 'N' to exit the game ");
			restartGame(scanner, isGameOngoing);
		}

		return isGameOngoing;

	}

	private static String getFeedback(String correctWord, String guessedWord) {
		List<String> guessedWordList = new ArrayList<>();
		List<String> indexOfCharAtDiffPos = new ArrayList<>();
		StringBuilder feedback = new StringBuilder();

		convertWordToListOfStr(guessedWord, guessedWordList);

		for (int i = 0; i < lengthOfWord; i++) {
			String charVal = guessedWordList.get(i);
			if (guessedWord.charAt(i) == correctWord.charAt(i)) {
				guessedWordList.set(i, greenColor + charVal + resetColor + "&g");
			} else {
				indexOfCharAtDiffPos.add(String.valueOf(correctWord.charAt(i)));
			}
		}

		for (int k = 0; k < lengthOfWord; k++) {
			if (!guessedWordList.get(k).contains("&g")) {
				if (indexOfCharAtDiffPos.contains(guessedWordList.get(k))) {
					indexOfCharAtDiffPos.remove(guessedWordList.get(k).toString());
					guessedWordList.set(k, yellowColor + guessedWordList.get(k) + resetColor);
				} else {
					guessedWordList.set(k, redColor + guessedWordList.get(k) + resetColor);
				}
			}
		}

		for (String str : guessedWordList) {
			feedback.append(str);
		}

		return feedback.toString().replaceAll("&g", "");
	}

	private static void convertWordToListOfStr(String guessedWord, List<String> guessedWordList) {
		for (char c : guessedWord.toCharArray()) {
			guessedWordList.add(String.valueOf(c));
		}
	}

	private static String getGuessedWord(Scanner scanner) {
		String guessedWord;

		do {
			guessedWord = scanner.nextLine().trim().toUpperCase();
			if (guessedWord.isBlank() || guessedWord.length() != lengthOfWord || !guessedWord.matches("[a-zA-Z]*")) {
				System.out
						.println("Entered word should be a " + lengthOfWord + " letter word only. Please enter again ");
				continue;
			}
		} while (guessedWord.isBlank() || guessedWord.length() != lengthOfWord || !guessedWord.matches("[a-zA-Z]*"));

		return guessedWord;
	}

	private static List<String> getWordsFromFile() throws Exception {

		try {

			if (filename != null && filename != "") {

				return Files.readAllLines(Paths.get(filename));
			}
		} catch (Exception e) {
			throw new Exception("Error occurred while loading the file:" + e.getMessage());
		}
		return Arrays.asList();
	}
}
