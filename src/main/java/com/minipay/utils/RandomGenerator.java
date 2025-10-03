package com.minipay.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
public class RandomGenerator {

    private RandomGenerator() {
        throw new UnsupportedOperationException("Utility class, cannot be instantiated");
    }
    private static final SecureRandom rand = new SecureRandom();

    private static long generateRandomDigits(int noOfDigits) {

        rand.setSeed(System.currentTimeMillis());
        long random = Math.abs(rand.nextLong());

        long power = (long) Math.pow(10, (double) noOfDigits - 1);
        if (random < power)
            generateRandomDigits(noOfDigits);
        String strRand = Long.toString(random);
        if (strRand.length() > noOfDigits) {
            random = Long.parseLong(strRand.substring(0, noOfDigits));
        }
        return random;
    }


    public static String generateWebhookSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateMerchantIdNumber() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
        String formattedDate = dateTime.format(formatter);
        int randomNum = 10000 + rand.nextInt(90000);
        return "MER" + formattedDate + randomNum;
    }

    /**
     * Generates a string of digits of the specified length
     *
     * @param noOfDigits length of string generated
     * @return string of digits with length specified
     */
    public static synchronized String generateRandomNumber(int noOfDigits) {
        long number = generateRandomDigits(noOfDigits);
        return Long.toString(number);
    }
}