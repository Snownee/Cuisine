package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.model.DrinkroMeshDefinition;
import snownee.cuisine.tiles.TileDrinkroBase;
import snownee.cuisine.tiles.TileDrinkroTank;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;
import snownee.kiwi.util.PlayerUtil;

public class BlockDrinkro extends BlockModHorizontal
{
    public static final PropertyBool NORMAL = PropertyBool.create("normal");
    public static final PropertyBool BASE = PropertyBool.create("base");
    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockDrinkro(String name)
    {
        super(name, Material.IRON);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(NORMAL, true).withProperty(BASE, true).withProperty(WORKING, false));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        Item item = Item.getItemFromBlock(this);
        ModelLoader.setCustomMeshDefinition(item, DrinkroMeshDefinition.INSTANCE);
        ModelBakery.registerItemVariants(item, new ResourceLocation(Cuisine.MODID, "drinkro"), new ResourceLocation(Cuisine.MODID, "drinkro_special"));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemGroup, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity != null)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), true);
        }

        BlockPos pos2 = state.getValue(BASE) ? pos.up() : pos.down();
        IBlockState state2 = worldIn.getBlockState(pos2);
        if (state2.getBlock() == this && state2.getValue(BASE) != state.getValue(BASE))
        {
            worldIn.setBlockToAir(pos2);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(getItemInternal(state));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getItemInternal(state);
    }

    private ItemStack getItemInternal(IBlockState state)
    {
        ItemStack stack = new ItemStack(this);
        if (!state.getValue(NORMAL))
        {
            stack.setStackDisplayName("SCP-294");
        }
        return stack;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile == null)
        {
            return true;
        }
        ItemStack held = playerIn.getHeldItem(hand);

        if (held.getItem() != Items.GLASS_BOTTLE)
        {
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(held, 1));
            if (handler != null)
            {
                FluidUtil.interactWithFluidHandler(playerIn, hand, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing));
                return true;
            }
        }

        ItemStackHandler inv = null;
        if (tile instanceof TileDrinkroBase)
        {
            TileDrinkroBase tileDrinkro = (TileDrinkroBase) tile;

            inv = tileDrinkro.inventory;
        }
        else if (tile instanceof TileDrinkroTank)
        {
            TileDrinkroTank tileDrinkro = (TileDrinkroTank) tile;
            if (tileDrinkro.builder != null)
            {
                Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(held);
                if (ingredient != null && ingredient.getForm() == Form.JUICE)
                {
                    tileDrinkro.builder.addIngredient(playerIn, ingredient, tileDrinkro);
                    ItemStack container = held.getItem().getContainerItem(held);
                    held.shrink(1);
                    if (!container.isEmpty())
                    {
                        PlayerUtil.mergeItemStack(container, playerIn, hand);
                    }
                    return true;
                }
            }

            inv = tileDrinkro.inventory;
        }

        if (inv != null)
        {
            if (held.isEmpty() || ItemHandlerHelper.insertItem(inv, held, true).getCount() == held.getCount())
            {
                for (int i = inv.getSlots() - 1; i >= 0; i--)
                {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (!(playerIn instanceof FakePlayer) && stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
                    {
                        playerIn.openGui(Cuisine.getInstance(), CuisineGUI.NAME_FOOD, worldIn, pos.getX(), pos.getY() + 1, pos.getZ());
                        break;
                    }
                    if (!stack.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(playerIn, stack);
                        inv.setStackInSlot(i, ItemStack.EMPTY);
                        break;
                    }
                }
            }
            else
            {
                for (int i = 0; i < inv.getSlots(); i++)
                {
                    if (inv.getStackInSlot(i).isEmpty())
                    {
                        playerIn.setHeldItem(hand, inv.insertItem(i, held, false));
                        break;
                    }
                }
            }
            return true;
        }
        return false;

    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (!state.getValue(BASE))
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDrinkroTank)
            {
                TileDrinkroTank tileDrinkro = (TileDrinkroTank) tile;
                state = state.withProperty(WORKING, tileDrinkro.isWorking());
            }
        }
        return state;
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int color)
    {
        if (id == 0)
        {
            pos = pos.down();
            double r = (color >> 16 & 255) / 255D;
            double g = (color >> 8 & 255) / 255D;
            double b = (color & 255) / 255D;

            for (int j = 0; j < 30; ++j)
            {
                worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), pos.getX() + RANDOM.nextDouble(), pos.getY() + RANDOM.nextDouble(), pos.getZ() + RANDOM.nextDouble(), r, g, b);
            }
            return true;
        }
        else if (id == 1)
        {
            spawnWorkingParticles(worldIn, pos);
            return true;
        }
        return false;
    }

    private static void spawnWorkingParticles(World world, BlockPos pos)
    {
        for (int j = 0; j < 10; ++j)
        {
            world.spawnAlwaysVisibleParticle(EnumParticleTypes.CRIT.getParticleID(), pos.getX() + RANDOM.nextDouble() * 0.4 + 0.3, pos.getY() + RANDOM.nextDouble() * 0.4 + 0.3, pos.getZ() + RANDOM.nextDouble() * 0.4 + 0.3, RANDOM.nextDouble() * 2 - 1, RANDOM.nextDouble() * 2 - 1, RANDOM.nextDouble() * 2 - 1);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return state.getValue(BASE) ? new TileDrinkroBase() : new TileDrinkroTank();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (state.getValue(BASE))
        {
            pos = pos.up();
            state = worldIn.getBlockState(pos);
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDrinkroTank)
        {
            ((TileDrinkroTank) tile).neighborChanged(state);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (state.getValue(BASE))
        {
            return;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileDrinkroTank)
        {
            TileDrinkroTank tileDrinkro = (TileDrinkroTank) tile;
            if (tileDrinkro.isWorking())
            {
                worldIn.setBlockState(pos, state.withProperty(WORKING, Boolean.FALSE));
                worldIn.setBlockState(pos.down(), worldIn.getBlockState(pos.down()).withProperty(WORKING, Boolean.FALSE));
                tileDrinkro.stopProcess();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return state.getValue(BASE) ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(WORKING) && !stateIn.getValue(BASE))
        {
            spawnWorkingParticles(worldIn, pos);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(NORMAL, meta == 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return super.getStateFromMeta(meta).withProperty(NORMAL, (meta & 7) < 4).withProperty(BASE, meta < 8);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return super.getMetaFromState(state) + (state.getValue(NORMAL) ? 0 : 4) + (state.getValue(BASE) ? 0 : 8);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, NORMAL, WORKING, BASE);
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
    public boolean isTranslucent(IBlockState state)
    {
        return state.getValue(BASE);
    }

    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return !state.getValue(BASE);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return state.getValue(BASE) && state.getValue(BlockHorizontal.FACING) == face ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

}
