package com.thelogicmaster.clearwing.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Tests for JDK 9+ Collection factory methods
 */
class CollectionFactoryTest {

    @Test
    @DisplayName("List.of() creates empty list")
    void testListOfEmpty() {
        List<String> list = List.of();
        assertNotNull(list);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("List.of(elements) creates list with elements")
    void testListOfElements() {
        List<String> list = List.of("apple", "banana", "cherry");
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("apple", list.get(0));
        assertEquals("banana", list.get(1));
        assertEquals("cherry", list.get(2));
    }

    @Test
    @DisplayName("List.of single element")
    void testListOfSingleElement() {
        List<String> list = List.of("single");
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("single", list.get(0));
    }

    @Test
    @DisplayName("List.of two elements")
    void testListOfTwoElements() {
        List<Integer> list = List.of(1, 2);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }

    @Test
    @DisplayName("List.of three elements")
    void testListOfThreeElements() {
        List<String> list = List.of("a", "b", "c");
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    @DisplayName("List.of four elements")
    void testListOfFourElements() {
        List<Integer> list = List.of(1, 2, 3, 4);
        assertNotNull(list);
        assertEquals(4, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(4), list.get(3));
    }

    @Test
    @DisplayName("List.of five elements")
    void testListOfFiveElements() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertNotNull(list);
        assertEquals(5, list.size());
        assertEquals("a", list.get(0));
        assertEquals("e", list.get(4));
    }

    @Test
    @DisplayName("List.copyOf creates copy of collection")
    void testListCopyOf() {
        List<String> original = new ArrayList<>();
        original.add("item1");
        original.add("item2");
        
        List<String> copy = List.copyOf(original);
        assertNotNull(copy);
        assertEquals(2, copy.size());
        assertEquals("item1", copy.get(0));
        assertEquals("item2", copy.get(1));
        
        // Verify it's a different instance
        assertNotSame(original, copy);
    }

    @Test
    @DisplayName("Set.of() creates empty set")
    void testSetOfEmpty() {
        Set<String> set = Set.of();
        assertNotNull(set);
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    @Test
    @DisplayName("Set.of(elements) creates set with elements")
    void testSetOfElements() {
        Set<String> set = Set.of("apple", "banana", "cherry");
        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("apple"));
        assertTrue(set.contains("banana"));
        assertTrue(set.contains("cherry"));
    }

    @Test
    @DisplayName("Set.of single element")
    void testSetOfSingleElement() {
        Set<String> set = Set.of("single");
        assertNotNull(set);
        assertEquals(1, set.size());
        assertTrue(set.contains("single"));
    }

    @Test
    @DisplayName("Set.of two elements")
    void testSetOfTwoElements() {
        Set<Integer> set = Set.of(1, 2);
        assertNotNull(set);
        assertEquals(2, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
    }

    @Test
    @DisplayName("Set.of three elements")
    void testSetOfThreeElements() {
        Set<String> set = Set.of("a", "b", "c");
        assertNotNull(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    @Test
    @DisplayName("Set.of four elements")
    void testSetOfFourElements() {
        Set<Integer> set = Set.of(1, 2, 3, 4);
        assertNotNull(set);
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(4));
    }

    @Test
    @DisplayName("Set.of five elements")
    void testSetOfFiveElements() {
        Set<String> set = Set.of("a", "b", "c", "d", "e");
        assertNotNull(set);
        assertEquals(5, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("e"));
    }

    @Test
    @DisplayName("Set.copyOf creates copy of collection")
    void testSetCopyOf() {
        Set<String> original = new HashSet<>();
        original.add("item1");
        original.add("item2");
        
        Set<String> copy = Set.copyOf(original);
        assertNotNull(copy);
        assertEquals(2, copy.size());
        assertTrue(copy.contains("item1"));
        assertTrue(copy.contains("item2"));
        
        // Verify it's a different instance
        assertNotSame(original, copy);
    }
}