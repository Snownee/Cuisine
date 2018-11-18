package snownee.cuisine.items;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.client.CuisineItemRendering;
import snownee.cuisine.client.model.IngredientMeshDefinition;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.food.IngredientFood;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.util.Util;

public final class ItemIngredient extends ItemFood implements IModItem, CookingVessel
{
    public ItemIngredient()
    {
        super(1, false);
    }

    @Override
    public String getName()
    {
        return "ingredient";
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

    @Override
    public Item cast()
    {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomMeshDefinition(this, IngredientMeshDefinition.INSTANCE);
        ModelBakery.registerItemVariants(this, CuisineItemRendering.EMPTY_MODEL, new ResourceLocation(Cuisine.MODID, "cmaterial/cubed"), new ResourceLocation(Cuisine.MODID, "cmaterial/diced"), new ResourceLocation(Cuisine.MODID, "cmaterial/minced"), new ResourceLocation(Cuisine.MODID, "cmaterial/paste"), new ResourceLocation(Cuisine.MODID, "cmaterial/shredded"), new ResourceLocation(Cuisine.MODID, "cmaterial/sliced"));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        Ingredient ingredient;
        if (stack.getTagCompound() != null && entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            this.onFoodEaten(stack, worldIn, player);
            player.addStat(StatList.getObjectUseStats(this));

            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            }

            ingredient = CuisinePersistenceCenter.deserializeIngredient(stack.getTagCompound());
            if (ingredient != null)
            {
                IngredientFood.Builder builder = new IngredientFood.Builder();
                builder.addIngredient(null, ingredient, this);
                Optional<IngredientFood> result = builder.build(this, null);
                if (result.isPresent())
                {
                    result.get().onEaten(stack, worldIn, player);
                }
            }
        }
        stack.shrink(1);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            return false;
        }
        Ingredient ingredient = CuisinePersistenceCenter.deserializeIngredient(stack.getTagCompound());
        if (ingredient == null)
        {
            return false;
        }
        return ingredient.getMaterial().hasGlowingOverlay(ingredient);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        NBTTagCompound data = stack.getTagCompound();
        if (data != null)
        {
            Ingredient ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
            if (ingredient != null)
            {
                for (Effect effect : ingredient.getEffects())
                {
                    if (effect.showInTooltips())
                    {
                        tooltip.add(Util.color(effect.getColor()) + I18n.format(effect.getName()));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack)
    {
        return AdvancedFontRenderer.INSTANCE;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        NBTTagCompound data = stack.getTagCompound();
        if (data == null)
        {
            return I18nUtil.translate("material.unknown");
        }
        else
        {
            Ingredient ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
            return ingredient == null ? I18nUtil.translate("material.unknown") : ingredient.getTranslation();
        }
    }

    @Override
    public final void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        // No-op to avoid bad things from happening
    }

    @Override
    public Optional<ItemStack> serve()
    {
        return Optional.empty();
    }

    public static ItemStack make(Material material, Form form)
    {
        return ItemIngredient.make(material, form, 1);
    }

    public static ItemStack make(Material material, Form form, float size)
    {
        return ItemIngredient.make(material, form, size, 1, form.getStandardActions());
    }

    public static ItemStack make(Material material, Form form, float size, int amount, int[] actions)
    {
        if (material.isValidForm(form))
        {
            ItemStack stack = new ItemStack(CuisineRegistry.INGREDIENT, amount);
            Ingredient ingredient = new Ingredient(material, form, size);
            NBTTagCompound data = CuisinePersistenceCenter.serialize(ingredient);
            data.setIntArray(KEY_ACTIONS, actions);
            stack.setTagCompound(data);
            return stack;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack make(Ingredient ingredient)
    {
        ItemStack itemStack = new ItemStack(CuisineRegistry.INGREDIENT);
        itemStack.setTagCompound(CuisinePersistenceCenter.serialize(ingredient));
        return itemStack;
    }

    public static int[] getActions(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(KEY_ACTIONS, Constants.NBT.TAG_INT_ARRAY))
        {
            if (stack.getItem() == CuisineRegistry.INGREDIENT)
            {
                Cuisine.logger.warn("Found invalid ItemIngredient: {}", stack);
            }
            return new int[2];
        }
        return tag.getIntArray(KEY_ACTIONS);
    }

    /**
     * Return a {@link List} of ItemStacks that represents {@link Material} in the form that is in both
     * specified range of forms and all possible forms of given {@link Material}. Example:
     *
     * <pre>
     *     // Contains Form.CUBED, Form.SLICED, Form.SHREDDED
     *     EnumSet&lt;Form&gt; allValidForms = materialFoo.getValidForms();
     *     // Contains Form.CUBED, Form.JUICE, Form.PASTE
     *     EnumSet&lt;Form&gt; desiredForms = EnumSet.of(Form.CUBED, Form.JUICE, Form.PASTE);
     *
     *     List&lt;ItemStack&gt; result = getAllValidFormsInRange(materialFoo, desiredForms);
     *
     *     // The only element would be ItemStack that represents materialFoo with form of
     *     // Form.CUBED because intersection between allValidForms and desiredForms is a set
     *     // with one element which is Form.CUBED.
     *     assert result.size() == 1;
     * </pre>
     *
     * 返回一个包含 ItemStack 的 {@link List}，其内容代表了指定{@linkplain Material 食材类型}在所有存在形态与
     * 指定形态范围的交集中的所有存在形态。
     *
     * @param material Desired material
     * @param range All exempted Form
     *
     * @return A list of desired ItemStacks, each represents a valid form of given Material
     */
    public static List<ItemStack> getAllValidFormsInRange(Material material, EnumSet<Form> range)
    {
        EnumSet<Form> forms = range.clone();
        forms.retainAll(material.getValidForms());
        return forms.stream().map(form -> make(material, form)).collect(Collectors.toList());
    }

    /**
     * Return a {@link List} of ItemStacks that represents {@link Material} in all of its valid forms
     * expect the specified forms.
     *
     * 返回一个包含 ItemStack 的 {@link List}，其内容代表了所有除指定{@linkplain Form 形态}外，指定{@linkplain Material
     * 食材类型}允许存在的所有形态。
     *
     * @param material Desired material
     * @param exceptions All exempted Form
     *
     * @return A list of desired ItemStacks, each represents a valid form of given Material
     */
    // TODO (3TUSK): In-code explanation. Intersection is way too abstract...
    public static List<ItemStack> getAllValidFormsWithException(Material material, EnumSet<Form> exceptions)
    {
        EnumSet<Form> forms = EnumSet.complementOf(exceptions);
        forms.retainAll(material.getValidForms());
        return forms.stream().map(form -> make(material, form)).collect(Collectors.toList());
    }

    public static final String KEY_ACTIONS = "actions";
}
