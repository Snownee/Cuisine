package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityCow;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Spice;
import snownee.cuisine.api.events.SpiceBottleContentConsumedEvent;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.util.I18nUtil;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.util.OreUtil;
import snownee.kiwi.util.PlayerUtil;

public class ItemSpiceBottle extends ItemMod
{
    public static class SpiceItemHandler extends ItemStackHandler
    {
        private final ItemStack container;

        public SpiceItemHandler(ItemStack container)
        {
            this.container = container;
            NBTTagCompound nbt = container.getTagCompound();
            if (nbt != null)
            {
                deserializeNBT(nbt);
            }
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!CulinaryHub.API_INSTANCE.isKnownSpice(stack))
            {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            boolean empty = getStackInSlot(0).isEmpty();
            container.setTagCompound(empty ? null : serializeNBT());
            if (!empty)
            {
                ItemNBTUtil.setInt(container, TAG_VOLUME, MAX_VOLUME);
            }
            container.setItemDamage(empty ? 0 : 1);
        }
    }

    public static class SpiceFluidHandler extends FluidHandlerItemStack
    {
        public SpiceFluidHandler(ItemStack container, int capacity)
        {
            super(container, capacity);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            return !fluid.getFluid().isGaseous(fluid) && fluid.getFluid().getTemperature(fluid) < 400 && super.canFillFluidType(fluid);
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.setTagCompound(null);
        }
    }

    public static final String TAG_VOLUME = "volume";
    public static final int MAX_VOLUME = 10;

