package snownee.cuisine.tiles.utensils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import snownee.cuisine.api.CookingVessel;
import snownee.kiwi.tile.TileBase;

public abstract class TileUtensil extends TileBase implements CookingVessel, ITickable
{
    public void onActivated(EntityPlayer playerIn, EnumHand hand, EnumFacing facing) {}
}
