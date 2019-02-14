package snownee.cuisine.tiles.heat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.IHeatable;
import snownee.kiwi.tile.TileBase;
import snownee.kiwi.util.NBTHelper;

import javax.annotation.Nonnull;

public class TileFirePit extends TileBase implements ITickable, IHeatable
{
    public final FuelHeatHandler heatHandler;
    public ItemStackHandler stacks = new FirePitItemHandler(1, 0);

    public TileFirePit()
    {
        heatHandler = new FuelHeatHandler(0, 230, 3, 0.6f);
    }

    public class FirePitItemHandler extends ItemStackHandler
    {
        protected final int fuelIndex;

        public FirePitItemHandler(int size, int fuelIndex)
        {
            super(size);
            this.fuelIndex = fuelIndex;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (!isItemValid(slot, stack))
            {
                return stack;
            }
            if (slot == fuelIndex)
            {
                return heatHandler.addFuel(stack);
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            if (slot < fuelIndex)
            {
                return stack.getItem() == CuisineRegistry.INGREDIENT || FurnaceRecipes.instance().getSmeltingResult(stack).getItem() instanceof ItemFood;
            }
            else
            {
                return FuelHeatHandler.isFuel(stack, true);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        NBTHelper helper = NBTHelper.of(data);
        stacks.deserializeNBT(helper.getTag("Items", true));
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setHeat(data.getFloat("heat"));
        }
        if (data.hasKey("burnTime", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setBurnTime(data.getFloat("burnTime"));
        }
        super.readFromNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        data.setTag("Items", this.stacks.serializeNBT());
        data.setFloat("heat", heatHandler.getHeat());
        data.setFloat("burnTime", heatHandler.getBurnTime());
        return super.writeToNBT(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setHeat(data.getFloat("heat"));
        }
        if (data.hasKey("burnTime", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setBurnTime(data.getFloat("burnTime"));
        }
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setFloat("heat", heatHandler.getHeat());
        data.setFloat("burnTime", heatHandler.getBurnTime());
        return data;
    }

    @Override
    public void update()
    {
        heatHandler.update(0);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        refresh();
    }

    @Override
    protected void refresh()
    {
        super.refresh();
        // https://minecraft.gamepedia.com/Biome#Temperature
        heatHandler.setMinHeat(getWorld().getBiome(getPos()).getTemperature(getPos()) * 28);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.FIRE_PIT || newState.getBlock() != CuisineRegistry.FIRE_PIT)
        {
            return true;
        }
        return super.shouldRefresh(world, pos, oldState, newState);
    }

    @Override
    public FuelHeatHandler getHeatHandler()
    {
        return heatHandler;
    }

}
