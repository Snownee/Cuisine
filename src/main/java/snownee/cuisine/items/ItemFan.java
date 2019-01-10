package snownee.cuisine.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import snownee.cuisine.tiles.TileFirePit;
import snownee.kiwi.item.ItemMod;

public class ItemFan extends ItemMod
{
    public ItemFan(String name)
    {
        super(name);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        double radius = 1.732 * 3;
        double squareRadius = radius * radius;
        Vec3d look = player.getLookVec();
        Vec3d posEye = player.getPositionEyes(1);
        Vec3d posSource = posEye.subtract(look.scale(radius / 2));
        for (EntityItem entity : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(posEye.subtract(radius, radius, radius), posEye.add(radius, radius, radius))))
        {
            Vec3d posItem = entity.getPositionVector();
            // 排除范围外的实体
            double squareDistance = posEye.squareDistanceTo(posItem);
            if (squareDistance > squareRadius)
            {
                continue;
            }
            // 排除夹角外的实体
            // 取实体与source的方向向量
            Vec3d lookItem = posItem.subtract(posSource).normalize();
            // 夹角
            double angle = lookItem.dotProduct(look);
            if (angle < 0.866) // √3 / 2
            {
                continue;
            }
            // 排除位于玩家后方的实体
            if (posSource.squareDistanceTo(posItem) < squareRadius / 4)
            {
                continue;
            }
            // 排除被方块阻挡的实体
            RayTraceResult result = world.rayTraceBlocks(posEye, lookItem, true, true, false);
            if (result != null && result.typeOfHit == Type.BLOCK)
            {
                continue;
            }
            // 对实体施加力
            Vec3d force = posItem.subtract(posEye).normalize();
            entity.addVelocity(force.x, force.y, force.z);
        }

        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileFirePit)
        {
            TileFirePit tileFirePit = (TileFirePit) tile;
            if (tileFirePit.heatHandler.getLevel() == 0)
            {
                return EnumActionResult.FAIL;
            }
            else
            {
                // do something
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

}
