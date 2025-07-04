package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * Base interface for streams, which are sequences of elements supporting
 * sequential operations.
 */
public interface BaseStream<T, S extends BaseStream<T, S>> extends AutoCloseable {

    /**
     * Returns an iterator for the elements of this stream.
     */
    Iterator<T> iterator();

    /**
     * Returns a spliterator for the elements of this stream.
     */
    Spliterator<T> spliterator();

    /**
     * Returns whether this stream, if a terminal operation were to be executed,
     * would execute in parallel.
     */
    boolean isParallel();

    /**
     * Returns an equivalent stream that is sequential.
     */
    S sequential();

    /**
     * Returns an equivalent stream that is parallel.
     */
    S parallel();

    /**
     * Returns an equivalent stream that is unordered.
     */
    S unordered();

    /**
     * Returns an equivalent stream with an additional close handler.
     */
    S onClose(Runnable closeHandler);

    /**
     * Closes this stream, causing all close handlers for this stream pipeline
     * to be called.
     */
    void close();
}