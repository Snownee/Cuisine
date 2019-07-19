package snownee.cuisine.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Holder of all capabilities used by Cuisine.
 *
 * 持有所有 Cuisine 使用的 Capability 的引用的类。
 */
public final class CulinaryCapabilities
{

    /**
     * The singleton {@link Capability} instance of {@link FoodContainer}.
     *
     * {@link FoodContainer} 对应的 {@link Capability} 单例引用。
     *
     * @deprecated Due to synchronization issue, this capability is now obsolete. DO NOT USE.
     */
    @CapabilityInject(FoodContainer.class)
    @Deprecated
    public static Capability<FoodContainer> FOOD_CONTAINER;

    /**
     * The singleton {@link Capability} instance of {@link CulinarySkillPointContainer}.
     *
     * {@link CulinarySkillPointContainer} 对应的 {@link Capability} 单例引用。
     */
    @CapabilityInject(CulinarySkillPointContainer.class)
    public static Capability<CulinarySkillPointContainer> CULINARY_SKILL;

    /**
     * Always-fail constructor to prevent instantiation.
     *
     * 阻止实例化的构造器，一定会抛出异常。
     */
    private CulinaryCapabilities()
    {
        throw new UnsupportedOperationException("No instance for you");
    }
}
