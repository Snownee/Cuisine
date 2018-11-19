package snownee.cuisine.tiles;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;

public class TileDrinkroTank extends TileBase implements CookingVessel
{
    protected static class DrinkroFluidWrapper implements IFluidHandler
    {
        private final TileDrinkroTank tile;

        public DrinkroFluidWrapper(TileDrinkroTank tile)
        {
            this.tile = tile;
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            return new IFluidTankProperties[0];
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource == null || tile.isWorking())
            {
                return 0;
            }
            // 1 size = 500mB, fine-tuning needed

            if (((int) tile.builder.getMaxSize() - tile.builder.getCurrentSize()) <= 0)
            {
                return 0;
            }
            int amountAdded = (int) Math.min(resource.amount, (tile.builder.getMaxSize() - tile.builder.getCurrentSize()) * 500);

            Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(resource);
            if (ingredient == null || ingredient.getForm() != Form.JUICE)
            {
                return 0;
            }
            ingredient.setSize(amountAdded / 500D);
            if (!tile.builder.canAddIntoThis(null, ingredient, tile))
            {
                return 0;
            }
            if (doFill && !tile.builder.addIngredient(null, ingredient, tile))
            {
                return 0;
            }
            return amountAdded;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return null;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return null;
        }

    }

    private boolean powered = false;
    protected boolean working = false;
    public Drink.Builder builder;
    public final ItemStackHandler inventory;
    @Nullable
    public final Battery battery;

    public TileDrinkroTank()
    {
        super();
        if (CuisineConfig.GENERAL.drinkroUsesFE > 0)
        {
            battery = new Battery(CuisineConfig.GENERAL.drinkroUsesFE * 50, CuisineConfig.GENERAL.drinkroUsesFE, 0);
        }
        else
        {
            battery = null;
        }
        builder = Drink.Builder.create();
        inventory = new ItemStackHandler(4)
        {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack)
            {
                if (Drink.Builder.isFeatureItem(stack))
                {
                    return true;
                }
                Spice spice = CulinaryHub.API_INSTANCE.findSpice(stack);
                return spice != null && builder != null && builder.canAddIntoThis(null, new Seasoning(spice), TileDrinkroTank.this);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                if (isWorking() || !isItemValid(slot, stack))
                {
                    return stack;
                }
                if (Drink.Builder.isFeatureItem(stack))
                {
                    // One drink can't have two or more feature items
                    for (ItemStack stack2 : stacks)
                    {
                        if (Drink.Builder.isFeatureItem(stack2))
                        {
                            return stack;
                        }
                    }
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                if (isWorking())
                {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }

            @Override
            protected void onContentsChanged(int slot)
            {
                refresh();
            }

        };

    }

    public void neighborChanged(IBlockState state)
    {
        boolean poweredIn = world.isBlockPowered(pos);
        if (!poweredIn)
        {
            poweredIn = world.isBlockPowered(pos.down());
        }
        if (poweredIn && !this.powered && !isWorking())
        {
            world.addBlockEvent(pos, getBlockType(), 1, 0);
            if (battery == null || battery.getEnergyStored() >= CuisineConfig.GENERAL.drinkroUsesFE)
            {
                working = true;
                // world.notifyBlockUpdate(pos, state, state, 3);
                world.setBlockState(pos, state.withProperty(BlockDrinkro.WORKING, Boolean.TRUE), 11);
                world.setBlockState(pos.down(), world.getBlockState(pos.down()).withProperty(BlockDrinkro.WORKING, Boolean.TRUE), 11);
                world.scheduleUpdate(pos, state.getBlock(), 100);
            }
        }
        this.powered = poweredIn;
    }

    protected TileDrinkroBase getBase()
    {
        if (hasWorld())
        {
            TileEntity tile = world.getTileEntity(pos.down());
            if (tile instanceof TileDrinkroBase)
            {
                return (TileDrinkroBase) tile;
            }
        }
        return null;
    }

    public void stopProcess()
    {
        working = false;
        if (battery != null && battery.getEnergyStored() < CuisineConfig.GENERAL.drinkroUsesFE)
        {
            return;
        }
        TileDrinkroBase tileBase = getBase();
        if (tileBase == null)
        {
            return;
        }
        ItemStackHandler inputs = this.inventory;

        int slotCount = 0;
        for (int i = 0; i < inputs.getSlots(); i++)
        {
            if (!inputs.getStackInSlot(i).isEmpty())
            {
                ++slotCount;
            }
        }
        if (builder.getIngredients().isEmpty() && builder.getIngredients().size() + builder.getIngredients().size() + slotCount < 2)
        {
            return;
        }
        // find feature
        DrinkType type = DrinkType.NORMAL;
        int featureSlot = -1;
        for (int i = 0; i < inputs.getSlots(); i++)
        {
            ItemStack input = inputs.getStackInSlot(i);
            if (!input.isEmpty())
            {
                type = Drink.Builder.findDrinkType(input);
                if (type != DrinkType.NORMAL)
                {
                    // we can't remove this input here because we don't know
                    // if it build successfully
                    featureSlot = i;
                    builder.drinkType = type;
                    break;
                }
            }
        }
        // check if container match the feature
        if (!type.getContainerPre().matches(tileBase.inventory.getStackInSlot(0)))
        {
            // TODO: way to modify player what error is,
            // as it is redstone-powered, so we may need a `lastError` variable
            return;
        }
        // add all inputs
        for (int i = 0; i < inputs.getSlots(); i++)
        {
            if (i == featureSlot)
            {
                continue;
            }
            ItemStack input = inputs.getStackInSlot(i);
            if (!input.isEmpty())
            {
                Spice spice = CulinaryHub.API_INSTANCE.findSpice(input);
                if (spice != null)
                {
                    builder.addSeasoning(null, new Seasoning(spice), this);
                }
            }
        }
        Optional<Drink> result = builder.build(this, null);
        if (!result.isPresent())
        {
            // TODO: copy builder before adding seasonings.
            // can't simply clear List<Seasoning> because effects may be added on stage of add seasoning
            return;
        }
        Drink drink = result.get();

        // No cook here, consider increasing point of who pick up this drink?
        // SkillUtil.increasePoint(null, CulinarySkillPoint.EXPERTISE, (int) (completedDish.getFoodLevel() * completedDish.getSaturationModifier()));
        // SkillUtil.increasePoint(null, CulinarySkillPoint.PROFICIENCY, 1);

        ItemStack itemDrink = new ItemStack(CuisineRegistry.DRINK);
        FoodContainer container = itemDrink.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            container.set(drink); // TODO AGAIN, THIS IS HACK
        }
        else
        {
            throw new NullPointerException("Null FoodContainer");
        }
        tileBase.inventory.setStackInSlot(0, itemDrink);
        world.addBlockEvent(pos, getBlockType(), 0, builder.getColor());

        if (battery != null)
        {
            battery.setEnergy(battery.getEnergyStored() - CuisineConfig.GENERAL.drinkroUsesFE);
        }

        for (int i = 0; i < inputs.getSlots(); i++)
        {
            ItemStack stack = inputs.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stack.shrink(1);
            }
        }
        builder = Drink.Builder.create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || (capability == CapabilityEnergy.ENERGY && battery != null) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new DrinkroFluidWrapper(this));
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        if (capability == CapabilityEnergy.ENERGY && battery != null)
        {
            return CapabilityEnergy.ENERGY.cast(battery);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        inventory.deserializeNBT(data.getCompoundTag("inventory"));
        if (battery != null)
        {
            battery.readFromNBT(data);
        }
        working = data.getBoolean("working");
        builder = Drink.Builder.fromNBT(data.getCompoundTag("builder"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setTag("inventory", inventory.serializeNBT());
        if (battery != null)
        {
            battery.writeToNBT(data);
        }
        data.setBoolean("working", working);
        data.setTag("builder", Drink.Builder.toNBT(builder));
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        this.inventory.deserializeNBT(data.getCompoundTag("inventory"));
        if (battery != null)
        {
            battery.readFromNBT(data);
        }
        this.builder = Drink.Builder.fromNBT(data.getCompoundTag("builder"));
        this.working = data.getBoolean("working");
    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setTag("inventory", this.inventory.serializeNBT());
        if (battery != null)
        {
            battery.writeToNBT(data);
        }
        data.setTag("builder", Drink.Builder.toNBT(builder));
        data.setBoolean("working", this.working);
        return data;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.DRINKRO || newState.getBlock() != CuisineRegistry.DRINKRO)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockDrinkro.BASE) != newState.getValue(BlockDrinkro.BASE);
        }
    }

    public boolean isWorking()
    {
        return working;
    }

    protected void refresh()
    {
        if (hasWorld() && !world.isRemote)
        {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, null, state, state, 11);
        }
    }

    @Override
    public Optional<ItemStack> serve()
    {
        if (!isWorking())
        {
            TileDrinkroBase base = getBase();
            if (base != null)
            {
                ItemStack stack = base.inventory.getStackInSlot(0);
                if (stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
                {
                    base.inventory.setStackInSlot(0, ItemStack.EMPTY);
                    return Optional.of(stack);
                }
            }
        }
        return Optional.empty();
    }

}
