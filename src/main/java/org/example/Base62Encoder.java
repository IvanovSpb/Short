package org.example;

public class Base62Encoder {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    public String encode(long value) {
        if (value == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.insert(0, ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.toString();
    }

    public long decode(String encoded) {
        long value = 0;
        long power = 1;
        for (int i = encoded.length() - 1; i >= 0; i--) {
            int digit = ALPHABET.indexOf(encoded.charAt(i));
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + encoded);
            }
            value += digit * power;
            power *= BASE;
        }
        return value;
    }
}