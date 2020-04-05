package de.thu.tpro.android4bikes.util.compression;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;

public class PositionCompressorTest {

    @Test
    public void compressPositions() {
        List<Position> finegrainedpositions = new ArrayList<>();
        finegrainedpositions.add(new Position(9.997507, 48.408880));
        finegrainedpositions.add(new Position(9.997509, 48.408887));

        PositionCompressor positionCompressor = new PositionCompressor();
        positionCompressor.compressPositions(finegrainedpositions);
    }
}