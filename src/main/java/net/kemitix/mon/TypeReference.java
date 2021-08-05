package net.kemitix.mon;

/**
 * Helper class to capture a reference to a type.
 *
 * <p>Usually to be used when passing a type as a parameter to method.</p>
 *
 * <pre><code>
 * TypeReference&lt;Integer&gt; ref1 = TypeReference.create();
 * var ref2 = TypeReference.&lt;Integer&gt;create();
 * </code></pre>
 *
 * @param <T> the type being references
 */
@SuppressWarnings("PMD.ClassNamingConventions")
final public class TypeReference<T> {

    private TypeReference() {
    }

    /**
     * Creates a new instance of a TypeReference.
     *
     * @param <R> the type being references.
     * @return the TypeReference
     */
    public static <R> TypeReference<R> create() {
        return new TypeReference<>();
    }

}
