package snownee.cuisine.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

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

    void register(Material material);

    void register(Spice spice);

    void register(Effect effect);

    void register(Recipe recipe);

    Collection<Material> getKnownMaterials();

    Collection<Spice> getKnownSpices();

    Collection<Effect> getKnownEffects();

    Material findMaterial(String uniqueId);

    Spice findSpice(String uniqueId);

    Effect findEffect(String uniqueId);

    Material findMaterial(ItemStack item);

    Spice findSpice(ItemStack item);

    Spice findSpice(FluidStack fluid);

    boolean isKnownMaterial(ItemStack item);

    boolean isKnownSpice(ItemStack item);

    boolean isKnownSpice(FluidStack fluid);

}
