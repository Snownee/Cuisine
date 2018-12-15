package snownee.cuisine.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.entities.EntityModBoat;
import snownee.kiwi.item.ItemMod;

public class ItemModBoat extends ItemMod
{

    public ItemModBoat(String name)
    {
        super(name);
        this.maxStackSize = 1;
        setCreativeTab(Cuisine.CREATIVE_TAB);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorDispenseBoat());
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack)
    {
        return 400;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * 1.0F;
        float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * 1.0F;
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * 1.0D;
        double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * 1.0D + playerIn.getEyeHeight();
        double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * 1.0D;
        Vec3d vec3d = new Vec3d(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3d vec3d1 = vec3d.add(f7 * 5.0D, f6 * 5.0D, f8 * 5.0D);
        RayTraceResult raytraceresult = worldIn.rayTraceBlocks(vec3d, vec3d1, true);

        if (raytraceresult == null)
        {
            return new ActionResult<>(EnumActionResult.PASS, itemstack);
        }
        else
        {
            Vec3d vec3d2 = playerIn.getLook(1.0F);
            boolean flag = false;
            List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().expand(vec3d2.x * 5.0D, vec3d2.y * 5.0D, vec3d2.z * 5.0D).grow(1.0D));

            for (Entity entity : list)
            {
                if (entity.canBeCollidedWith())
                {
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());

                    if (axisalignedbb.contains(vec3d))
                    {
                        flag = true;
                    }
                }
            }

            if (flag)
            {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            }
            else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
            {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            }
            else
            {
                Block block = worldIn.getBlockState(raytraceresult.getBlockPos()).getBlock();
                boolean flag1 = block == Blocks.WATER || block == Blocks.FLOWING_WATER;
                EntityModBoat entityboat = new EntityModBoat(worldIn, raytraceresult.hitVec.x, flag1 ? raytraceresult.hitVec.y - 0.12D : raytraceresult.hitVec.y, raytraceresult.hitVec.z);
                entityboat.rotationYaw = playerIn.rotationYaw;

                if (!worldIn.getCollisionBoxes(entityboat, entityboat.getEntityBoundingBox().grow(-0.1D)).isEmpty())
                {
                    return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                }
                else
                {
                    if (!worldIn.isRemote)
                    {
                        worldIn.spawnEntity(entityboat);
                    }

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }

                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
                }
            }
        }
    }

    public static class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem
    {
        private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
            World world = source.getWorld();
            double d0 = source.getX() + enumfacing.getXOffset() * 1.125F;
            double d1 = source.getY() + enumfacing.getYOffset() * 1.125F;
            double d2 = source.getZ() + enumfacing.getZOffset() * 1.125F;
            BlockPos blockpos = source.getBlockPos().offset(enumfacing);
            Material material = world.getBlockState(blockpos).getMaterial();
            double d3;

            if (Material.WATER.equals(material))
            {
                d3 = 1.0D;
            }
            else
            {
                if (!Material.AIR.equals(material) || !Material.WATER.equals(world.getBlockState(blockpos.down()).getMaterial()))
                {
                    return this.dispenseBehavior.dispense(source, stack);
                }

                d3 = 0.0D;
            }

            EntityModBoat entityboat = new EntityModBoat(world, d0, d1 + d3, d2);
            entityboat.rotationYaw = enumfacing.getHorizontalAngle();
            world.spawnEntity(entityboat);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playDispenseSound(IBlockSource source)
        {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    }
}
