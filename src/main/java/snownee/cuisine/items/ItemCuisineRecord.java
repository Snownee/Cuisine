package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.item.ItemMod;

public class ItemCuisineRecord extends ItemMod
{

    public ItemCuisineRecord(String name)
    {
        super(name);
        setMaxStackSize(1);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == Blocks.JUKEBOX && !iblockstate.getValue(BlockJukebox.HAS_RECORD))
        {
            if (!worldIn.isRemote)
            {
                ItemStack itemstack = player.getHeldItem(hand);
                ((BlockJukebox) Blocks.JUKEBOX).insertRecord(worldIn, pos, iblockstate, itemstack);
                // worldIn.playEvent((EntityPlayer)null, 1010, pos, Item.getIdFromItem(this));
                itemstack.shrink(1);
                player.addStat(StatList.RECORD_PLAYED);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.PASS;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18nUtil.translate("tip.record"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.RARE;
    }
}
