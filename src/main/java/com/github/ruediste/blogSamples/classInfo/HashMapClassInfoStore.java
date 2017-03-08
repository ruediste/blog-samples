package com.github.ruediste.blogSamples.classInfo;

import java.util.HashMap;
import java.util.Map;

public class HashMapClassInfoStore<T> implements ClassInfoStore<T> {

    private Map<Class<?>, T> store = new HashMap<>();

    @Override
    public T get(Class<?> cls) {
        return store.get(cls);
    }

    @Override
    public void store(Class<?> cls, T value) {
        store.put(cls, value);
    }
}
