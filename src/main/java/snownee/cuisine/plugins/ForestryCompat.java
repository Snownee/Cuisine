package snownee.cuisine.plugins;

import java.util.EnumSet;

import net.minecraft.item.Item;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.internal.food.Drink;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "forestry", dependency = "forestry", optional = true)
public class ForestryCompat implements IModule
{
    @Override
    public void init()
    {
        Item material = Item.getByNameOrId("forestry:crafting_material");
        if (material != null)
        {
            Drink.Builder.FEATURE_INPUTS.put(ItemDefinition.of(material, 5), Drink.DrinkType.SMOOTHIE);
        }

        Material cherry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("cherry", 0xb91023, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropCherry", cherry);
        Material walnut = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("walnut", 0xd88a44, 0, 0, 0, 0, 0, MaterialCategory.NUT).setValidForms(EnumSet.of(Form.MINCED)));
        CulinaryHub.API_INSTANCE.registerMapping("cropWalnut", walnut);
        Material chestnut = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("chestnut", 0x9e443b, 0, 0, 0, 0, 0, MaterialCategory.NUT).setValidForms(EnumSet.of(Form.MINCED, Form.PASTE)));
        CulinaryHub.API_INSTANCE.registerMapping("cropChestnut", chestnut);
        Material plum = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("plum", 0x4b213b, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPlum", plum);
        Material date = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("date", 0x4e302d, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropDate", date);
        Material papaya = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("papaya", 0xa99027, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPapaya", papaya);
    }
}
