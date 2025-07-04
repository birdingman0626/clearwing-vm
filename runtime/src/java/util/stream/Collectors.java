package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * Implementations of Collector that implement various useful reduction
 * operations.
 */
public final class Collectors {

    private Collectors() {}

    /**
     * Returns a Collector that accumulates the input elements into a new List.
     */
    public static <T> Collector<T, ?, List<T>> toList() {
        return new CollectorImpl<>(ArrayList::new, List::add, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        }, Function.identity(), Collections.emptySet());
    }

    /**
     * Returns a Collector that accumulates the input elements into a new Set.
     */
    public static <T> Collector<T, ?, Set<T>> toSet() {
        return new CollectorImpl<>(HashSet::new, Set::add, (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        }, Function.identity(), Collections.emptySet());
    }

    /**
     * Returns a Collector that concatenates the input elements into a String.
     */
    public static Collector<CharSequence, ?, String> joining() {
        return joining("");
    }

    /**
     * Returns a Collector that concatenates the input elements, separated by
     * the specified delimiter.
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, "", "");
    }

    /**
     * Returns a Collector that concatenates the input elements, separated by
     * the specified delimiter, with the specified prefix and suffix.
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter,
                                                             CharSequence prefix,
                                                             CharSequence suffix) {
        return new CollectorImpl<>(
            () -> new StringBuilder().append(prefix),
            (sb, cs) -> {
                if (sb.length() > prefix.length()) {
                    sb.append(delimiter);
                }
                sb.append(cs);
            },
            (sb1, sb2) -> {
                if (sb1.length() > prefix.length()) {
                    sb1.append(delimiter);
                }
                sb1.append(sb2, prefix.length(), sb2.length());
                return sb1;
            },
            sb -> sb.append(suffix).toString(),
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector that produces the sum of a integer-valued function
     * applied to the input elements.
     */
    public static <T> Collector<T, ?, Integer> summingInt(ToIntFunction<? super T> mapper) {
        return new CollectorImpl<>(
            () -> new int[1],
            (acc, t) -> acc[0] += mapper.applyAsInt(t),
            (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; },
            acc -> acc[0],
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector that produces the sum of a long-valued function
     * applied to the input elements.
     */
    public static <T> Collector<T, ?, Long> summingLong(ToLongFunction<? super T> mapper) {
        return new CollectorImpl<>(
            () -> new long[1],
            (acc, t) -> acc[0] += mapper.applyAsLong(t),
            (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; },
            acc -> acc[0],
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector that produces the sum of a double-valued function
     * applied to the input elements.
     */
    public static <T> Collector<T, ?, Double> summingDouble(ToDoubleFunction<? super T> mapper) {
        return new CollectorImpl<>(
            () -> new double[1],
            (acc, t) -> acc[0] += mapper.applyAsDouble(t),
            (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; },
            acc -> acc[0],
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector implementing a "group by" operation on input elements
     * of type T, grouping elements according to a classification function.
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
        return groupingBy(classifier, toList());
    }

    /**
     * Returns a Collector implementing a cascaded "group by" operation on input
     * elements of type T, grouping elements according to a classification function.
     */
    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
                                                                      Collector<? super T, A, D> downstream) {
        return new CollectorImpl<>(
            HashMap::new,
            (map, element) -> {
                K key = classifier.apply(element);
                map.computeIfAbsent(key, k -> downstream.supplier().get());
                downstream.accumulator().accept(map.get(key), element);
            },
            (map1, map2) -> {
                for (Map.Entry<K, A> entry : map2.entrySet()) {
                    K key = entry.getKey();
                    A value = entry.getValue();
                    map1.merge(key, value, downstream.combiner());
                }
                return map1;
            },
            map -> {
                Map<K, D> result = new HashMap<>();
                for (Map.Entry<K, A> entry : map.entrySet()) {
                    result.put(entry.getKey(), downstream.finisher().apply(entry.getValue()));
                }
                return result;
            },
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector which partitions the input elements according to a
     * Predicate, and organizes them into a Map&lt;Boolean, List&lt;T&gt;&gt;.
     */
    public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> predicate) {
        return partitioningBy(predicate, toList());
    }

    /**
     * Returns a Collector which partitions the input elements according to a
     * Predicate, reduces the values in each partition according to another
     * Collector, and organizes them into a Map&lt;Boolean, D&gt;.
     */
    public static <T, D, A> Collector<T, ?, Map<Boolean, D>> partitioningBy(Predicate<? super T> predicate,
                                                                            Collector<? super T, A, D> downstream) {
        return new CollectorImpl<>(
            () -> {
                Map<Boolean, A> map = new HashMap<>();
                map.put(true, downstream.supplier().get());
                map.put(false, downstream.supplier().get());
                return map;
            },
            (map, element) -> {
                Boolean key = predicate.test(element);
                downstream.accumulator().accept(map.get(key), element);
            },
            (map1, map2) -> {
                map1.put(true, downstream.combiner().apply(map1.get(true), map2.get(true)));
                map1.put(false, downstream.combiner().apply(map1.get(false), map2.get(false)));
                return map1;
            },
            map -> {
                Map<Boolean, D> result = new HashMap<>();
                result.put(true, downstream.finisher().apply(map.get(true)));
                result.put(false, downstream.finisher().apply(map.get(false)));
                return result;
            },
            Collections.emptySet()
        );
    }

    /**
     * Returns a Collector that counts the number of input elements.
     */
    public static <T> Collector<T, ?, Long> counting() {
        return new CollectorImpl<>(
            () -> new long[1],
            (acc, t) -> acc[0]++,
            (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; },
            acc -> acc[0],
            Collections.emptySet()
        );
    }
}