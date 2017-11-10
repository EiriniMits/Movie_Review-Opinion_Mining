/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package KNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * @author eirinimitsopoulou
 */

public class Document implements Comparable<Document> {
	

	private final HashMap<String, Integer> Freqcatalog;
	private final String filename;
	

	//It will read the file and pre-process it.
	public Document(String filename) {
		this.filename = filename;
		Freqcatalog = new HashMap<>();	
		createcatalog();
	}
	
	/**	 
	 * Reads every txt file and converts every word to lower case.
	 * Every character that is not a letter or a digit is removed.
	 * Common words is removed.
         * Creates a catalog of word frequencies
	 */
	private void createcatalog() {
            ArrayList<String> commonWords;
		try {
			Scanner in = new Scanner(new File(filename));
			//System.out.println("Reading file: " + filename + " and preprocessing");
			commonWords = getCommonWordsList("common_words.txt");
			while (in.hasNext()) {
				String nextWord = in.next();
				
				String filteredWord = nextWord.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
				filteredWord = sanitizeContents(filteredWord, commonWords);
				if (!(filteredWord.equalsIgnoreCase(""))) {
					if (Freqcatalog.containsKey(filteredWord)) {
						int oldCount = Freqcatalog.get(filteredWord);
						Freqcatalog.put(filteredWord, ++oldCount);
					} else {
						Freqcatalog.put(filteredWord, 1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
        
	// Get array list of words from the common_words.txt
        private static ArrayList<String> getCommonWordsList(String filename) {

		ArrayList<String> listOfCommonWords = new ArrayList<>();
		try {
			InputStream inputStream = Document.class.getClassLoader().getResourceAsStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			for (String word; (word = br.readLine()) != null;) {
				listOfCommonWords.add(word);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return listOfCommonWords;
	}

	// Remove  common  words to decrease word map size
	private static String sanitizeContents(String contents, ArrayList<String> wordsToRemove) {

		String wordArray[] = contents.split("\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(wordArray));
		String sanitizedContents = "";
		for (int x = 0; x < wordsToRemove.size(); x++) {
			String word = wordsToRemove.get(x);
			if (wordList.contains(word)) {
				wordList.removeAll(Collections.singleton(word));
			}
		}

		for (int x = 0; x < wordList.size(); x++) {
			sanitizedContents += wordList.get(x) + " ";
		}

		return sanitizedContents;
	}

        //return the frequency for a given word
	public double getWordFrequency(String word) {
		if (Freqcatalog.containsKey(word)) {
			return Freqcatalog.get(word);
		} else {
			return 0;
		}
	}
	

	 //return a set of all words in this document
	public Set<String> getTermList() {
		return Freqcatalog.keySet();
	}

	@Override
	public int compareTo(Document other) {
		return filename.compareTo(other.getFileName());
	}


	private String getFileName() {
		return filename;
	}
	

	public String toString() {
		return filename;
	}
}
