package org.ripple.power.password;

import java.util.Arrays;

public class PasswordCrackerBFArray {


	private boolean completed = false;
	private String[] charSet;
	private String[] currentPass;
	private int count = 0;
	private int skip = 1;

	public void init(String[] charS, String[] from) {
		charSet = charS;
		currentPass = from;
	}

	public String toString() {
		StringBuilder sbr=new StringBuilder();
		for(String s:currentPass){
			sbr.append(s);
		}
		return sbr.toString();
	}

	public String next() {
		for (int i = 0; i < skip; i++) {
			nextGuess();
		}
		StringBuilder sbr=new StringBuilder();
		for(String s:currentPass){
			sbr.append(s);
		}
		return sbr.toString();
	}

	public void nextGuess() {
		int index = currentPass.length - 1;
		for (; index >= 0;) {
			if (currentPass[index] == charSet[charSet.length - 1]) {
				if (index == 0) {
					currentPass = new String[currentPass.length];
					Arrays.fill(currentPass, charSet[0]);
					break;
				} else {
					currentPass[index] = charSet[0];
					index--;
				}
			} else {
				int i;
				for (i = 0; i < charSet.length; i++) {
					if (currentPass[index] == charSet[i]) {
						currentPass[index] = charSet[i + 1];
						break;
					}
				}
				break;
			}

		}
		if (currentPass[index] == charSet[charSet.length - 1]) {
			count++;
		}
		if (count >= (currentPass.length * charSet.length) * (currentPass.length * charSet.length)
				* (currentPass.length * charSet.length) * (currentPass.length * charSet.length)) {
			completed = true;
		}
	}

	public void setSkin(int s) {
		this.skip = s;
	}

	public boolean isCompleted() {
		return completed;
	}


}
