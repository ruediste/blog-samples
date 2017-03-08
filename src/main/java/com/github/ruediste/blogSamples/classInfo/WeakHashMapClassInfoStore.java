package com.github.ruediste.blogSamples.classInfo;

import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapClassInfoStore<T> implements ClassInfoStore<T> {

    private Map<Class<?>, T> store = new WeakHashMap<>();

    @Override
    public T get(Class<?> cls) {
        return store.get(cls);
    }

    @Override
    public void store(Class<?> cls, T value) {
        store.put(cls, value);
    }
}
