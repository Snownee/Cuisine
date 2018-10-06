package snownee.cuisine.client.renderer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.TileChoppingBoard;

public class TESRChoppingBoard extends TileEntitySpecialRenderer<TileChoppingBoard>
{

    @Override
    public void render(TileChoppingBoard tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemStack = tile.stacks.getStackInSlot(0);
        if (!itemStack.isEmpty())
        {
            RenderItem renderItem = mc.getRenderItem();
            IBakedModel iBakedModel = renderItem.getItemModelWithOverrides(itemStack, tile.getWorld(), mc.player);

            GlStateManager.pushMatrix();
            RenderHelper.disableStandardItemLighting();

            GlStateManager.translate(x + 0.5, y, z + 0.5);

            int angle = 0;
            if (tile.getFacing().getHorizontalAngle() % 180 != 0)
            {
                angle = 180;
            }
            if (itemStack.getItem() == CuisineRegistry.KITCHEN_KNIFE)
            {
                angle += 90;
            }
            GlStateManager.rotate(tile.getFacing().getHorizontalAngle() + angle, 0, 1, 0);

            if (itemStack.getItem() == CuisineRegistry.KITCHEN_KNIFE)
            {
                GlStateManager.scale(0.75, 0.75, 0.75);
                GlStateManager.translate(0, 0.75, -0.2);
                GlStateManager.rotate(180, 1, 0F, 0.2F);
            }
            else if (iBakedModel.isGui3d())
            {
                // Block
                GlStateManager.scale(0.8, 0.8, 0.8);
                GlStateManager.translate(0, 0.55, 0);
            }
            else
            {
                // Item
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.translate(0, 0.5, 0);
                GlStateManager.rotate(90, 1, 0, 0);
            }

            RenderHelper.enableStandardItemLighting();
            renderItem.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
        }

        IBlockState state = tile.getCover();
        if (state.getMaterial() == Material.AIR)
        {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75, 0.25, 0.75);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        BlockPos pos = tile.getPos();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT);

        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(x / 0.75 - pos.getX() + 0.167F, y / 0.25 - pos.getY(), z / 0.75 - pos.getZ() + 0.167F);

        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        IBakedModel bakedModel = blockRendererDispatcher.getBlockModelShapes().getModelForState(state);

        blockRendererDispatcher.getBlockModelRenderer().renderModel(getWorld(), bakedModel, state, pos, buffer, false);

        if (destroyStage >= 0)
        {
            TextureAtlasSprite texture = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/destroy_stage_" + destroyStage);
            IBakedModel bakedModelDestroy = ForgeHooksClient.getDamageModel(bakedModel, texture, state, getWorld(), pos);
            blockRendererDispatcher.getBlockModelRenderer().renderModel(getWorld(), bakedModelDestroy, state, pos, buffer, false);
        }

        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        Tessellator.getInstance().draw();

        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}
