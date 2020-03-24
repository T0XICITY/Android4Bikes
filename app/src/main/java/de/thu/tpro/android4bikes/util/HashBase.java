package de.thu.tpro.android4bikes.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HashBase {
    /**
     * calculates the sha256-Hash of a given string
     *
     * @param input string
     * @return sha256 hash value in hexadecimal representation as a string
     */
    public static String calcSha256HashAsHexString(String input) {
        //compare: https://www.baeldung.com/sha-256-hashing-java
        String sha256hex = Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
        return sha256hex;
    }
}
