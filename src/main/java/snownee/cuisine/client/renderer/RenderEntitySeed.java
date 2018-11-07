package snownee.cuisine.client.renderer;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.entities.EntitySeed;

@SideOnly(Side.CLIENT)
public class RenderEntitySeed extends RenderSnowball<EntitySeed>
{

    public RenderEntitySeed(RenderManager renderManagerIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn, Items.WHEAT_SEEDS, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(EntitySeed entityIn)
    {
        return entityIn.getItem();
    }

}
