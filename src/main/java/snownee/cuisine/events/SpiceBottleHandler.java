package snownee.cuisine.events;

import java.util.Optional;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.events.SpiceBottleContentConsumedEvent;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.internal.food.Drink;

@EventBusSubscriber(modid = Cuisine.MODID)
public class SpiceBottleHandler
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onSpiceBottleUsed(SpiceBottleContentConsumedEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (event.getContent().getClass() == FluidStack.class)
        {
            FluidStack stack = (FluidStack) event.getContent();
            String name = stack.getFluid().getName();
            if (name.equals("milk") || name.equals("soy_milk")) // TODO: TaN compat
            {
                entity.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
                event.setCanceled(true);
            }
            else if (name.equals(CuisineFluids.SOY_SAUCE.getName()) || name.equals(CuisineFluids.EDIBLE_OIL.getName()) || name.equals(CuisineFluids.SESAME_OIL.getName()))
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60 * 20, 3));
                entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60 * 20, 0));
                event.setCanceled(true);
            }
            else if (name.equals("quicksand")) // BoP
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300 * 20, 5));
                entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 300 * 20, 5));
                entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300 * 20, 0));
            }
            else if (name.equals("poison")) // BoP
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
                entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100));
                event.setCanceled(true);
            }
            else if (name.equals("hot_spring_water")) // BoP
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 120 * 20, 0));
                event.setCanceled(true);
            }
            else if (name.equals("honey")) // BoP
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 120 * 20, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 120 * 20, 0));
                event.setCanceled(true);
            }
            else if (name.equals("blood")) // BoP
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1));
                event.setCanceled(true);
            }
            else if (event.getEntityLiving() instanceof EntityPlayer)
            {
                Material material = CulinaryHub.API_INSTANCE.findMaterial(stack);
                if (material == null || !material.isValidForm(Form.JUICE))
                {
                    return;
                }
                Drink.Builder builder = Drink.Builder.create();
                builder.addIngredient(null, new Ingredient(material, Form.JUICE), CuisineRegistry.BOTTLE);
                builder.addIngredient(null, new Ingredient(material, Form.JUICE), CuisineRegistry.BOTTLE);
                Optional<Drink> result = builder.build(CuisineRegistry.BOTTLE, null);
                if (!result.isPresent())
                {
                    return;
                }
                result.get().onEaten(event.getItemStack(), event.getWorld(), (EntityPlayer) event.getEntityLiving());
                event.setCanceled(true);
            }
        }
        else if (event.getContent().getClass() == ItemStack.class)
        {
            ItemStack stack = (ItemStack) event.getContent();
            if (stack.isItemEqual(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT)) || stack.isItemEqual(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT)))
            {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60 * 20, 3));
                entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60 * 20, 0));
                event.setCanceled(true);
            }
            else if (stack.isItemEqual(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CHILI_POWDER)))
            {
                if (!entity.isImmuneToFire())
                {
                    entity.setFire(60);
                    event.setCanceled(true);
                }
            }
            else if (stack.isItemEqual(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SICHUAN_PEPPER_POWDER)))
            {
                entity.addVelocity(0, entity.jumpMovementFactor * 20, 0);
                event.setCanceled(true);
            }
        }
    }
}
