package snownee.cuisine.items;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockDoubleCrops;
import snownee.cuisine.client.particle.ParticleGrowth;
import snownee.cuisine.entities.EntityLifeEssence;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.network.NetworkChannel;

public class ItemLifeEssence extends ItemMod
{

    public ItemLifeEssence(String name)
    {
        super(name);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        addPropertyOverride(new ResourceLocation("frame"), new IItemPropertyGetter()
        {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                if (entityIn instanceof EntityPlayer && (entityIn.getHeldItemMainhand() == stack || entityIn.getHeldItemOffhand() == stack))
                {
                    // Explicit cast to float, IDEA complains about integer division in float-returning context
                    return (float) ((Minecraft.getSystemTime() / 200) % 12);
                }
                else
                {
                    return 4;
                }
            }
        });
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (canApply(player, worldIn, pos, hand, state))
        {
            if (worldIn.isRemote)
            {
                return EnumActionResult.SUCCESS;
            }
            player.getHeldItem(hand).shrink(1);
            Block block = state.getBlock();
            Vec3d particlePos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            if (block instanceof BlockTallGrass)
            {
                worldIn.spawnEntity(new EntityLifeEssence(worldIn, pos, EntityLifeEssence.EnumInvokeType.GARDEN));
                return EnumActionResult.SUCCESS;
            }
            else if (block instanceof BlockDirt)
            {
                particlePos = particlePos.add(0, 0.6F, 0);
                worldIn.setBlockState(pos, Blocks.GRASS.getDefaultState(), 3);
            }
            else if (block instanceof BlockSand)
            {
                particlePos = particlePos.add(0, 0.6F, 0);
                worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
            }
            else if (block instanceof BlockMushroom)
            {
                ((BlockMushroom) block).generateBigMushroom(worldIn, pos, state, worldIn.rand);
            }
            else if (block instanceof BlockSapling)
            {
                ((BlockSapling) block).generateTree(worldIn, pos, state, worldIn.rand);
            }
            else if (block instanceof BlockCrops)
            {
                if (((BlockCrops) block).isMaxAge(state))
                {
                    return EnumActionResult.FAIL;
                }
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
                {
                    worldIn.setBlockState(pos, ((BlockCrops) block).withAge(((BlockCrops) block).getMaxAge()), 3);
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
                else
                {
                    return EnumActionResult.FAIL;
                }
            }
            else if (block instanceof BlockCuisineCrops)
            {
                IBlockState targetState = state;
                if (block instanceof BlockDoubleCrops && ((BlockDoubleCrops) block).isUpper(targetState))
                {
                    pos = pos.down();
                    targetState = worldIn.getBlockState(pos);
                    if (targetState.getBlock() != block || ((BlockDoubleCrops) targetState.getBlock()).isUpper(targetState))
                    {
                        return EnumActionResult.FAIL;
                    }
                }
                if (((BlockCuisineCrops) block).isMaxAge(targetState, worldIn, pos))
                {
                    return EnumActionResult.FAIL;
                }
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, targetState, true))
                {
                    worldIn.setBlockState(pos, targetState.withProperty(((BlockCuisineCrops) block).getAgeProperty(), ((BlockCuisineCrops) block).getMaxAge()), 3);
                    ForgeHooks.onCropsGrowPost(worldIn, pos, targetState, worldIn.getBlockState(pos));
                }
                else
                {
                    return EnumActionResult.FAIL;
                }
            }
            else if (block instanceof BlockDeadBush)
            {
                BlockPlanks.EnumType[] types = BlockPlanks.EnumType.values();
                IBlockState newState = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, types[worldIn.rand.nextInt(types.length)]);
                worldIn.setBlockState(pos, newState, 3);
                worldIn.updateBlockTick(pos, Blocks.SAPLING, 0, 0); // Snownee: Can't ensure this is correct
            }
            else if (block instanceof BlockCocoa)
            {
                worldIn.setBlockState(pos, state.withProperty(BlockCocoa.AGE, 2), 3);
            }
            else if (block instanceof BlockNetherWart)
            {
                worldIn.setBlockState(pos, state.withProperty(BlockNetherWart.AGE, 3), 3);
            }
            else if (block instanceof BlockFlower || block instanceof BlockDoublePlant)
            {
                worldIn.spawnEntity(new EntityLifeEssence(worldIn, pos, EntityLifeEssence.EnumInvokeType.FLOWER));
                return EnumActionResult.SUCCESS;
            }

            NetworkChannel.INSTANCE.sendToAllAround(new PacketCustomEvent(1, particlePos), worldIn.provider.getDimension(), pos);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private boolean canApply(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, IBlockState state)
    {
        if (worldIn.provider.isSurfaceWorld() && worldIn.canBlockSeeSky(pos.up()))
        {
            Block block = state.getBlock();
            if (block instanceof BlockMushroom || block instanceof BlockSapling || block instanceof BlockDeadBush || block instanceof BlockDirt || block instanceof BlockSand)
            {
                return true;
            }
            else if (block instanceof BlockCrops)
            {
                return ((BlockCrops) block).canGrow(worldIn, pos, state, false);
            }
            else if (block instanceof BlockCuisineCrops)
            {
                return ((BlockCuisineCrops) block).canGrow(worldIn, pos, state, false);
            }
            else if (block instanceof BlockCocoa)
            {
                return ((BlockCocoa) block).canGrow(worldIn, pos, state, false);
            }
            else if (block instanceof BlockNetherWart)
            {
                return state.getValue(BlockNetherWart.AGE) < 3;
            }
            else if (block instanceof BlockTallGrass || block instanceof BlockFlower)
            {
                return worldIn.getEntitiesWithinAABB(EntityLifeEssence.class, new AxisAlignedBB(pos), null).isEmpty();
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void splashParticles(World worldIn, BlockPos pos)
    {
        splashParticles(worldIn, new Vec3d(pos));
    }

    @SideOnly(Side.CLIENT)
    public static void splashParticles(World worldIn, Vec3d pos)
    {
        for (int i = 0; i < (FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 30 : 15); i++)
        {
            double d0 = worldIn.rand.nextGaussian() * 0.1D;
            double d2 = worldIn.rand.nextGaussian() * 0.1D;
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGrowth(worldIn, pos.x + d0 + 0.5D, pos.y + 0.5D, pos.z + d2 + 0.5D));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
