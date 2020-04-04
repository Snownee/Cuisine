package snownee.cuisine.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.blocks.BlockChoppingBoard;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @FunctionalInterface
    public interface ModelResolver
    {
        Queue<ModelResolver> resolvers = new ArrayDeque<>();

        @Nullable IBakedModel resolve(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity);

        static IBakedModel tryGetFor(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            for (ModelResolver resolver : resolvers) {
                IBakedModel model = resolver.resolve(stack, world, entity);
                if (model != null) {
                    return model;
                }
            }
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }
    }

    static List<Item> useBlockModelFirst = Collections.emptyList();

    public static void updateSpecialItemList(final String[] itemList)
    {
        useBlockModelFirst = Arrays.stream(itemList)
                .map(ResourceLocation::new)
                .map(ForgeRegistries.ITEMS::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    static
    {
        /*
         * A special resolver that maps item to block first if viable.
         * Used to solve issue where the item does NOT use block model, for example
         * TerraFirmaCraft.
         *
         * See https://github.com/Snownee/Cuisine/issues/80 for more details.
         *
         * TODO (3TUSK): Precisely map ItemStack to IBlockState.
         *  MUST require users to specify the mapping.
         */
        ModelResolver.resolvers.add((stack, world, entity) ->
        {
            boolean found = false;
            for (Item i : useBlockModelFirst)
            {
                if (i == stack.getItem())
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                Block block = Block.getBlockFromItem(stack.getItem());
                return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
            }
            return null;
        });
        updateSpecialItemList(CuisineConfig.CLIENT.useBlockModelForChoppingBoardFirst);
        /*
         * The default resolver, assuming that the cover item has the correct model.
         * That said, we only use items that has ore dictionary entry of "logWood", and it means
         * that we assume the cover will have a model whose growth ring is facing up (like what
         * vanilla log does).
         * This solves the issue where item metadata is not associated with block state as
         * strictly as that of vanilla, for examples the "iron wood" from Extra Utilities 2.
         *
         * No, this cannot be in method reference. If it's a method reference, you will have to
         * resolve Minecraft.getMinecraft().getRenderItem() first, which is null when this
         * static initializer is triggered.
         * We have to use plain lambda. There is a semantic difference.
         */
        ModelResolver.resolvers.add((stack, world, entity) -> Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, entity));
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

        private final boolean ambientOcclusion;
        private final boolean gui3D;
        private final TextureAtlasSprite particleTexture;

        Baked(boolean ambientOcclusion, boolean gui3D, TextureAtlasSprite particleTexture)
        {
            this.ambientOcclusion = ambientOcclusion;
            this.gui3D = gui3D;
            this.particleTexture = particleTexture;
        }

        // TODO Cache it. Generating quads at runtime can be very expensive...
        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
        {
            if (state instanceof IExtendedBlockState)
            {
                ItemStack cover = ((IExtendedBlockState) state).getValue(BlockChoppingBoard.COVER_KEY);
                if (cover.isEmpty())
                {
                    // For whatsoever reason, there was a certain amount of latency.
                    // We return an empty list for rendering nothing if the cover data are not ready yet.
                    return Collections.emptyList();
                }
                IBakedModel coverModel = ModelResolver.tryGetFor(cover, null, null);
                List<BakedQuad> quads;
                try
                {
                    quads = coverModel.getQuads(null, side, rand);
                }
                catch (Exception e)
                {
                    // Cuisine.logger.catching(e);
                    // Looks like it's impossible to get quads now. Return empty list instead.
                    return Collections.emptyList();
                }
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
                        TRSRBasedQuadTransformer transformer = new TRSRBasedQuadTransformer(TRANSFORM, quad.getFormat());
                        quad.pipe(transformer);
                        transformedQuads.add(transformer.build());
                    }
                    return transformedQuads;
                }
            }
            else
            {
                // Wut? this can never happen unless we changed implementation, or this model
                // is used on something else. Render nothing for now, we need a better solution
                // for this situation.
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
            return ChoppingBoardOverride.INSTANCE;
        }

    }
}
