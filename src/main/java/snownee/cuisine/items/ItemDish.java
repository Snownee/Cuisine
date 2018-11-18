package snownee.cuisine.items;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.CuisineItemRendering;
import snownee.cuisine.client.model.DishMeshDefinition;
import snownee.cuisine.internal.capabilities.DishContainer;
import snownee.cuisine.tiles.TileDish;
import snownee.kiwi.util.PlayerUtil;

public class ItemDish extends ItemAbstractComposite
{

    public ItemDish(String name)
    {
        super(name);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomMeshDefinition(this, DishMeshDefinition.INSTANCE);
        ModelBakery.registerItemVariants(this, CuisineItemRendering.EMPTY_MODEL, new ResourceLocation(Cuisine.MODID, "dish/fish0"), new ResourceLocation(Cuisine.MODID, "dish/rice0"), new ResourceLocation(Cuisine.MODID, "dish/meat0"), new ResourceLocation(Cuisine.MODID, "dish/meat1"), new ResourceLocation(Cuisine.MODID, "dish/veges0"), new ResourceLocation(Cuisine.MODID, "dish/veges1"), new ResourceLocation(Cuisine.MODID, "dish/mixed0"), new ResourceLocation(Cuisine.MODID, "dish/mixed1"), new ResourceLocation(Cuisine.MODID, "placed_dish"));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new DishContainer();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            ItemStack heldItem = player.getHeldItem(hand);
            ItemStack copy = heldItem.copy();
            copy.setCount(1);
            IBlockState dishBlock = CuisineRegistry.PLACED_DISH.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);
            BlockPos target = PlayerUtil.tryPlaceBlock(worldIn, pos, facing, player, hand, dishBlock, heldItem, false);
            if (target != null && worldIn.getBlockState(target).getBlock() == CuisineRegistry.PLACED_DISH)
            {
                TileEntity tile = worldIn.getTileEntity(target);
                if (tile instanceof TileDish)
                {
                    ((TileDish) tile).readDish(copy);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }
}
