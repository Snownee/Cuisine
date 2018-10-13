package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.client.model.DishMeshDefinition;
import snownee.cuisine.internal.capabilities.DrinkContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.proxy.ClientProxy;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.Kiwi;
import toughasnails.api.TANCapabilities;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.IThirst;

public class ItemDrink extends ItemAbstractComposite
{

    public ItemDrink(String name)
    {
        super(name);
        // Creative tab
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomMeshDefinition(this, DishMeshDefinition.INSTANCE);
        ModelBakery.registerItemVariants(this, ClientProxy.EMPTY, new ResourceLocation(Cuisine.MODID, "dish/drink"), new ResourceLocation(Cuisine.MODID, "dish/smoothie"), new ResourceLocation(Cuisine.MODID, "dish/gelo"), new ResourceLocation(Cuisine.MODID, "dish/soda"));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new DrinkContainer();
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = ItemNBTUtil.getString(stack, "customName", "");
        if (!s.isEmpty())
        {
            return s;
        }
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood drink = container.get();
            if (drink != null && drink.getClass() == Drink.class)
            {
                List<Ingredient> ingredients = drink.getIngredients();
                if (ingredients.size() == 1)
                {
                    if (((Drink) drink).getDrinkType() == DrinkType.NORMAL)
                    {
                        return ingredients.get(0).getTranslation();
                    }
                    else
                    {
                        return I18n.format(((Drink) drink).getDrinkType().getTranslationKey() + ".specific", I18n.format(ingredients.get(0).getMaterial().getTranslationKey()));
                    }
                }
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood drink = container.get();
            if (drink != null && drink.getClass() == Drink.class)
            {
                return ((Drink) drink).getDrinkType().getTranslationKey();
            }
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood drink = container.get();
            if (drink != null && drink.getClass() == Drink.class && ((Drink) drink).getDrinkType() == DrinkType.SMOOTHIE)
            {
                return EnumAction.EAT;
            }
        }
        return EnumAction.DRINK;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!Kiwi.isOptionalModuleLoaded(Cuisine.MODID, "toughasnails"))
        {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
        ItemStack stack = playerIn.getHeldItem(handIn);
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood drink;
        if (container == null || (drink = container.get()) == null)
        {
            return new ActionResult<>(EnumActionResult.FAIL, ItemStack.EMPTY);
        }
        IThirst thirst = playerIn.getCapability(TANCapabilities.THIRST, null);
        if (thirst == null)
        {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (drink.alwaysEdible() || thirst.getThirst() < 20)
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
            CompositeFood drink = foodContainer.get();
            if (drink == null)
            {
                stack.setCount(0);
                return stack;
            }

            if (!Kiwi.isOptionalModuleLoaded(Cuisine.MODID, "toughasnails") || !SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
            {
                player.getFoodStats().addStats(Math.min((int) (drink.getFoodLevel() * 0.5), 2), drink.getSaturationModifier());
            }

            drink.setServes(drink.getServes() - 1);
            drink.onEaten(stack, worldIn, player);
            player.addStat(StatList.getObjectUseStats(this));

            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            }

            if (drink.getServes() < 1)
            {
                return foodContainer.getEmptyContainer(stack); // Return the container back
            }
        }

        return stack;
    }
}
