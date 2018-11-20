package snownee.cuisine.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.item.ItemModBlock;

public class ItemEmptyPlate extends ItemModBlock
{

    public ItemEmptyPlate(IModBlock block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!player.isCreative() && !player.isSneaking())
        {
            return EnumActionResult.PASS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

}
