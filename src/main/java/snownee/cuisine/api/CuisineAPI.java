package snownee.cuisine.api;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * Main interface that one can use to interact with Cuisine's internal.
 *
 * It provides registration and query functionality against {@link Material},
 * {@link Spice} and {@link Effect}. In addition to that, it also has
 * functionality to determine if Cuisine internals knows that a given {@link
 * ItemStack} or {@link FluidStack} instance is mapped to a certain
 * {@link Material} or {@link Spice}.
 * <p>
 * An instance of {@code CuisineAPI} is accessible, and will be available
 * after FML Pre-Initialization stage, at {@link CulinaryHub#API_INSTANCE}.
 * </p>
 */
public interface CuisineAPI
{

    // Registration method

    /**
     * Register a {@link Material} into Cuisine, so that Cuisine will recognize it.
     * The registration process follows "first come, first serve" basis; that said,
     * if two {@link Material} instances with the same {@link Material#getID()
     * identifier} are registered, only the first one registered will be recognized.
     *
     * @param material The material to be registered.
     *
     * @return The currently recognized Material object with the same identifier.
     */
    Material register(Material material);

    /**
     * Register a {@link Spice} into Cuisine, so that Cuisine will recognize it.
     * The registration process follows "first come, first serve" basis; that said,
     * if two {@link Spice} instances with the same {@link Spice#getID()
     * identifier} are registered, only the first one registered will be recognized.
     *
     * @param spice The spice to be registered.
     *
     * @return The currently recognized Spice object with the same identifier.
     */
    Spice register(Spice spice);

    /**
     * Register a {@link Effect} into Cuisine, so that Cuisine will recognize it.
     * The registration process follows "first come, first serve" basis; that said,
     * if two {@link Effect} instances with the same {@link Effect#getID()
     * identifier} are registered, only the first one registered will be recognized.
     *
     * @param effect The effect to be registered.
     *
     * @return The currently recognized Effect object with the same identifier.
     */
    Effect register(Effect effect);

    /**
     * Register a {@link Recipe} into Cuisine, so that Cuisine will recognize it.
     * The registration process follows "first come, first serve" basis; that said,
     * if two {@link Recipe} instances with the same {@link Recipe#name()
     * identifier} are registered, only the first one registered will be recognized.
     *
     * @param recipe The recipe to be registered.
     *
     * @return The currently recognized Recipe object with the same identifier.
     */
    Recipe register(Recipe recipe);

    // Serialization service

    /**
     * Register a new sub-type of {@link CompositeFood} with a unique identifier
     * associated with it, as well as corresponding serializer and deserializer.
     *
     * @param typeToken the Class object that represents concrete type of CompositeFood
     * @param uniqueLocator the unique identifier for identification purpose
     * @param serializer the corresponding serializer
     * @param deserializer the corresponding deserializer
     *
     * @param <F> the concrete type of CompositeFood
     */
    <F extends CompositeFood> void registerFoodType(ResourceLocation uniqueLocator,
                                                    Class<F> typeToken,
                                                    Function<F, NBTTagCompound> serializer,
                                                    Function<NBTTagCompound, F> deserializer);
    // TODO Do we really need that typeToken?

    /**
     *
     * @param dishObject the actual CompositeFood object being serialized
     *
     * @param <F> the concrete type of CompositeFood
     *
     * @return a NBTTagCompound instance that contains serialized data for dishObject
     */
    <F extends CompositeFood> NBTTagCompound serialize(F dishObject);

    /**
     *
     * @param identifier the unique identifier for identification purpose
     * @param data the NBT data that represents a serialized CompositeFood instance
     *
     * @param <F> the concrete type of CompositeFood
     *
     * @return the corresponding CompositeFood object
     */
    @Nullable
    <F extends CompositeFood> F deserialize(ResourceLocation identifier, NBTTagCompound data);

    // Bulk registry candidates accessor

    /**
     * Return a read-only {@link Collection} that contains all {@link Material}
     * that Cuisine API knows about.
     *
     * @return A collection of registered Material
     */
    Collection<Material> getKnownMaterials();

    /**
     * Return a read-only {@link Collection} that contains all {@link Spice}
     * that Cuisine API knows about.
     *
     * @return A collection of registered Spice
     */
    Collection<Spice> getKnownSpices();

    /**
     * Return a read-only {@link Collection} that contains all {@link Effect}
     * that Cuisine API knows about.
     *
     * @return A collection of registered Effect
     */
    Collection<Effect> getKnownEffects();

    // Query methods

    /**
     * Query the whole registry and return a best-matched {@link Recipe} instance
     * based on the {@link CompositeFood} instance given.
     *
     * @return the matched Recipe instance; if match failed, returns null.
     *
     * @deprecated WIP, do not use
     */
    @Deprecated default @Nullable Recipe tryMatchRecipe(CompositeFood food) { return null; } // TODO

    /**
     * Query the whole registry and find the desired {@link Material} object
     * based on the unique name supplied.
     *
     * @param uniqueId The unique id of material
     *
     * @return The Material reference that has given unique id; null if not found.
     */
    Material findMaterial(String uniqueId);

    /**
     * Query the whole registry and find the desired {@link Spice} object
     * based on the unique name supplied.
     *
     * @param uniqueId The unique id of spice
     *
     * @return The Spice reference that has given unique id; null if not found.
     */
    Spice findSpice(String uniqueId);

    /**
     * Query the whole registry and find the desired {@link Effect} object
     * based on the unique name supplied.
     *
     * @param uniqueId The unique id of effect
     *
     * @return The Effect reference that has given unique id; null if not found.
     */
    Effect findEffect(String uniqueId);

    /**
     * Query the whole registry and find the desired {@link Material} object
     * based on the ItemStack supplied. The method will look up for item and
     * metadata combination first, and fall back to Ore Dictionary if not found
     * in the first round.
     *
     * @param item the ItemStack instance
     *
     * @return The Material reference that given item is mapped to; null if not found.
     *
     * @deprecated Thought working correctly, this method is not as descriptive as
     *             that of {@link #findIngredient(ItemStack)}. Use the more precise
     *             {@link #findIngredient(ItemStack)} instead.
     */
    @Deprecated
    default Material findMaterial(ItemStack item)
    {
        Ingredient ingredient = this.findIngredient(item);
        return ingredient == null ? null : ingredient.getMaterial();
    }

    /**
     * Query the whole registry and find the desired {@link Material} object
     * based on the FluidStack supplied.
     *
     * @param fluid the FluidStack instance
     *
     * @return The Material reference that given item is mapped to; null if not found.
     *
     * @deprecated Thought working correctly, this method is not as descriptive as
     *             that of {@link #findIngredient(FluidStack)}. Use the more precise
     *             {@link #findIngredient(FluidStack)} instead.
     */
    @Deprecated
    default Material findMaterial(@Nullable FluidStack fluid)
    {
        Ingredient ingredient = this.findIngredient(fluid);
        return ingredient == null ? null : ingredient.getMaterial();
    }

    /**
     * Query the whole registry and find the desired {@link Spice} object
     * based on the ItemStack supplied. The method will look up for item and
     * metadata combination first, and fall back to Ore Dictionary if not found
     * in the first round.
     *
     * @param item the ItemStack instance
     *
     * @return The Spice reference that given item is mapped to; null if not found.
     */
    Spice findSpice(ItemStack item);

    /**
     * Query the whole registry and find the desired {@link Material} object
     * based on the ItemStack supplied. The method will look up for Fluid
     * reference only.
     *
     * @param fluid the FluidStack instance
     *
     * @return The Material reference that given fluid is mapped to; null if not found.
     */
    Spice findSpice(@Nullable FluidStack fluid);

    /**
     * Query the whole registry and find whether the given item is mapped to
     * a certain {@link Material}.
     *
     * @param item the ItemStack instance
     *
     * @return true if there is an associated Material; false otherwise.
     */
    boolean isKnownMaterial(ItemStack item);

    /**
     * Query the whole registry and find whether the given fluid is mapped to
     * a certain {@link Material}.
     *
     * @param fluid the FluidStack instance
     *
     * @return true if there is an associated Material; false otherwise.
     */
    boolean isKnownMaterial(@Nullable FluidStack fluid);

    /**
     * Query the whole registry and find whether the given item is mapped to
     * a certain {@link Spice}.
     *
     * @param item the ItemStack instance
     *
     * @return true if there is an associated Spice; false otherwise.
     */
    boolean isKnownSpice(ItemStack item);

    /**
     * Query the whole registry and find whether the given fluid is mapped to
     * a certain {@link Material}.
     *
     * @param fluid the FluidStack instance
     *
     * @return true if there is an associated Material; false otherwise.
     */
    boolean isKnownSpice(@Nullable FluidStack fluid);

    // TODO (3TUSK): Documentation
    @Nullable Ingredient findIngredient(ItemStack item);

    // TODO (3TUSK): Documentation
    @Nullable Ingredient findIngredient(@Nullable FluidStack fluid);

}
