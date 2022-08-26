package com.dan.minecraft7tv.client.interfaces;

@FunctionalInterface
public interface Action<T> {
    void execute(T t);
}
