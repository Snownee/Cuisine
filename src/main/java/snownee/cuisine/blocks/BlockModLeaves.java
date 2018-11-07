package snownee.cuisine.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockModSapling.Type;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemBasicFood.Variants.SubItem;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.VariantsHolder.Variant;

public class BlockModLeaves extends BlockMod implements IGrowable, IShearable
{

    public static final PropertyBool CORE = PropertyBool.create("core");
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    private final Variant<SubItem> fruit;

    public BlockModLeaves(String name, Variant<SubItem> fruit)
    {
        super(name, Material.LEAVES);
        this.setTickRandomly(true);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        setDefaultState(blockState.getBaseState().withProperty(CORE, false).withProperty(AGE, 1).withProperty(BlockLeaves.DECAYABLE, false));
        this.fruit = fruit;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(CORE).ignore(BlockLeaves.DECAYABLE).build());
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CORE, AGE, BlockLeaves.DECAYABLE);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(AGE);
        if (state.getValue(CORE))
        {
            meta |= 4;
        }
        if (state.getValue(BlockLeaves.DECAYABLE))
        {
            meta |= 8;
        }
        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState state = getDefaultState().withProperty(AGE, meta % 4);
        if ((meta & 4) != 0)
        {
            state = state.withProperty(CORE, true);
        }
        if ((meta & 8) != 0)
        {
            state = state.withProperty(BlockLeaves.DECAYABLE, true);
        }
        return state;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, getItemInternal(world.getBlockState(pos)));
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(AGE) > 0 && state.getValue(AGE) < 3;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return worldIn.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, state.cycleProperty(AGE));
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (worldIn.isRainingAt(pos.up()) && !worldIn.getBlockState(pos.down()).isTopSolid() && rand.nextInt(15) == 1)
        {
            double d0 = pos.getX() + rand.nextFloat();
            double d1 = pos.getY() - 0.05D;
            double d2 = pos.getZ() + rand.nextFloat();
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return getItemInternal(state);
    }

    private ItemStack getItemInternal(IBlockState state)
    {
        return CuisineRegistry.SHEARED_LEAVES.getItemInternal(getShearedState(state));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        CuisineRegistry.SHEARED_LEAVES.getDrops(drops, world, pos, getShearedState(state), fortune);
    }

    private IBlockState getShearedState(IBlockState state)
    {
        IBlockState newState = CuisineRegistry.SHEARED_LEAVES.getDefaultState();
        if (state.getValue(AGE) == 2)
        {
            newState = newState.withProperty(BlockShearedLeaves.FLOWER, true);
        }
        Type type = Type.POMELO;
        if (fruit == ItemBasicFood.Variants.CITRON)
        {
            type = Type.CITRON;
        }
        else if (fruit == ItemBasicFood.Variants.LEMON)
        {
            type = Type.LEMON;
        }
        else if (fruit == ItemBasicFood.Variants.LIME)
        {
            type = Type.LIME;
        }
        else if (fruit == ItemBasicFood.Variants.MANDARIN)
        {
            type = Type.MANDARIN;
        }
        else if (fruit == ItemBasicFood.Variants.GRAPEFRUIT)
        {
            type = Type.GRAPEFRUIT;
        }
        else if (fruit == ItemBasicFood.Variants.ORANGE)
        {
            type = Type.ORANGE;
        }
        return newState.withProperty(BlockModSapling.VARIANT, type);
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return getItemInternal(state);
    }

}
