package snownee.cuisine.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockChoppingBoard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ChoppingBoardModel implements IModel
{
    public static final class Loader implements ICustomModelLoader
    {
        public static final Loader INSTANCE = new Loader();

        private Loader()
        {
            // No-op, only for private access
        }

        @Override
        public void onResourceManagerReload(IResourceManager manager)
        {
            // Does nothing - what else could we do here?
        }

        @Override
        public boolean accepts(ResourceLocation location)
        {
            return Cuisine.MODID.equals(location.getNamespace()) && "models/block/chopping_board_special".equals(location.getPath());
        }

        @Override
        public IModel loadModel(ResourceLocation location)
        {
            return new ChoppingBoardModel();
        }
    }

    private boolean ambientOcclusion, gui3D;

    private ChoppingBoardModel()
    {
        // No-op, only used for restricting access level
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        TextureAtlasSprite particleTexture = bakedTextureGetter.apply(new ResourceLocation("blocks/log_oak_top"));
        return new Baked(ambientOcclusion, gui3D, particleTexture);
    }

    @Override
    public IModel smoothLighting(boolean value)
    {
        this.ambientOcclusion = value;
        return this;
    }

    @Override
    public IModel gui3d(boolean value)
    {
        this.gui3D = value;
        return this;
    }

    private static final class Baked implements IBakedModel
    {
        private static final TRSRTransformation TRANSFORM = TRSRTransformation.blockCenterToCorner(ChoppingBoardOverride.CHOPPING_BOARD_SCALE_DOWN);

        private boolean ambientOcclusion;
        private boolean gui3D;
        private TextureAtlasSprite particleTexture;

        Baked(boolean ambientOcclusion, boolean gui3D, TextureAtlasSprite particleTexture)
        {
            this.ambientOcclusion = ambientOcclusion;
            this.gui3D = gui3D;
            this.particleTexture = particleTexture;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if (state instanceof IExtendedBlockState)
            {
                ItemStack cover = ((IExtendedBlockState) state).getValue(BlockChoppingBoard.COVER_KEY);
                if (cover.isEmpty())
                {
                    // There was a certain amount of latency; we return an empty list for rendering
                    // nothing if the cover data are not ready yet.
                    return Collections.emptyList();
                }
                IBakedModel coverModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(cover, null, null);
                List<BakedQuad> quads = coverModel.getQuads(state, side, rand);
                // Doing magic to transform quads, so that they look small
                if (coverModel.isBuiltInRenderer())
                {
                    return quads;
                }
                else
                {
                    List<BakedQuad> transformedQuads = new ArrayList<>();
                    for (BakedQuad quad : quads)
                    {
                        QuadTransformer transformer = new QuadTransformer(TRANSFORM, quad.getFormat());
                        quad.pipe(transformer);
                        transformedQuads.add(transformer.build());
                    }
                    return transformedQuads;
                }
            }
            else
            {
                // Wut? this can never happen unless we changed implementation, or this model
                // is used on something else. Render nothing please.
                return Collections.emptyList();
            }
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return this.ambientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return this.gui3D;
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return this.particleTexture;
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return new ChoppingBoardOverride();
        }

    }
}
