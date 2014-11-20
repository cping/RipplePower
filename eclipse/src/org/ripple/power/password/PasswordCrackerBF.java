package org.ripple.power.password;

import java.util.Arrays;

public class PasswordCrackerBF {

    private char[] charSet; 
    private char[] currentPass; 
    
    public void init(char[] charS, String from) {
        charSet = charS;
        currentPass = from.toCharArray();
    }

    public String toString() {
        return String.valueOf(currentPass);
    }
    
    public String next(){
        nextGuess();
    	return String.valueOf(currentPass);
    }
    
    public void nextGuess() {
        int index = currentPass.length - 1;
        for(;index >= 0;) {
            if (currentPass[index] == charSet[charSet.length-1]) { 
                if (index == 0) {
                    currentPass = new char[currentPass.length]; 
                    Arrays.fill(currentPass, charSet[0]); 
                    break;
                }
                else {
                    currentPass[index] = charSet[0];
                    index--; 
                }
            }
            else { 
                int i;
                for(i = 0; i < charSet.length; i++){ 
                    if(currentPass[index] == charSet[i]){
                        currentPass[index] = charSet[i+1];
                        break;
                    }
                }
                break;
            }
        }
    }
    
}
