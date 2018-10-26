package snownee.cuisine.plugins;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.kiwi.util.definition.ItemDefinition;

public final class Helper
{
    private Helper()
    {
    }

    @Nullable
    public static Material registerMaterial(Material material, String uid)
    {
        return registerMaterial(material, uid, 0);
    }

    @Nullable
    public static Material registerMaterial(Material material, String uid, int meta)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(uid));
        if (material != null && item != null)
        {
            material = CulinaryHub.API_INSTANCE.register(material);
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(item, meta), material);
            return material;
        }
        return null;
    }

    public static void registerMapping(Ingredient ingredient, String uid)
    {
        registerMapping(ingredient, uid, 0);
    }

    public static void registerMapping(Ingredient ingredient, String uid, int meta)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(uid));
        if (ingredient != null && item != null)
        {
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(item, meta), ingredient);
        }
    }
}
