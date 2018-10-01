package snownee.cuisine.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.BlockMod;

public class BlockBasin extends BlockMod
{

    public BlockBasin(String name, Material materialIn)
    {
        super(name, materialIn);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        System.out.println(fallDistance);
        entityIn.fall(fallDistance, 0.5F);
    }

}
