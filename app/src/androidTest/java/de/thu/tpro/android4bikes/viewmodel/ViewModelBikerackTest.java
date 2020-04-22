package de.thu.tpro.android4bikes.viewmodel;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class ViewModelBikerackTest {
    @BeforeClass
    public static void setup(){
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void test(){
        assertTrue(true);
    }
}