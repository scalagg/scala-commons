package com.newproject.helper.spigot.sidebar.util.lang;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    T get() throws E;
}
