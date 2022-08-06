package com.dan.minecraft7tv.interfaces;

@FunctionalInterface
public interface Action<T> {
    void execute(T t);
}
