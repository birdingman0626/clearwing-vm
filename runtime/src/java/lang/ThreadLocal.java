/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.lang;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 *
 * @author shannah
 */
public class ThreadLocal<T> extends Object {

    private Map<Thread,T> value = new HashMap<Thread,T>();
    private Set<Thread> _initialized = new HashSet<Thread>();

    public ThreadLocal() {
        super();
    }

    protected T initialValue() {
        return null;
    }

    public synchronized T get() {
        Thread t = Thread.currentThread();
        if (!_initialized.contains(t)) {
            _initialized.add(t);
            value.put(t, initialValue());
        }
        return value.get(t);
        
        
    }

    public synchronized void set(T value) {
        Thread t = Thread.currentThread();
        
        _initialized.add(t);
        this.value.put(t, value);
    }

    public synchronized void remove() {
        Thread t = Thread.currentThread();
        _initialized.remove(t);
        value.remove(t);
    }
    
    public static <T> ThreadLocal<T> withInitial(Supplier<T> supplier) {
        return new Proxy<>(supplier);
    }
    
    public static class Proxy<T> extends ThreadLocal<T> {
        private final Supplier<T> supplier;
        
        public Proxy(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }
}
