package net.fina.common.shared;

import java.io.Serializable;

public class FinaPasswordPolicy implements Serializable {
    private int minLength = 1;
    private boolean letters;
    private boolean numbers;
    private boolean upperCase;
    private boolean specialCharacters;

    public FinaPasswordPolicy() {
    }

    public FinaPasswordPolicy(String minLength, String letters, String numbers, String upperCase, String specialCharacters) {
        this.minLength = minLength != null && !minLength.trim().isEmpty() ? Integer.parseInt(minLength) : -1;
        this.letters = letters != null && !letters.isEmpty() && (Integer.parseInt(letters) == 1);
        this.numbers = numbers != null && !numbers.isEmpty() && (Integer.parseInt(numbers) == 1);
        this.upperCase = upperCase != null && !upperCase.isEmpty() && (Integer.parseInt(upperCase) == 1);
        this.specialCharacters = specialCharacters != null && !specialCharacters.isEmpty() && (Integer.parseInt(specialCharacters) == 1);
    }


    public int getMinLength() {
        return minLength;
    }

    public boolean isLetters() {
        return letters;
    }

    public boolean isNumbers() {
        return numbers;
    }

    public boolean isUpperCase() {
        return upperCase;
    }

    public boolean isSpecialCharacters() {
        return specialCharacters;
    }

    public void setLetters(boolean letters) {
        this.letters = letters;
    }

    public void setNumbers(boolean numbers) {
        this.numbers = numbers;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    public void setSpecialCharacters(boolean specialCharacters) {
        this.specialCharacters = specialCharacters;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMinLength(String minLength) {
        if (minLength != null && !minLength.trim().isEmpty()) {
            setMinLength(Integer.parseInt(minLength));
        }
    }
}
