package snownee.cuisine.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import snownee.cuisine.Cuisine;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ChoppingBoardItemModel implements IModel
{
    public static final class Loader implements ICustomModelLoader
    {

        @Override
        public void onResourceManagerReload(IResourceManager manager)
        {
            // Does nothing - what else could we do here?
        }

        @Override
        public boolean accepts(ResourceLocation location)
        {
            return Cuisine.MODID.equals(location.getNamespace()) && "models/block/chopping_board_item".equals(location.getPath());
        }

        @Override
        public IModel loadModel(ResourceLocation location)
        {
            return new ChoppingBoardItemModel();
        }
    }

    private boolean ambientOcclusion, gui3D;

    private ChoppingBoardItemModel()
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

    public static final class Baked implements IBakedModel
    {
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
            return new ArrayList<>(0); // wtf
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
