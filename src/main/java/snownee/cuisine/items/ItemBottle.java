package snownee.cuisine.items;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.crafting.DrinkBrewingRecipe;
import snownee.cuisine.internal.capabilities.GlassBottleWrapper;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.item.ItemMod;

public class ItemBottle extends ItemMod implements CookingVessel
{

    public ItemBottle(String name)
    {
        super(name);
        setContainerItem(Items.GLASS_BOTTLE);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (ItemNBTUtil.verifyExistence(stack, "potion"))
        {
            PotionUtils.addPotionTooltip(DrinkBrewingRecipe.makeDummyPotionItem(stack), tooltip, 1.0F);
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return super.hasEffect(stack) || ItemNBTUtil.verifyExistence(stack, "potion");
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new GlassBottleWrapper(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
        if (handler != null)
        {
            FluidStack fluid = handler.drain(Integer.MAX_VALUE, false);
            if (fluid != null)
            {
                return Cuisine.sidedDelegate.translate(getTranslationKey(stack) + ".name", fluid.getLocalizedName());
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (!(entityLiving instanceof EntityPlayer))
        {
            return stack;
        }
        EntityPlayer entityplayer = (EntityPlayer) entityLiving;

        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(copy);
        if (handler == null)
        {
            return stack;
        }
        FluidStack fluid = handler.drain(Integer.MAX_VALUE, false);
        if (fluid == null)
        {
            return stack;
        }
        Material material = CulinaryHub.API_INSTANCE.findMaterial(fluid);
        if (material == null || !material.isValidForm(Form.JUICE))
        {
            return stack;
        }
        Drink.Builder builder = Drink.Builder.create();
        builder.addIngredient(entityplayer, new Ingredient(material, Form.JUICE), this);
        Optional<Drink> result = builder.build(this, entityplayer);
        if (!result.isPresent())
        {
            return stack;
        }
        result.get().onEaten(copy, worldIn, entityplayer);
        if (!worldIn.isRemote)
        {
            for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack))
            {
                if (potioneffect.getPotion().isInstant())
                {
                    potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                }
                else
                {
                    entityLiving.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        if (!entityplayer.capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }

        if (entityplayer instanceof EntityPlayerMP)
        {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
        }

        entityplayer.addStat(StatList.getObjectUseStats(this));

        if (!entityplayer.capabilities.isCreativeMode)
        {
            if (stack.isEmpty())
            {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
        }

        return stack;
    }

    @Override
    public Optional<ItemStack> serve()
    {
        return Optional.empty();
    }

}
