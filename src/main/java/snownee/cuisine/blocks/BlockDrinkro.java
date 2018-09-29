package snownee.cuisine.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.BlockMod;

public class BlockDrinkro extends BlockMod implements ITileEntityProvider
{

    protected BlockDrinkro(String name)
    {
        super(name, Material.IRON);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
