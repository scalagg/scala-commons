package gg.scala.commons.core.tryto;

@FunctionalInterface
public interface CheckedFunction<T, R> {

    R apply(T t) throws Throwable;

}
