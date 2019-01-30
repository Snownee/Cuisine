package snownee.cuisine.internal.spice;

import java.util.Collections;

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
        super(id, color, false, Collections.singleton("spicy"));
    }

    @Override
    public void onMade(CompositeFood.Builder<?> dish, Seasoning seasoning, CookingVessel vessel, EffectCollector collector)
    {
        if (dish.getEffects().contains(CulinaryHub.CommonEffects.HOT))
        {
            return;
        }
        else if (dish.contains(CulinaryHub.CommonMaterials.SICHUAN_PEPPER) || dish.contains(CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER))
        {
            dish.addEffect(CulinaryHub.CommonEffects.HOT);
            return;
        }
        int count = (int) dish.getIngredients().stream().filter(i -> i.getMaterial() == CulinaryHub.CommonMaterials.CHILI).count() * 5;
        for (Seasoning s : dish.getSeasonings())
        {
            if (s.getSpice() == CulinaryHub.CommonSpices.CHILI_POWDER)
            {
                count += s.getSize();
                break;
            }
        }
        if (count >= 8)
        {
            dish.addEffect(CulinaryHub.CommonEffects.HOT);
        }
    }

}
