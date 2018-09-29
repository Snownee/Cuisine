package snownee.cuisine.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileChoppingBoard;
import snownee.cuisine.util.ItemNBTUtil;

public class CuisineTEISR extends TileEntityItemStackRenderer
{
    public static final CuisineTEISR INSTANCE = new CuisineTEISR();

    private final TileChoppingBoard choppingBoard = new TileChoppingBoard(true);

    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        if (stack.getItem() == CuisineRegistry.ITEM_CHOPPING_BOARD)
        {
            NBTTagCompound tag = ItemNBTUtil.getCompound(stack, "BlockEntityTag", true);
            if (tag != null && tag.hasKey("cover", Constants.NBT.TAG_COMPOUND))
            {
                choppingBoard.setCover(new ItemStack(tag.getCompoundTag("cover")));
            }
            else
            {
                choppingBoard.setCover(TileChoppingBoard.DEFAULT_COVER);
            }

            TileEntityRendererDispatcher.instance.render(this.choppingBoard, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        }
    }
}
