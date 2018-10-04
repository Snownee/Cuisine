package snownee.cuisine.blocks;

import net.minecraft.block.material.Material;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.BlockMod;

public class BlockSqueezer extends BlockMod
{

    public BlockSqueezer(String name)
    {
        super(name, Material.PISTON);
        setHardness(0.5F);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

}
