package snownee.cuisine.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import snownee.cuisine.blocks.BlockChoppingBoard;
import snownee.kiwi.util.NBTHelper;

import javax.annotation.Nullable;

/**
 * A special implementation of IItemColor an IBlockColor for Chopping Board that delegates its
 * tinting logic to the cover ItemStack.
 */
public final class ChoppingBoardColorProxy implements IItemColor, IBlockColor
{
    public static final ChoppingBoardColorProxy INSTANCE = new ChoppingBoardColorProxy();

    private ChoppingBoardColorProxy()
    {
        // No-op, only restrict access level
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex)
    {
        final NBTTagCompound coverData = NBTHelper.of(stack).getTag("BlockEntityTag.cover");
        if (coverData == null) {
            return -1;
        }
        ItemStack cover = new ItemStack(coverData);
        if (cover.isEmpty())
        {
            return -1;
        }
        // TODO (3TUSK): Guess we have to recover the original tintIndex in the future.
        return Minecraft.getMinecraft().getItemColors().colorMultiplier(cover, tintIndex);
    }

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
    {
        if (state instanceof IExtendedBlockState)
        {
            ItemStack cover = ((IExtendedBlockState) state).getValue(BlockChoppingBoard.COVER_KEY);
            // Hell yeah! This is the payment to the Evil for using IUnlistProperty<ItemStack>! ItemStack can be null here!!!
            if (cover == null)
            {
                return -1;
            }
            // TODO (3TUSK): Guess we have to recover the original tintIndex in the future.
            return Minecraft.getMinecraft().getItemColors().colorMultiplier(cover, tintIndex);
        }
        else
        {
            return -1;
        }
    }
}
