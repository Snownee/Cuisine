package snownee.cuisine.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class DrinkroMeshDefinition implements ItemMeshDefinition
{

    public static final DrinkroMeshDefinition INSTANCE = new DrinkroMeshDefinition();

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getSubCompound("display");

        if (nbttagcompound != null)
        {
            if (nbttagcompound.hasKey("Name", Constants.NBT.TAG_STRING) && nbttagcompound.getString("Name").equals("SCP-294"))
            {
                return new ModelResourceLocation(stack.getItem().getRegistryName() + "_special", "inventory");
            }
        }
        return new ModelResourceLocation(stack.getItem().getRegistryName(), "inventory");
    }

}
