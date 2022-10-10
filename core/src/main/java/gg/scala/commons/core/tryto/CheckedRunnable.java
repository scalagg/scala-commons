package gg.scala.commons.core.tryto;

@FunctionalInterface
public interface CheckedRunnable {

    void run() throws Throwable;

}
