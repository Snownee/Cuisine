package snownee.cuisine.items;

import java.util.List;
import java.util.Set;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.events.ConsumeCompositeFoodEvent;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.util.NBTHelper;
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
            final String type;
            if (stack.getTagCompound() == null || (type = stack.getTagCompound().getString(CuisineSharedSecrets.KEY_TYPE)).isEmpty())
            {
                return stack;
            }
            CompositeFood dish = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(type), stack.getTagCompound());
            if (dish == null)
            {
                stack.setCount(0);
                return stack;
            }
            ConsumeCompositeFoodEvent.Pre pre = new ConsumeCompositeFoodEvent.Pre(dish, player, null);
            if (!MinecraftForge.EVENT_BUS.post(pre) && pre.getResult() != Event.Result.DENY)
            {
                if (!player.isCreative())
                {
                    dish.setServes(dish.getServes() - 1);
                }
                worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                dish.onEaten(stack, worldIn, player);
                player.addStat(StatList.getObjectUseStats(this));

                ConsumeCompositeFoodEvent.Post post = new ConsumeCompositeFoodEvent.Post(dish, player, null);
                MinecraftForge.EVENT_BUS.post(post);

                if (player instanceof EntityPlayerMP)
                {
                    CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
                }

                if (dish.getServes() < 1)
                {
                    stack.setTagCompound(null);
                    return stack; // TODO (3TUSK): is it how it works?
                    //return foodContainer.getEmptyContainer(stack); // Return the container back
                }
            }
            else
            {
                return stack;
            }
        }

        return stack;
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = NBTHelper.of(stack).getString("customName", "");
        return s.isEmpty() ? super.getItemStackDisplayName(stack) : s;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound data;
        CompositeFood dish;
        if ((data = stack.getTagCompound()) == null || (dish = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE)), data)) == null)
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
            String key = "tip." + (dish.getKeywords().contains("drink") ? "drink" : "food") + "_serve_amount";
            tooltip.add(I18nUtil.translateWithFormat(key, dish.getServes()));

            for (Effect effect : dish.getMergedEffects())
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
        return (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && stack.hasTagCompound();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack)
    {
        final NBTTagCompound data;
        if ((data = stack.getTagCompound()) == null)
        {
            return 0;
        }
        CompositeFood dish = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE)), data);
        if (dish != null)
        {
            return MathHelper.clamp(1 - dish.getServes() / (double) dish.getMaxServes(), 0, 1);
        }
        return 0;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        final NBTTagCompound data;
        if ((data = stack.getTagCompound()) != null)
        {
            CompositeFood dish = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE)), data);
            if (dish != null)
            {
                return Math.max((int) (getDefaultItemUseDuration() * dish.getUseDurationModifier()), 1);
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
        NBTTagCompound data = stack.getTagCompound();
        if (data == null)
        {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        CompositeFood dish = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE)), data);;
        if (dish == null)
        {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (playerIn.isCreative() || playerIn.canEat(dish.alwaysEdible()))
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

    /*@Override // TODO (3TUSK): We need to re-evaluate this
    public ItemStack getContainerItem(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            return container.getEmptyContainer(stack);
        }
        return ItemStack.EMPTY;
    }*/
}
