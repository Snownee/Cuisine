package snownee.cuisine.blocks;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.IModBlock;

public class BlockModSapling extends BlockBush implements IModBlock
{
    public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class);
    protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
    private final String name;

    public BlockModSapling(String name)
    {
        // setDefaultState in constructor of BlockSapling so we can't extend that.
        this.name = name;
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, Type.POMELO).withProperty(BlockSapling.STAGE, 0));
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
        return 7;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BlockSapling.STAGE).build());
        Item item = Item.getItemFromBlock(this);
        Type[] values = Type.values();
        for (int i = 0; i < values.length; i++)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, "sapling_" + values[i].getName()), "inventory"));
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

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getStateFromMeta(meta * 2);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, Type.values()[meta / 2]).withProperty(BlockSapling.STAGE, meta % 2);
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal() * 2 + state.getValue(BlockSapling.STAGE);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT, BlockSapling.STAGE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    public enum Type implements IStringSerializable
    {
        POMELO, CITRON, TANGERINE, GRAPEFRUIT, ORANGE, LEMON, LIME;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }

    }

}
