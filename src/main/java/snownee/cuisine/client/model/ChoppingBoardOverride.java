package snownee.cuisine.client.model;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.util.ItemNBTUtil;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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
            CHOPPING_BOARD_TRANSFORMS = builder.build();
        }
        else
        {
            throw new NullPointerException("Transform 'forge:default-block' does not exist. In theory this should never happen.");
        }
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
        NBTTagCompound tag = ItemNBTUtil.getCompound(stack, "BlockEntityTag", true);
        IBakedModel rawModel;
        if (tag != null && tag.hasKey("cover", Constants.NBT.TAG_COMPOUND))
        {
            // Assumption: we assume the cover item has the correct model. That said, we only use
            // items that has ore dictionary entry of "logWood", and it means that we assume the
            // cover will have a model whose growth ring is facing up (like what vanilla log does).
            // This solves the issue where item metadata is not associated with block state as
            // strictly as that of vanilla, for examples the "iron wood" from Extra Utilities 2.
            ItemStack coverData = new ItemStack(tag.getCompoundTag("cover"));
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
