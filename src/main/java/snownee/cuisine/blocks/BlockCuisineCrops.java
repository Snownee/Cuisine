package snownee.cuisine.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.library.RarityManager;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.definition.ItemDefinition;

@SuppressWarnings("deprecation")
public class BlockCuisineCrops extends BlockMod implements IGrowable, IPlantable
{

    private final EnumPlantType plantType;

    private final ItemDefinition seed;
    private final ItemDefinition crop;

    public BlockCuisineCrops(String name, ItemDefinition crop)
    {
        this(name, crop, crop);
    }

    public BlockCuisineCrops(String name, ItemDefinition crop, ItemDefinition seed)
    {
        this(name, EnumPlantType.Crop, crop, seed);
    }

    public BlockCuisineCrops(String name, EnumPlantType plantType, ItemDefinition crop)
    {
        this(name, plantType, crop, crop);
    }

    public BlockCuisineCrops(String name, EnumPlantType plantType, ItemDefinition crop, ItemDefinition seed)
    {
        super(name, Material.PLANTS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(getAgeProperty(), 0));
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        this.setTickRandomly(true);
        this.disableStats();
        this.plantType = plantType;
        this.crop = crop;
        this.seed = seed;
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    public String getTranslationKey()
    {
        return getCrop().getItemStack().getTranslationKey();
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean lightSufficient = worldIn.getLight(pos) >= 8;
        boolean exposedUnderSunlight = worldIn.canBlockSeeSky(pos);
        return (lightSufficient || exposedUnderSunlight) && canSustain(worldIn, pos, worldIn.getBlockState(pos.down()));
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    protected boolean canSustain(World world, BlockPos pos, IBlockState state)
    {
        IBlockState soilState = world.getBlockState(pos.down());
        if (getPlantType(world, pos) == EnumPlantType.Water)
        {
            if (soilState.getBlock() != Blocks.WATER)
            {
                return false;
            }
            soilState = world.getBlockState(pos.down(2));
            return soilState.getMaterial() == Material.GROUND || soilState.getMaterial() == Material.GRASS;
        }
        return soilState.getBlock().canSustainPlant(soilState, world, pos, EnumFacing.UP, this);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        checkAndDropBlock(worldIn, pos, state);
    }

    public ItemDefinition getSeed()
    {
        return seed;
    }

    public ItemDefinition getCrop()
    {
        return crop;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(getSeed().getItemStack());
        int age = getAge(state, world, pos);
        if (age == getMaxAge())
        {
            for (int i = 0; i < EnumRarity.EPIC.ordinal() - getCrop().getItemStack().getRarity().ordinal() + 2; ++i)
            {
                if (RANDOM.nextInt(2 * getMaxAge()) <= age)
                {
                    drops.add(getCrop().getItemStack());
                }
            }

            if (getCrop().getItemStack().getItem() instanceof ItemBasicFood && RANDOM.nextInt(99) < (1 + age - getMaxAge()) * 10)
            {
                ItemStack stack = getCrop().getItemStack();
                RarityManager.setRarity(stack, getCrop().getItemStack().getRarity().ordinal() + 1);
                drops.add(stack);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);

        if (!worldIn.isAreaLoaded(pos, 1))
            return; // prevent loading unloaded chunks when checking neighbor's light
        if (getPlantType(worldIn, pos) == EnumPlantType.Water)
        {
            checkAndDropBlock(worldIn, pos, state);
        }
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
            int i = this.getAge(state, worldIn, pos);

            if (i < this.getMaxAge())
            {
                float f = getGrowthChance(this, worldIn, pos);

                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0))
                {
                    worldIn.setBlockState(pos, this.withAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }
    }

    protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos)
    {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i)
        {
            for (int j = -1; j <= 1; ++j)
            {
                float f1 = 0.0F;
                IBlockState iblockstate = worldIn.getBlockState(blockpos.add(i, 0, j));

                if (iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, blockpos.add(i, 0, j), EnumFacing.UP, (IPlantable) blockIn))
                {
                    f1 = 1.0F;

                    if (iblockstate.getBlock().isFertile(worldIn, blockpos.add(i, 0, j)))
                    {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0)
                {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
        boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();

        if (flag && flag1)
        {
            f /= 2.0F;
        }
        else
        {
            boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();

            if (flag2)
            {
                f /= 2.0F;
            }
        }

        return f;
    }

    protected int getBonemealAgeIncrease(World worldIn)
    {
        return MathHelper.getInt(worldIn.rand, 2, 3);
    }

    public PropertyInteger getAgeProperty()
    {
        return BlockCrops.AGE;
    }

    public int getMaxAge()
    {
        return 7;
    }

    public int getAge(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(getAgeProperty());
    }

    public IBlockState withAge(int age)
    {
        return this.getDefaultState().withProperty(getAgeProperty(), age);
    }

    public boolean isMaxAge(IBlockState state, World world, BlockPos pos)
    {
        return getAge(state, world, pos) == getMaxAge();
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return !this.isMaxAge(state, worldIn, pos);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        int i = this.getAge(state, worldIn, pos) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();

        if (i > j)
        {
            i = j;
        }

        worldIn.setBlockState(pos, withAge(i), 2);
        if (state.getBlock() instanceof BlockCorn && i > 1)
        {
            worldIn.setBlockState(pos.up(), withAge(8), 2);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.withAge(meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(getAgeProperty());
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getAgeProperty());
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return plantType;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return getDefaultState();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return Blocks.WHEAT.getBoundingBox(state, source, pos);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getSeed().getItemStack();
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return false;
    }
}
