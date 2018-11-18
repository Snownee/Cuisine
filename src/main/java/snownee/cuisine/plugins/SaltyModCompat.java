package snownee.cuisine.plugins;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.internal.effect.EffectPotions;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.internal.material.MaterialWithEffect;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "saltmod", dependency = "saltmod", optional = true)
public class SaltyModCompat implements IModule
{
    @Override
    public void init()
    {
        Drink.Builder.FEATURE_INPUTS.put(OreDictDefinition.of("dustSoda"), DrinkType.SODA);

        Item fermented_saltwort = ForgeRegistries.ITEMS.getValue(new ResourceLocation("saltmod", "fermented_saltwort"));
        if (fermented_saltwort != null)
        {
            fermented_saltwort.setContainerItem(Items.GLASS_BOTTLE);
            Effect effect = new EffectPotions("fermented_saltwort").addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 300, 2));
            Material material = CulinaryHub.API_INSTANCE.register(new MaterialWithEffect("fermented_saltwort", effect, 0x6A7A2E, 0, 0, 0, 0, 0, MaterialCategory.VEGETABLES).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(fermented_saltwort), new Ingredient(material, Form.JUICE, 0.5));
        }
    }
}
