package snownee.cuisine.client.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.cuisine.util.ItemNBTUtil;
import snownee.kiwi.util.AABBUtil;

public class TESRBarbecueRack extends TESRFirePit<TileBarbecueRack>
{
    @Override
    public void render(final TileBarbecueRack tile, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha)
    {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderItem = mc.getRenderItem();

        EnumFacing facing = EnumFacing.NORTH;
        if (tile.hasWorld())
        {
            facing = CuisineRegistry.FIRE_PIT.getStateFromMeta(tile.getBlockMetadata()).getValue(BlockHorizontal.FACING);
        }

        GlStateManager.pushMatrix();

        if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {

            AxisAlignedBB aabbItem = AABBUtil.rotate(new AxisAlignedBB(0.3D, 0.5D, 0.2D, 0.7D, 0.9D, 0.4D), facing);
            AxisAlignedBB aabbEmpty = AABBUtil.rotate(new AxisAlignedBB(0.45D, 0.65D, 0.2D, 0.55D, 0.75D, 0.4D), facing);
            GlStateManager.disableTexture2D();
            for (int i = 0; i < 3; i++)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                RenderGlobal.drawSelectionBoundingBox((tile.stacks.getStackInSlot(2 - i).isEmpty() ? aabbEmpty : aabbItem).offset(facing.getDirectionVec().getX() * 0.2 * i, 0, facing.getOpposite().getDirectionVec().getZ() * 0.2 * i), 0, 0, 0, 1);
                GlStateManager.popMatrix();
            }
            GlStateManager.enableTexture2D();
        }

        GlStateManager.translate(x + 0.5, y + 0.7, z + 0.5);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0.1F);
        GlStateManager.translate(0, 0, -0.4);
        float rotate = 0.15F;
        for (int i = 0; i < 3; ++i)
        {
            ItemStack stack = tile.stacks.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            }
            GlStateManager.translate(0, 0, 0.4);
            rotate *= -1.2;
            GlStateManager.rotate(10, rotate * 1.5F, 0, rotate);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected List<IngredientInfo> getIngredientInfo(TileBarbecueRack tile)
    {
        if (tile.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        else
        {
            List<IngredientInfo> infos = new ArrayList<>(tile.stacks.getSlots());
            for (int i = 0; i < tile.stacks.getSlots(); ++i)
            {
                ItemStack stack = tile.stacks.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    int doneness = 0;
                    if (stack.getItem() == CuisineRegistry.INGREDIENT)
                    {
                        doneness = ItemNBTUtil.getInt(stack, CuisineSharedSecrets.KEY_DONENESS, 0);
                    }
                    infos.add(new IngredientInfo(stack, doneness));
                }
            }
            return infos;
        }
    }
}
