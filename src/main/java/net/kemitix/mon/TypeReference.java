package net.kemitix.mon;

public class TypeReference<T> {

    private TypeReference() {
    }

    public static <R> TypeReference<R> create() {
        return new TypeReference<>();
    }

}
