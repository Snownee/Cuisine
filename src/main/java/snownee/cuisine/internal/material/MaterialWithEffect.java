package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialWithEffect extends SimpleMaterialImpl
{
    private final Effect effect;

    public MaterialWithEffect(String id, Effect effect, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue)
    {
        this(id, effect, rawColor, cookedColor, waterValue, oilValue, heatValue, 0.1F);
    }

    public MaterialWithEffect(String id, Effect effect, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float foodSaturationModifier)
    {
        super(id, rawColor, cookedColor, waterValue, oilValue, heatValue, foodSaturationModifier);
        this.effect = effect;
    }

    public MaterialWithEffect(String id, Effect effect, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float foodSaturationModifier, MaterialCategory... categories)
    {
        super(id, rawColor, cookedColor, waterValue, oilValue, heatValue, foodSaturationModifier, categories);
        this.effect = effect;
    }

    @Override
    public void onCooked(CompositeFood dish, Ingredient ingredient, CookingVessel vessel, final EffectCollector collector)
    {
        if (ingredient.hasTrait(IngredientTrait.UNDERCOOKED) || ingredient.hasTrait(IngredientTrait.OVERCOOKED))
        {
            return;
        }
        ingredient.addEffect(effect);
    }
}
