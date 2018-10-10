package snownee.cuisine.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.entities.EntitySeed;
import snownee.cuisine.items.ItemCrops;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.AABBUtil;

public class BlockBamboo extends BlockMod
{
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NE = PropertyBool.create("ne");
    public static final PropertyBool NW = PropertyBool.create("nw");
    public static final PropertyBool SE = PropertyBool.create("se");
    public static final PropertyBool SW = PropertyBool.create("sw");

    public static final AxisAlignedBB PLATE_AABB = new AxisAlignedBB(0.3125D, 0.625D, 0.3125D, 1, 1, 1);
    public static final AxisAlignedBB SIDE_AABB = new AxisAlignedBB(0.375D, 0.625D, 0.375D, 1, 0.875D, 0.625D);

    public BlockBamboo(String name)
    {
        super(name, Material.WOOD);
        IBlockState stateDefault = blockState.getBaseState().withProperty(NORTH, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(WEST, Boolean.FALSE).withProperty(DOWN, Boolean.FALSE).withProperty(NE, Boolean.FALSE).withProperty(NW, Boolean.FALSE).withProperty(SE, Boolean.FALSE).withProperty(SW, Boolean.FALSE);
        setDefaultState(stateDefault);
        setHardness(0.25F);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        if (CuisineConfig.GENERAL.bambooBlowpipe)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        state = getActualState(state, world, pos);
        return state.getValue(DOWN) || state.getValue(NE) || state.getValue(NW) || state.getValue(SE) || state.getValue(SW) || entity.posY < pos.getY() + 0.875D;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        List<AxisAlignedBB> aabbs = new ArrayList<>();
        addCollisionBoxToListInternal(state, source, pos, new AxisAlignedBB(pos), aabbs, null, false);
        if (aabbs.size() == 0)
        {
            return FULL_BLOCK_AABB;
        }
        AxisAlignedBB aabb = aabbs.get(0);
        for (int i = 1; i < aabbs.size(); ++i)
        {
            aabb = aabb.union(aabbs.get(i));
        }
        return aabb.offset(new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ()));
    }

