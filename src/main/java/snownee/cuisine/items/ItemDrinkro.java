package snownee.cuisine.items;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.item.ItemModBlock;

public class ItemDrinkro extends ItemModBlock
{

    public ItemDrinkro(IModBlock block)
    {
        super(block);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return EntityEquipmentSlot.HEAD;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, null))
        {
            IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, player, hand);
            NBTTagCompound nbttagcompound = itemstack.getSubCompound("display");

            if (nbttagcompound != null)
            {
                if (nbttagcompound.hasKey("Name", Constants.NBT.TAG_STRING) && nbttagcompound.getString("Name").equals("SCP-294"))
                {
                    iblockstate1 = iblockstate1.withProperty(BlockDrinkro.NORMAL, false);
                }
            }

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        IBlockState newState2 = newState.withProperty(BlockDrinkro.BASE, false);
        if (!world.setBlockState(pos.up(), newState2, 11))
            return false;
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }

}
