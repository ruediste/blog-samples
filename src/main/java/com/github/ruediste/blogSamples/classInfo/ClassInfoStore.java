package com.github.ruediste.blogSamples.classInfo;

public interface ClassInfoStore<T> {
    T get(Class<?> cls);

    void store(Class<?> cls, T value);
}