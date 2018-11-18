package snownee.cuisine.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import snownee.cuisine.library.RarityManager;
import snownee.cuisine.util.I18nUtil;

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

    /**
     *
     * @param stack item
     * @param baseSize initial size of returned ingredient
     * @return Ingredient instance, with adjusted size
     */
    public static Ingredient make(ItemStack stack, double baseSize)
    {
        Ingredient i = CulinaryHub.API_INSTANCE.findIngredient(stack);
        if (i != null)
        {
            if (RarityManager.getRarity(stack) != EnumRarity.COMMON)
            {
                baseSize *= 1.5;
            }
            i.setSize(baseSize);
        }
        return i;
    }

    public double getSize()
    {
        return quantity;
    }

    public double getFoodLevel()
    {
        return getSize() * getMaterial().getCategories().size();
    }

    public void setSize(double newSize)
    {
        this.quantity = newSize;
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

    public boolean equalsIgnoreSize(@Nonnull Ingredient other)
    {
        return getMaterial().equals(other.getMaterial()) && getForm().equals(other.getForm()) && this.traits.equals(other.traits);
    }

    public final Ingredient copy()
    {
        Ingredient theCopy = new Ingredient(this.material, this.form, this.quantity, this.traits.clone());
        theCopy.water = this.water;
        theCopy.oil = this.oil;
        theCopy.heat = this.heat;
        return theCopy;
    }

    /**
     * @deprecated This shall never be in API package; this method will be removed from public API.
     * @return translated name of this ingredient
     */
    @Deprecated
    public String getTranslation()
    {
        String ingredientForm = this.getForm().getName();
        String ingredientMaterial = this.getMaterial().getID();
        if (I18nUtil.canTranslate("ingredient." + ingredientMaterial + '.' + ingredientForm))
        {
            return I18nUtil.translate("ingredient." + ingredientMaterial + '.' + ingredientForm);
        }
        else
        {
            String material = I18nUtil.translate("material." + this.getMaterial().getID());
            return I18nUtil.translate("shape." + ingredientForm, material);
        }
    }

}
