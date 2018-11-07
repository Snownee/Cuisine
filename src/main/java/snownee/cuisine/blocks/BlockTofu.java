package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemBasicFood;
import snownee.kiwi.block.BlockMod;

public class BlockTofu extends BlockMod
{

    public BlockTofu(String name)
    {
        super(name, Material.CLAY, SoundType.SLIME);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultSlipperiness(0.5F);
        setHardness(0.2F);
        disableStats();
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        if (entityIn.isSneaking())
        {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        }
        else
        {
            entityIn.fall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(World worldIn, Entity entityIn)
    {
        if (entityIn.isSneaking())
        {
            super.onLanded(worldIn, entityIn);
        }
        else if (entityIn.motionY < 0.0D)
        {
            entityIn.motionY = -entityIn.motionY;

            if (!(entityIn instanceof EntityLivingBase))
            {
                entityIn.motionY *= 0.5D;
            }
        }
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 3 + random.nextInt(2);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random)
    {
        return fortune > 0 ? 4 : quantityDropped(random);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;

        int count = quantityDropped(state, fortune, rand);
        ItemStack stack = CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.TOFU);
        for (int i = 0; i < count; i++)
        {
            drops.add(stack.copy());
        }
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return fortune == 0 ? quantityDropped(random) : 4;
    }
}
