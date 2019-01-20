package snownee.cuisine.tiles;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.BasinInteracting.Output;
import snownee.cuisine.api.process.Boiling;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.util.StacksUtil;

public class TileBasinHeatable extends TileBasin implements ITickable
{
    public static final Map<Block, Integer> BLOCK_HEAT_SOURCES = new HashMap<>();
    public static final Map<IBlockState, Integer> STATE_HEAT_SOURCES = new HashMap<>();
    public static final Map<Block, ItemStack> BLOCK_TO_ITEM = new HashMap<>();
    public static final Map<IBlockState, ItemStack> STATE_TO_ITEM = new HashMap<>();
    private int tickCheckHeating = 0;
    protected boolean invLock = false;
    protected boolean failed = false;

    static
    {
        registerHeatSource(1, Blocks.LIT_PUMPKIN, new ItemStack(Blocks.LIT_PUMPKIN));
        registerHeatSource(1, Blocks.TORCH, new ItemStack(Blocks.TORCH));
        registerHeatSource(2, Blocks.LIT_FURNACE, new ItemStack(Blocks.FURNACE));
        registerHeatSource(3, Blocks.FIRE, new ItemStack(Items.FLINT_AND_STEEL));
        registerHeatSource(3, Blocks.MAGMA, new ItemStack(Blocks.MAGMA));
        registerHeatSource(4, Blocks.LAVA, new ItemStack(Items.LAVA_BUCKET));
        registerHeatSource(4, Blocks.FLOWING_LAVA);
    }

    public static void registerHeatSource(int heatValue, IBlockState state)
    {
        STATE_HEAT_SOURCES.put(state, heatValue);
    }

    public static void registerHeatSource(int heatValue, IBlockState state, ItemStack stack)
    {
        STATE_HEAT_SOURCES.put(state, heatValue);
        STATE_TO_ITEM.put(state, stack);
    }

    public static void registerHeatSource(int heatValue, Block block)
    {
        BLOCK_HEAT_SOURCES.put(block, heatValue);
    }

    public static void registerHeatSource(int heatValue, Block block, ItemStack stack)
    {
        BLOCK_HEAT_SOURCES.put(block, heatValue);
        BLOCK_TO_ITEM.put(block, stack);
    }

    @Override
    public void update()
    {
        if (!world.isRemote && !failed && tank.getFluid() != null && --tickCheckHeating <= 0)
        {
            int heat = getHeatValueFromState(world.getBlockState(pos.down()));
            if (heat == 0 && !CuisineConfig.GENERAL.basinHeatingInDaylight)
            {
                failed = true;
                return;
            }
            tickCheckHeating = heat > 0 ? 200 / heat : 600;
            if (heat == 0 && !world.provider.isNether())
            {
                if (!world.provider.hasSkyLight() || !world.isDaytime() || world.isRaining() || !world.canSeeSky(pos))
                {
                    return;
                }
            }
            Boiling recipe = Processing.BOILING.findRecipe(stacks.getStackInSlot(0), tank.getFluid(), heat);
            if (recipe != null)
            {
                invLock = true;
                Output output = recipe.getOutputAndConsumeInput(stacks.getStackInSlot(0), tank.getFluid(), heat, world.rand);
                if (output.fluid != null && output.fluid.amount <= 0)
                {
                    output.fluid = null;
                }
                tank.setFluid(output.fluid);
                StacksUtil.spawnItemStack(world, pos, output.item, true);
                invLock = false;
                refresh();
            }
            else
            {
                failed = true;
            }
        }
    }

    public boolean isWorking()
    {
        boolean flag = !failed && tank.getFluid() != null;
        if (flag)
        {
            int heat = getHeatValueFromState(world.getBlockState(pos.down()));
            if (heat == 0 && !world.provider.isNether())
            {
                if (!world.provider.hasSkyLight() || !world.isDaytime() || world.isRaining() || !world.canSeeSky(pos))
                {
                    return false;
                }
            }
        }
        return flag;
    }

    public int getMaxHeatingTick()
    {
        int heat = getHeatValueFromState(world.getBlockState(pos.down()));
        return heat > 0 ? 200 / heat : 600;
    }

    public int getCurrentHeatingTick()
    {
        return tickCheckHeating;
    }

    @Override
    public void onContentsChanged(int slot)
    {
        if (!invLock)
        {
            super.onContentsChanged(slot);
            if (!world.isRemote && tank.getFluid() != null)
            {
                failed = false;
                tickCheckHeating = getMaxHeatingTick(); // on this stage, blockstates are not actual states
            }
        }
    }

    public static int getHeatValueFromState(IBlockState state)
    {
        Integer heat = STATE_HEAT_SOURCES.get(state);
        if (heat != null)
        {
            return heat;
        }
        heat = BLOCK_HEAT_SOURCES.get(state.getBlock());
        return heat != null ? heat : 0;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        invLock = true;
        super.readFromNBT(compound);
        invLock = false;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        // Split from readFromNBT, since TileEntity.world may not be ready
        // when TileEntity data are read from disk, but it is ready at this
        // time moment.
        // After all, this method is here for catching up the very first
        // "tick" (actually a pseudo-tick) of TileEntity lifecycle.
        this.onContentsChanged(0);
    }
}
