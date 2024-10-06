package com.newproject.helper.spigot.sidebar.util.lang;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Throwable> {

    boolean test(T t) throws E;
}
