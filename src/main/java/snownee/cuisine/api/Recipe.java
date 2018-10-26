package snownee.cuisine.api;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * A {@code Recipe} is a matcher that determines whether a specific
 * dish belongs to a certain type of dish, used for answering
 * questions like "is this dish pasta or smashed potato or
 * something else".
 *
 * {@code Recipe} 对象是一个用于判断某道菜品的具体类型的匹配器，用于回答诸如
 * “这盘菜是红烧牛肉还是糖醋鲤鱼还是别的什么”这样的问题。
 */
public interface Recipe
{

    /**
     * Accessor of the unique name of this recipe.
     *
     * @return The name of this recipe
     */
    String name();

    /**
     * Determine whether given dish object matches this recipe. This does not give any details
     * about to what extent the dish matches this recipe; it only guarantees that {@link
     * #similarity(CompositeFood)} returns a value larger than zero (0) if this returns
     * <code>true</code>, or zero (0) itself if this returns <code>false</code>.
     * <p>
     * In order to get more details, methods such as {@link #similarity(CompositeFood)} shall
     * be used instead.
     * </p>
     *
     * @param dish The {@code CompositeFood} object to be examined.
     *
     * @return <code>true</code> if the given dish is matched; <code>false</code> otherwise.
     */
    boolean matches(CompositeFood dish);

    /**
     * Determine to what extent the dish matches this recipe, using an integer ranged from
     * 0 to 100. 0 is defined as "given dish is not following this recipe", and this implies
     * that {@link #matches(CompositeFood)} return {@code false}; on the contrary, any return
     * value that is larger than zero implies that {@link #matches(CompositeFood)}
     * returns {@code true}. Return value that is negative or larger than 100 is undefined here.
     *
     * @param dish The {@code CompositeFood} object to be examined.
     *
     * @return An integer ranged from 0 - 100 which describes similarity
     */
    int similarity(CompositeFood dish);

    /**
     * Return a {@link Collection} of criteria that are all expect to have their {@link
     * Criterion#matches(CompositeFood)} returns {@code true} when test against the
     * same {@link CompositeFood} instance. That said, the returned collection must
     * satisfy that:
     * <pre>
     *     CompositeFood dish = ...;
     *     if (recipe.matches(dish)) {
     *         assert recipe.getRequiredCriteria().stream().allMatch(c -&gt; c.matches(dish));
     *     }
     *
     *     // or in the equivalent form:
     *
     *     recipe.getRequiredCriteria().stream().reduce(
     *         dish -&gt; true,
     *         Predicate::and,
     *         (BinaryOperator&lt;Predicate&lt;CompositeFood&gt;&gt;) Predicate::and
     *     ).test(dish) == recipe.matches(dish);
     * </pre>
     *
     * i.e. the argument with conclusion of <code>recipe.matches(dish)</code> and
     * all {@link Criterion criteria} as premises (with the <code>dish</code> as
     * the input of each predicate) is valid.
     *
     * @return A read-only collection that contains all required criteria
     */
    /*
     * Before you ask: yes, criteria is the plural form of criterion, and no, this is
     * Greek, not Latin, even though it still follows second declension in Latin (i.e.
     * the Greek type).
     */
    Collection<Criterion> getRequiredCriteria();

    /**
     * Return a {@link Collection} of {@link Criterion criteria} that are expected
     * to affect the return value of {@link #similarity(CompositeFood)} on a per-
     * instance basis. The return value is expect to comply that:
     *
     * <pre>
     *     this.getKnownCriteria().containsAll(this.getRequiredCriteria());
     * </pre>
     *
     * i.e. {@link #getRequiredCriteria()} is a subset of {@code getKnownCriteria()}.
     * <p>
     * Do note that there is NO guarantee that this method will give all {@link Criterion
     * criteria} used by this {@code Recipe}; it is possible that there are "hidden"
     * criteria that are not accessible solely within this interface.
     *
     * The relation between {@link Criterion#evaluate} and {@link #similarity} is
     * undefined here.
     * </p>
     * @return A read-only collection that contains all visible criteria
     */
    Collection<Criterion> getKnownCriteria();

    /**
     * A {@code Criterion} represents a single inspection entry against a {@link CompositeFood}
     * instance. It also implements {@link Predicate} and {@link ToIntFunction} for the usage
     * in other scenarios.
     */
    interface Criterion extends Predicate<CompositeFood>, ToIntFunction<CompositeFood>
    {
        boolean matches(CompositeFood dish);

        int evaluate(CompositeFood dish);

        /**
         * {@inheritDoc}
         *
         * @implNote
         * By default, the actual implementation is delegated to {@link #matches}.
         */
        @Override
        default boolean test(CompositeFood dish)
        {
            return this.matches(dish);
        }

        /**
         * {@inheritDoc}
         *
         * @implNote
         * By default, the actual implementation is delegated to {@link #evaluate}.
         */
        @Override
        default int applyAsInt(CompositeFood dish)
        {
            return this.evaluate(dish);
        }
    }

}
