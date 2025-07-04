package com.thelogicmaster.example;

import java.util.List;
import java.util.Set;

/**
 * Example demonstrating JDK 17 features
 */
public class JDK17Example {
    
    public static void testModernCollections() {
        // JDK 9+ factory methods
        List<String> list = List.of("apple", "banana", "cherry");
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);
        
        System.out.println("List: " + list);
        System.out.println("Set: " + numbers);
    }
    
    public static void testModernStringMethods() {
        String text = "  Hello World  ";
        
        // JDK 11+ methods
        System.out.println("Original: '" + text + "'");
        System.out.println("Stripped: '" + text.strip() + "'");
        System.out.println("Is blank: " + "   ".isBlank());
        System.out.println("Repeat: " + "Ha".repeat(3));
        
        // JDK 15+ methods
        String template = "Hello %s!";
        System.out.println(template.formatted("World"));
    }
    
    public static void testModernClassMethods() {
        Class<?> clazz = String.class;
        
        // JDK 9+ methods
        System.out.println("Package name: " + clazz.getPackageName());
        System.out.println("Type name: " + clazz.getTypeName());
        System.out.println("Module: " + clazz.getModule());
        
        // JDK 14+ and JDK 16+ methods
        System.out.println("Is sealed: " + clazz.isSealed());
        System.out.println("Is record: " + clazz.isRecord());
    }
    
    public static void testTextBlocks() {
        // JDK 15+ text blocks (if compiler supports them)
        String json = """
            {
                "name": "JDK 17 Test",
                "version": "1.0",
                "features": ["text blocks", "records", "sealed classes"]
            }
            """;
        System.out.println("JSON: " + json);
    }
    
    public static void testPatternMatching() {
        Object obj = "Hello";
        
        // Enhanced instanceof (JDK 16+)
        if (obj instanceof String s) {
            System.out.println("String length: " + s.length());
        }
    }
    
    public static void demonstrateAll() {
        System.out.println("=== JDK 17 Feature Demo ===");
        
        testModernCollections();
        testModernStringMethods();
        testModernClassMethods();
        testTextBlocks();
        testPatternMatching();
        
        System.out.println("=== Demo Complete ===");
    }
}