package snownee.cuisine.blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.world.feature.WorldFeatureBamboo;
import snownee.kiwi.block.BlockMod;

public class BlockBambooPlant extends BlockMod implements IPlantable, IGrowable
{
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 1, 0.6875D);
    public static final AxisAlignedBB AABB_SHOOT = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 0.6875D, 0.6875D);
    public static final AxisAlignedBB AABB_LEAVES = new AxisAlignedBB(0.1875D, 0, 0.1875D, 0.8125D, 0.625D, 0.8125D);

    public static long LAST_RAIN_TIME = -12000;

    public BlockBambooPlant(String name)
    {
        super(name, Material.PLANTS);
        setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Type.A_0));
        setTickRandomly(true);
        setHardness(0.25F);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasItem()
    {
        return false;
    }

    public boolean generateBamboo(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        WorldGenerator generator = new WorldFeatureBamboo(true);
        return generator.generate(worldIn, rand, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (!worldIn.isAreaLoaded(pos, 1))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light
            if (worldIn.isRaining())
            {
                LAST_RAIN_TIME = worldIn.getTotalWorldTime();
            }
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(6) == 0)
            {
                Biome biome = worldIn.getBiome(pos);
                if (!biome.canRain() || biome.isSnowyBiome())
                {
                    return;
                }
                else if (state.getValue(TYPE).ordinal() < 2)
                {
                    this.grow(worldIn, rand, pos, state);
                }
                else if (state.getValue(TYPE).ordinal() < 6 && !worldIn.isRaining() && worldIn.getBlockState(pos.down()).getBlock() != this && worldIn.canSeeSky(pos))
                {
                    for (int i = 1; i < 6; ++i)
                    {
                        if (worldIn.getBlockState(pos.up(i)).getBlock() != this)
                        {
                            return;
                        }
                    }
                    long interval = worldIn.getTotalWorldTime() - LAST_RAIN_TIME;
                    if (!worldIn.getGameRules().getBoolean("doWeatherCycle") || (interval > 0 && interval < 9000))
                    {
                        boolean flag = false;
                        int count = Math.min(1 + rand.nextInt(3), 2);
                        while (--count != 0)
                        {
                            BlockPos position = pos.add(rand.nextInt(7) - 3, 3, rand.nextInt(7) - 3);
                            IBlockState iblockstate;
                            for (iblockstate = worldIn.getBlockState(position); iblockstate.getBlock().isReplaceable(worldIn, position) && !(iblockstate.getBlock() instanceof IFluidBlock) && !(iblockstate.getBlock() instanceof BlockLiquid); iblockstate = worldIn.getBlockState(position))
                            {
                                if (position.getY() + 3 < pos.getY())
                                {
                                    break;
                                }
                                position = position.down();
                            }
                            if (!(iblockstate.getBlock() == Blocks.FARMLAND && iblockstate.getValue(BlockFarmland.MOISTURE) == 7) && rand.nextBoolean())
                            {
                                continue;
                            }
                            position = position.up();
                            if (canPlaceBlockAt(worldIn, position))
                            {
                                worldIn.setBlockState(position, getDefaultState());
                                worldIn.playEvent(2005, position, 0);
                                worldIn.playEvent(2001, position, Block.getStateId(getDefaultState()));
                                flag = true;
                            }
                        }
                        if (flag)
                        {
                            worldIn.setBlockState(pos, state.cycleProperty(TYPE));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(TYPE).ordinal() < 2;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return worldIn.rand.nextFloat() < 0.45F;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (state.getValue(TYPE) == Type.A_0)
        {
            worldIn.setBlockState(pos, state.withProperty(TYPE, Type.A_1), 4);
        }
        else if (generateBamboo(worldIn, pos, state, rand))
        {
            worldIn.playEvent(2001, pos, Block.getStateId(state));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getItemInternal(world, pos, state);
    }

    public static ItemStack getItemInternal(IBlockAccess blockAccess, BlockPos pos, IBlockState state)
    {
        int ordinal = state.getValue(TYPE).ordinal();
        if (ordinal < 2)
        {
            return CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.BAMBOO_SHOOT);
        }
        else if (ordinal < 7)
        {
            return new ItemStack(CuisineRegistry.BAMBOO);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(getItemInternal(world, pos, state));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos.down());
        return super.canPlaceBlockAt(worldIn, pos) && !(state.getBlock() instanceof IFluidBlock) && !(state.getBlock() instanceof IFluidBlock) && state.getBlock().canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.checkForDrop(worldIn, pos, state);
    }

    protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        int ordinal = state.getValue(TYPE).ordinal();
        if (ordinal < 7)
        {
            if (this.canBlockStay(worldIn, pos))
            {
                return true;
            }
            else
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return false;
            }
        }
        else
        {
            BlockPos basePos = pos.offset(EnumFacing.byHorizontalIndex(ordinal - 7).getOpposite());
            boolean flag = worldIn.getBlockState(basePos).getBlock() == this;
            if (!flag)
            {
                worldIn.setBlockToAir(pos);
            }
            return flag;
        }
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getValue(TYPE).ordinal() < 2)
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos.down());
        return state.getBlock().canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this) || worldIn.getBlockState(pos.down()).getBlock() == this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TYPE).ordinal();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        Type[] values = Type.values();
        meta = MathHelper.clamp(meta, 0, values.length - 1);
        return this.getDefaultState().withProperty(TYPE, values[meta]);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Plains;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return getDefaultState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side != EnumFacing.UP && side != EnumFacing.DOWN && blockState.getValue(TYPE).ordinal() < 7;
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int ordinal = state.getValue(TYPE).ordinal();
        if (ordinal > 6)
        {
            BlockPos basePos = pos.offset(EnumFacing.byHorizontalIndex(ordinal - 7).getOpposite());
            IBlockState baseState = worldIn.getBlockState(basePos);
            if (baseState.getBlock() == this && baseState.getValue(TYPE).ordinal() < 7) // Avoid potential StackOverflow risk
            {
                return baseState.getOffset(worldIn, basePos);
            }
        }
        return super.getOffset(state, worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return blockState.getValue(TYPE).ordinal() < 2 ? NULL_AABB : super.getCollisionBoundingBox(blockState, worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        int ordinal = state.getValue(TYPE).ordinal();
        AxisAlignedBB aabb;
        if (ordinal < 2)
        {
            aabb = AABB_SHOOT;
        }
        else if (ordinal < 7)
        {
            aabb = AABB;
        }
        else
        {
            aabb = AABB_LEAVES;
        }
        return aabb.offset(state.getOffset(source, pos));
    }

    public enum Type implements IStringSerializable
    {
        // A for Age, B for Branch
        A_0, A_1, A_2, A_3, A_4, A_5, A_6, B_S, B_W, B_N, B_E;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ROOT);
        }
    }
}
