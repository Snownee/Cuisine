package snownee.cuisine.crafting;

import org.apache.commons.lang3.Validate;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.library.DummyVanillaRecipe;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class VanillaRecipeRegistry
{
    // Yes, we understand that this is a huge hack, but there is no way around unless we can also kill advancements.
    @SubscribeEvent
    public static void onVanillaRecipeRegistry(final RegistryEvent.Register<IRecipe> event)
    {
        if (CuisineConfig.HARDCORE.enable)
        {
            ModContainer cuisineModContainer = Loader.instance().activeModContainer();
            Validate.isTrue(Cuisine.MODID.equals(Validate.notNull(cuisineModContainer).getModId()));
            Loader.instance().setActiveModContainer(Loader.instance().getMinecraftModContainer());

            if (CuisineConfig.HARDCORE.harderSugarProduction)
            {
                final ResourceLocation sugarRecipeName = new ResourceLocation("minecraft", "sugar");
                event.getRegistry().register(new DummyVanillaRecipe().setRegistryName(sugarRecipeName));
            }

            if (CuisineConfig.HARDCORE.harderBreadProduction)
            {
                final ResourceLocation breadRecipeName = new ResourceLocation("minecraft", "bread");
                event.getRegistry().register(new DummyVanillaRecipe().setRegistryName(breadRecipeName));
            }

            if (CuisineConfig.HARDCORE.harderCookieProduction)
            {
                final ResourceLocation cookieRecipeName = new ResourceLocation("minecraft", "cookie");
                event.getRegistry().register(new DummyVanillaRecipe().setRegistryName(cookieRecipeName));
            }

            Loader.instance().setActiveModContainer(cuisineModContainer);
        }

        GameRegistry.addSmelting(CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.DOUGH), new ItemStack(Items.BREAD), 0.35F);
        GameRegistry.addSmelting(CuisineRegistry.IRON_SPATULA, new ItemStack(Items.IRON_INGOT), 0.1F);
        GameRegistry.addSmelting(CuisineRegistry.KITCHEN_KNIFE, new ItemStack(Items.IRON_INGOT), 0.1F);
        GameRegistry.addSmelting(CuisineRegistry.WOK, new ItemStack(Items.IRON_INGOT, 3), 0.1F);
        GameRegistry.addSmelting(CuisineRegistry.BAMBOO, CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.BAMBOO_CHARCOAL), 0.1F);
        GameRegistry.addSmelting(CuisineRegistry.LOG, new ItemStack(Items.COAL, 1, 1), 0.15F);
    }

    @SubscribeEvent
    public static void getBurnTime(FurnaceFuelBurnTimeEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Item.getItemFromBlock(CuisineRegistry.BAMBOO))
        {
            event.setBurnTime(200);
        }
        else if (stack.getItem() == CuisineRegistry.MATERIAL && stack.getItemDamage() == Cuisine.Materials.WOODEN_HANDLE.getMeta())
        {
            event.setBurnTime(100);
        }
        else if (stack.getItem() == CuisineRegistry.MATERIAL && stack.getItemDamage() == Cuisine.Materials.BAMBOO_CHARCOAL.getMeta())
        {
            event.setBurnTime(1200);
        }
    }
}
