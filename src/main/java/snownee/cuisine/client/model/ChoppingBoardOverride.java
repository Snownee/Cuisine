package snownee.cuisine.client.model;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.util.Constants;
import snownee.kiwi.util.NBTHelper;

public final class ChoppingBoardOverride extends ItemOverrideList implements ISelectiveResourceReloadListener
{
    /**
     * Reference of ChoppingBoardOverride singleton instance.
     */
    static final ChoppingBoardOverride INSTANCE = new ChoppingBoardOverride();

    /**
     * The transformation that transforms a wood block into a "chopping board".
     */
    static final TRSRTransformation CHOPPING_BOARD_SCALE_DOWN = new TRSRTransformation(new Vector3f(0F, -.38F, 0F), null, new Vector3f(0.75F, 0.25F, 0.75F), null);

    /**
     * The correct TRSRTransformation data for correctly rendering a chopping board
     * as an item.
     */
    private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> CHOPPING_BOARD_TRANSFORMS;

    static
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        try
        {
            // ForgeBlockStateV1.Transforms is available since Forge 14.23.5.2772.
            Optional<IModelState> defaultBlockTransform = ForgeBlockStateV1.Transforms.get("forge:default-block");
            if (defaultBlockTransform.isPresent())
            {
                IModelState modelState = defaultBlockTransform.get();
                TRSRTransformation extraTransform = TRSRTransformation.blockCenterToCorner(CHOPPING_BOARD_SCALE_DOWN);
                for (ItemCameraTransforms.TransformType transformType : ItemCameraTransforms.TransformType.values())
                {
                    Optional<TRSRTransformation> result = modelState.apply(Optional.of(transformType));
                    if (result.isPresent())
                    {
                        TRSRTransformation actualTransform = result.get();
                        builder.put(transformType, actualTransform.compose(extraTransform));
                    }
                }
            }
        }
        catch (Exception e)
        {
            /*
             * The correct TRSRTransformation data for correctly rendering a chopping board
             * as an item. The core data is cited from Connected Texture Mod (CTM) with
             * permission from tterrag. In addition to those data, we do an additional
             * transform in order to make them look like a "board". We also call
             * blockCenterToCorner to make sure everything is sane for an ItemBlock.
             * This primarily functions as a fall-back for those who stubborn users who
             * refuse to use latest Forge.
             *
             * Permission:
             * http://tritusk.info/pics/tterrag-permission-default-block-transform-data.jpg
             *
             * Reference:
             * https://github.com/Chisel-Team/ConnectedTexturesMod/blob/1.10/dev/src/main/
             * java/team/chisel/ctm/client/model/AbstractCTMBakedModel.java#L245-L253
             */
            builder = ImmutableMap.builder(); // Re-initialize with an empty one to avoid error
            builder.put(ItemCameraTransforms.TransformType.GUI, TRSRTransformation.blockCenterToCorner(
                            of(0, 0, 0, 30, 45, 0, 0.625f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(
                            of(0, 2.5f, 0, 75, 45, 0, 0.375f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(
                            of(0, 2.5f, 0, 75, 45, 0, 0.375f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TRSRTransformation.blockCenterToCorner(
                            of(0, 0, 0, 0, 45, 0, 0.4f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(
                            of(0, 0, 0, 0, 225, 0, 0.4f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.GROUND, TRSRTransformation.blockCenterToCorner(
                            of(0, 2, 0, 0, 0, 0, 0.25f).compose(CHOPPING_BOARD_SCALE_DOWN)))
                    .put(ItemCameraTransforms.TransformType.FIXED, TRSRTransformation.blockCenterToCorner(
                            of(0, 0, 0, 0, 0, 0, 0.5f).compose(CHOPPING_BOARD_SCALE_DOWN)));
        }
        CHOPPING_BOARD_TRANSFORMS = builder.build();
     }

    /**
     * Get a {@code TRSRTransformation} object using supplied data. Only uniform scale is supported.
     * This is NOT a general purpose short-cut for getting a TRSRTransformation! It's here for mere
     * purpose of being a human-readable shortcut.
     *
     * @param tx Translation x in pixel
     * @param ty Translation y in pixel
     * @param tz Translation z in pixel
     * @param ax Rotation angle x in degree
     * @param ay Rotation angle y in degree
     * @param az Rotation angle z in degree
     * @param scale Uniform scale quantity
     *
     * @return The correct TRSRTransformation object
     */
    @SuppressWarnings("All") // Hush, IDEA
    private static TRSRTransformation of(float tx, float ty, float tz, float ax, float ay, float az, float scale)
    {
        return new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16), // translation
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), // left-rotation
                new Vector3f(scale, scale, scale), // uniform scale
                null // we don't do right rotation here
        );
    }

    private final Cache<ItemStack, IBakedModel> modelCache = CacheBuilder.newBuilder()
            .maximumSize(500L)
            .expireAfterWrite(300L, TimeUnit.SECONDS)
            .weakKeys()
            .build();

    private ChoppingBoardOverride()
    {
        super(Collections.emptyList());
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
    {
        try
        {
            return modelCache.get(stack, () -> this.getChoppingBoardModel(stack, world, entity));
        }
        catch (ExecutionException e)
        {
            return originalModel; // Signify the issue when there is one
        }
    }

    private IBakedModel getChoppingBoardModel(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
    {
        NBTHelper helper = NBTHelper.of(stack);
        IBakedModel rawModel;
        if (helper.hasTag("BlockEntityTag.cover", Constants.NBT.TAG_COMPOUND))
        {
            // Assumption: we assume the cover item has the correct model. That said, we only use
            // items that has ore dictionary entry of "logWood", and it means that we assume the
            // cover will have a model whose growth ring is facing up (like what vanilla log does).
            // This solves the issue where item metadata is not associated with block state as
            // strictly as that of vanilla, for examples the "iron wood" from Extra Utilities 2.
            ItemStack coverData = new ItemStack(helper.getTag("BlockEntityTag.cover"));
            rawModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(coverData, world, entity);
        }
        else
        {
            // Oak wood, with growth ring facing up.
            rawModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(Blocks.LOG.getDefaultState());
        }
        return new PerspectiveMapWrapper(rawModel, CHOPPING_BOARD_TRANSFORMS);

    }

    @Override
    public ImmutableList<ItemOverride> getOverrides()
    {
        return ImmutableList.of();
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> predicate)
    {
        if (predicate.test(VanillaResourceType.TEXTURES) || predicate.test(VanillaResourceType.MODELS))
        {
            // We have to invalidate all caches when there is a texture/model reloading, as the
            // old model data may be no longer valid after reloading.
            this.modelCache.invalidateAll();
        }
    }
}
