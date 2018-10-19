package snownee.cuisine.plugins;

import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "rustic", dependency = "rustic", optional = true)
public class RusticCompat implements IModule
{
    @Override
    public void init()
    {
        Material grape = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("grape", 0x582945, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictToMaterialMapping.put("cropGrape", grape);
        Material ironberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("ironberry", 0x424242, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictToMaterialMapping.put("cropIronberry", ironberry);
        Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictToMaterialMapping.put("cropWildberry", wildberry);
    }
}
