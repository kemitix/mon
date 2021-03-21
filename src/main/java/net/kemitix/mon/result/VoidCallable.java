package net.kemitix.mon.result;

@FunctionalInterface
public interface VoidCallable {
    void call() throws Exception;
}
