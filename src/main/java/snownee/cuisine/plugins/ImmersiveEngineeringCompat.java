package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.OreDictDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "immersiveengineering", dependency = "immersiveengineering", optional = true)
public class ImmersiveEngineeringCompat implements IModule
{
    @Override
    public void init()
    {
        if (CuisineConfig.GENERAL.axeChopping)
        {
            Item stick = ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering", "material"));
            if (stick != null)
            {
                Processing.CHOPPING.add(new Chopping(OreDictDefinition.of("plankTreatedWood"), new ItemStack(stick, 4)));
            }
        }
    }
}
