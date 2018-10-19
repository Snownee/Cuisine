package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "biomesoplenty", dependency = "biomesoplenty", optional = true)
public class BOPCompat implements IModule
{
    @Override
    public void init()
    {
        Material pear = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("pear", 0xA3B26D, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPear", pear);
        Material peach = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("peach", 0xFEB5A0, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPeach", peach);
        Material persimmon = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("persimmon", 0xFF9528, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPersimmon", persimmon);
        Item itemBerry = Item.getByNameOrId("biomesoplenty:berries");
        if (itemBerry != null)
        {
            Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(itemBerry), wildberry);
        }
        Item itemKelp = Item.getByNameOrId("biomesoplenty:seaweed");
        if (itemKelp != null)
        {
            OreDictionary.registerOre("cropSeaweed", new ItemStack(itemKelp));
            Material seaweed = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("seaweed", 0x99909648, 0, 0, 0, 0, 0, MaterialCategory.SEAFOOD).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping("cropSeaweed", seaweed);
        }
        Item itemBambooshoot = Item.getByNameOrId("biomesoplenty:sapling_0");
        if (itemBambooshoot != null)
        {
            OreDictionary.registerOre("cropBambooshoot", new ItemStack(itemBambooshoot, 1, 2));
        }
    }
}
