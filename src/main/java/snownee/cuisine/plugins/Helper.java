package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Material;
import snownee.kiwi.util.definition.ItemDefinition;

public final class Helper
{
    private Helper()
    {
    }

    public static void registerMaterial(Material material, String uid)
    {
        registerMaterial(material, uid, 0);
    }

    public static void registerMaterial(Material material, String uid, int meta)
    {
        Item item = Item.getByNameOrId(uid);
        if (uid != null)
        {
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(item), material);
        }
    }
}
