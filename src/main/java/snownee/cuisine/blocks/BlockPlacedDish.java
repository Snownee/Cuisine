package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileDish;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockMod;

@SuppressWarnings("deprecation")
public class BlockPlacedDish extends BlockMod
{

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0625D, 0.125D, 0.875D, 0.09375D, 0.875D);

    private static final PropertyEnum<ModelType> MODEL = PropertyEnum.create("model_type", ModelType.class);

    public BlockPlacedDish(String name)
    {
        super(name, Material.CLAY);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(this.blockState.getBaseState().withProperty(MODEL, ModelType.EMPTY));
    }

    @Override
    public int getItemSubtypeAmount()
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void getSubBlocks(CreativeTabs creativeTabs, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileDish)
        {
            return ((TileDish) te).onEatenBy(playerIn);
        }
        return false;
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
        return false;
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
        return AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        if (canBlockStay(worldIn, pos))
        {
            return super.canPlaceBlockAt(worldIn, pos);
        }
        else
        {
            return false;
        }
    }

    private boolean canBlockStay(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos.down());
        return state.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileDish)
        {
            return state.withProperty(MODEL, ModelType.of(((TileDish) te).getDishModelType()));
        }
        else
        {
            return state.withProperty(MODEL, ModelType.EMPTY);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState(); // Data are read from TileEntity
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0; // Data are saved in TileEntity
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, MODEL);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileDish)
        {
            return ((TileDish) te).getItem();
        }
        return new ItemStack(CuisineRegistry.PLACED_DISH);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        // NO-OP
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileDish)
        {
            ItemStack stack = ((TileDish) te).getItem();
            if (!stack.isEmpty())
            {
                StacksUtil.spawnItemStack(worldIn, pos, stack, true);
            }
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    enum ModelType implements IStringSerializable
    {
        FISH_0, RICE_0, MEAT_0, MEAT_1, VEGES_0, VEGES_1, MIXED_0, MIXED_1, EMPTY;

        @Override
        public String getName()
        {
            return toString().toLowerCase().replace("_", "");
        }

        static ModelType of(String s)
        {
            switch (s)
            {
            case "fish0":
                return FISH_0;
            case "rice0":
                return RICE_0;
            case "meat0":
                return MEAT_0;
            case "meat1":
                return MEAT_1;
            case "veges0":
                return VEGES_0;
            case "veges1":
                return VEGES_1;
            case "mixed0":
                return MIXED_0;
            case "mixed1":
                return MIXED_1;
            default:
                return EMPTY;
            }
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState meta)
    {
        return new TileDish();
    }
}
