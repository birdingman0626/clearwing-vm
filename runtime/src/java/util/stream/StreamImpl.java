package java.util.stream;

import java.util.*;
import java.util.function.*;

/**
 * Internal implementation of Stream interface.
 */
final class StreamImpl<T> implements Stream<T> {
    private final Iterator<T> iterator;
    private final Spliterator<T> spliterator;
    private boolean parallel = false;
    private boolean ordered = true;
    private final List<Runnable> closeHandlers = new ArrayList<>();

    private StreamImpl(Iterator<T> iterator) {
        this.iterator = iterator;
        this.spliterator = null;
    }

    private StreamImpl(Spliterator<T> spliterator) {
        this.iterator = null;
        this.spliterator = spliterator;
    }

    StreamImpl(Collection<T> collection) {
        this.iterator = collection.iterator();
        this.spliterator = collection.spliterator();
    }

    // Factory methods
    static <T> Stream<T> empty() {
        return new StreamImpl<>(Collections.emptyList());
    }

    static <T> Stream<T> of(T value) {
        return new StreamImpl<>(Collections.singletonList(value));
    }

    @SafeVarargs
    static <T> Stream<T> of(T... values) {
        return new StreamImpl<>(Arrays.asList(values));
    }

    static <T> Stream<T> iterate(T seed, UnaryOperator<T> f) {
        return new StreamImpl<>(new Iterator<T>() {
            private T current = seed;
            private boolean first = true;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                if (first) {
                    first = false;
                    return current;
                }
                return current = f.apply(current);
            }
        });
    }

    static <T> Stream<T> generate(Supplier<T> s) {
        return new StreamImpl<>(new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return s.get();
            }
        });
    }

    @SuppressWarnings("unchecked")
    static <T> Stream<T> concat(Stream<? extends T> a, Stream<? extends T> b) {
        List<T> combined = new ArrayList<>();
        a.forEach(combined::add);
        b.forEach(combined::add);
        return new StreamImpl<>(combined);
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
        List<T> filtered = new ArrayList<>();
        forEach(t -> {
            if (predicate.test(t)) {
                filtered.add(t);
            }
        });
        return new StreamImpl<>(filtered);
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = new ArrayList<>();
        forEach(t -> mapped.add(mapper.apply(t)));
        return new StreamImpl<>(mapped);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        List<R> result = new ArrayList<>();
        forEach(t -> {
            Stream<? extends R> stream = mapper.apply(t);
            stream.forEach(result::add);
        });
        return new StreamImpl<>(result);
    }

    @Override
    public Stream<T> distinct() {
        Set<T> seen = new HashSet<>();
        List<T> result = new ArrayList<>();
        forEach(t -> {
            if (seen.add(t)) {
                result.add(t);
            }
        });
        return new StreamImpl<>(result);
    }

    @Override
    public Stream<T> sorted() {
        List<T> list = new ArrayList<>();
        forEach(list::add);
        Collections.sort((List<? extends Comparable<? super T>>) list);
        return new StreamImpl<>(list);
    }

    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
        List<T> list = new ArrayList<>();
        forEach(list::add);
        list.sort(comparator);
        return new StreamImpl<>(list);
    }

    @Override
    public Stream<T> peek(Consumer<? super T> action) {
        List<T> result = new ArrayList<>();
        forEach(t -> {
            action.accept(t);
            result.add(t);
        });
        return new StreamImpl<>(result);
    }

    @Override
    public Stream<T> limit(long maxSize) {
        List<T> result = new ArrayList<>();
        Iterator<T> iter = iterator();
        long count = 0;
        while (iter.hasNext() && count < maxSize) {
            result.add(iter.next());
            count++;
        }
        return new StreamImpl<>(result);
    }

    @Override
    public Stream<T> skip(long n) {
        List<T> result = new ArrayList<>();
        Iterator<T> iter = iterator();
        long count = 0;
        while (iter.hasNext()) {
            T item = iter.next();
            if (count >= n) {
                result.add(item);
            }
            count++;
        }
        return new StreamImpl<>(result);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            action.accept(iter.next());
        }
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        forEach(action);
    }

    @Override
    public Object[] toArray() {
        List<T> list = new ArrayList<>();
        forEach(list::add);
        return list.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        List<T> list = new ArrayList<>();
        forEach(list::add);
        return list.toArray(generator.apply(list.size()));
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            result = accumulator.apply(result, iter.next());
        }
        return result;
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        Iterator<T> iter = iterator();
        if (!iter.hasNext()) {
            return Optional.empty();
        }
        T result = iter.next();
        while (iter.hasNext()) {
            result = accumulator.apply(result, iter.next());
        }
        return Optional.of(result);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        U result = identity;
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            result = accumulator.apply(result, iter.next());
        }
        return result;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        R result = supplier.get();
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            accumulator.accept(result, iter.next());
        }
        return result;
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            collector.accumulator().accept(container, iter.next());
        }
        return collector.finisher().apply(container);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        Iterator<T> iter = iterator();
        if (!iter.hasNext()) {
            return Optional.empty();
        }
        T min = iter.next();
        while (iter.hasNext()) {
            T current = iter.next();
            if (comparator.compare(current, min) < 0) {
                min = current;
            }
        }
        return Optional.of(min);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        Iterator<T> iter = iterator();
        if (!iter.hasNext()) {
            return Optional.empty();
        }
        T max = iter.next();
        while (iter.hasNext()) {
            T current = iter.next();
            if (comparator.compare(current, max) > 0) {
                max = current;
            }
        }
        return Optional.of(max);
    }

    @Override
    public long count() {
        long count = 0;
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        return count;
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            if (predicate.test(iter.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        Iterator<T> iter = iterator();
        while (iter.hasNext()) {
            if (!predicate.test(iter.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        Iterator<T> iter = iterator();
        if (iter.hasNext()) {
            return Optional.of(iter.next());
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> findAny() {
        return findFirst();
    }

    @Override
    public Iterator<T> iterator() {
        if (iterator != null) {
            return iterator;
        }
        return spliterator.hasCharacteristics(Spliterator.SIZED) ? 
            Spliterators.iterator(spliterator) : 
            new Iterator<T>() {
                private final Spliterator<T> s = spliterator;
                private T next;
                private boolean hasNext = false;

                @Override
                public boolean hasNext() {
                    if (!hasNext) {
                        hasNext = s.tryAdvance(t -> next = t);
                    }
                    return hasNext;
                }

                @Override
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    hasNext = false;
                    return next;
                }
            };
    }

    @Override
    public Spliterator<T> spliterator() {
        return spliterator != null ? spliterator : Spliterators.spliterator(iterator(), 0, 0);
    }

    @Override
    public boolean isParallel() {
        return parallel;
    }

    @Override
    public Stream<T> sequential() {
        StreamImpl<T> result = new StreamImpl<>(iterator != null ? iterator : spliterator);
        result.parallel = false;
        return result;
    }

    @Override
    public Stream<T> parallel() {
        StreamImpl<T> result = new StreamImpl<>(iterator != null ? iterator : spliterator);
        result.parallel = true;
        return result;
    }

    @Override
    public Stream<T> unordered() {
        StreamImpl<T> result = new StreamImpl<>(iterator != null ? iterator : spliterator);
        result.ordered = false;
        return result;
    }

    @Override
    public Stream<T> onClose(Runnable closeHandler) {
        closeHandlers.add(closeHandler);
        return this;
    }

    @Override
    public void close() {
        for (Runnable handler : closeHandlers) {
            handler.run();
        }
    }

    static final class BuilderImpl<T> implements Stream.Builder<T> {
        private final List<T> elements = new ArrayList<>();

        @Override
        public void accept(T t) {
            elements.add(t);
        }

        @Override
        public Stream<T> build() {
            return new StreamImpl<>(elements);
        }
    }
}