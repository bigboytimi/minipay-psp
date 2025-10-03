package com.minipay.utils;


import java.security.SecureRandom;

public class SecurityUtil {

    private SecurityUtil() {
    }

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;':\",.<>/?";

    public static boolean isComplex(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (SPECIAL_CHARS.indexOf(ch) >= 0) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String generatePassword(int minLength, int maxLength, boolean includeSpecialChars) {
        int passwordLength = RANDOM.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder password = new StringBuilder();

        password.append(CHARS.charAt(RANDOM.nextInt(26)));


        password.append(CHARS.charAt(26 + RANDOM.nextInt(26)));

        password.append(CHARS.charAt(52 + RANDOM.nextInt(10)));

        if (includeSpecialChars) {
            password.append(CHARS.charAt(62 + RANDOM.nextInt(8)));
        }

        while (password.length() < passwordLength) {
            int index = RANDOM.nextInt(includeSpecialChars ? CHARS.length() : 62);
            password.append(CHARS.charAt(index));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

}
