package snownee.cuisine.plugins;

import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "pizzacraft", dependency = "pizzacraft", optional = true)
public class PizzaCraftCompat implements IModule
{
    @Override
    public void init()
    {
        Material pineapple = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("pineapple", 0xa9861c, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPineapple", pineapple);
        Material broccoli = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("broccoli", 0x71a141, 0, 0, 0, 0, 0, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        CulinaryHub.API_INSTANCE.registerMapping("cropBroccoli", broccoli);
    }
}
