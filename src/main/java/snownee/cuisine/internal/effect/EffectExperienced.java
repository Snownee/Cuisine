package snownee.cuisine.internal.effect;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectExperienced extends SimpleEffectImpl
{

    public EffectExperienced()
    {
        super("experienced", 0);
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, player);

        Set<MaterialCategory> categories = EnumSet.noneOf(MaterialCategory.class);
        for (Ingredient in : food.getIngredients())
        {
            categories.addAll(in.getMaterial().getCategories());
        }

        int xpValue = categories.size() * ingredients.size() * 3;

        if (!itemstack.isEmpty() && itemstack.isItemDamaged())
        {
            int i = Math.min(xpValue * 2, itemstack.getItemDamage());
            xpValue -= i * 2;
            itemstack.setItemDamage(itemstack.getItemDamage() - i);
        }

        if (xpValue > 0)
        {
            player.addExperience(xpValue);
            if (player.world.isRemote)
            {
                Random rand = new Random();
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, (rand.nextFloat() - rand.nextFloat()) * 0.35F + 0.9F);
            }
        }
    }

    @Override
    public int getColor()
    {
        return CulinaryHub.CommonMaterials.APPLE.getRawColorCode();
    }
}
