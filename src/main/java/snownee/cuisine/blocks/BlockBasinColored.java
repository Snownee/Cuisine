package snownee.cuisine.blocks;

import net.minecraft.block.BlockColored;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;

public class BlockBasinColored extends BlockBasin
{

    public BlockBasinColored(String name, Material materialIn)
    {
        super(name, materialIn);
    }

    @Override
    public int getItemSubtypeAmount()
    {
        return 16;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        Item item = Item.getItemFromBlock(this);
        EnumDyeColor[] values = EnumDyeColor.values();
        for (int i = 0; i < getItemSubtypeAmount(); i++)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, "earthen_basin_colored"), "color=" + values[i].getDyeColorName()));
        }
    }

    @Override
    public String getTranslationKey()
    {
        return "tile." + Cuisine.MODID + ".earthen_basin";
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(BlockColored.COLOR).getMetadata();
    }

    @SuppressWarnings("deprecation")
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.getBlockColor(state.getValue(BlockColored.COLOR));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockColored.COLOR).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockColored.COLOR);
    }
}
