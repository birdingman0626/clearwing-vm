package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * A sequence of elements supporting sequential operations.
 * Basic implementation of Stream API for JDK 8 compatibility.
 */
public interface Stream<T> extends BaseStream<T, Stream<T>> {

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
     */
    Stream<T> filter(Predicate<? super T> predicate);

    /**
     * Returns a stream consisting of the results of applying the given
     * function to the elements of this stream.
     */
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream.
     */
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     */
    Stream<T> distinct();

    /**
     * Returns a stream consisting of the elements of this stream, sorted
     * according to natural order.
     */
    Stream<T> sorted();

    /**
     * Returns a stream consisting of the elements of this stream, sorted
     * according to the provided Comparator.
     */
    Stream<T> sorted(Comparator<? super T> comparator);

    /**
     * Returns a stream consisting of the elements of this stream, after
     * performing the provided action on each element.
     */
    Stream<T> peek(Consumer<? super T> action);

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than maxSize in length.
     */
    Stream<T> limit(long maxSize);

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first n elements.
     */
    Stream<T> skip(long n);

    /**
     * Performs an action for each element of this stream.
     */
    void forEach(Consumer<? super T> action);

    /**
     * Performs an action for each element of this stream, in the encounter
     * order of the stream if the stream has a defined encounter order.
     */
    void forEachOrdered(Consumer<? super T> action);

    /**
     * Returns an array containing the elements of this stream.
     */
    Object[] toArray();

    /**
     * Returns an array containing the elements of this stream, using the
     * provided generator function to allocate the returned array.
     */
    <A> A[] toArray(IntFunction<A[]> generator);

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity value and an associative accumulation function.
     */
    T reduce(T identity, BinaryOperator<T> accumulator);

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function.
     */
    Optional<T> reduce(BinaryOperator<T> accumulator);

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity, accumulation and combining functions.
     */
    <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

    /**
     * Performs a mutable reduction operation on the elements of this stream.
     */
    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

    /**
     * Performs a mutable reduction operation on the elements of this stream.
     */
    <R, A> R collect(Collector<? super T, A, R> collector);

    /**
     * Returns the minimum element of this stream according to the provided
     * Comparator.
     */
    Optional<T> min(Comparator<? super T> comparator);

    /**
     * Returns the maximum element of this stream according to the provided
     * Comparator.
     */
    Optional<T> max(Comparator<? super T> comparator);

    /**
     * Returns the count of elements in this stream.
     */
    long count();

    /**
     * Returns whether any elements of this stream match the provided predicate.
     */
    boolean anyMatch(Predicate<? super T> predicate);

    /**
     * Returns whether all elements of this stream match the provided predicate.
     */
    boolean allMatch(Predicate<? super T> predicate);

    /**
     * Returns whether no elements of this stream match the provided predicate.
     */
    boolean noneMatch(Predicate<? super T> predicate);

    /**
     * Returns an Optional describing the first element of this stream.
     */
    Optional<T> findFirst();

    /**
     * Returns an Optional describing some element of the stream.
     */
    Optional<T> findAny();

    // Static factory methods

    /**
     * Returns a builder for a Stream.
     */
    static <T> Stream.Builder<T> builder() {
        return new StreamImpl.BuilderImpl<>();
    }

    /**
     * Returns an empty sequential Stream.
     */
    static <T> Stream<T> empty() {
        return StreamImpl.empty();
    }

    /**
     * Returns a sequential Stream containing a single element.
     */
    static <T> Stream<T> of(T t) {
        return StreamImpl.of(t);
    }

    /**
     * Returns a sequential ordered stream whose elements are the specified values.
     */
    @SafeVarargs
    static <T> Stream<T> of(T... values) {
        return StreamImpl.of(values);
    }

    /**
     * Returns an infinite sequential ordered Stream produced by iterative
     * application of a function f to an initial element seed.
     */
    static <T> Stream<T> iterate(T seed, UnaryOperator<T> f) {
        return StreamImpl.iterate(seed, f);
    }

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided Supplier.
     */
    static <T> Stream<T> generate(Supplier<T> s) {
        return StreamImpl.generate(s);
    }

    /**
     * Creates a lazily concatenated stream whose elements are all the elements
     * of the first stream followed by all the elements of the second stream.
     */
    static <T> Stream<T> concat(Stream<? extends T> a, Stream<? extends T> b) {
        return StreamImpl.concat(a, b);
    }

    /**
     * Builder for Stream instances.
     */
    interface Builder<T> extends Consumer<T> {
        void accept(T t);
        default Builder<T> add(T t) {
            accept(t);
            return this;
        }
        Stream<T> build();
    }
}