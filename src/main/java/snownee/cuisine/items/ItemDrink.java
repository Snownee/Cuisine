package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.resources.I18n;
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
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.client.model.DishMeshDefinition;
import snownee.cuisine.internal.capabilities.DishContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.proxy.ClientProxy;
import snownee.cuisine.util.ItemNBTUtil;

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

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = ItemNBTUtil.getString(stack, "customName", "");
        if (!s.isEmpty())
        {
            return s;
        }
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            CompositeFood drink = container.get();
            if (drink != null && drink.getClass() == Drink.class)
            {
                List<Ingredient> ingredients = drink.getIngredients();
                if (ingredients.size() == 1)
                {
                    if (((Drink) drink).getDrinkType() == DrinkType.NORMAL)
                    {
                        return ingredients.get(0).getTranslation();
                    }
                    else
                    {
                        return I18n.format(((Drink) drink).getDrinkType().getTranslationKey() + ".specific", I18n.format(ingredients.get(0).getMaterial().getTranslationKey()));
                    }
                }
            }
        }
        return super.getItemStackDisplayName(stack);
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
