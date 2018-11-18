package snownee.cuisine.items;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.util.I18nUtil;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.util.Util;

public abstract class ItemAbstractComposite extends ItemMod
{

    public ItemAbstractComposite(String name)
    {
        super(name);
        // Don't stack together, because reasons.
        setMaxStackSize(1);
    }

    /*
     * Copy of ItemFood::onItemUseFinish. Change:
     *   1. Change the ItemStack::shrink call to CompositeFood::setServes
     *      call, which also reflects back to ItemStack later
     *   2. FoodStats::addStats call changed to (int, float) version
     *      because this is not `ItemFood`
     *   3. Item::onFoodEaten call redirect to CompositeFood::onEaten
     */
    @Nonnull
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            FoodContainer foodContainer = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            if (foodContainer == null)
            {
                return stack;
            }
            CompositeFood dish = foodContainer.get();
            if (dish == null)
            {
                stack.setCount(0);
                return stack;
            }

            dish.setServes(dish.getServes() - 1);
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            dish.onEaten(stack, worldIn, player);
            player.addStat(StatList.getObjectUseStats(this));

            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            }

            if (dish.getServes() < 1)
            {
                return foodContainer.getEmptyContainer(stack); // Return the container back
            }
        }

        return stack;
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        NBTTagCompound data = new NBTTagCompound();
        if (stack.getTagCompound() != null)
        {
            data.setTag("default", stack.getTagCompound());
        }
        FoodContainer foodContainer = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood food;
        if (foodContainer != null && (food = foodContainer.get()) != null)
        {
            // If anyone is looking at this: yes, syncing the entire cap. data may
            // lead to large bandwidth resource cost, but in order to correctly
            // implement the behavior of dish tooltip and the behavior of dish-
            // sensitive item model (also the recipe-sensitive item model in the
            // future), there is no way around, due to the fact that cap. data are
            // not magically synced over to client.
            // To preserve original behavior, we also sync the NBT data of ItemStack
            // itself, if exists.
            // If you have ideas regarding improvements please let us know.
            //
            // Thought left by 3TUSK: if we don't display tooltips, the only thing
            // client needs to know is the "dish type" (or recipe name in the future),
            // which means that we could just sync a recipe name to client. Perhaps
            // we can add a config option for this?
            data.setTag("dish", CulinaryHub.API_INSTANCE.serialize(food));
        }
        return data;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return;
        }
        if (nbt.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            FoodContainer foodContainer = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            if (foodContainer != null)
            {
                NBTTagCompound data = nbt.getCompoundTag("dish");
                ResourceLocation id = new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE));
                foodContainer.set(CulinaryHub.API_INSTANCE.deserialize(id, data));
                if (foodContainer.get() == null)
                {
                    // Backward compatibility: assume failed-to-load data are from previous version.
                    // See TileDish.readFromNBT for more info
                    foodContainer.set(Dish.deserialize(data));
                }
            }
        }
        if (nbt.hasKey("default"))
        {
            super.readNBTShareTag(stack, nbt.getCompoundTag("default"));
        }
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = ItemNBTUtil.getString(stack, "customName", "");
        return s.isEmpty() ? super.getItemStackDisplayName(stack) : s;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood dish;
        if (container == null || (dish = container.get()) == null)
        {
            tooltip.add(I18nUtil.translate("tip.empty_dish"));
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
        {
            List<Ingredient> ingredients = dish.getIngredients();
            if (!ingredients.isEmpty())
            {
                tooltip.add(I18nUtil.translate("tip.ingredients"));

                for (Ingredient ingredient : ingredients)
                {
                    StringBuilder line = new StringBuilder("  " + ingredient.getTranslation());
                    Set<IngredientTrait> characteristics = ingredient.getAllTraits();
                    if (!characteristics.isEmpty())
                    {
                        line.append(" ").append(TextFormatting.ITALIC);
                    }
                    for (IngredientTrait characteristic : characteristics)
                    {
                        line.append(" ").append(I18n.format(characteristic.getTranslationKey()));
                    }
                    tooltip.add(line.toString());
                }
            }

            List<Seasoning> seasonings = dish.getSeasonings();
            if (!seasonings.isEmpty())
            {
                if (!ingredients.isEmpty())
                {
                    tooltip.add("");
                }
                tooltip.add(I18nUtil.translate("tip.seasonings"));

                for (Seasoning seasoning : seasonings)
                {
                    tooltip.add("  " + I18n.format(seasoning.getSpice().getTranslationKey()) + " * " + seasoning.getSize());
                }
            }
        }
        else
        {
            tooltip.add(I18nUtil.translateWithFormat("tip.food_serve_amount", dish.getServes()));

            Set<Effect> effects = dish.getIngredients().stream().map(Ingredient::getEffects).flatMap(Set::stream).collect(Collectors.toSet());
            effects.addAll(dish.getEffects());
            for (Effect effect : effects)
            {
                if (effect.showInTooltips())
                {
                    tooltip.add(Util.color(effect.getColor()) + I18n.format(effect.getName()));
                }
            }

            tooltip.add(TextFormatting.WHITE + TextFormatting.ITALIC.toString() + I18nUtil.translate("tip.shift_ingredients"));

            if (flagIn.isAdvanced() && Minecraft.getMinecraft().gameSettings.showDebugInfo)
            {
                tooltip.add(I18nUtil.translate("tip.food.hunger_regen", dish.getFoodLevel()));
                tooltip.add(I18nUtil.translateWithFormat("tip.food.saturation", dish.getSaturationModifier()));
            }
        }
    }

    @Override
    public final void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        // No-op
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack)
    {
        // Your capability.
        return stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null) && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container == null)
        {
            return 0;
        }
        CompositeFood dish = container.get();
        if (dish != null)
        {
            return MathHelper.clamp(1 - dish.getServes() / (double) dish.getMaxServes(), 0, 1);
        }
        return 0;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood dish = container.get();
            if (dish != null)
            {
                return (int) (getDefaultItemUseDuration() * dish.getUseDurationModifier());
            }
        }
        return 0;
    }

    protected int getDefaultItemUseDuration()
    {
        return 32;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.EAT;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood dish;
        if (container == null || (dish = container.get()) == null)
        {
            return new ActionResult<>(EnumActionResult.FAIL, ItemStack.EMPTY);
        }
        if (playerIn.canEat(dish.alwaysEdible()))
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        else
        {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack)
    {
        return AdvancedFontRenderer.INSTANCE;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            return container.getEmptyContainer(stack);
        }
        return ItemStack.EMPTY;
    }
}
