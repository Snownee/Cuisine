package snownee.cuisine.tiles;

import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;

public class TileDrinkro extends TileBase implements CookingVessel
{
    protected static class DrinkroFluidWrapper implements IFluidHandler
    {
        private final TileDrinkro tile;

        public DrinkroFluidWrapper(TileDrinkro tile)
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
            // TODO (Snownee): fire event
            if (resource == null)
            {
                return 0;
            }
            // 1 size = 500mB, fine-tuning needed

            if (((int) tile.builder.getMaxSize() - tile.builder.getCurrentSize()) <= 0)
            {
                return 0;
            }
            int amountAdded = (int) Math.min(resource.amount, (tile.builder.getMaxSize() - tile.builder.getCurrentSize()) * 500);

            Material material = CulinaryHub.API_INSTANCE.findMaterial(resource);
            if (material == null)
            {
                return 0;
            }
            Ingredient ingredient = new Ingredient(material, Form.JUICE, amountAdded / 500D);
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

    protected static class DrinkroItemWrapper implements IItemHandler
    {
        private final IItemHandler input, output;

        public DrinkroItemWrapper(IItemHandler input, IItemHandler output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public int getSlots()
        {
            return input.getSlots() + output.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            if (slot < input.getSlots())
            {
                return input.getStackInSlot(slot);
            }
            else
            {
                return output.getStackInSlot(slot - input.getSlots());
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!isItemValid(slot, stack))
            {
                return stack;
            }
            if (slot < input.getSlots())
            {
                return input.insertItem(slot, stack, simulate);
            }
            else
            {
                return output.insertItem(slot - input.getSlots(), stack, simulate);
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot < input.getSlots())
            {
                return ItemStack.EMPTY;
            }
            else
            {
                return input.extractItem(slot, amount, simulate);
            }
        }

        @Override
        public int getSlotLimit(int slot)
        {
            if (slot < input.getSlots())
            {
                return input.getSlotLimit(slot);
            }
            else
            {
                return output.getSlotLimit(slot - input.getSlots());
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            if (slot < input.getSlots())
            {
                return input.isItemValid(slot, stack);
            }
            else
            {
                return output.isItemValid(slot - input.getSlots(), stack);
            }
        }
    }

    private boolean powered = false;
    public Drink.Builder builder;
    public ItemStackHandler inputs = new ItemStackHandler(4)
    {
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        public boolean isItemValid(int slot, ItemStack stack)
        {
            if (Drink.Builder.isFeatureItem(stack))
            {
                return true;
            }
            Spice spice = CulinaryHub.API_INSTANCE.findSpice(stack);
            return spice != null && builder.canAddIntoThis(null, new Seasoning(spice), TileDrinkro.this);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
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
            else if (!isItemValid(slot, stack))
            {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }
    };
    public ItemStackHandler output = new ItemStackHandler()
    {
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        public boolean isItemValid(int slot, ItemStack stack)
        {
            return Drink.Builder.isContainerItem(stack);
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!isItemValid(slot, stack))
            {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }
    };

    public TileDrinkro()
    {
        builder = Drink.Builder.create();
    }

    public void neighborChanged(IBlockState state)
    {
        boolean poweredIn = world.isBlockPowered(pos);
        if (poweredIn && !this.powered)
        {
            world.setBlockState(pos, state.withProperty(BlockDrinkro.WORKING, true));
            world.scheduleUpdate(pos, state.getBlock(), 100);
        }
        this.powered = poweredIn;
    }

    public void stopProcess()
    {
        if (world.isBlockPowered(pos))
        {
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
            if (!type.getContainerPre().matches(output.getStackInSlot(0)))
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
            output.setStackInSlot(0, itemDrink);
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
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new DrinkroItemWrapper(inputs, output));
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        inputs.deserializeNBT(data.getCompoundTag("inputs"));
        output.deserializeNBT(data.getCompoundTag("outputs"));
        builder = Drink.Builder.fromNBT(data.getCompoundTag("builder"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setTag("inputs", inputs.serializeNBT());
        data.setTag("outputs", output.serializeNBT());
        data.setTag("builder", Drink.Builder.toNBT(builder));
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        // TODO Auto-generated method stub
        return data;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

}
