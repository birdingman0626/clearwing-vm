package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * A mutable reduction operation that accumulates input elements into a
 * mutable result container.
 */
public interface Collector<T, A, R> {

    /**
     * A function that creates and returns a new mutable result container.
     */
    Supplier<A> supplier();

    /**
     * A function that folds a value into a mutable result container.
     */
    BiConsumer<A, T> accumulator();

    /**
     * A function that accepts two partial results and merges them.
     */
    BinaryOperator<A> combiner();

    /**
     * Performs the final transformation from the intermediate accumulation type
     * to the final result type.
     */
    Function<A, R> finisher();

    /**
     * Returns a Set of Collector.Characteristics indicating the characteristics
     * of this Collector.
     */
    Set<Characteristics> characteristics();

    /**
     * Returns a new Collector described by the given supplier, accumulator,
     * and combiner functions.
     */
    static <T, R> Collector<T, R, R> of(Supplier<R> supplier,
                                        BiConsumer<R, T> accumulator,
                                        BinaryOperator<R> combiner,
                                        Characteristics... characteristics) {
        return new CollectorImpl<>(supplier, accumulator, combiner, Function.identity(), 
                                   Set.of(characteristics));
    }

    /**
     * Returns a new Collector described by the given supplier, accumulator,
     * combiner, and finisher functions.
     */
    static <T, A, R> Collector<T, A, R> of(Supplier<A> supplier,
                                           BiConsumer<A, T> accumulator,
                                           BinaryOperator<A> combiner,
                                           Function<A, R> finisher,
                                           Characteristics... characteristics) {
        return new CollectorImpl<>(supplier, accumulator, combiner, finisher, 
                                   Set.of(characteristics));
    }

    /**
     * Characteristics indicating properties of a Collector.
     */
    enum Characteristics {
        CONCURRENT,
        UNORDERED,
        IDENTITY_FINISH
    }
}