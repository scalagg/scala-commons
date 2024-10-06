package com.newproject.helper.spigot.sidebar.util;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomString {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private RandomString() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
