package de.thu.tpro.android4bikes.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class HashBaseTest {
    @Test
    public void testGetHashIdempotent() {
        Random random = new Random();
        String password = "";
        StringBuffer strb = new StringBuffer();

        for (int i = 0; i < 1000; ++i) {
            char a = (char) random.nextInt(256); //ASCII (8 Byte = 256 Mglkt.)
            strb.append(a);
        } //Zufaelliges Passwort aus 1000 Zeichen erstellen (ASCII)

        password = strb.toString();
        String hash1 = HashBase.calcSha256HashAsHexString(password);
        String hash2 = HashBase.calcSha256HashAsHexString(password);
        assertEquals(hash1, hash2);
    }

    @Test
    public void testGetHashDifferentPasswords() {
        Random random = new Random();
        String password = "";
        StringBuffer strb = new StringBuffer();

        for (int i = 0; i < 1000; ++i) {
            char a = (char) random.nextInt(256); //ASCII (8 Byte = 256 Mglkt.)
            strb.append(a);
        } //Zufaelliges Passwort aus 1000 Zeichen erstellen (ASCII)

        password = strb.toString();
        String hash1 = HashBase.calcSha256HashAsHexString(password);

        for (int i = 0; i < 100; ++i) {
            String hash2 = HashBase.calcSha256HashAsHexString(password + (char) i);
            assertNotEquals(hash1, hash2);
        }
    }
}