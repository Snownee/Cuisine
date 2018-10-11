package snownee.cuisine.blocks;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.IModBlock;

public class BlockModLog extends BlockLog implements IModBlock
{
    public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class);

    private final String name;

    public BlockModLog(String name)
    {
        this.name = name;
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, Type.POMELO).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
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
    public int getItemSubtypeAmount()
    {
        return 4;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        Item item = Item.getItemFromBlock(this);
        Type[] values = Type.values();
        for (int i = 0; i < values.length; i++)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(getRegistryName(), "axis=y,variant=" + values[i].getName()));
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < getItemSubtypeAmount(); i++)
        {
            items.add(new ItemStack(this, 1, i));
        }
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT, LOG_AXIS);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, Type.values()[(meta & 3) % 4]);

        switch (meta & 12)
        {
        case 0:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
            break;
        case 4:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
            break;
        case 8:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
            break;
        default:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return iblockstate;
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(VARIANT).ordinal();

        switch (state.getValue(LOG_AXIS))
        {
        case X:
            i |= 4;
            break;
        case Y:
            break;
        case Z:
            i |= 8;
            break;
        case NONE:
            i |= 12;
        }

        return i;
    }

    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal();
    }

    public enum Type implements IStringSerializable
    {
        POMELO, CITRON, TANGERINE, GRAPEFRUIT;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }
    }

}
