package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

import static org.junit.jupiter.api.Assertions.*;

@ExcludeFromJacocoGeneratedReport
public class TestExcercise {
    private Excercise testExcercise;
    
    @BeforeEach
    void runBefore() {
        testExcercise = new Excercise("Bench Press", 1, 10, 100.0, 0);
    }

    @Test
    void testConstructor() {
        assertEquals("Bench Press", testExcercise.getName());
        assertEquals(1, testExcercise.getSets());
        assertEquals(10, testExcercise.getReps());
        assertEquals(100.0, testExcercise.getWeight());
        assertEquals(133.37, testExcercise.getOrm());
        assertEquals(1000.0, testExcercise.getVolume()); 
        assertTrue(testExcercise.getOrmIsEstimated());
    }

    @Test
    void testConstructorWithProvidedOrm() {
        testExcercise = new Excercise("Squat", 3, 5, 225.0, 250.0);
        assertEquals("Squat", testExcercise.getName());
        assertEquals(3, testExcercise.getSets());
        assertEquals(5, testExcercise.getReps());
        assertEquals(225.0, testExcercise.getWeight());
        assertEquals(250.0, testExcercise.getOrm());
        assertEquals(3375.0, testExcercise.getVolume()); 
        assertFalse(testExcercise.getOrmIsEstimated());
    }

    @Test
    void testCalculateOrm() {
        assertTrue(testExcercise.getOrmIsEstimated());
        assertEquals(133.37, testExcercise.getOrm(), 0.01); 
        testExcercise = new Excercise("Bench Press", 1, 10, 100.0, 160.0);
        assertFalse(testExcercise.getOrmIsEstimated());
        assertEquals(160.0, testExcercise.getOrm());
    }

    @Test
    void testCalculateVolume() {
        assertEquals(1000.0, testExcercise.getVolume());
        testExcercise = new Excercise("Bench Press", 0, 10, 100.0, 160.0);
        assertEquals(0, testExcercise.getVolume());
        double volume = testExcercise.calculateVolume(3, 8, 135.0);
        assertEquals(3240.0, volume);
    }
}
