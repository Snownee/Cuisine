package snownee.cuisine.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockModSapling.Type;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemBasicFood.Variants.SubItem;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.VariantsHolder.Variant;

@EventBusSubscriber(modid = Cuisine.MODID)
public class BlockModLeaves extends BlockMod implements IGrowable, IShearable
{

    public static final PropertyBool CORE = PropertyBool.create("core");
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

    private final Variant<SubItem> fruit;
    private static int[] surroundings;

    public BlockModLeaves(String name, Variant<SubItem> fruit)
    {
        super(name, Material.LEAVES);
        this.setTickRandomly(true);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        setDefaultState(blockState.getBaseState().withProperty(CORE, false).withProperty(AGE, 1).withProperty(BlockLeaves.CHECK_DECAY, false));
        this.fruit = fruit;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockHarvested(BlockEvent.HarvestDropsEvent event)
    {
        if (event.getState().getBlock() == this && event.getState().getValue(AGE) == 3)
        {
            event.getDrops().add(CuisineRegistry.BASIC_FOOD.getItemStack(fruit));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(CORE).ignore(BlockLeaves.CHECK_DECAY).build());
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, CORE, AGE, BlockLeaves.CHECK_DECAY);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(AGE);
        if (state.getValue(CORE))
        {
            meta |= 4;
        }
        if (state.getValue(BlockLeaves.CHECK_DECAY))
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
            state = state.withProperty(BlockLeaves.CHECK_DECAY, true);
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
        if (!CuisineConfig.GENERAL.fruitDrops && state.getValue(AGE) == 3)
        {
            return false;
        }
        return state.getValue(AGE) > 0;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return state.getValue(AGE) != 3 && worldIn.rand.nextFloat() < 0.7F;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (state.getValue(AGE) == 3)
        {
            worldIn.setBlockState(pos, state.withProperty(AGE, 1));
            spawnAsEntity(worldIn, pos, CuisineRegistry.BASIC_FOOD.getItemStack(fruit));
        }
        else
        {
            worldIn.setBlockState(pos, state.cycleProperty(AGE));
        }
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
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

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(AGE) == 3 && worldIn.setBlockState(pos, state.withProperty(AGE, 1)))
        {
            ItemHandlerHelper.giveItemToPlayer(playerIn, CuisineRegistry.BASIC_FOOD.getItemStack(fruit));
            return true;
        }
        return false;
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        if (!state.getValue(BlockLeaves.CHECK_DECAY))
        {
            world.setBlockState(pos, state.withProperty(BlockLeaves.CHECK_DECAY, true), 4);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        int k = pos.getX();
        int l = pos.getY();
        int i1 = pos.getZ();

        if (worldIn.isAreaLoaded(new BlockPos(k - 2, l - 2, i1 - 2), new BlockPos(k + 2, l + 2, i1 + 2)))
        {
            for (int j1 = -1; j1 <= 1; ++j1)
            {
                for (int k1 = -1; k1 <= 1; ++k1)
                {
                    for (int l1 = -1; l1 <= 1; ++l1)
                    {
                        BlockPos blockpos = pos.add(j1, k1, l1);
                        IBlockState iblockstate = worldIn.getBlockState(blockpos);

                        if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos))
                        {
                            iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(BlockLeaves.CHECK_DECAY))
            {
                int k = pos.getX();
                int l = pos.getY();
                int i1 = pos.getZ();
                if (BlockModLeaves.surroundings == null)
                {
                    BlockModLeaves.surroundings = new int[32768];
                }

                if (!worldIn.isAreaLoaded(pos, 1))
                    return; // Forge: prevent decaying leaves from updating neighbors and loading unloaded chunks
                if (worldIn.isAreaLoaded(pos, 6)) // Forge: extend range from 5 to 6 to account for neighbor checks in world.markAndNotifyBlock -> world.updateObservingBlocksAt
                {
                    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                    for (int i2 = -4; i2 <= 4; ++i2)
                    {
                        for (int j2 = -4; j2 <= 4; ++j2)
                        {
                            for (int k2 = -4; k2 <= 4; ++k2)
                            {
                                IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2));
                                Block block = iblockstate.getBlock();

                                if (!block.canSustainLeaves(iblockstate, worldIn, blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2)))
                                {
                                    if (block.isLeaves(iblockstate, worldIn, blockpos$mutableblockpos.setPos(k + i2, l + j2, i1 + k2)))
                                    {
                                        BlockModLeaves.surroundings[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -2;
                                    }
                                    else
                                    {
                                        BlockModLeaves.surroundings[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -1;
                                    }
                                }
                                else
                                {
                                    BlockModLeaves.surroundings[(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = 0;
                                }
                            }
                        }
                    }

                    for (int i3 = 1; i3 <= 4; ++i3)
                    {
                        for (int j3 = -4; j3 <= 4; ++j3)
                        {
                            for (int k3 = -4; k3 <= 4; ++k3)
                            {
                                for (int l3 = -4; l3 <= 4; ++l3)
                                {
                                    if (BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16] == i3 - 1)
                                    {
                                        if (BlockModLeaves.surroundings[(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3;
                                        }

                                        if (BlockModLeaves.surroundings[(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3;
                                        }

                                        if (BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] = i3;
                                        }

                                        if (BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] = i3;
                                        }

                                        if (BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] = i3;
                                        }

                                        if (BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] == -2)
                                        {
                                            BlockModLeaves.surroundings[(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] = i3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int l2 = BlockModLeaves.surroundings[16912];

                if (l2 >= 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockLeaves.CHECK_DECAY, false), 4);
                }
                else
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                    worldIn.setBlockToAir(pos);
                }
            }
            else if (canGrow(worldIn, pos, state, false) && worldIn.isAreaLoaded(pos, 1) && worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(100) > 99 - CuisineConfig.GENERAL.fruitGrowingSpeed)
            {
                grow(worldIn, rand, pos, state);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState)
    {
        if (entityIn != null && !(entityIn instanceof EntityItem))
        {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        if (fallDistance >= 1 && entityIn instanceof EntityLivingBase)
        {
            for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 1, Math.max(0, pos.getY() - 2), pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1))
            {
                IBlockState state = worldIn.getBlockState(pos2);
                if (state.getBlock() instanceof BlockModLeaves && state.getValue(AGE) == 3)
                {
                    ((BlockModLeaves) state.getBlock()).grow(worldIn, worldIn.rand, pos2, state);
                }
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote && state.getBlock() == CuisineRegistry.LEAVES_CITRON && blockIn == Blocks.AIR && fromPos.equals(pos.up()) && worldIn.getBlockState(fromPos).getBlock() == Blocks.FIRE)
        {
            boolean flag = false;
            for (Entity entity : worldIn.weatherEffects)
            {
                if (entity instanceof EntityLightningBolt && entity.getPosition().equals(fromPos))
                {
                    flag = true;
                }
            }
            if (!flag)
            {
                return;
            }
            for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3))
            {
                IBlockState state2 = worldIn.getBlockState(pos2);
                if (state2.getBlock() == this && state2.getValue(AGE) == 3)
                {
                    worldIn.setBlockState(pos2, state2.withProperty(AGE, 1));
                    if (worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
                    {
                        ItemStack stack = CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.EMPOWERED_CITRON);
                        if (captureDrops.get())
                        {
                            capturedDrops.get().add(stack);
                            continue;
                        }
                        double d0 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        double d1 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        double d2 = worldIn.rand.nextFloat() * 0.5F + 0.25D;
                        EntityItem entityitem = new EntityItem(worldIn, pos2.getX() + d0, pos2.getY() + d1, pos2.getZ() + d2, stack);
                        entityitem.setDefaultPickupDelay();
                        entityitem.setEntityInvulnerable(true);
                        worldIn.spawnEntity(entityitem);
                    }
                }
            }
        }
    }
}
