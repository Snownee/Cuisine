package snownee.cuisine.items;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemCrops.SubCrop;
import snownee.cuisine.items.ItemCrops.Variant;
import snownee.cuisine.library.RarityManager;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.item.IVariant;
import snownee.kiwi.util.PlayerUtil;

public class ItemCrops extends ItemBasicFood<SubCrop, Variant> implements IPlantable
{

    public ItemCrops(String name)
    {
        super(name, Variant.values());
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos)
    {
        return Blocks.AIR.getDefaultState();
    }

    public static enum Variant implements IVariant<SubCrop>, IRarityGetter
    {
        PEANUT(() -> CuisineRegistry.PEANUT),
        SESAME(() -> CuisineRegistry.SESAME),
        SOYBEAN(() -> CuisineRegistry.SOYBEAN),
        RICE(() -> CuisineRegistry.RICE, EnumPlantType.Water),
        TOMATO(() -> CuisineRegistry.TOMATO),
        CHILI(() -> CuisineRegistry.CHILI, EnumPlantType.Nether),
        GARLIC(() -> CuisineRegistry.GARLIC),
        GINGER(() -> CuisineRegistry.GINGER),
        SICHUAN_PEPPER(() -> CuisineRegistry.SICHUAN_PEPPER),
        SCALLION(() -> CuisineRegistry.SCALLION),
        TURNIP(() -> CuisineRegistry.TURNIP),
        CHINESE_CABBAGE(() -> CuisineRegistry.CHINESE_CABBAGE),
        LETTUCE(() -> CuisineRegistry.LETTUCE),
        CORN(() -> CuisineRegistry.CORN),
        CUCUMBER(() -> CuisineRegistry.CUCUMBER),
        GREEN_PEPPER(() -> CuisineRegistry.GREEN_PEPPER),
        RED_PEPPER(() -> CuisineRegistry.RED_PEPPER),
        LEEK(() -> CuisineRegistry.LEEK),
        ONION(() -> CuisineRegistry.ONION),
        EGGPLANT(() -> CuisineRegistry.EGGPLANT),
        SPINACH(() -> CuisineRegistry.SPINACH),
        BAMBOO_SHOOT(() -> CuisineRegistry.BAMBOO_PLANT, EnumPlantType.Plains);

        private final SubCrop subCrop;

        Variant(Supplier<Block> block)
        {
            this(block, EnumPlantType.Crop);
        }

        Variant(Supplier<Block> block, EnumPlantType plantType)
        {
            this.subCrop = new SubCrop(block, plantType);
        }

        @Override
        public EnumRarity getRarity()
        {
            return EnumRarity.COMMON;
        };

        @Override
        public int getMeta()
        {
            return ordinal();
        }

        @Override
        public SubCrop getValue()
        {
            return subCrop;
        }

    }

    public static class SubCrop
    {
        private final EnumPlantType plantType;
        private final Supplier<Block> block;

        protected SubCrop(Supplier<Block> block, EnumPlantType plantType)
        {
            this.block = block;
            this.plantType = plantType;
        }

        public EnumPlantType getPlantType()
        {
            return plantType;
        }

