package snownee.cuisine.blocks;

import net.minecraft.block.material.Material;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.BlockMod;

public class BlockDrinkro extends BlockMod
{

    public BlockDrinkro(String name)
    {
        super(name, Material.IRON);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

}
