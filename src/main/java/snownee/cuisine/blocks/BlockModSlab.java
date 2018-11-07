package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import snownee.kiwi.block.IModBlock;

public class BlockModSlab extends BlockSlab implements IModBlock
{
    private final String name;

    public BlockModSlab(String name, IBlockState baseState)
    {
        super(baseState.getMaterial());
        this.name = name;
    }

    @Override
    public Block cast()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

    @Override
    public String getTranslationKey(int meta)
    {
        return null;
    }

    @Override
    public boolean isDouble()
    {
        return false;
    }

    @Override
    public IProperty<?> getVariantProperty()
    {
        return null;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack)
    {
        return null;
    }

}
