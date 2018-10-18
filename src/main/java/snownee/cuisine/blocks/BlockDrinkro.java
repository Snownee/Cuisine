package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.client.model.DrinkroMeshDefinition;
import snownee.cuisine.tiles.TileDrinkro;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;
import snownee.kiwi.util.PlayerUtil;

public class BlockDrinkro extends BlockModHorizontal
{
    public static final PropertyBool NORMAL = PropertyBool.create("normal");
    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockDrinkro(String name)
    {
        super(name, Material.IRON);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(NORMAL, true).withProperty(WORKING, false));
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
    public boolean hasItem()
    {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileDrinkro)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileDrinkro) tileentity).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), true);
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
        if (tile instanceof TileDrinkro)
        {
            TileDrinkro tileDrinkro = (TileDrinkro) tile;
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

            Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(held);
            if (ingredient != null)
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

            ItemStackHandler inv = hitY > 0.5 ? tileDrinkro.inputs : tileDrinkro.output;

            if (held.isEmpty() || ItemHandlerHelper.insertItem(inv, held, true).getCount() == held.getCount())
            {
                for (int i = inv.getSlots() - 1; i >= 0; i--)
                {
                    ItemStack stack = inv.getStackInSlot(i);
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
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileDrinkro();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!state.getValue(WORKING))
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDrinkro)
            {
                ((TileDrinkro) tile).neighborChanged(state);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (state.getValue(WORKING))
        {
            worldIn.setBlockState(pos, state.withProperty(WORKING, false));
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDrinkro)
            {
                ((TileDrinkro) tile).stopProcess();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        //        if (stateIn.getValue(WORKING))
        //        {
        //            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL.getParticleID(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ(), 0xFF, 0, 0, 0xFFFF00FF);
        //            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0xFF, 0, 0, 0xFFFF00FF);
        //            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), pos.getX(), pos.getY() + 1, pos.getZ() + 0.5, 0xFF, 0, 0, 0xFFFF00FF);
        //            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, 0xFF, 0, 0, 0xFFFF00FF);
        //        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(NORMAL, meta == 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return super.getStateFromMeta(meta).withProperty(NORMAL, (meta & 7) < 4).withProperty(WORKING, meta < 8);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return super.getMetaFromState(state) + (state.getValue(NORMAL) ? 0 : 4) + (state.getValue(WORKING) ? 0 : 8);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, NORMAL, WORKING);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

}
