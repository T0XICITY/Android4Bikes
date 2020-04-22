package de.thu.tpro.android4bikes.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class UUIDGeneratorTest {

    /**
     * tests how random the generated UUIDs are
     */
    @Test
    public void generateUUID() {
        List<String> list_uuids = new ArrayList<>();
        String uuid = null;
        for (int i = 0; i < 20000; ++i) {
            uuid = UUIDGenerator.generateUUID();
            assertFalse(list_uuids.contains(uuid));
            list_uuids.add(uuid);
        }
    }
}