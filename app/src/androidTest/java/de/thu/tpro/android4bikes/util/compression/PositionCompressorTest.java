package de.thu.tpro.android4bikes.util.compression;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;

import static org.junit.Assert.assertEquals;

public class PositionCompressorTest {

    @Test
    public void compressAndDecompressPositions() {
        List<Position> finegrainedpositions = new ArrayList<>();
        finegrainedpositions.add(new Position(9.997507, 48.408880));
        finegrainedpositions.add(new Position(9.997509, 48.408887));

        PositionCompressor positionCompressor = new PositionCompressor();
        byte[] compressedPositionList = positionCompressor.compressPositions(finegrainedpositions);

        List<Position> decompressedPositionList = positionCompressor.decompressPositions(compressedPositionList);
        assertEquals(finegrainedpositions, decompressedPositionList);
    }
}