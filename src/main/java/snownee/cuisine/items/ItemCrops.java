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
import snownee.cuisine.library.RarityManager;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.ModelUtil;
import snownee.kiwi.util.PlayerUtil;
import snownee.kiwi.util.VariantsHolder.Variant;

public class ItemCrops extends ItemBasicFood implements IPlantable
{

    public ItemCrops(String name)
    {
        super(name);
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

    @Override
    public void mapModel()
    {
        ModelUtil.mapItemVariantsModel(this, "food_", Variants.INSTANCE, "");
    }

    @Override
    public List<? extends Variant<? extends snownee.cuisine.items.ItemBasicFood.Variants.SubItem>> getVariants()
    {
        return Variants.INSTANCE;
    }

    public static class Variants extends snownee.kiwi.util.VariantsHolder<Variants.SubCrop>
    {
        static final Variants INSTANCE = new Variants();

        public static final Variant<SubCrop> PEANUT = INSTANCE.addVariant(new SubCrop("peanut", () -> CuisineRegistry.PEANUT));
        public static final Variant<SubCrop> SESAME = INSTANCE.addVariant(new SubCrop("sesame", () -> CuisineRegistry.SESAME));
        public static final Variant<SubCrop> SOYBEAN = INSTANCE.addVariant(new SubCrop("soybean", () -> CuisineRegistry.SOYBEAN));
        public static final Variant<SubCrop> RICE = INSTANCE.addVariant(new SubCrop("rice", () -> CuisineRegistry.RICE, EnumPlantType.Water));
        public static final Variant<SubCrop> TOMATO = INSTANCE.addVariant(new SubCrop("tomato", () -> CuisineRegistry.TOMATO));
        public static final Variant<SubCrop> CHILI = INSTANCE.addVariant(new SubCrop("chili", () -> CuisineRegistry.CHILI, EnumPlantType.Nether));
        public static final Variant<SubCrop> GARLIC = INSTANCE.addVariant(new SubCrop("garlic", () -> CuisineRegistry.GARLIC));
        public static final Variant<SubCrop> GINGER = INSTANCE.addVariant(new SubCrop("ginger", () -> CuisineRegistry.GINGER));
        public static final Variant<SubCrop> SICHUAN_PEPPER = INSTANCE.addVariant(new SubCrop("sichuan_pepper", () -> CuisineRegistry.SICHUAN_PEPPER));
        public static final Variant<SubCrop> SCALLION = INSTANCE.addVariant(new SubCrop("scallion", () -> CuisineRegistry.SCALLION));
        public static final Variant<SubCrop> TURNIP = INSTANCE.addVariant(new SubCrop("turnip", () -> CuisineRegistry.TURNIP));
        public static final Variant<SubCrop> CHINESE_CABBAGE = INSTANCE.addVariant(new SubCrop("chinese_cabbage", () -> CuisineRegistry.CHINESE_CABBAGE));
        public static final Variant<SubCrop> LETTUCE = INSTANCE.addVariant(new SubCrop("lettuce", () -> CuisineRegistry.LETTUCE));
        public static final Variant<SubCrop> CORN = INSTANCE.addVariant(new SubCrop("corn", () -> CuisineRegistry.CORN));
        public static final Variant<SubCrop> CUCUMBER = INSTANCE.addVariant(new SubCrop("cucumber", () -> CuisineRegistry.CUCUMBER));
        public static final Variant<SubCrop> GREEN_PEPPER = INSTANCE.addVariant(new SubCrop("green_pepper", () -> CuisineRegistry.GREEN_PEPPER));
        public static final Variant<SubCrop> RED_PEPPER = INSTANCE.addVariant(new SubCrop("red_pepper", () -> CuisineRegistry.RED_PEPPER));
        public static final Variant<SubCrop> LEEK = INSTANCE.addVariant(new SubCrop("leek", () -> CuisineRegistry.LEEK));
        public static final Variant<SubCrop> ONION = INSTANCE.addVariant(new SubCrop("onion", () -> CuisineRegistry.ONION));
        public static final Variant<SubCrop> EGGPLANT = INSTANCE.addVariant(new SubCrop("eggplant", () -> CuisineRegistry.EGGPLANT));
        public static final Variant<SubCrop> SPINACH = INSTANCE.addVariant(new SubCrop("spinach", () -> CuisineRegistry.SPINACH));
        public static final Variant<SubCrop> BAMBOO_SHOOT = INSTANCE.addVariant(new SubCrop("bamboo_shoot", () -> CuisineRegistry.BAMBOO_PLANT, EnumPlantType.Plains));

        public static class SubCrop extends snownee.cuisine.items.ItemBasicFood.Variants.SubItem
        {
            private final EnumPlantType plantType;
            private final Supplier<Block> block;

            protected SubCrop(String name, Supplier<Block> block)
            {
                this(name, block, EnumPlantType.Crop);
            }

            protected SubCrop(String name, Supplier<Block> block, EnumPlantType plantType)
            {
                super(name);
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
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        Variants.SubCrop crop = (Variants.SubCrop) getVariants().get(stack.getMetadata()).getValue();

        BlockPos target = canPlantAt(worldIn, pos, facing, crop, player);
        if (target != null)
        {
            IBlockState newState = crop.getBlock().getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);
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
    public BlockPos canPlantAt(World world, BlockPos pos, EnumFacing side, Variants.SubCrop subCrop, EntityPlayer player)
    {
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
        {
            pos = pos.offset(side);
        }
        if (subCrop.getBlock() == null)
        {
            Cuisine.logger.error("{}: Attempting plant a crop which doesnt have a block!", subCrop.getName());
            return null;
        }
        if (!world.isBlockModifiable(player, pos) || !world.mayPlace(subCrop.getBlock(), pos, true, side, player) || !player.capabilities.allowEdit)
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
        if (subCrop.getPlantType() == EnumPlantType.Water)
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
            if (!(subCrop.getBlock() instanceof IPlantable))
            {
                return null;
            }
            IBlockState soilState = world.getBlockState(pos.down());
            return soilState.getBlock().canSustainPlant(soilState, world, pos.down(), side, (IPlantable) subCrop.getBlock())
                    ? pos
                    : null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        Variants.SubCrop subCrop = (Variants.SubCrop) getVariants().get(stack.getMetadata()).getValue();
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
