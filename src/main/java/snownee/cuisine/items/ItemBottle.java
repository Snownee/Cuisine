package snownee.cuisine.items;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.crafting.DrinkBrewingRecipe;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.capabilities.GlassBottleWrapper;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.util.I18nUtil;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.util.NBTHelper;

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
            PotionUtils.addPotionTooltip(DrinkBrewingRecipe.makeDummyPotionItem(stack), tooltip, CuisineConfig.GENERAL.winePotionDurationModifier);
            String id = getMaterial(stack);
            if ("corn".equals(id))
            {
                tooltip.add(I18nUtil.translate("bourbon"));
            }
            else if ("orange".equals(id) || "lime".equals(id) || "mandarin".equals(id))
            {
                tooltip.add(I18nUtil.translate("curasao"));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        if (ItemNBTUtil.verifyExistence(stack, "liquidColor"))
        {
            return false;
        }
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
        if (ItemNBTUtil.verifyExistence(stack, "potion"))
        {
            String id = getMaterial(stack);
            if (id != null)
            {
                Material material = CulinaryHub.API_INSTANCE.findMaterial(id);
                if (material != null)
                {
                    return I18nUtil.translate("wine.specific", I18n.format(material.getTranslationKey()));
                }
            }
            return I18nUtil.translate("wine.name");
        }
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

    @Nullable
    private static String getMaterial(ItemStack stack)
    {
        NBTTagCompound compound = ItemNBTUtil.getCompound(stack, "Fluid", true);
        if (compound != null && compound.hasKey("Tag", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound tag = compound.getCompoundTag("Tag");
            if (tag.hasKey(CuisineSharedSecrets.KEY_MATERIAL, Constants.NBT.TAG_STRING))
            {
                return tag.getString(CuisineSharedSecrets.KEY_MATERIAL);
            }
        }
        return null;
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

        String id = NBTHelper.of(stack).getString("Fluid.Tag.material", "");
        Material material = CulinaryHub.API_INSTANCE.findMaterial(id);
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
        result.get().onEaten(stack, worldIn, entityplayer);
        if (!worldIn.isRemote)
        {
            ItemStack dummy = DrinkBrewingRecipe.makeDummyPotionItem(stack);
            int duration = 0;
            for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(dummy))
            {
                potioneffect.duration *= CuisineConfig.GENERAL.winePotionDurationModifier;
                duration += potioneffect.duration;
                if (potioneffect.getPotion().isInstant())
                {
                    potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), CuisineConfig.GENERAL.winePotionDurationModifier);
                }
                else
                {
                    entityLiving.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
            if (duration > 0)
            {
                int amplifier = 0;
                PotionEffect potionEffect = entityLiving.getActivePotionEffect(CuisineRegistry.DRUNK);
                if (potionEffect != null)
                {
                    duration += potionEffect.duration;
                    amplifier = potionEffect.getAmplifier() + 1;
                    if (amplifier > 2)
                    {
                        amplifier = 2;
                    }
                }
                entityLiving.addPotionEffect(new PotionEffect(CuisineRegistry.DRUNK, duration, amplifier, true, true));
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
