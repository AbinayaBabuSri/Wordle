package com.wordlegame.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class WordleTest {

	private static String filename;

	private List<String> wordsFromFile;
	private Scanner scanner;
	String redColor = "\033[0;31m";
	String greenColor = "\033[0;32m";
	String yellowColor = "\033[0;33m";
	String resetColor = "\033[0m";

	@BeforeEach
	public void setUp() {
		filename = "src/test/resources/WordsTest.txt";
		wordsFromFile = Arrays.asList("APPLE", "WATER");
		scanner = Mockito.mock(Scanner.class);

	}

	@Test
	public void testGetWordsFromFile() throws Exception {
		Field field = Wordle.class.getDeclaredField("filename");
		field.setAccessible(true);
		field.set(null, filename);
		Method method = Wordle.class.getDeclaredMethod("getWordsFromFile");
		method.setAccessible(true);
		List<String> words = (List<String>) method.invoke(null);
		assertNotNull(words);
		assertFalse(words.isEmpty());

		field.set(null, "src/test/resources/EmptyFile.txt");

		List<String> words1 = (List<String>) method.invoke(null);
		assertTrue(words1.isEmpty());
	}

	@Test
	public void testStartGameMethod() throws Exception {
		Method method = Wordle.class.getDeclaredMethod("startGame", Scanner.class, boolean.class, List.class);
		method.setAccessible(true);

		Mockito.when(scanner.nextLine()).thenReturn("OTTER", "OTTER", "OTTER", "OTTER", "PAPER", "N");
		assertFalse((boolean) method.invoke(null, scanner, true, wordsFromFile));

		Mockito.when(scanner.nextLine()).thenReturn("OTTER", "OTTER", "OTTER", "OTTER", "PAPER", "KOL", "Y", "OTTER",
				"OTTER", "OTTER", "OTTER", "APPLE", "N");
		assertTrue((boolean) method.invoke(null, scanner, true, wordsFromFile));
	}

	@Test
	public void testLengthOfGuessedWord() throws Exception {

		Method method = Wordle.class.getDeclaredMethod("getGuessedWord", Scanner.class);
		method.setAccessible(true);

		Mockito.when(scanner.nextLine()).thenReturn(" ", "", "123e3", "waters", "WATER");
		String guessedWord = (String) method.invoke(null, scanner);
		assertEquals("WATER", guessedWord);
	}

	@Test
	public void testGetFeedbackMethod() throws Exception {

		Method method = Wordle.class.getDeclaredMethod("getFeedback", String.class, String.class);
		method.setAccessible(true);

		String feedback = (String) method.invoke(null, "WATER", "PAPRE");

		assertTrue(feedback.contains(redColor + "P" + resetColor));
		assertTrue(feedback.contains(greenColor + "A" + resetColor));
		assertTrue(feedback.contains(yellowColor + "E" + resetColor));
		assertTrue(feedback.contains(yellowColor + "R" + resetColor));

		String feedback1 = (String) method.invoke(null, "WATER", "OTTER");

		assertEquals(feedback1, redColor + "O" + resetColor + redColor + "T" + resetColor + greenColor + "T"
				+ resetColor + greenColor + "E" + resetColor + greenColor + "R" + resetColor);

	}

	@Test
	public void testMainMethod() throws Exception {

		Field field = Wordle.class.getDeclaredField("filename");
		field.setAccessible(true);
		field.set(null, filename);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));

		System.setIn(new java.io.ByteArrayInputStream("PAPER\nN\n".getBytes()));

		Wordle.main(new String[] {});

		String output = outputStream.toString();

		assertTrue(output.contains("Welcome to Wordle!!!"), "Expected welcome message.");
		assertTrue(output.contains("Congratulations!!!"), "Expected congratulatory message.");
		assertTrue(output.contains("Do you want to try the game again?"), "Expected restart message.");

		System.setOut(originalOut);

		output = testWordFile(outputStream, field, "src/test/resources/EmptyFile.txt");
		assertTrue(output.contains("Word list is empty. Please check the file and then try again"),
				"Expected check the file and try again message.");
		System.setOut(originalOut);

		output = testWordFile(outputStream, field, "");
		assertTrue(output.contains("Word list is empty. Please check the file and then try again"),
				"Expected check the file and try again message.");
		System.setOut(originalOut);

		output = testWordFile(outputStream, field, null);
		assertTrue(output.contains("Word list is empty. Please check the file and then try again"),
				"Expected check the file and try again message.");

		System.setOut(originalOut);
	}

	private String testWordFile(ByteArrayOutputStream outputStream, Field field, Object argValue) throws Exception {

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
		field.set(null, argValue);
		Wordle.main(new String[] {});
		return outputStream.toString();
	}

	@Test
	public void testException() throws Exception {

		Field field = Wordle.class.getDeclaredField("filename");
		field.setAccessible(true);
		field.set(null, "src/test/resources/EmptyFileTest.txt");
		Exception exception = assertThrows(Exception.class, () -> {
			Wordle.main(new String[] {});
		});

		String expectedMessage = "Error occurred while loading the file:";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

}
