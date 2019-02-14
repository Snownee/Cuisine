package snownee.cuisine.client.renderer;

import snownee.cuisine.tiles.heat.TileFirePit;

import java.util.Collections;
import java.util.List;

public class TESRFirePit<T extends TileFirePit> extends TESRHeatable<T>
{
    protected List<IngredientInfo> getIngredientInfo(T tile) {
        return Collections.emptyList();
    }
}
