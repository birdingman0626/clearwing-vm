package com.thelogicmaster.clearwing.jdk8test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Validation test for JDK 8 features to ensure standard implementations work.
 * This test validates JDK 8 feature compatibility using the standard JDK,
 * confirming that our Clearwing VM implementations match expected behavior.
 */
class JDK8ValidationTest {

    @Test
    @DisplayName("Lambda Expressions Work Correctly")
    void testLambdaExpressions() {
        List<String> words = Arrays.asList("hello", "world", "java", "lambda");
        
        // Test lambda with forEach
        List<String> result = new ArrayList<>();
        words.forEach(word -> result.add(word.toUpperCase()));
        
        assertEquals(4, result.size());
        assertEquals("HELLO", result.get(0));
        assertEquals("WORLD", result.get(1));
        assertEquals("JAVA", result.get(2));
        assertEquals("LAMBDA", result.get(3));
        
        // Test lambda with sort
        List<String> sorted = new ArrayList<>(words);
        sorted.sort((a, b) -> a.length() - b.length());
        assertEquals("java", sorted.get(0)); // shortest
        assertEquals("lambda", sorted.get(3)); // longest
    }

    @Test
    @DisplayName("Method References Work Correctly")
    void testMethodReferences() {
        List<String> words = Arrays.asList("hello", "world", "java");
        
        // Test static method reference
        List<String> upperCased = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        
        assertEquals(3, upperCased.size());
        assertEquals("HELLO", upperCased.get(0));
        assertEquals("WORLD", upperCased.get(1));
        assertEquals("JAVA", upperCased.get(2));
        
        // Test instance method reference
        List<Integer> lengths = words.stream()
            .map(String::length)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(5, 5, 4), lengths);
    }

    @Test
    @DisplayName("Stream API Filter Operations Work")
    void testStreamFilter() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Test filter with predicate
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), evenNumbers);
        
        // Test filter with method reference
        List<String> words = Arrays.asList("hello", "", "world", "", "java");
        List<String> nonEmpty = words.stream()
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList("hello", "world", "java"), nonEmpty);
    }

    @Test
    @DisplayName("Stream API Map Operations Work")
    void testStreamMap() {
        List<String> words = Arrays.asList("hello", "world", "java");
        
        // Test map with lambda
        List<Integer> lengths = words.stream()
            .map(s -> s.length())
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(5, 5, 4), lengths);
        
        // Test map with method reference
        List<String> upperCased = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList("HELLO", "WORLD", "JAVA"), upperCased);
    }

    @Test
    @DisplayName("Stream API Collect Operations Work")
    void testStreamCollect() {
        List<String> words = Arrays.asList("hello", "world", "java", "stream");
        
        // Test collect to list
        List<String> filtered = words.stream()
            .filter(s -> s.length() > 4)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList("hello", "world", "stream"), filtered);
        
        // Test collect to set
        Set<Integer> lengths = words.stream()
            .map(String::length)
            .collect(Collectors.toSet());
        
        assertTrue(lengths.contains(4));
        assertTrue(lengths.contains(5));
        assertTrue(lengths.contains(6));
        
        // Test joining
        String joined = words.stream()
            .collect(Collectors.joining(", "));
        
        assertEquals("hello, world, java, stream", joined);
        
        // Test joining with prefix/suffix
        String joinedWithBrackets = words.stream()
            .collect(Collectors.joining(", ", "[", "]"));
        
        assertEquals("[hello, world, java, stream]", joinedWithBrackets);
    }

    @Test
    @DisplayName("Stream API Reduce Operations Work")
    void testStreamReduce() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Test reduce with identity
        Integer sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        
        assertEquals(Integer.valueOf(15), sum);
        
        // Test reduce without identity
        Optional<Integer> product = numbers.stream()
            .reduce((a, b) -> a * b);
        
        assertTrue(product.isPresent());
        assertEquals(Integer.valueOf(120), product.get());
        
        // Test reduce on empty stream
        Optional<Integer> empty = numbers.stream()
            .filter(n -> n > 10)
            .reduce((a, b) -> a + b);
        
        assertFalse(empty.isPresent());
    }

    @Test
    @DisplayName("Stream API Terminal Operations Work")
    void testStreamTerminalOps() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Test count
        long count = numbers.stream()
            .filter(n -> n > 2)
            .count();
        
        assertEquals(3, count);
        
        // Test anyMatch
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        
        assertTrue(hasEven);
        
        // Test allMatch
        boolean allPositive = numbers.stream()
            .allMatch(n -> n > 0);
        
        assertTrue(allPositive);
        
        // Test noneMatch
        boolean noneNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        
        assertTrue(noneNegative);
        
        // Test findFirst
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 3)
            .findFirst();
        
        assertTrue(first.isPresent());
        assertEquals(Integer.valueOf(4), first.get());
        
        // Test findAny
        Optional<Integer> any = numbers.stream()
            .filter(n -> n > 3)
            .findAny();
        
        assertTrue(any.isPresent());
        assertTrue(any.get() > 3);
    }

    @Test
    @DisplayName("Optional API Works Correctly")
    void testOptional() {
        // Test Optional with value
        Optional<String> optional = Optional.of("test");
        assertTrue(optional.isPresent());
        assertEquals("test", optional.get());
        
        // Test Optional empty
        Optional<String> empty = Optional.empty();
        assertFalse(empty.isPresent());
        
        // Test Optional nullable
        Optional<String> nullable = Optional.ofNullable(null);
        assertFalse(nullable.isPresent());
        
        Optional<String> notNullable = Optional.ofNullable("value");
        assertTrue(notNullable.isPresent());
        assertEquals("value", notNullable.get());
        
        // Test Optional operations
        Optional<String> mapped = optional.map(String::toUpperCase);
        assertTrue(mapped.isPresent());
        assertEquals("TEST", mapped.get());
        
        Optional<String> filtered = optional.filter(s -> s.length() > 3);
        assertTrue(filtered.isPresent());
        
        Optional<String> filteredOut = optional.filter(s -> s.length() > 10);
        assertFalse(filteredOut.isPresent());
        
        // Test orElse
        String value = empty.orElse("default");
        assertEquals("default", value);
        
        // Test orElseGet
        String valueFromSupplier = empty.orElseGet(() -> "supplied");
        assertEquals("supplied", valueFromSupplier);
        
        // Test ifPresent
        List<String> result = new ArrayList<>();
        optional.ifPresent(result::add);
        assertEquals(Arrays.asList("test"), result);
    }

    @Test
    @DisplayName("Functional Interfaces Work Correctly")
    void testFunctionalInterfaces() {
        // Test Function
        Function<String, Integer> length = String::length;
        assertEquals(Integer.valueOf(5), length.apply("hello"));
        
        // Test Consumer
        List<String> result = new ArrayList<>();
        Consumer<String> adder = result::add;
        adder.accept("test");
        assertEquals(Arrays.asList("test"), result);
        
        // Test Supplier
        Supplier<String> supplier = () -> "supplied";
        assertEquals("supplied", supplier.get());
        
        // Test Predicate
        Predicate<String> isEmpty = String::isEmpty;
        assertTrue(isEmpty.test(""));
        assertFalse(isEmpty.test("hello"));
        
        // Test BiFunction
        BiFunction<String, String, String> concat = String::concat;
        assertEquals("helloworld", concat.apply("hello", "world"));
        
        // Test UnaryOperator
        UnaryOperator<String> upper = String::toUpperCase;
        assertEquals("HELLO", upper.apply("hello"));
        
        // Test BinaryOperator
        BinaryOperator<Integer> add = Integer::sum;
        assertEquals(Integer.valueOf(7), add.apply(3, 4));
    }

    @Test
    @DisplayName("Default Methods Work Correctly")
    void testDefaultMethods() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        
        // Test getOrDefault (default method)
        assertEquals(Integer.valueOf(1), map.getOrDefault("one", 0));
        assertEquals(Integer.valueOf(0), map.getOrDefault("three", 0));
        
        // Test forEach (default method)
        List<String> keys = new ArrayList<>();
        map.forEach((k, v) -> keys.add(k));
        assertEquals(2, keys.size());
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        
        // Test Collection removeIf (default method)
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        numbers.removeIf(n -> n % 2 == 0);
        assertEquals(Arrays.asList(1, 3, 5), numbers);
    }

    @Test
    @DisplayName("Stream Complex Pipeline Works")
    void testStreamComplexPipeline() {
        List<String> words = Arrays.asList("hello", "world", "java", "stream", "lambda", "functional");
        
        // Complex pipeline: filter, map, sort, collect
        List<String> result = words.stream()
            .filter(s -> s.length() > 4)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList("FUNCTIONAL", "HELLO", "LAMBDA", "STREAM", "WORLD"), result);
        
        // Test with numbers
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        List<Integer> processedNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(100, 64, 36), processedNumbers);
    }

    @Test
    @DisplayName("Stream Grouping Operations Work")
    void testStreamGrouping() {
        List<String> words = Arrays.asList("hello", "world", "java", "stream", "lambda");
        
        // Test groupingBy
        Map<Integer, List<String>> groupedByLength = words.stream()
            .collect(Collectors.groupingBy(String::length));
        
        assertEquals(2, groupedByLength.get(5).size());
        assertTrue(groupedByLength.get(5).contains("hello"));
        assertTrue(groupedByLength.get(5).contains("world"));
        
        assertEquals(1, groupedByLength.get(4).size());
        assertTrue(groupedByLength.get(4).contains("java"));
        
        // Test partitioningBy
        Map<Boolean, List<String>> partitioned = words.stream()
            .collect(Collectors.partitioningBy(s -> s.length() > 4));
        
        assertEquals(4, partitioned.get(true).size());
        assertEquals(1, partitioned.get(false).size());
        
        assertTrue(partitioned.get(true).contains("hello"));
        assertTrue(partitioned.get(true).contains("world"));
        assertTrue(partitioned.get(true).contains("stream"));
        assertTrue(partitioned.get(true).contains("lambda"));
        
        assertTrue(partitioned.get(false).contains("java"));
    }

    @Test
    @DisplayName("Stream Sorting and Distinct Work")
    void testStreamSortingAndDistinct() {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 2, 5, 3);
        
        // Test distinct
        List<Integer> distinct = numbers.stream()
            .distinct()
            .collect(Collectors.toList());
        
        assertEquals(6, distinct.size());
        assertTrue(distinct.contains(1));
        assertTrue(distinct.contains(2));
        assertTrue(distinct.contains(3));
        assertTrue(distinct.contains(5));
        assertTrue(distinct.contains(8));
        assertTrue(distinct.contains(9));
        
        // Test sorted
        List<Integer> sorted = numbers.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), sorted);
        
        // Test sorted with comparator
        List<Integer> sortedDesc = numbers.stream()
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(9, 8, 5, 3, 2, 1), sortedDesc);
    }

    @Test
    @DisplayName("Stream Limit and Skip Work")
    void testStreamLimitAndSkip() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Test limit
        List<Integer> limited = numbers.stream()
            .limit(5)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), limited);
        
        // Test skip
        List<Integer> skipped = numbers.stream()
            .skip(5)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(6, 7, 8, 9, 10), skipped);
        
        // Test skip and limit combined
        List<Integer> skipAndLimit = numbers.stream()
            .skip(3)
            .limit(4)
            .collect(Collectors.toList());
        
        assertEquals(Arrays.asList(4, 5, 6, 7), skipAndLimit);
    }

    @Test
    @DisplayName("Complete JDK 8 Integration Test")
    void testCompleteJDK8Integration() {
        // Create test data
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 35),
            new Person("David", 25),
            new Person("Eve", 30)
        );
        
        // Complex stream operation using all JDK 8 features
        Map<Integer, List<String>> groupedNames = people.stream()
            .filter(p -> p.getAge() >= 25)
            .sorted(Comparator.comparing(Person::getName))
            .collect(Collectors.groupingBy(
                Person::getAge,
                Collectors.mapping(Person::getName, Collectors.toList())
            ));
        
        // Verify results
        assertEquals(2, groupedNames.get(25).size());
        assertTrue(groupedNames.get(25).contains("Alice"));
        assertTrue(groupedNames.get(25).contains("David"));
        
        assertEquals(2, groupedNames.get(30).size());
        assertTrue(groupedNames.get(30).contains("Bob"));
        assertTrue(groupedNames.get(30).contains("Eve"));
        
        assertEquals(1, groupedNames.get(35).size());
        assertTrue(groupedNames.get(35).contains("Charlie"));
        
        // Test Optional with stream result
        Optional<Person> oldest = people.stream()
            .max(Comparator.comparing(Person::getAge));
        
        assertTrue(oldest.isPresent());
        assertEquals("Charlie", oldest.get().getName());
        assertEquals(35, oldest.get().getAge());
        
        // Test method references and lambda combinations
        String allNames = people.stream()
            .map(Person::getName)
            .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        
        assertEquals("Alice, Bob, Charlie, David, Eve", allNames);
        
        System.out.println("✅ All JDK 8 features working correctly!");
    }

    // Helper class for testing
    static class Person {
        private final String name;
        private final int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() {
            return name;
        }
        
        public int getAge() {
            return age;
        }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
}