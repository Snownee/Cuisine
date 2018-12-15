package snownee.cuisine.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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

    @Override
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
    {
        Block block = state.getBlock();
        if (block == CuisineFluidBlocks.SESAME_OIL)
        {
            return getVecColor(0xCE8600);
        }
        if (block == CuisineFluidBlocks.EDIBLE_OIL)
        {
            return getVecColor(0xD1A71A);
        }
        if (block == CuisineFluidBlocks.SOY_MILK)
        {
            return getVecColor(0xDDD7BF);
        }
        if (block == CuisineFluidBlocks.MILK)
        {
            return getVecColor(0xFFFFFF);
        }
        if (block == CuisineFluidBlocks.SUGARCANE_JUICE)
        {
            return getVecColor(0x8E8F6A);
        }
        if (block == CuisineFluidBlocks.SOY_SAUCE || block == CuisineFluidBlocks.FRUIT_VINEGAR || block == CuisineFluidBlocks.RICE_VINEGAR)
        {
            return getVecColor(0x100000);
        }
        return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
    }

    protected static final Vec3d getVecColor(int color)
    {
        double r = (color >> 16 & 255) / 255.0F;
        double g = (color >> 8 & 255) / 255.0F;
        double b = (color & 255) / 255.0F;
        return new Vec3d(r, g, b);
    }
}
