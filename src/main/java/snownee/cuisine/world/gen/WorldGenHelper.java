package snownee.cuisine.world.gen;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class WorldGenHelper
{
    @Nullable
    public static BlockPos.MutableBlockPos findGround(World world, BlockPos pos, boolean ignoreLeaves)
    {
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(world.getHeight(pos));
        if (position.getY() > 0)
        {
            int yOrigin = position.getY();
            do
            {
                IBlockState state = world.getBlockState(position);
                if (!state.getBlock().isReplaceable(world, position) && (!ignoreLeaves || !state.getBlock().isLeaves(state, world, position)))
                {
                    return position.move(EnumFacing.UP);
                }
            }
            while (yOrigin - position.getY() < 9 && position.move(EnumFacing.DOWN).getY() > 0);
        }
        return null;
    }
}
