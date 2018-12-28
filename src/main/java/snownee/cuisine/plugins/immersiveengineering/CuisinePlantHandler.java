package snownee.cuisine.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.tool.BelljarHandler.IPlantHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemCrops.Variant;
import snownee.kiwi.util.OreUtil;

public class CuisinePlantHandler implements IPlantHandler
{
    private final Variant variant;

    public CuisinePlantHandler(Variant variant)
    {
        this.variant = variant;
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState[] getRenderedPlant(ItemStack seed, ItemStack soil, float growth, TileEntity tile)
    {
        if (variant == Variant.BAMBOO_SHOOT) // cannot tint?
        {
            return new IBlockState[] { variant.getValue().getBlock().getDefaultState() };
        }
        else
        {
            int meta = MathHelper.clamp((int) (growth * 8), 0, 7);
            return new IBlockState[] { variant.getValue().getBlock().getStateFromMeta(meta) };
        }
    }

    @Override
    public boolean isValid(ItemStack seed)
    {
        return seed.getItem() == CuisineRegistry.CROPS && seed.getMetadata() == variant.getMeta();
    }

    @Override
    public float getGrowthStep(ItemStack seed, ItemStack soil, float growth, TileEntity tile, float fertilizer, boolean render)
    {
        return .003125f * fertilizer;
    }

    @Override
    public ItemStack[] getOutput(ItemStack seed, ItemStack soil, TileEntity tile)
    {
        ItemStack output;
        if (variant == Variant.BAMBOO_SHOOT) // cannot tint?
        {
            output = new ItemStack(CuisineRegistry.BAMBOO, 2);
        }
        else
        {
            output = ItemHandlerHelper.copyStackWithSize(seed, 2);
        }
        return new ItemStack[] { output };
    }

    @Override
    public boolean isCorrectSoil(ItemStack seed, ItemStack soil)
    {
        switch (variant.getValue().getPlantType())
        {
        case Crop:
        case Plains:
        case Water:
            return soil.getItem() == Item.getItemFromBlock(Blocks.DIRT) || soil.getItem() == Item.getItemFromBlock(Blocks.FARMLAND) || OreUtil.doesItemHaveOreName(soil, "dirt");
        case Beach:
        case Desert:
            return soil.getItem() == Item.getItemFromBlock(Blocks.SAND) || OreUtil.doesItemHaveOreName(soil, "sand");
        case Cave:
            return soil.getItem() == Item.getItemFromBlock(Blocks.STONE) || soil.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE) || OreUtil.doesItemHaveOreName(soil, "stone") || OreUtil.doesItemHaveOreName(soil, "cobblestone");
        case Nether:
            return soil.getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND);
        default:
            return false;
        }
    }

}
