package snownee.cuisine.events;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.client.CulinaryRenderHelper;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.library.RarityManager;

@Mod.EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public final class TooltipHandler
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void makeTooltip(ItemTooltipEvent event)
    {
        if (event.getToolTip().isEmpty())
        {
            return;
        }
        ItemStack stack = event.getItemStack();
        int i;
        if (stack.getItem() == CuisineRegistry.INGREDIENT || CulinaryHub.API_INSTANCE.isKnownMaterial(stack))
        {
            Ingredient ingredient = null;
            if (stack.getItem() == CuisineRegistry.INGREDIENT)
            {
                NBTTagCompound data = stack.getTagCompound();
                if (data != null)
                {
                    ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
                }
            }
            else
            {
                ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);
                if (ingredient != null)
                {
                    if (event.getFlags().isAdvanced())
                    {
                        // TODO (3TUSK): Proper localization
                        event.getToolTip().add("Cuisine Ingredient: " + I18n.format(ingredient.getTranslation()));
                    }
                }
            }

            if (ingredient == null)
            {
                return;
            }

            // add categories line
            i = ingredient.getMaterial().getCategories().size() * MathHelper.ceil(ingredient.getSize());
        }
        else if (stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood composite;
            if (container == null || (composite = container.get()) == null)
            {
                return;
            }

            Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
            for (Ingredient ingredient : composite.getIngredients())
            {
                for (MaterialCategory category : ingredient.getMaterial().getCategories())
                {
                    map.put(category, map.getOrDefault(category, 0D) + ingredient.getSize());
                }
            }
            i = map.values().stream().mapToInt(MathHelper::ceil).sum();
        }
        else
        {
            return;
        }
        if (i > 0)
        {
            i = Math.min(i, 12);
            StringBuilder string = new StringBuilder();
            for (int j = 0; j < i; ++j)
            {
                string.append("  ");
            }
            event.getToolTip().add(1, string.toString());
        }
    }

    @SubscribeEvent
    public static void renderTooltipRarity(RenderTooltipEvent.PostText event)
    {
        ItemStack stack = event.getStack();
        if (!stack.isEmpty() && stack.hasTagCompound())
        {
            EnumRarity rarity = RarityManager.getRarity(stack);
            if (rarity != EnumRarity.COMMON)
            {
                int rgb = ItemDye.DYE_COLORS[rarity.color.getColorIndex()];

                String name = stack.getDisplayName();

                if (stack.hasDisplayName())
                    name = TextFormatting.ITALIC + name;

                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, event.getX(), event.getY(), rgb);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void renderTooltip(RenderTooltipEvent.PostText event)
    {
        ItemStack stack = event.getStack();
        int y = event.getY() + event.getFontRenderer().FONT_HEIGHT + 3;

        List<String> lines = event.getLines();
        for (int i = 1; i < lines.size(); ++i)
        {
            String line = lines.get(i);
            if (line.trim().equals("\u00a77"))
            {
                break;
            }
            else
            {
                if (i == lines.size() - 1)
                {
                    return;
                }
                y += event.getFontRenderer().FONT_HEIGHT + 1;
            }
        }

        int x = event.getX();

        // TODO (3TUSK): Isn't CulinaryHub.API_INSTANCE.findIngredient(stack) doing all of these?
        if (stack.getItem() == CuisineRegistry.INGREDIENT || CulinaryHub.API_INSTANCE.isKnownMaterial(stack))
        {
            if (event.getLines().size() == 0)
            {
                return;
            }
            final Ingredient ingredient;
            if (stack.getItem() == CuisineRegistry.INGREDIENT)
            {
                NBTTagCompound data = stack.getTagCompound();
                if (data == null)
                {
                    return;
                }
                ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
                if (ingredient == null)
                {
                    return; // Safety measure
                }
            }
            else
            {
                ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);
                if (ingredient == null || ingredient.getForm() != Form.FULL)
                {
                    return;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);

            Set<MaterialCategory> set = ingredient.getMaterial().getCategories();
            if (!set.isEmpty())
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, 0);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);

                Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
                for (MaterialCategory category : set)
                {
                    map.put(category, ingredient.getSize());
                }
                CulinaryRenderHelper.renderMaterialCategoryIcons(map, 0, 0, 0.4F + ingredient.getMaterial().getSaturationModifier(ingredient), event.getWidth() * 2 - 5);
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }
        else if (stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood composite;
            if (container == null || (composite = container.get()) == null)
            {
                return;
            }

            Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
            for (Ingredient ingredient : composite.getIngredients())
            {
                for (MaterialCategory category : ingredient.getMaterial().getCategories())
                {
                    map.put(category, map.getOrDefault(category, 0D) + ingredient.getSize());
                }
            }

            if (!map.isEmpty())
            {
                GlStateManager.pushMatrix();
                GlStateManager.color(1F, 1F, 1F);
                GlStateManager.translate(x, y, 0);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                CulinaryRenderHelper.renderMaterialCategoryIcons(map, 0, 0, composite.getSaturationModifier(), event.getWidth() * 2 - 5);
                GlStateManager.popMatrix();
            }
        }
    }
}
