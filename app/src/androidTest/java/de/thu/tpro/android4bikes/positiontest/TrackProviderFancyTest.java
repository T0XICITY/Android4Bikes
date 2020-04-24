package de.thu.tpro.android4bikes.positiontest;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class TrackProviderFancyTest {
    @Test
    public void getDummyTrack() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        Track track = TrackProvider.getDummyTrack();
        assertNotNull(track);
        assertNotNull(track.getStartPosition());
    }
}