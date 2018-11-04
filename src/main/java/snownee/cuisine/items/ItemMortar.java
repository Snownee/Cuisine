package snownee.cuisine.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileMortar;
import snownee.kiwi.item.ItemModVariants;
import snownee.kiwi.util.PlayerUtil;
import snownee.kiwi.util.VariantsHolder;

public class ItemMortar extends ItemModVariants
{
    public class MortarFluidWrapper implements IFluidHandlerItem, ICapabilityProvider
    {
        protected ItemStack container;

        public MortarFluidWrapper(@Nonnull ItemStack container)
        {
            this.container = container;
        }

        @Nullable
        public FluidStack getFluid()
        {
            int meta = container.getMetadata();
            if (meta == Variants.WATER.getMeta())
            {
                return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
            }
            else
            {
                return null;
            }
        }

        protected void setFluid(@Nullable FluidStack fluidStack)
        {
            container = container.copy();
            container.setCount(1);
            container.setItemDamage(fluidStack == null ? Variants.EMPTY.getMeta() : Variants.WATER.getMeta());
        }

        public boolean canFillFluidType(FluidStack fluid)
        {
            return fluid != null && fluid.getFluid() == FluidRegistry.WATER;
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource == null || resource.amount < Fluid.BUCKET_VOLUME || getFluid() != null || !canFillFluidType(resource))
            {
                return 0;
            }

            if (doFill)
            {
                setFluid(resource);
            }

            return Fluid.BUCKET_VOLUME;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            if (resource == null || getFluid() == null || !getFluid().isFluidEqual(resource))
            {
                return null;
            }
            return drain(resource.amount, doDrain);
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (maxDrain < Fluid.BUCKET_VOLUME || getFluid() == null)
            {
                return null;
            }
            FluidStack fluidStack = getFluid();
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return container.getMetadata() != Variants.SALT.getMeta() && capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if (container.getMetadata() != Variants.SALT.getMeta() && capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
            {
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
            }
            return null;
        }

        @Override
        public ItemStack getContainer()
        {
            return container;
        }

    }

    public ItemMortar(String name, Block block)
    {
        super(name, Variants.INSTANCE);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setContainerItem(this);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new MortarFluidWrapper(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (stack.getMetadata() == Variants.SALT.getMeta())
        {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
        if (raytraceresult == null)
        {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos blockpos = raytraceresult.getBlockPos();

            if (worldIn.getBlockState(blockpos).getBlock() != Blocks.WATER)
            {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }

            worldIn.playSound(playerIn, playerIn.posX, playerIn.posY + playerIn.height / 2, playerIn.posZ, stack.getMetadata() == Variants.EMPTY.getMeta() ? SoundEvents.ITEM_BOTTLE_FILL : SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            stack.shrink(1);
            ItemStack newStack = getItemStack(stack.getMetadata() == Variants.EMPTY.getMeta() ? Variants.WATER : Variants.EMPTY);

            if (stack.isEmpty())
            {
                return new ActionResult<>(EnumActionResult.SUCCESS, newStack);
            }
            else
            {
                playerIn.addItemStackToInventory(newStack);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ActionResult<ItemStack> actionResult = onItemRightClick(worldIn, player, hand);
        if (actionResult.getType() == EnumActionResult.SUCCESS)
        {
            player.setHeldItem(hand, actionResult.getResult());
            return EnumActionResult.SUCCESS;
        }

        ItemStack stack = player.getHeldItem(hand);
        int meta = stack.getMetadata();
        if (meta != Variants.WATER.getMeta())
        {
            BlockPos result = PlayerUtil.tryPlaceBlock(worldIn, pos, side, player, hand, CuisineRegistry.MORTAR.getStateForPlacement(worldIn, pos, side, hitX, hitY, hitZ, 0, player, hand), stack, false);
            if (result != null && meta == Variants.SALT.getMeta())
            {
                TileEntity te = worldIn.getTileEntity(result);
                if (te instanceof TileMortar)
                {
                    ((TileMortar) te).insertItem(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT));
                }
            }
            return result != null ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            items.add(new ItemStack(this));
            items.add(new ItemStack(this, 1, 1));
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BLOCK;
    }

    public static class Variants extends VariantsHolder<IStringSerializable>
    {
        static final Variants INSTANCE = new Variants();

        public static final Variant EMPTY = INSTANCE.addVariant(new Type("empty"));
        public static final Variant WATER = INSTANCE.addVariant(new Type("water"));
        public static final Variant SALT = INSTANCE.addVariant(new Type("salt"));
    }

}
