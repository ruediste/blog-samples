package com.github.ruediste.blogSamples.classInfo;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakRefClassInfoStore<T> implements ClassInfoStore<T> {

    private Map<Class<?>, WeakReference<T>> store = new WeakHashMap<>();

    @Override
    public T get(Class<?> cls) {
        WeakReference<T> ref = store.get(cls);
        if (ref == null)
            return null;
        return ref.get();
    }

    @Override
    public void store(Class<?> cls, T value) {
        store.put(cls, new WeakReference<>(value));
    }
}
