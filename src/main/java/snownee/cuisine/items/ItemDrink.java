package snownee.cuisine.items;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.client.model.DishMeshDefinition;
import snownee.cuisine.internal.capabilities.DishContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.proxy.ClientProxy;

public class ItemDrink extends ItemAbstractComposite
{

    public ItemDrink(String name)
    {
        super(name);
        // Creative tab
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelLoader.setCustomMeshDefinition(this, DishMeshDefinition.INSTANCE);
        ModelBakery.registerItemVariants(this, ClientProxy.EMPTY, new ResourceLocation(Cuisine.MODID, "dish/drink"), new ResourceLocation(Cuisine.MODID, "dish/smoothie"), new ResourceLocation(Cuisine.MODID, "dish/gelo"), new ResourceLocation(Cuisine.MODID, "dish/soda"));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new DishContainer();
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood drink = container.get();
            if (drink != null && drink.getClass() == Drink.class)
            {
                return ((Drink) drink).getDrinkType().getTranslationKey();
            }
        }
        return super.getTranslationKey(stack);
    }
}
