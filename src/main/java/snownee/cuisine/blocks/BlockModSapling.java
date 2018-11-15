package snownee.cuisine.blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.world.feature.WorldFeatureCitrusGenusTree;
import snownee.kiwi.block.IModBlock;

public class BlockModSapling extends BlockBush implements IModBlock, IGrowable
{
    public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class);
    private static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);

    private final String name;

    public BlockModSapling(String name)
    {
        // setDefaultState in constructor of BlockSapling so we can't extend that.
        this.name = name;
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, Type.POMELO).withProperty(BlockSapling.STAGE, 0));
        this.setTickRandomly(true);
        setSoundType(SoundType.PLANT);
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
        return this.getDefaultState().withProperty(VARIANT, Type.values()[meta]);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, Type.values()[meta / 2]).withProperty(BlockSapling.STAGE, meta % 2);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal() * 2 + state.getValue(BlockSapling.STAGE);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT, BlockSapling.STAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SAPLING_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (!world.isRemote)
        {
            super.updateTick(world, pos, state, rand);
            if (world.isAreaLoaded(pos, 1)) // getLightFromNeighbors (func_175671_l) may cause chunk-loading
            {
                if (world.getLightFromNeighbors(pos.up()) > 8 && rand.nextInt(8) == 0)
                {
                    if (state.getValue(BlockSapling.STAGE) == 0)
                    {
                        // Nothing to re-render, so we pass 4 to avoid unnecessary cost from re-rendering.
                        world.setBlockState(pos, state.cycleProperty(BlockSapling.STAGE), 4);
                    }
                    else
                    {
                        this.growIntoTree(world, rand, pos, state);
                    }
                }
            }
        }
    }

    private void growIntoTree(World world, Random rand, BlockPos pos, IBlockState state)
    {
        if (TerrainGen.saplingGrowTree(world, rand, pos))
        {
            /*
             * Set the tree sapling block to air, so that the tree generator can properly set
             * that spot to wood log.
             *
             * Why not World::setBlockToAir (func_175698_g)? That method use 3 as update flag,
             * which is bit vector 00011 - the last 1 means "cause block update", and the second
             * last 1 means "sync changes to client". Not desired.
             * Thus we have to manually call setBlockState with 4 as update flag. The bit vector
             * will be 00100 - it means "client won't re-render stuff".
             *
             * The explanation is directly taken from MCP.
             */
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

            // And if the tree generation fails, we need to roll back to the sapling block.
            if (!new WorldFeatureCitrusGenusTree(true, state.getValue(VARIANT), false).generate(world, rand, pos))
            {
                world.setBlockState(pos, state, 4);
            }
        }
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return world.rand.nextDouble() < 0.45;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (state.getValue(BlockSapling.STAGE) == 0)
        {
            worldIn.setBlockState(pos, state.withProperty(BlockSapling.STAGE, 1));
        }
        else
        {
            this.growIntoTree(worldIn, rand, pos, state);
        }
    }

    public enum Type implements IStringSerializable
    {
        POMELO, CITRON, MANDARIN, GRAPEFRUIT, ORANGE, LEMON, LIME;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }

    }

}
