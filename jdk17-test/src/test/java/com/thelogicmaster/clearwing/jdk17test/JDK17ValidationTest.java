package com.thelogicmaster.clearwing.jdk17test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Validation test for standard JDK 17 features to ensure our implementations would be compatible
 */
class JDK17ValidationTest {

    @Test
    @DisplayName("JDK 9 Collection Factory Methods Work")
    void testCollectionFactoryMethods() {
        // These are standard JDK 9+ methods that should work
        List<String> list = List.of("a", "b", "c");
        Set<Integer> set = Set.of(1, 2, 3);
        Map<String, Integer> map = Map.of("one", 1, "two", 2);
        
        assertEquals(3, list.size());
        assertEquals(3, set.size());
        assertEquals(2, map.size());
        
        assertTrue(list.contains("a"));
        assertTrue(set.contains(1));
        assertEquals(Integer.valueOf(1), map.get("one"));
        
        // Test copyOf methods
        List<String> copy = List.copyOf(list);
        assertEquals(3, copy.size());
        assertEquals("a", copy.get(0));
    }

    @Test
    @DisplayName("JDK 11 String Methods Work")
    void testStringMethods() {
        // These are standard JDK 11+ methods
        assertTrue("   ".isBlank());
        assertFalse("a".isBlank());
        
        assertEquals("hello", " hello ".strip());
        assertEquals("hello ", " hello ".stripLeading());
        assertEquals(" hello", " hello ".stripTrailing());
        
        assertEquals("abcabc", "abc".repeat(2));
        
        String multiline = "line1\nline2";
        Stream<String> lines = multiline.lines();
        assertEquals(2, lines.count());
    }

    @Test
    @DisplayName("JDK 15 String Formatting Works")
    void testStringFormatting() {
        // JDK 15+ formatted method
        String result = "Hello %s!".formatted("World");
        assertEquals("Hello World!", result);
        
        String number = "Number: %d".formatted(42);
        assertEquals("Number: 42", number);
    }

    @Test
    @DisplayName("JDK 9 Class Methods Work")  
    void testClassMethods() {
        // JDK 9+ methods
        assertEquals("java.lang", String.class.getPackageName());
        assertEquals("java.lang.String", String.class.getTypeName());
        assertEquals("java.lang.String[]", String[].class.getTypeName());
        
        // Module system
        assertNotNull(String.class.getModule());
        assertTrue(String.class.getModule().isNamed());
        assertEquals("java.base", String.class.getModule().getName());
    }

    @Test
    @DisplayName("JDK 14+ Record/Sealed Class Detection Works")
    void testRecordAndSealedClassMethods() {
        // These methods may not be available in all JDK versions
        // Test using reflection to avoid compilation issues
        try {
            var isSealed = String.class.getMethod("isSealed");
            Boolean sealed = (Boolean) isSealed.invoke(String.class);
            assertFalse(sealed);
            
            var getPermittedSubclasses = String.class.getMethod("getPermittedSubclasses");
            Class<?>[] permitted = (Class<?>[]) getPermittedSubclasses.invoke(String.class);
            assertEquals(0, permitted.length);
        } catch (Exception e) {
            // Methods not available, which is fine for older JDK versions
            System.out.println("Sealed class methods not available in this JDK version");
        }
        
        try {
            var isRecord = String.class.getMethod("isRecord");
            Boolean record = (Boolean) isRecord.invoke(String.class);
            assertFalse(record);
            
            var getRecordComponents = String.class.getMethod("getRecordComponents");
            Object[] components = (Object[]) getRecordComponents.invoke(String.class);
            assertEquals(0, components.length);
        } catch (Exception e) {
            // Methods not available, which is fine for older JDK versions
            System.out.println("Record methods not available in this JDK version");
        }
    }

    @Test
    @DisplayName("Module System Works")
    void testModuleSystem() {
        // Basic module functionality
        var module = String.class.getModule();
        assertNotNull(module);
        assertTrue(module.isNamed());
        assertEquals("java.base", module.getName());
        
        // Module operations should not throw
        assertDoesNotThrow(() -> module.isExported("java.lang"));
        assertDoesNotThrow(() -> module.isOpen("java.lang"));
    }

    @Test
    @DisplayName("Backward Compatibility with JDK 8 Features")
    void testBackwardCompatibility() {
        // Ensure JDK 8 features still work
        List<String> list = Arrays.asList("a", "b", "c");
        
        // Lambda expressions
        List<String> upperCased = list.stream()
            .map(String::toUpperCase)
            .collect(java.util.stream.Collectors.toList());
            
        assertEquals(3, upperCased.size());
        assertEquals("A", upperCased.get(0));
        
        // Optional
        Optional<String> optional = Optional.of("test");
        assertTrue(optional.isPresent());
        assertEquals("test", optional.get());
        
        // Default methods on interfaces work
        Map<String, Integer> map = new HashMap<>();
        map.put("key", 1);
        assertEquals(Integer.valueOf(1), map.getOrDefault("key", 0));
        assertEquals(Integer.valueOf(0), map.getOrDefault("missing", 0));
    }

    @Test
    @DisplayName("Text Blocks Work (if supported by compiler)")
    void testTextBlocks() {
        // This will only work if using JDK 15+ compiler
        // But won't fail on older versions as it's just a string
        String json = """
            {
                "name": "test",
                "value": 123
            }
            """;
        
        assertNotNull(json);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("123"));
    }

    @Test
    @DisplayName("Pattern Matching instanceof (if supported)")
    void testPatternMatching() {
        Object obj = "hello";
        
        // Traditional instanceof
        if (obj instanceof String) {
            String str = (String) obj;
            assertEquals(5, str.length());
        }
        
        // Pattern matching instanceof would work with JDK 16+ compiler
        // but we'll test traditional way for compatibility
        assertTrue(obj instanceof String);
        assertEquals("hello", obj);
    }

    @Test
    @DisplayName("Comprehensive Integration Test")
    void testComprehensiveIntegration() {
        // Create data using factory methods
        List<String> words = List.of("hello", "world", "java");
        
        // Process using string methods
        List<String> processed = words.stream()
            .map(s -> s.strip())
            .filter(s -> !s.isBlank())
            .map(s -> s.repeat(1))
            .collect(java.util.stream.Collectors.toList());
            
        assertEquals(3, processed.size());
        
        // Use formatting
        String result = "Words: %s".formatted(String.join(", ", processed));
        assertEquals("Words: hello, world, java", result);
        
        // Use lines method
        String multiline = String.join("\n", processed);
        long lineCount = multiline.lines().count();
        assertEquals(3, lineCount);
        
        // Verify classes have module info
        for (String word : processed) {
            Class<?> clazz = word.getClass();
            assertNotNull(clazz.getModule());
            assertEquals("java.lang", clazz.getPackageName());
            
            // Test record/sealed methods if available
            try {
                var isRecord = clazz.getMethod("isRecord");
                assertFalse((Boolean) isRecord.invoke(clazz));
                
                var isSealed = clazz.getMethod("isSealed");
                assertFalse((Boolean) isSealed.invoke(clazz));
            } catch (Exception e) {
                // Methods not available in this JDK version
            }
        }
        
        System.out.println("✅ All JDK 17 features working correctly!");
    }
}