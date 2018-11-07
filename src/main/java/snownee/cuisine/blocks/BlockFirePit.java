package snownee.cuisine.blocks;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.cuisine.tiles.TileWok;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;
import snownee.kiwi.util.AABBUtil;
import snownee.kiwi.util.OreUtil;

@SuppressWarnings("deprecation")
public class BlockFirePit extends BlockModHorizontal
{

    public static final PropertyEnum<Component> COMPONENT = PropertyEnum.create("component", Component.class);

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 1D);
    private static final AxisAlignedBB AABB_WITH_WOK = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.475D, 1D);
    private static final AxisAlignedBB AABB_STICKS = new AxisAlignedBB(0.4D, 0D, 0D, 0.6D, 1D, 1D);

    public BlockFirePit(String name)
    {
        super(name, Material.ROCK);
        setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH).withProperty(COMPONENT, Component.NONE));
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setLightLevel(0.9375F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        Item item = Item.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(getRegistryName(), "fire_pit"));
        ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(getRegistryName(), "wok"));
        ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(getRegistryName(), "bbq_rack"));
    }

    @Override
    public int getItemSubtypeAmount()
    {
        return 3;
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
    public boolean isBlockNormalCube(IBlockState blockState)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return hasComponent(state, Component.WOK) ? AABB_WITH_WOK : AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        if (hasComponent(state, Component.STICKS))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(AABB_STICKS, state.getValue(BlockHorizontal.FACING)));
        }
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        if (hasComponent(state, Component.STICKS))
        {
            RayTraceResult result = rayTrace(pos, start, end, AABBUtil.rotate(AABB_STICKS, state.getValue(BlockHorizontal.FACING)));
            if (result != null && result.typeOfHit != RayTraceResult.Type.MISS)
            {
                return result;
            }
        }
        return super.collisionRayTrace(state, worldIn, pos, start, end);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem() == CuisineRegistry.WOK && hasComponent(state, Component.NONE))
        {
            stack.shrink(1);
            worldIn.setBlockState(pos, getDefaultState().withProperty(BlockHorizontal.FACING, playerIn.getHorizontalFacing().getOpposite()).withProperty(COMPONENT, Component.WOK));
            return true;
        }
        if (OreUtil.doesItemHaveOreName(stack, "stickWood") && stack.getCount() >= 3 && hasComponent(state, Component.NONE))
        {
            stack.shrink(3);
            worldIn.setBlockState(pos, state.withProperty(COMPONENT, Component.STICKS));
            return true;
        }
        else if (hand == EnumHand.MAIN_HAND && hasComponent(state, Component.WOK)) // You cannot use off-hand to control wok. Simply can't.
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileWok)
            {
                if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP)
                {
                    ((TileWok) tile).onActivated((EntityPlayerMP) playerIn, hand, facing);
                }
                return true;
            }
        }
        else if (hand == EnumHand.MAIN_HAND && hasComponent(state, Component.STICKS))
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileBarbecueRack)
            {
                if (!worldIn.isRemote)
                {
                    TileBarbecueRack teBR = (TileBarbecueRack) tile;
                    List<AxisAlignedBB> aabbs = Lists.newArrayList();
                    EnumFacing facing2 = state.getValue(BlockHorizontal.FACING);
                    AxisAlignedBB aabbItem = AABBUtil.rotate(new AxisAlignedBB(0.3D, 0.5D, 0.2D, 0.7D, 0.9D, 0.4D), facing2);
                    AxisAlignedBB aabbEmpty = AABBUtil.rotate(new AxisAlignedBB(0.45D, 0.65D, 0.2D, 0.55D, 0.75D, 0.4D), facing2);
                    for (int i = 0; i < 3; i++)
                    {
                        aabbs.add((teBR.stacks.getStackInSlot(2 - i).isEmpty() ? aabbEmpty : aabbItem).offset(facing2.getDirectionVec().getX() * 0.2 * i, 0, facing2.getOpposite().getDirectionVec().getZ() * 0.2 * i).offset(pos));
                    }
                    int result = AABBUtil.rayTraceByDistance(playerIn, aabbs);
                    if (result != -1)
                    {
                        result = 2 - result;
                        ItemStack stackSlot = teBR.stacks.extractItem(result, Integer.MAX_VALUE, false);
                        if (stackSlot.isEmpty())
                        {
                            if (!stack.isEmpty())
                            {
                                playerIn.setHeldItem(hand, teBR.stacks.insertItem(result, stack, false));
                            }
                        }
                        else
                        {
                            EntityItem entityitem = new EntityItem(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stackSlot);
                            entityitem.motionX = 0;
                            entityitem.motionY = 0;
                            entityitem.motionZ = 0;
                            worldIn.spawnEntity(entityitem);
                            if (!(playerIn instanceof FakePlayer))
                            {
                                entityitem.onCollideWithPlayer(playerIn);
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileBarbecueRack)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileBarbecueRack) tileentity).stacks, true);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(CuisineRegistry.FIRE_PIT));
        if (hasComponent(state, Component.WOK))
        {
            drops.add(new ItemStack(CuisineRegistry.WOK));
        }
        else if (hasComponent(state, Component.STICKS))
        {
            drops.add(new ItemStack(Items.STICK, 3));
        }
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        switch (state.getValue(COMPONENT))
        {
        default:
            return 0;
        case WOK:
            return 1;
        case STICKS:
            return 2;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        float f = (float) (rand.nextFloat() * Math.PI * 2);
        double x = pos.getX() + 0.5D + MathHelper.sin(f) * 0.1D;
        double y = pos.getY() + 0.12D + rand.nextDouble() * 0.05D;
        double z = pos.getZ() + 0.5D + MathHelper.cos(f) * 0.1D;
        if (!hasComponent(stateIn, Component.WOK))
        {
            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
        else if (rand.nextInt(5) == 0)
        {
            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), pos.getX() + 0.5D + MathHelper.sin(f) * rand.nextDouble() * 0.5D, y + 0.2D, pos.getZ() + 0.5D + MathHelper.cos(f) * rand.nextDouble() * 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }

    public boolean hasComponent(IBlockState state, Component component)
    {
        return state.getValue(COMPONENT) == component;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        if (meta >= 0 && meta < Component.values().length)
        {
            state = state.withProperty(COMPONENT, Component.values()[meta]);
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byHorizontalIndex(meta % 4)).withProperty(COMPONENT, Component.values()[MathHelper.clamp(meta / 4, 0, Component.values().length - 1)]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockHorizontal.FACING).getHorizontalIndex() + state.getValue(COMPONENT).ordinal() * 4;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, COMPONENT);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return !hasComponent(state, Component.NONE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        switch (state.getValue(COMPONENT))
        {
        case WOK:
            return new TileWok();
        case STICKS:
            return new TileBarbecueRack();
        default:
            return null;
        }
    }

    public enum Component implements IStringSerializable
    {
        NONE, WOK, STICKS;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBarbecueRack)
        {
            TileBarbecueRack teBR = (TileBarbecueRack) te;
            int output = 0;
            for (ItemStack stack : teBR.stacks.getStacks())
            {
                if (!stack.isEmpty())
                {
                    output += teBR.isItemValidForSlot(0, stack) ? 1 : 5;
                }
            }
            return output;
        }
        return 0;
    }
}
