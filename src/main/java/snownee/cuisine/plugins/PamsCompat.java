package snownee.cuisine.plugins;

import mezz.jei.color.ColorGetter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "harvestcraft", dependency = "harvestcraft", optional = true)
public class PamsCompat implements IModule
{
    @Override
    public void init()
    {
        Material blackberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("blackberry", 0x1D1019, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictIngredients.put("cropBlackberry", new Ingredient(blackberry, Form.FULL, 1));
        Material blueberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("blueberry", 0x204569, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictIngredients.put("cropBlueberry", new Ingredient(blueberry, Form.FULL, 1));
        Material grape = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("grape", 0x582945, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CuisineInternalGateway.INSTANCE.oreDictIngredients.put("cropGrape", new Ingredient(grape, Form.FULL, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void postInit()
    {
        for (ItemStack stack : OreDictionary.getOres("listAllfruit", false))
        {
            if (stack.getItem().getCreatorModId(stack).equals("harvestcraft"))
            {
                System.out.println(stack.getItem().getRegistryName());
                System.out.println(ColorGetter.getColors(stack, 1).get(0).getRGB());
            }
        }
    }
}
