package snownee.cuisine.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.kiwi.util.definition.ItemDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * A specialized {@link Ingredient} which permits real wildcard value
 * (namely {@link net.minecraftforge.oredict.OreDictionary#WILDCARD_VALUE}
 * or {@link Short#MAX_VALUE}). When a wildcard value is specified as input,
 * it will only matches the item type for incoming input, effectively
 * bypassing validations of metadata.
 * <p>
 * This specialized Ingredient exists as a hack for bypassing vanilla's
 * design choice of deprecating metadata in favor of explicitly stating
 * all inputs and the Flattening. Generally speaking, this class should
 * not exist from the beginning of the time; however, as Forge has no proper
 * ways to validate metadata besides using {@link Item#getSubItems} ({@code
 * func_150895_a}, we are unable to hide a valid sub-item from creative
 * inventory and make it valid for wildcard input at the same time. Therefore,
 * we are left with no other choices; hence the reason of {@link #isSimple()}
 * returning {@code false}.
 * </p>
 *
 * @see net.minecraft.client.util.RecipeItemHelper
 * @see net.minecraft.item.crafting.Ingredient
 * @see net.minecraftforge.oredict.OreDictionary#WILDCARD_VALUE
 *
 * @see <a href=https://github.com/MinecraftForge/MinecraftForge/commit/fc87d83d73e7b6f202c55add758d55e2d7b81307>
 *      "Make Ingredient explode wildcard values like OreIngredient."
 *     </a>
 *
 * @see <a href=https://github.com/MinecraftForge/MinecraftForge/pull/4472>
 *      "Fix the incorrect matching algorithm in ShapelessRecipes and ShapelessOreRecipe"
 *     </a>
 * @see <a href=https://github.com/MinecraftForge/MinecraftForge/issues/4516>
 *      "Recipes with damaged ingredients are broken"
 *     </a>
 * @see <a href=https://github.com/MinecraftForge/MinecraftForge/pull/4519>
 *      "Fix the unsuitable implementation of shapeless recipes"
 *     </a>
 *
 * @author 3TUSK
 */
public final class WildcardIngredient extends Ingredient
{

    /**
     * The validator used for checking matches. We delegate the implementation
     * to {@link ItemDefinition#matches(ItemStack)}.
     */
    private final ItemDefinition definition;
    /**
     * Cache of all known item stacks that respects {@link Item#getSubItems}.
     * It will be reset to {@code null} upon {@link #invalidate} is called.
     */
    private ItemStack[] cachedMatchedInputs;
    /**
     * Cache of all known item stacks that respects {@link Item#getSubItems}
     * and packed in a {@link IntList}. It will be reset to {@code null} upon
     * {@link #invalidate} is called.
     */
    private IntList cachedPackedMatchedInputs;

    /**
     * Private constructor for internal usage. Regular users can use this
     * by using a structure similar to the following in their recipe JSON:
     * <pre>
     *     {
     *         "type": "cuisine:hacked_wildcard",
     *         "item": "minecraft:diamond",
     *         "data": 32767
     *     }
     * </pre>
     */
    private WildcardIngredient(ItemDefinition definition)
    {
        super(0);
        this.definition = definition;
    }

    @Override
    public boolean apply(@Nullable ItemStack input)
    {
        return input != null && this.definition.matches(input);
    }

    @Nonnull
    @Override
    public ItemStack[] getMatchingStacks()
    {
        if (this.cachedMatchedInputs == null)
        {
            NonNullList<ItemStack> sink = NonNullList.create();
            definition.getItem().getSubItems(CreativeTabs.SEARCH, sink);
            return (this.cachedMatchedInputs = sink.toArray(new ItemStack[0]));
        }
        return this.cachedMatchedInputs;
    }

    @Nonnull
    @Override
    public IntList getValidItemStacksPacked()
    {
        if (this.cachedPackedMatchedInputs == null)
        {
            return (this.cachedPackedMatchedInputs = new IntArrayList(
                    Arrays.stream(this.getMatchingStacks())
                            .mapToInt(RecipeItemHelper::pack)
                            .sorted()
                            .toArray())
            );
        }
        return this.cachedPackedMatchedInputs;
    }

    @Override
    protected void invalidate()
    {
        this.cachedMatchedInputs = null;
        this.cachedPackedMatchedInputs = null;
    }

    /*
     * This MUST return false in order to utilize the Forge-provided input matching logic.
     * The meaning of 'MUST' here follows RFC 2119 (https://tools.ietf.org/html/rfc2119).
     */
    @Override
    public boolean isSimple()
    {
        return false;
    }

    public static final class Factory implements IIngredientFactory
    {

        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json)
        {
            String identifier = JsonUtils.getString(json, "item");
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(identifier));
            if (item == null)
            {
                throw new JsonSyntaxException("Expect 'item' to be an item identifier, was unknown item identifier '" + identifier + "'");
            }
            int meta = JsonUtils.getInt(json, "data", 0);
            return new WildcardIngredient(new ItemDefinition(item, meta));
        }
    }
}
