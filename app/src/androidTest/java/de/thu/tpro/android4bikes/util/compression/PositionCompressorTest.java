package de.thu.tpro.android4bikes.util.compression;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;

import static org.junit.Assert.assertEquals;

public class PositionCompressorTest {

    @Test
    public void compressAndDecompressPositions() {
        List<Position> fineGrainedPositions = new ArrayList<>();
        for(int i =0; i<50000; ++i) {
            fineGrainedPositions.add(new Position(9.997507 + i, 48.408880 + i));
        }

        PositionCompressor positionCompressor = new PositionCompressor();
        byte[] compressedPositionList = positionCompressor.compressPositions(fineGrainedPositions);

        List<Position> decompressedPositionList = positionCompressor.decompressPositions(compressedPositionList);
        assertEquals(fineGrainedPositions, decompressedPositionList);
    }
}