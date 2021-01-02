package org.ripple.power.password;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class PasswordGeneratorArray {

	private StringBuffer temp = new StringBuffer();
	private long wordCount = 0;
	private int wordLenght;
	private int maxWordLength;
	private ArrayList<String> alphabet;
	private final long MAX_WORDS;
	private boolean isUpper = false;
	private long skip = 1;
	private final int RADIX;
	private char flag = (char) -1;
	private boolean not_repeat;

	public PasswordGeneratorArray(int startLength, int maxWordLength, ArrayList<String> alphabet) {
		this.wordLenght = startLength;
		this.maxWordLength = maxWordLength;
		this.alphabet = new ArrayList<String>(alphabet);
		this.RADIX = alphabet.size();
		this.MAX_WORDS = (long) Math.pow(RADIX, wordLenght);
	}

	public long getSkip() {
		return skip;
	}

	public void setSkip(long skip) {
		this.skip = skip;
	}

	public PasswordGeneratorArray(int startLength, int maxWordLength, String filename) throws IOException {
		alphabet = new ArrayList<String>(10000);
		HashSet<String> caches = new HashSet<String>(10000);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"))) {
			String text = null;
			for (; (text = reader.readLine()) != null;) {
				String tmp = text.trim();
				if (caches.add(tmp)) {
					alphabet.add(tmp);
				}
			}
			reader.close();
		}
		this.wordLenght = startLength;
		this.maxWordLength = maxWordLength;
		this.alphabet = new ArrayList<String>(alphabet);
		this.RADIX = alphabet.size();
		this.MAX_WORDS = (long) Math.pow(RADIX, wordLenght);
	}

	public String generateNextWord() {
		int[] indices = convertToRadix(RADIX, wordCount, wordLenght);
		temp.delete(0, temp.length());
		for (int k = 0; k < wordLenght; k++) {
			String result = alphabet.get(indices[k]);
			if (isUpper) {
				result = result.substring(0, 1).toUpperCase() + result.substring(1, result.length());
			}
			if (not_repeat) {
				if (temp.indexOf(result) == -1) {
					temp.append(result);
				}
			} else {
				temp.append(result);
			}
			if (flag != -1) {
				temp.append(flag);
			}
		}
		if (flag != -1) {
			temp.delete(temp.length() - 1, temp.length());
		}
		wordCount += skip;
		if (wordCount > MAX_WORDS) {
			wordCount = 1;
			wordLenght++;
			if (wordLenght > maxWordLength) {
				return null;
			}
		}

		return temp.toString();

	}

	public long getNextCount() {
		return wordCount;
	}

	public void setNextCount(long val) {
		this.wordCount = val;
	}

	public void setWordLenght(int val) {
		this.wordLenght = val;
	}

	public int getWordLength() {
		return this.wordLenght;
	}

	private static int[] convertToRadix(int radix, long number, int wordlength) {
		int[] indices = new int[wordlength];

		for (int i = wordlength - 1; i >= 0; i--) {
			if (number > 0) {
				int rest = (int) (number % radix);
				number /= radix;
				indices[i] = rest;
			} else {
				indices[i] = 0;
			}

		}
		return indices;
	}

	public char getFlag() {
		return flag;
	}

	public void setFlag(char flag) {
		this.flag = flag;
	}

	public boolean isNot_repeat() {
		return not_repeat;
	}

	public void setNot_repeat(boolean not_repeat) {
		this.not_repeat = not_repeat;
	}

}