        public Block getBlock()
        {
            return block.get();
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        Variant variant = getVariants().get(stack.getMetadata());

        BlockPos target = canPlantAt(worldIn, pos, facing, variant, player);
        if (target != null)
        {
            IBlockState newState = variant.subCrop.getBlock().getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);
            EnumRarity rarity = RarityManager.getRarity(stack);
            boolean flag = PlayerUtil.tryPlaceBlock(worldIn, target, facing, player, hand, newState, stack);
            if (flag && rarity != EnumRarity.COMMON)
            {
                IBlockState iblockstate = worldIn.getBlockState(target);
                if (iblockstate.getBlock() instanceof IGrowable)
                {
                    IGrowable igrowable = (IGrowable) iblockstate.getBlock();

                    if (igrowable.canGrow(worldIn, target, iblockstate, worldIn.isRemote))
                    {
                        if (!worldIn.isRemote)
                        {
                            worldIn.playEvent(2005, target, 0);
                            if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, iblockstate))
                            {
                                igrowable.grow(worldIn, worldIn.rand, target, iblockstate);
                            }
                        }
                    }
                }
            }
            return flag ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }

    @Nullable
    public BlockPos canPlantAt(World world, BlockPos pos, EnumFacing side, Variant variant, EntityPlayer player)
    {
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
        {
            pos = pos.offset(side);
        }
        if (variant.subCrop.getBlock() == null)
        {
            Cuisine.logger.error("{}: Attempting plant a crop which doesnt have a block!", variant.getName());
            return null;
        }
        if (!world.isBlockModifiable(player, pos) || !world.mayPlace(variant.subCrop.getBlock(), pos, true, side, player) || !player.capabilities.allowEdit)
        {
            return null;
        }
        //        if (subCrop.getPlantType() == EnumPlantType.Crop)
        //        {
        //            IBlockState soilState = world.getBlockState(pos.down());
        //            if (soilState.getBlock().canSustainPlant(soilState, world, pos.down(), EnumFacing.UP, (IPlantable) Blocks.WHEAT))
        //            {
        //                IBlockState oldState = world.getBlockState(pos);
        //                return oldState.getBlock().isReplaceable(world, pos) ? pos : null;
        //            }
        //        }
        if (variant.subCrop.getPlantType() == EnumPlantType.Water)
        {
            IBlockState soilState = world.getBlockState(pos.down());
            if (soilState.getMaterial() != Material.GROUND && soilState.getMaterial() != Material.GRASS)
            {
                return null;
            }
            IBlockState waterState = world.getBlockState(pos);
            if (waterState.getBlock() != Blocks.WATER)
            {
                return null;
            }
            IBlockState oldState = world.getBlockState(pos.up());
            return oldState.getBlock().isReplaceable(world, pos.up()) ? pos.up() : null;
        }
        else
        {
            if (!(variant.subCrop.getBlock() instanceof IPlantable))
            {
                return null;
            }
            IBlockState soilState = world.getBlockState(pos.down());
            return soilState.getBlock().canSustainPlant(soilState, world, pos.down(), side, (IPlantable) variant.subCrop.getBlock()) ? pos : null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        SubCrop subCrop = getVariants().get(stack.getMetadata()).subCrop;
        if (subCrop.getPlantType() != EnumPlantType.Crop)
        {
            tooltip.add(I18nUtil.translate("tip.crops." + subCrop.getPlantType().toString().toLowerCase(Locale.ROOT)));
        }
    }

    //    public boolean canStayAt(World world, BlockPos pos, SubCrop subCrop)
    //    {
    //        if (subCrop.getPlantType() == EnumPlantType.Crop)
    //        {
    //            IBlockState soilState = world.getBlockState(pos.down());
    //            if (soilState.getBlock().canSustainPlant(soilState, world, pos, EnumFacing.UP, (IPlantable) Blocks.WHEAT))
    //            {
    //                IBlockState oldState = world.getBlockState(pos);
    //                return oldState.getBlock().isReplaceable(world, pos);
    //            }
    //        }
    //        else if (subCrop.getPlantType() == EnumPlantType.Water) {
    //            IBlockState soilState = world.getBlockState(pos.down());
    //            if (soilState.getMaterial() != Material.GROUND && soilState.getMaterial() != Material.GRASS)
    //            {
    //                return false;
    //            }
    //            IBlockState waterState = world.getBlockState(pos);
    //            if (waterState.getBlock() == Blocks.WATER)
    //            {
    //                return false;
    //            }
    //            IBlockState oldState = world.getBlockState(pos.up());
    //            return oldState.getBlock().isReplaceable(world, pos);
    //        }
    //        return false;
    //    }

}
