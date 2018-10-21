package snownee.cuisine.events;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.client.CulinaryRenderHelper;
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
        Ingredient ingredient;
        if (stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood composite;
            if (container == null || (composite = container.get()) == null)
            {
                return;
            }

            Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
            for (Ingredient ingredientIn : composite.getIngredients())
            {
                for (MaterialCategory category : ingredientIn.getMaterial().getCategories())
                {
                    map.put(category, map.getOrDefault(category, 0D) + ingredientIn.getSize());
                }
            }
            i = map.values().stream().mapToInt(MathHelper::ceil).sum();
        }
        else if ((ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack)) != null)
        {
            // add categories line
            i = ingredient.getMaterial().getCategories().size() * MathHelper.ceil(ingredient.getSize());
            if (event.getFlags().isAdvanced())
            {
                event.getToolTip().add(stack.getItem().getContainerItem(stack).toString());
            }
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
        List<String> lines = event.getLines();
        if (lines.isEmpty())
        {
            return;
        }

        ItemStack stack = event.getStack();
        int y = event.getY() + event.getFontRenderer().FONT_HEIGHT + 3;

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

        final Ingredient ingredient;
        if (stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood composite;
            if (container == null || (composite = container.get()) == null)
            {
                return;
            }

            Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
            for (Ingredient ingredientIn : composite.getIngredients())
            {
                for (MaterialCategory category : ingredientIn.getMaterial().getCategories())
                {
                    map.put(category, map.getOrDefault(category, 0D) + ingredientIn.getSize());
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
        else if ((ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack)) != null)
        {
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
    }
}
