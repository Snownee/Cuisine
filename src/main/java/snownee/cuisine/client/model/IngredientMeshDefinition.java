package snownee.cuisine.client.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.Form;
import snownee.cuisine.client.CuisineItemRendering;
import snownee.cuisine.internal.CuisineSharedSecrets;

public final class IngredientMeshDefinition implements ItemMeshDefinition, ISelectiveResourceReloadListener
{
    public static final IngredientMeshDefinition INSTANCE = new IngredientMeshDefinition();

    private final EnumMap<Form, Map<String, String>> overrides = new EnumMap<>(Form.class);

    private IngredientMeshDefinition()
    {
        for (Form form : Form.values())
        {
            overrides.put(form, new HashMap<>());
        }
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound data = stack.getTagCompound();
            if (data != null)
            {
                String formName = data.getString(CuisineSharedSecrets.KEY_FORM);
                Form form = Form.of(formName);
                if (form != null)
                {
                    String material = data.getString(CuisineSharedSecrets.KEY_MATERIAL);
                    return new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, overrides.get(form).getOrDefault(material, "cmaterial/" + formName.toLowerCase(Locale.ENGLISH))), "inventory");
                }
            }
        }
        return new ModelResourceLocation(CuisineItemRendering.EMPTY_MODEL, "inventory"); // Catch all
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> tester)
    {
        if (tester.test(VanillaResourceType.MODELS))
        {
            overrides.values().forEach(Map::clear);
            // TODO Read new models
            /*overrides.values()
                    .stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .map(path -> new ResourceLocation(Cuisine.MODID, path))
                    .forEach(path -> ModelLoader.registerItemVariants(CuisineRegistry.INGREDIENT, path));*/
        }
    }
}
