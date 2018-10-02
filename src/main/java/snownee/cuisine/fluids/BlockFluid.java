package snownee.cuisine.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import snownee.cuisine.Cuisine;

public class BlockFluid extends BlockFluidClassic
{

    public BlockFluid(Fluid fluid, String name, MaterialLiquid material)
    {
        super(fluid, material);
        setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
        setRegistryName(Cuisine.MODID, name);
        FluidRegistry.addBucketForFluid(fluid);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead)
    {
        AxisAlignedBB box = iblockstate.getBoundingBox(world, blockpos).offset(blockpos);
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        return box.intersects(entityBox) && materialIn.isLiquid();
    }
}