    public ItemSpiceBottle(String name)
    {
        super(name);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setHasSubtypes(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void mapModel()
    {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "fluid"));
        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName(), "item"));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new SpiceFluidHandler(stack, Fluid.BUCKET_VOLUME);
    }

    @Nullable
    public SpiceItemHandler getItemHandler(ItemStack stack)
    {
        if (!hasFluid(stack))
        {
            return new SpiceItemHandler(stack);
        }
        return null;
    }

    @Nullable
    public IFluidHandlerItem getFluidHandler(ItemStack stack)
    {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    @Nullable
    public Spice getSpice(ItemStack stack)
    {
        if (hasItem(stack))
        {
            IItemHandler handler = getItemHandler(stack);
            if (handler != null)
            {
                return CulinaryHub.API_INSTANCE.findSpice(handler.getStackInSlot(0));
            }
        }
        else if (hasFluid(stack))
        {
            FluidStack fluidStack = getFluidHandler(stack).drain(Integer.MAX_VALUE, false);
            if (fluidStack != null)
            {
                return CulinaryHub.API_INSTANCE.findSpice(fluidStack);
            }
        }
        return null;
    }

    public boolean isContainerEmpty(ItemStack stack)
    {
        return !hasFluid(stack) && !hasItem(stack);
    }

    public boolean hasFluid(ItemStack stack)
    {
        if (!stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            return false;
        }
        return getFluidHandler(stack).drain(Integer.MAX_VALUE, false) != null;
    }

    public boolean hasItem(ItemStack stack)
    {
        return stack.getMetadata() == 1;
    }

    public boolean consume(ItemStack stack, int amount)
    {
        if (amount <= MAX_VOLUME && amount > 0)
        {
            if (hasFluid(stack))
            {
                IFluidHandlerItem handler = getFluidHandler(stack);
                int amountFluid = Fluid.BUCKET_VOLUME * amount / MAX_VOLUME;
                FluidStack fluidStack = handler.drain(amountFluid, false);
                if (fluidStack != null && fluidStack.amount == amountFluid)
                {
                    handler.drain(amountFluid, true);
                    return true;
                }
            }
            else if (hasItem(stack))
            {
                int volume = ItemNBTUtil.getInt(stack, TAG_VOLUME, 0);
                if (volume >= amount)
                {
                    setDurability(stack, volume - amount);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return isContainerEmpty(stack) ? super.getItemStackLimit(stack) : 1;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return !isContainerEmpty(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return hasContainerItem(itemStack) ? new ItemStack(this) : ItemStack.EMPTY;
    }

    public int getDurability(ItemStack stack)
    {
        return ItemNBTUtil.getInt(stack, TAG_VOLUME, 0);
    }

    public void setDurability(ItemStack stack, int durability)
    {
        if (durability > 0)
        {
            ItemNBTUtil.setInt(stack, TAG_VOLUME, durability);
            stack.setItemDamage(1);
        }
        else
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return (hasItem(stack) && getDurability(stack) != MAX_VOLUME) || (hasFluid(stack) && getFluidHandler(stack).drain(Integer.MAX_VALUE, false).amount < Fluid.BUCKET_VOLUME);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        if (hasFluid(stack))
        {
            FluidStack fluidStack = getFluidHandler(stack).drain(Integer.MAX_VALUE, false);
            if (fluidStack == null)
            {
                return 0;
            }
            return 1 - fluidStack.amount / (double) Fluid.BUCKET_VOLUME;
        }
        else
        {
            return 1 - getDurability(stack) / (double) MAX_VOLUME;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (hasItem(stack))
        {
            tooltip.add(I18nUtil.translateWithFormat("tip.spice.item", getDurability(stack)));
        }
        else if (hasFluid(stack))
        {
            FluidStack fluidStack = getFluidHandler(stack).drain(Integer.MAX_VALUE, false);
            tooltip.add(I18nUtil.translate("tip.spice.fluid", fluidStack.amount));
        }
        else
        {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if (hasItem(stack))
        {
            SpiceItemHandler handler = getItemHandler(stack);
            if (handler != null)
            {
                return handler.getStackInSlot(0).getDisplayName();
            }
        }
        else if (hasFluid(stack))
        {
            return getFluidHandler(stack).drain(Integer.MAX_VALUE, false).getLocalizedName();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (!isContainerEmpty(held))
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(getItemUseAction(held) == EnumAction.NONE ? EnumActionResult.PASS : EnumActionResult.SUCCESS, held);
        }

        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
        if (raytraceresult != null)
        {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                FluidActionResult result = FluidUtil.tryPickUpFluid(held, playerIn, worldIn, raytraceresult.getBlockPos(), raytraceresult.sideHit);
                if (result.isSuccess())
                {
                    held.shrink(1);
                    return PlayerUtil.mergeItemStack(result.getResult(), playerIn, handIn);
                }
                BlockPos offset = new BlockPos(0, 0, 0).offset(raytraceresult.sideHit);
                Vec3d hit = raytraceresult.hitVec.add(offset.getX() * 0.5, offset.getY() * 0.5, offset.getZ() * 0.5);

                List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(hit.x - 0.5, hit.y - 0.5, hit.z - 0.5, hit.x + 0.5, hit.y + 0.5, hit.z + 0.5));
                ItemStack heldCopy = held.copy();
                heldCopy.setCount(1);
                IItemHandler handler = getItemHandler(heldCopy);
                for (EntityItem item : items)
                {
                    ItemStack stack = item.getItem();

                    if (handler != null && handler.getSlots() > 0 && handler.insertItem(0, stack, false).isEmpty())
                    {
                        worldIn.playSound(playerIn, item.posX, item.posY, item.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                        item.setDead();
                        held.shrink(1);
                        return PlayerUtil.mergeItemStack(heldCopy, playerIn, handIn);
                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, held);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (isContainerEmpty(stack))
        {
            return stack;
        }
        if (entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entityLiving;
            entityplayermp.addStat(StatList.getObjectUseStats(this));
        }
        Object content;
        if (hasItem(stack))
        {
            content = getItemHandler(stack).getStackInSlot(0);
        }
        else
        {
            content = getFluidHandler(stack).drain(Integer.MAX_VALUE, false);
        }
        if (consume(stack, MAX_VOLUME))
            MinecraftForge.EVENT_BUS.post(new SpiceBottleContentConsumedEvent(worldIn, entityLiving, stack, content, MAX_VOLUME));
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        if (hasItem(stack) && getDurability(stack) == MAX_VOLUME)
        {
            return EnumAction.DRINK;
        }
        else if (hasFluid(stack))
        {
            FluidStack fluidStack = getFluidHandler(stack).drain(Integer.MAX_VALUE, false);
            return fluidStack != null && fluidStack.amount == Fluid.BUCKET_VOLUME ? EnumAction.DRINK : EnumAction.NONE;
        }
        return EnumAction.NONE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            ItemStack stack = new ItemStack(this);
            items.add(stack.copy());
            CuisineInternalGateway.INSTANCE.fluidToSpiceMapping.keySet().forEach(f -> {
                Fluid fluid = FluidRegistry.getFluid(f);
                if (fluid != null)
                {
                    ItemStack copy = stack.copy();
                    getFluidHandler(copy).fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
                    items.add(copy);
                }
            });
            CuisineInternalGateway.INSTANCE.itemToSpiceMapping.keySet().forEach(id -> {
                ItemStack copy = stack.copy();
                getItemHandler(copy).insertItem(0, id.getItemStack(), false);
                items.add(copy);
            });
            CuisineInternalGateway.INSTANCE.oreDictToSpiceMapping.keySet().forEach(ore -> {
                ItemStack copy = stack.copy();
                getItemHandler(copy).insertItem(0, OreUtil.getPreferredItemFromOre(ore), false);
                items.add(copy);
            });
        }
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteract event)
    {
        Entity target = event.getTarget();
        if (target.getClass() != EntityCow.class || ((EntityCow) target).isChild())
        {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        if (player.capabilities.isCreativeMode)
        {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!FluidRegistry.isFluidRegistered("milk") || stack.getItem() != this || !isContainerEmpty(stack))
        {
            return;
        }
        ItemStack copy = stack.copy();
        copy.setCount(1);
        stack.shrink(1);
        getFluidHandler(copy).fill(new FluidStack(FluidRegistry.getFluid("milk"), Fluid.BUCKET_VOLUME), true);
        player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
        PlayerUtil.mergeItemStack(copy, player, event.getHand());
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }
}
