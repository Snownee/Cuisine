package snownee.cuisine.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.particle.ParticleGrowth;
import snownee.cuisine.items.ItemCrops;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.util.VariantsHolder.Variant;

@SuppressWarnings("deprecation")
public class BlockGarden extends BlockBush implements IModBlock, IShearable
{
    public static class DropPool // loot table?
    {
        public static final Variant[] INSTANCE = new Variant[] { ItemCrops.Variants.CHINESE_CABBAGE,
                ItemCrops.Variants.CORN, ItemCrops.Variants.CUCUMBER, ItemCrops.Variants.EGGPLANT,
                ItemCrops.Variants.GINGER, ItemCrops.Variants.GREEN_PEPPER, ItemCrops.Variants.LEEK,
                ItemCrops.Variants.LETTUCE, ItemCrops.Variants.ONION, ItemCrops.Variants.PEANUT,
                ItemCrops.Variants.RED_PEPPER, ItemCrops.Variants.RICE, ItemCrops.Variants.SCALLION,
                ItemCrops.Variants.SESAME, ItemCrops.Variants.SICHUAN_PEPPER, ItemCrops.Variants.SOYBEAN,
                ItemCrops.Variants.SPINACH, ItemCrops.Variants.TOMATO, ItemCrops.Variants.TURNIP };

        public static Variant draw(Random rand)
        {
            return INSTANCE[rand.nextInt(INSTANCE.length)];
        }
    }

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
    private final String name;

    public BlockGarden(String name)
    {
        super();
        this.name = name;
        setTickRandomly(false);
        setSoundType(SoundType.PLANT);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(CuisineRegistry.CROPS.getItemStack(DropPool.draw(RANDOM)));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, name);
        setTranslationKey(modid + "." + name);
    }

    @Override
    public Block cast()
    {
        return this;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextBoolean())
        {
            double x = pos.getX() + rand.nextDouble();
            double z = pos.getZ() + rand.nextDouble();
            ParticleGrowth particle = new ParticleGrowth(worldIn, x, pos.getY(), z, 0, 0.2, 0);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this));
    }
}