    @SuppressWarnings("deprecation")
    private static void addCollisionBoxToListInternal(IBlockState state, IBlockAccess worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState)
    {
        if (!isActualState)
        {
            state = state.getActualState(worldIn, pos);
        }

        if (state.getValue(DOWN) || !(state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST)))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockBambooPlant.AABB);
        }

        if (state.getValue(NW))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(PLATE_AABB, EnumFacing.SOUTH));
        }
        if (state.getValue(NE))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(PLATE_AABB, EnumFacing.EAST));
        }
        if (state.getValue(SW))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(PLATE_AABB, EnumFacing.WEST));
        }
        if (state.getValue(SE))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, PLATE_AABB);
        }

        if (!state.getValue(NW) && !state.getValue(NE) && state.getValue(NORTH))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(SIDE_AABB, EnumFacing.EAST));
        }
        if (!state.getValue(SW) && !state.getValue(SE) && state.getValue(SOUTH))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(SIDE_AABB, EnumFacing.WEST));
        }
        if (!state.getValue(NW) && !state.getValue(SW) && state.getValue(WEST))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(SIDE_AABB, EnumFacing.SOUTH));
        }
        if (!state.getValue(SE) && !state.getValue(NE) && state.getValue(EAST))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_AABB);
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToListInternal(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, NORTH, SOUTH, WEST, EAST, DOWN, NE, NW, SE, SW);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        ImmutableMap<EnumFacing, PropertyBool> map = ImmutableMap.of(EnumFacing.NORTH, NORTH, EnumFacing.SOUTH, SOUTH, EnumFacing.EAST, EAST, EnumFacing.WEST, WEST, EnumFacing.DOWN, DOWN);
        for (Entry<EnumFacing, PropertyBool> entry : map.entrySet())
        {
            BlockPos pos2 = pos.offset(entry.getKey());
            IBlockState stateFaced = worldIn.getBlockState(pos2);
            if (stateFaced.getBlock() == this || stateFaced.isSideSolid(worldIn, pos2, entry.getKey().getOpposite()))
            {
                state = state.withProperty(entry.getValue(), Boolean.TRUE);
            }
        }
        //        if (worldIn.getBlockState(pos.up()).getBlock() != this)
        {
            if (state.getValue(NORTH) && state.getValue(EAST))
            {
                BlockPos posFaced = pos.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
                IBlockState stateFaced = worldIn.getBlockState(posFaced);
                if (stateFaced.getBlock() == this || stateFaced.isFullBlock())
                {
                    state = state.withProperty(NE, Boolean.TRUE);
                }
            }
            if (state.getValue(NORTH) && state.getValue(WEST))
            {
                BlockPos posFaced = pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
                IBlockState stateFaced = worldIn.getBlockState(posFaced);
                if (stateFaced.getBlock() == this || stateFaced.isFullBlock())
                {
                    state = state.withProperty(NW, Boolean.TRUE);
                }
            }
            if (state.getValue(SOUTH) && state.getValue(EAST))
            {
                BlockPos posFaced = pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
                IBlockState stateFaced = worldIn.getBlockState(posFaced);
                if (stateFaced.getBlock() == this || stateFaced.isFullBlock())
                {
                    state = state.withProperty(SE, Boolean.TRUE);
                }
            }
            if (state.getValue(SOUTH) && state.getValue(WEST))
            {
                BlockPos posFaced = pos.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST);
                IBlockState stateFaced = worldIn.getBlockState(posFaced);
                if (stateFaced.getBlock() == this || stateFaced.isFullBlock())
                {
                    state = state.withProperty(SW, Boolean.TRUE);
                }
            }
        }
        return state;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if (face == EnumFacing.DOWN)
        {
            if (state.getValue(DOWN) || !(state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST)))
            {
                return BlockFaceShape.CENTER;
            }
            else
            {
                return BlockFaceShape.UNDEFINED;
            }
        }
        if (face == EnumFacing.UP)
        {
            boolean flag = state.getValue(NW) && state.getValue(NE) && state.getValue(SW) && state.getValue(SE);
            return flag ? BlockFaceShape.SOLID : BlockFaceShape.CENTER;
        }
        if (face == EnumFacing.NORTH)
        {
            return state.getValue(NORTH) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        if (face == EnumFacing.SOUTH)
        {
            return state.getValue(SOUTH) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        if (face == EnumFacing.WEST)
        {
            return state.getValue(WEST) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        if (face == EnumFacing.EAST)
        {
            return state.getValue(EAST) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        return BlockFaceShape.UNDEFINED;
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
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.UP || side == EnumFacing.DOWN)
        {
            return blockState.getValue(NORTH) || blockState.getValue(SOUTH) || blockState.getValue(WEST) || blockState.getValue(EAST);
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getHand() == EnumHand.MAIN_HAND && event.getItemStack().getItem() == Item.getItemFromBlock(this))
        {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.getHeldItemOffhand();
            if (stack.isEmpty())
            {
                return;
            }
            if (stack.getItem() instanceof ItemSeeds || (stack.getItem() == Items.DYE && stack.getMetadata() == 3) || (stack.getItem() == CuisineRegistry.CROPS && (stack.getMetadata() == ItemCrops.Variants.SOYBEAN.getMeta() || stack.getMetadata() == ItemCrops.Variants.PEANUT.getMeta())))
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (event.getHand() == EnumHand.MAIN_HAND && event.getItemStack().getItem() == Item.getItemFromBlock(this))
        {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.getHeldItemOffhand();
            if (stack.isEmpty())
            {
                return;
            }
            if (stack.getItem() instanceof ItemSeeds || (stack.getItem() == Items.DYE && stack.getMetadata() == 3) || (stack.getItem() == CuisineRegistry.CROPS && (stack.getMetadata() == ItemCrops.Variants.SOYBEAN.getMeta() || stack.getMetadata() == ItemCrops.Variants.PEANUT.getMeta())))
            {
                if (!event.getWorld().isRemote)
                {
                    EntitySeed seed = new EntitySeed(event.getWorld(), player, ItemHandlerHelper.copyStackWithSize(stack, 1));
                    seed.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
                    event.getWorld().spawnEntity(seed);
                }

                player.getCooldownTracker().setCooldown(Item.getItemFromBlock(CuisineRegistry.BAMBOO), 20);
                event.getWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (event.getWorld().rand.nextFloat() * 0.4F + 0.8F));
                if (!player.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
            }
        }
    }
}
