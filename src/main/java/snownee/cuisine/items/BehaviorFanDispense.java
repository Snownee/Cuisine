package snownee.cuisine.items;

import net.minecraft.block.BlockDirectional;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BehaviorFanDispense extends BehaviorDefaultDispenseItem
{

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        EnumFacing facing = source.getBlockState().getValue(BlockDirectional.FACING);
        BlockPos pos = source.getBlockPos();
        World world = source.getWorld();
        int distance;
        for (distance = 0; distance < 6; ++distance)
        {
            BlockPos pos2 = pos.offset(facing, distance + 1);
            if (!world.getBlockState(pos2).getBlock().isReplaceable(world, pos2))
            {
                break;
            }
        }
        if (distance > 0)
        {
            Vec3d force = new Vec3d(facing.getDirectionVec());
            if (facing.getHorizontalIndex() == -1)
            {
                force = force.scale(0.5);
            }
            for (EntityItem entity : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.offset(facing)).union(new AxisAlignedBB(pos.offset(facing, distance)))))
            {
                entity.addVelocity(force.x, force.y, force.z);
            }
        }
        return stack;
    }
}
