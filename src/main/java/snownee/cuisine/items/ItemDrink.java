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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.client.CuisineItemRendering;
import snownee.cuisine.client.model.DishMeshDefinition;
import snownee.cuisine.internal.capabilities.DrinkContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.plugins.TANCompat;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.Kiwi;

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
        ModelBakery.registerItemVariants(this, CuisineItemRendering.EMPTY_MODEL, new ResourceLocation(Cuisine.MODID, "dish/drink"), new ResourceLocation(Cuisine.MODID, "dish/smoothie"), new ResourceLocation(Cuisine.MODID, "dish/gelo"), new ResourceLocation(Cuisine.MODID, "dish/soda"));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new DrinkContainer();
    }

    @Nonnull
    @Override
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
        if (!Loader.isModLoaded("toughasnails") || !Kiwi.isOptionalModuleLoaded(Cuisine.MODID, "toughasnails") || !TANCompat.enableThirst())
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
        if (drink.alwaysEdible() || TANCompat.isPlayerThirsty(playerIn))
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
