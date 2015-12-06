package org.ripple.power.password;

public class PasswordGenerator {

	private StringBuffer temp = new StringBuffer();
	private long skip = 1;
	private long wordCount = 0;
	private int wordLenght;
	private int maxWordLength;
	private char[] alphabet;
	final long MAX_WORDS;
	final int RADIX;

	public PasswordGenerator(int startLength, int maxWordLength, String alphabet) {
		this.wordLenght = startLength;
		this.maxWordLength = maxWordLength;
		this.alphabet = alphabet.toCharArray();
		this.RADIX = alphabet.length();
		this.MAX_WORDS = (long) Math.pow(RADIX, wordLenght);
	}

	public String generateNextWord() {
		int[] indices = convertToRadix(RADIX, wordCount, wordLenght);
		temp.delete(0, temp.length());
		for (int k = 0; k < wordLenght; k++) {
			temp.append(alphabet[indices[k]]);
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

	public long getSkip() {
		return skip;
	}

	public void setSkip(long skip) {
		this.skip = skip;
	}

}
