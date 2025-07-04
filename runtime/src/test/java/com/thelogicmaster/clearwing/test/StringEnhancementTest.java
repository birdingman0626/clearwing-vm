package com.thelogicmaster.clearwing.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

/**
 * Tests for JDK 11+ String enhancement methods
 */
class StringEnhancementTest {

    @Test
    @DisplayName("isBlank() returns true for blank strings")
    void testIsBlank() {
        assertTrue("".isBlank());
        assertTrue(" ".isBlank());
        assertTrue("  ".isBlank());
        assertTrue("\t".isBlank());
        assertTrue("\n".isBlank());
        assertTrue(" \t \n ".isBlank());
        
        assertFalse("a".isBlank());
        assertFalse(" a ".isBlank());
        assertFalse("hello".isBlank());
    }

    @Test
    @DisplayName("strip() removes leading and trailing whitespace")
    void testStrip() {
        assertEquals("", "".strip());
        assertEquals("", "   ".strip());
        assertEquals("hello", "hello".strip());
        assertEquals("hello", " hello ".strip());
        assertEquals("hello", "  hello  ".strip());
        assertEquals("hello", "\t hello \n".strip());
        assertEquals("hello world", " hello world ".strip());
    }

    @Test
    @DisplayName("stripLeading() removes only leading whitespace")
    void testStripLeading() {
        assertEquals("", "".stripLeading());
        assertEquals("", "   ".stripLeading());
        assertEquals("hello", "hello".stripLeading());
        assertEquals("hello ", " hello ".stripLeading());
        assertEquals("hello  ", "  hello  ".stripLeading());
        assertEquals("hello \n", "\t hello \n".stripLeading());
        assertEquals("hello world ", " hello world ".stripLeading());
    }

    @Test
    @DisplayName("stripTrailing() removes only trailing whitespace")
    void testStripTrailing() {
        assertEquals("", "".stripTrailing());
        assertEquals("", "   ".stripTrailing());
        assertEquals("hello", "hello".stripTrailing());
        assertEquals(" hello", " hello ".stripTrailing());
        assertEquals("  hello", "  hello  ".stripTrailing());
        assertEquals("\t hello", "\t hello \n".stripTrailing());
        assertEquals(" hello world", " hello world ".stripTrailing());
    }

    @Test
    @DisplayName("repeat() repeats string n times")
    void testRepeat() {
        assertEquals("", "".repeat(0));
        assertEquals("", "".repeat(5));
        assertEquals("", "hello".repeat(0));
        assertEquals("hello", "hello".repeat(1));
        assertEquals("hellohello", "hello".repeat(2));
        assertEquals("hellohellohello", "hello".repeat(3));
        assertEquals("aaaa", "a".repeat(4));
        assertEquals("abababab", "ab".repeat(4));
    }

    @Test
    @DisplayName("repeat() throws IllegalArgumentException for negative count")
    void testRepeatNegative() {
        assertThrows(IllegalArgumentException.class, () -> "hello".repeat(-1));
        assertThrows(IllegalArgumentException.class, () -> "".repeat(-5));
    }

    @Test
    @DisplayName("lines() returns stream of lines")
    void testLines() {
        // Simple case - single line
        Stream<String> lines1 = "hello".lines();
        String[] result1 = lines1.toArray(String[]::new);
        assertEquals(1, result1.length);
        assertEquals("hello", result1[0]);

        // Multiple lines with different line separators
        Stream<String> lines2 = "line1\nline2\rline3\r\nline4".lines();
        String[] result2 = lines2.toArray(String[]::new);
        assertEquals(4, result2.length);
        assertEquals("line1", result2[0]);
        assertEquals("line2", result2[1]);
        assertEquals("line3", result2[2]);
        assertEquals("line4", result2[3]);

        // Empty string
        Stream<String> lines3 = "".lines();
        String[] result3 = lines3.toArray(String[]::new);
        assertEquals(1, result3.length);
        assertEquals("", result3[0]);
    }

    @Test
    @DisplayName("formatted() formats string with arguments (JDK 15+)")
    void testFormatted() {
        assertEquals("Hello World!", "Hello %s!".formatted("World"));
        assertEquals("Number: 42", "Number: %d".formatted(42));
        assertEquals("Pi: 3.14", "Pi: %.2f".formatted(3.14159));
        assertEquals("Multiple: Hello 42", "Multiple: %s %d".formatted("Hello", 42));
    }

    @Test
    @DisplayName("stripIndent() returns same string (simplified implementation)")
    void testStripIndent() {
        // Our simplified implementation just returns the same string
        String text = "  hello\n    world\n  ";
        assertEquals(text, text.stripIndent());
    }

    @Test
    @DisplayName("translateEscapes() returns same string (simplified implementation)")
    void testTranslateEscapes() {
        // Our simplified implementation just returns the same string
        String text = "hello\\nworld\\t";
        assertEquals(text, text.translateEscapes());
    }
}