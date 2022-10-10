package gg.scala.commons.core.tryto;

@FunctionalInterface
public interface CheckedSupplier<T> {

    T get() throws Throwable;

}
