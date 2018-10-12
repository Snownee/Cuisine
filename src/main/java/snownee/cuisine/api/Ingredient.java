package snownee.cuisine.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.library.RarityManager;

public final class Ingredient
{
    private final Material material;
    private Form form;
    private double quantity;
    private int water, oil, heat;
    private final EnumSet<IngredientTrait> traits;
    private final Set<Effect> effects;

    public Ingredient(Material material)
    {
        this(material, 1);
    }

    public Ingredient(Material material, double quantity)
    {
        this(material, Form.FULL, quantity);
    }

    public Ingredient(Material material, Form form, double quantity)
    {
        this(material, form, quantity, EnumSet.noneOf(IngredientTrait.class));
    }

    public Ingredient(Material material, Form form, double quantity, EnumSet<IngredientTrait> traits)
    {
        this.material = material;
        this.form = form;
        this.quantity = quantity;
        this.traits = traits;
        this.effects = new HashSet<>(4);

        this.water = material.getInitialWaterValue();
        this.oil = material.getInitialOilValue();
        this.heat = material.getInitialHeatValue();
        material.onCrafted(this);
    }

    // TODO (3TUSK): abstraction
    public static Ingredient make(ItemStack stack, float baseSize)
    {
        Material m = CulinaryHub.API_INSTANCE.findMaterial(stack);
        if (m != null && m.isValidForm(Form.FULL))
        {
            if (RarityManager.getRarity(stack) != EnumRarity.COMMON)
            {
                baseSize *= 1.5F;
            }
            return new Ingredient(m, baseSize);
        }
        return null;
    }

    public double getSize()
    {
        return quantity;
    }

    public double getFoodLevel()
    {
        return getSize() * getMaterial().getCategories().size();
    }

    public void increaseSizeBy(double increment)
    {
        quantity += increment;
    }

    public void decreaseSizeBy(double decrement)
    {
        quantity -= decrement;
    }

    public Material getMaterial()
    {
        return material;
    }

    public float getSaturationModifier()
    {
        return material.getSaturationModifier(this);
    }

    public void setForm(Form form)
    {
        if (material.isValidForm(form))
        {
            this.form = form;
        }
    }

    public Form getForm()
    {
        return this.form;
    }

    public void addTrait(IngredientTrait characteristic)
    {
        this.traits.add(characteristic);
    }

    public void removeTrait(IngredientTrait trait)
    {
        this.traits.remove(trait);
    }

    public Set<IngredientTrait> getAllTraits()
    {
        return Collections.unmodifiableSet(this.traits);
    }

    public boolean hasTrait(IngredientTrait trait)
    {
        return traits.contains(trait);
    }

    public void addEffect(Effect effect)
    {
        this.effects.add(effect);
    }

    public void removeEffect(Effect effect)
    {
        this.effects.remove(effect);
    }

    public Set<Effect> getEffects()
    {
        return Collections.unmodifiableSet(this.effects);
    }

    // Auto-generated accessors & mutators begin
    // TODO (3TUSK): evaluate these methods: name choices, immutable, etc..

    public int getWater()
    {
        return water;
    }

    public void setWater(int water)
    {
        this.water = water;
    }

    public int getOil()
    {
        return oil;
    }

    public void setOil(int oil)
    {
        this.oil = oil;
    }

    public int getHeat()
    {
        return heat;
    }

    public void setHeat(int heat)
    {
        this.heat = heat;
    }

    // Auto-generated accessors & mutators end

    public boolean equalsIgnoreSize(@Nonnull Ingredient other)
    {
        return getMaterial().equals(other.getMaterial()) && getForm().equals(other.getForm()) && this.traits.equals(other.traits);
    }

    @SideOnly(Side.CLIENT)
    public String getTranslation()
    {
        String ingredientForm = this.getForm().getName();
        String ingredientMaterial = this.getMaterial().getID();
        if (I18n.hasKey("cuisine.ingredient." + ingredientMaterial + '.' + ingredientForm))
        {
            return I18n.format("cuisine.ingredient." + ingredientMaterial + '.' + ingredientForm);
        }
        else
        {
            String material = I18n.format("cuisine.material." + this.getMaterial().getID());
            return I18n.format("cuisine.shape." + ingredientForm, material);
        }
    }

}
