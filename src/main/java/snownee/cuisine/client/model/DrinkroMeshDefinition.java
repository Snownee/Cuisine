package snownee.cuisine.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class DrinkroMeshDefinition implements ItemMeshDefinition
{

    public static final DrinkroMeshDefinition INSTANCE = new DrinkroMeshDefinition();

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        return new ModelResourceLocation(stack.getItem().getRegistryName() + (stack.getDisplayName().equals("SCP-294") ? "_special" : ""), "inventory");
    }

}
