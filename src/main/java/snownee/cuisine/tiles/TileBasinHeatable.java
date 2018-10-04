package snownee.cuisine.tiles;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.ITickable;

public class TileBasinHeatable extends TileBasin implements ITickable
{
    protected static final Map<Block, Integer> HEAT_SOURCES = new HashMap<>();

    @Override
    public void update()
    {
        // TODO Auto-generated method stub
    }

}
