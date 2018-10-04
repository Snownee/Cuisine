package snownee.cuisine.blocks;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.items.ItemKitchenKnife;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.tiles.TileChoppingBoard;
import snownee.cuisine.tiles.TileChoppingBoard.ProcessionType;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.util.OreUtil;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public class BlockChoppingBoard extends BlockMod
{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

    public BlockChoppingBoard(String name)
    {
        super(name, Material.WOOD);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setHardness(1.5F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileChoppingBoard)
        {
            TileChoppingBoard teCB = (TileChoppingBoard) te;
            ItemStack held = playerIn.getHeldItem(hand);
            boolean empty = teCB.stacks.getStackInSlot(0).isEmpty();
            if (!empty && hand == EnumHand.MAIN_HAND && held.getItem() == CuisineRegistry.KITCHEN_KNIFE)
            {
                if (!worldIn.isRemote)
                {
                    // spawn particles
                    NetworkChannel.INSTANCE.sendToAllAround(new PacketCustomEvent(2, pos.getX(), pos.getY(), pos.getZ()), worldIn.provider.getDimension(), pos);
                }
                teCB.process(playerIn, held, ProcessionType.KNIFE_VERTICAL, null);
                return true;
            }
            else if (empty && teCB.isItemValidForSlot(0, held))
            {
                if (hand == EnumHand.MAIN_HAND && teCB.isItemValidForSlot(0, playerIn.getHeldItemOffhand()))
                {
                    return false;
                }
                teCB.setFacing(playerIn.getHorizontalFacing());
                if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP)
                {
                    held = teCB.insertItem((EntityPlayerMP) playerIn, held);
                    playerIn.setHeldItem(hand, held);
                    return !teCB.stacks.getStackInSlot(0).isEmpty();
                }
                return true;
            }
            else if (!empty)
            {
                StacksUtil.dropInventoryItems(worldIn, pos, teCB.stacks, false);
                teCB.resetProcess();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileChoppingBoard)
        {
            ItemStack held = playerIn.getHeldItemMainhand();
            if (held.getItem() == CuisineRegistry.KITCHEN_KNIFE)
            {
                ((TileChoppingBoard) te).process(playerIn, held, ProcessionType.KNIFE_HORIZONTAL, null);
                return;
            }
            else if (!worldIn.isRemote && CuisineConfig.PROGRESSION.axeChopping && playerIn.getCooledAttackStrength(0) > 0.5)
            {
                int harvestLevel = held.getItem().getHarvestLevel(held, "axe", playerIn, null);
                if (harvestLevel >= 0)
                {
                    ((TileChoppingBoard) te).process(playerIn, held, ProcessionType.AXE, harvestLevel);
                }
            }
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
        if (te instanceof TileChoppingBoard)
        {
            TileChoppingBoard teCB = (TileChoppingBoard) te;
            ItemStack stack = teCB.stacks.getStackInSlot(0);
            if (stack.isEmpty())
            {
                return 0;
            }
            if (stack.getItem() != CuisineRegistry.INGREDIENT)
            {
                return 1;
            }
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null)
            {
                Ingredient ingredient = CuisinePersistenceCenter.deserializeIngredient(tag);
                return ingredient.getForm().ordinal() + 1;
            }
        }
        return 0;
    }

    @Override
    public boolean isTranslucent(IBlockState state)
    {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState state)
    {
        return new TileChoppingBoard(false);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    public ItemStack getItemStack(ItemStack cover)
    {
        ItemStack stack = new ItemStack(this);
        if (stack.getItem() instanceof ItemBlock)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("cover", cover.serializeNBT());
            ItemNBTUtil.setCompound(stack, "BlockEntityTag", tag);
        }
        return stack;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        // NO-OP
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileChoppingBoard)
        {
            return ((TileChoppingBoard) te).getSelfItem();
        }
        return new ItemStack(this);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileChoppingBoard)
        {
            spawnAsEntity(worldIn, pos, ((TileChoppingBoard) te).getSelfItem());
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileChoppingBoard) te).stacks, true);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public static NonNullList<ItemStack> getSuitableCovers()
    {
        // TODO We probably want to expose the NonNullList constructor that allows custom delegates
        List<ItemStack> items = OreUtil.getItemsFromOre("logWood", 1).stream().filter(i -> i.getItem() instanceof ItemBlock).collect(Collectors.toList());
        NonNullList<ItemStack> results = NonNullList.create();
        results.addAll(items);
        return results;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        getSuitableCovers().forEach(cover -> items.add(getItemStack(cover)));
    }

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound tag = ItemNBTUtil.getCompound(stack, "BlockEntityTag", true);
        if (tag != null && tag.hasKey("cover", Constants.NBT.TAG_COMPOUND))
        {
            ItemStack stack2 = new ItemStack(tag.getCompoundTag("cover"));
            tooltip.add(stack2.getDisplayName());
        }
        else
        {
            tooltip.add(TileChoppingBoard.DEFAULT_COVER.getDisplayName());
        }
    }

    @SubscribeEvent
    public static void onChoppingBoardClick(PlayerInteractEvent.LeftClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (event.getEntityPlayer().isCreative() && state.getBlock() == CuisineRegistry.CHOPPING_BOARD)
        {
            ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());
            if (held.getItem() instanceof ItemKitchenKnife || held.getItem().getToolClasses(held).contains("axe"))
            {
                event.setCanceled(true);
                state.getBlock().onBlockClicked(world, pos, event.getEntityPlayer());
                event.setCancellationResult(EnumActionResult.SUCCESS);
            }
        }
    }
}
