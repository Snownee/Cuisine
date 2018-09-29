package snownee.cuisine.blocks.fluids;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidVinegar extends BlockFluid
{
    public BlockFluidVinegar(Fluid fluid, String name)
    {
        super(fluid, name, new MaterialLiquid(MapColor.BROWN_STAINED_HARDENED_CLAY));
    }

}
