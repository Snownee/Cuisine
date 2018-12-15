package snownee.cuisine.internal.spice;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.prefab.SimpleSpiceImpl;

public class SpiceChiliPowder extends SimpleSpiceImpl
{

    public SpiceChiliPowder(String id, int color)
    {
        super(id, color, false);
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Seasoning seasoning, CookingVessel vessel, EffectCollector collector)
    {
        if (dish.contains(CulinaryHub.CommonMaterials.SICHUAN_PEPPER) || dish.contains(CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER))
        {
            dish.addEffect(CulinaryHub.CommonEffects.HOT);
        }
    }

}
