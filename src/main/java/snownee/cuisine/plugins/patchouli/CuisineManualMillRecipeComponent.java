package snownee.cuisine.plugins.patchouli;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

import java.util.Collections;

public final class CuisineManualMillRecipeComponent implements ICustomComponent
{

    @VariableHolder
    public String recipe;

    private transient Milling recipeObject;
    private transient int beginX = -1, beginY = -1;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.recipeObject = Processing.MILLING.findRecipe(new ResourceLocation(this.recipe));
        this.beginX = componentX;
        this.beginY = componentY;
    }

    @Override
    public void render(IComponentRenderContext context, float pTicks, int mouseX, int mouseY)
    {
        if (this.recipeObject != null)
        {
            GuiScreen current = context.getGui();
            FluidStack inputFluid;
            if (!this.recipeObject.getInput().isEmpty()) {
                context.renderIngredient(beginX + 5, beginY + 5, mouseX, mouseY, Ingredient.fromStacks(this.recipeObject.getInput().examples().toArray(new ItemStack[0])));
                // TODO (3TUSK): Handles fluid input when item input is present
            } else if ((inputFluid = this.recipeObject.getInputFluid()) != null) {
                TextureAtlasSprite sprite = current.mc.getTextureMapBlocks().getAtlasSprite(inputFluid.getFluid().getStill().toString());
                current.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                current.drawTexturedModalRect(beginX + 4, beginY + 4, sprite, 18, 18);
                if (context.isAreaHovered(mouseX, mouseY, beginX + 4, beginY + 4, 18, 18)) {
                    context.setHoverTooltip(Collections.singletonList(inputFluid.getLocalizedName() + " * " + inputFluid.amount));
                }
            }
            FluidStack outputFluid;
            if (!this.recipeObject.getOutput().isEmpty()) {
                context.renderItemStack(beginX + 5 + 44 + 16, beginY + 5, mouseX, mouseY, this.recipeObject.getOutput());
                // TODO (3TUSK): Handles fluid output when item output is present
            } else if ((outputFluid = this.recipeObject.getOutputFluid()) != null) {
                TextureAtlasSprite sprite = current.mc.getTextureMapBlocks().getAtlasSprite(outputFluid.getFluid().getStill().toString());
                current.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                current.drawTexturedModalRect(beginX + 4 + 44 + 16, beginY + 4, sprite, 18, 18);
                if (context.isAreaHovered(mouseX, mouseY, beginX + 4 + 44 + 16, beginY + 4, 18, 18)) {
                    context.setHoverTooltip(Collections.singletonList(outputFluid.getLocalizedName() + " * " + outputFluid.amount));
                }
            }
            // TODO (3TUSK): We most likely need ... our own texture.
            current.mc.renderEngine.bindTexture(new ResourceLocation("patchouli", "textures/gui/crafting.png"));
            Gui.drawModalRectWithCustomSizedTexture(beginX, beginY, 0, 0, 44, 24, 128, 128);
            Gui.drawModalRectWithCustomSizedTexture(beginX + 44 + 16, beginY, 0, 0, 44, 24, 128, 128);
        }
    }
}
