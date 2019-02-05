package snownee.cuisine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockFirePit;
import snownee.kiwi.tile.TileBase;

import javax.annotation.Nonnull;

public class TileFirePit extends TileBase implements ITickable, IHeatable
{
    public final FuelHeatHandler heatHandler;

    public TileFirePit()
    {
        heatHandler = new FuelHeatHandler(0, 230, 3, 0.6f);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
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
        // https://minecraft.gamepedia.com/Biome#Temperature
        heatHandler.setMinHeat(getWorld().getBiome(getPos()).getTemperature(getPos()) * 28);
        heatHandler.update(0);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.FIRE_PIT || newState.getBlock() != CuisineRegistry.FIRE_PIT)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockFirePit.COMPONENT) != newState.getValue(BlockFirePit.COMPONENT);
        }
    }

    @Override
    public FuelHeatHandler getHeatHandler()
    {
        return heatHandler;
    }

}
