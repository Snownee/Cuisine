package snownee.cuisine.items;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.heat.TileFirePit;
import snownee.kiwi.item.ItemMod;

public class ItemFan extends ItemMod
{
    public ItemFan(String name)
    {
        super(name);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setMaxStackSize(1);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorFanDispense());
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        return new ActionResult<ItemStack>(onItemUse(playerIn, worldIn, BlockPos.ORIGIN, handIn, EnumFacing.NORTH, 0, 0, 0), playerIn.getHeldItem(handIn));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        double radius = 1.732 * 3; // 3 * Math.sqrt(3)
        double squareRadius = radius * radius;
        Vec3d look = player.getLookVec();
        Vec3d eye = player.getPositionEyes(1);
        Vec3d posSource = eye.subtract(look.scale(radius / 2));
        for (EntityItem entity : worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(eye.x - radius, eye.y - radius, eye.z - radius, eye.x + radius, eye.y + radius, eye.z + radius)))
        {
            Vec3d posItem = entity.getPositionVector().add(0, entity.height / 2, 0);
            // 排除范围外的实体
            double squareDistance = eye.squareDistanceTo(posItem);
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
            RayTraceResult result = worldIn.rayTraceBlocks(eye, lookItem, true, true, false);
            if (result != null && result.typeOfHit == Type.BLOCK)
            {
                continue;
            }
            // 对实体施加力
            Vec3d force = posItem.subtract(eye).normalize();
            entity.addVelocity(force.x, force.y, force.z);
        }
        player.getCooldownTracker().setCooldown(this, 10);

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
                tileFirePit.heatHandler.encourage();
                if (worldIn.isRemote)
                {
                    for (int i = 0; i < 6; i++)
                    {
                        float f = (float) (worldIn.rand.nextFloat() * Math.PI * 2);
                        double x = MathHelper.sin(f) * 0.1D;
                        double y = pos.getY() + 0.12D + worldIn.rand.nextDouble() * 0.05D;
                        double z = MathHelper.cos(f) * 0.1D;
                        worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), pos.getX() + 0.5D + x, y, pos.getZ() + 0.5D + z, x * 0.2, 0.01 * 3, z * 0.2);
                    }
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

}
