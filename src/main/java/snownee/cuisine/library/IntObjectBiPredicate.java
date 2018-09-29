package snownee.cuisine.library;

import java.util.function.BiPredicate;

/**
 * An <code>IntObjectBiPredicate</code> represents a predicate that
 * takes two arguments, in which the first one is primitive int, and
 * the second one is Object.
 *
 * This is a primitive-specialized version of {@link BiPredicate},
 * where the first argument has type that corresponds to
 * {@link Integer}. This interface is also retrofitted into
 * {@link BiPredicate}.
 *
 * @param <T> The type of the second argument
 *
 * @see BiPredicate
 */
@FunctionalInterface
public interface IntObjectBiPredicate<T> extends BiPredicate<Integer, T>
{
    boolean test(int a, T b);

    @Override
    default boolean test(Integer a, T b)
    {
        /*
         * We have to explicitly call intValue() to avoid
         * potential stack overflow caused by confused
         * compiler (the Integer argument may be treated
         * verbatim or as un-boxed value, where the former
         * is going to cause stack overflow, and the latter
         * is what we are meant to call)
         */
        return test(a.intValue(), b);
    }
}
