package snownee.cuisine.api.prefab;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;

public class SimpleMaterialImpl implements Material
{

    private final String id;
    private final int rawColor, cookedColor, waterValue, oilValue, heatValue;
    private final float saturationModifier;
    private final EnumSet<MaterialCategory> categories;
    private EnumSet<Form> validForms = EnumSet.noneOf(Form.class);

    public SimpleMaterialImpl(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue)
    {
        this(id, rawColor, cookedColor, waterValue, oilValue, heatValue, 0F);
    }

    public SimpleMaterialImpl(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float foodSaturationModifier)
    {
        this.id = id;
        this.rawColor = rawColor;
        this.cookedColor = cookedColor;
        this.waterValue = waterValue;
        this.oilValue = oilValue;
        this.heatValue = heatValue;
        this.saturationModifier = foodSaturationModifier;
        this.categories = EnumSet.noneOf(MaterialCategory.class);
    }

    public SimpleMaterialImpl(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float foodSaturationModifier, MaterialCategory... categories)
    {
        this(id, rawColor, cookedColor, waterValue, oilValue, heatValue, foodSaturationModifier);
        this.categories.addAll(Arrays.asList(categories));
    }

    @Override
    public String getID()
    {
        return this.id;
    }

    @Override
    public String getTranslationKey()
    {
        return "cuisine.material." + getID();
    }

    @Override
    public float getSaturationModifier()
    {
        return this.saturationModifier;
    }

    @Override
    public int getRawColorCode()
    {
        return this.rawColor;
    }

    @Override
    public int getCookedColorCode()
    {
        return this.cookedColor;
    }

    @Override
    public int getWaterValue()
    {
        return waterValue;
    }

    @Override
    public int getOilValue()
    {
        return oilValue;
    }

    @Override
    public int getHeatValue()
    {
        return heatValue;
    }

    @Override
    public boolean isUnderCategoryOf(MaterialCategory category)
    {
        return this.categories.contains(category);
    }

    @Override
    public Set<MaterialCategory> getCategories()
    {
        return Collections.unmodifiableSet(categories);
    }

    public SimpleMaterialImpl addValidForms(Form... forms)
    {
        this.validForms.addAll(Arrays.asList(forms));
        return this;
    }

    public SimpleMaterialImpl setValidForms(EnumSet<Form> validForms)
    {
        this.validForms = validForms;
        return this;
    }

    @Override
    public boolean isValidForm(Form form)
    {
        return form == Form.FULL || validForms.contains(form);
    }

    @Override
    public EnumSet<Form> getValidForms()
    {
        return validForms;
    }

    @Override
    public String toString()
    {
        return "Material{" + this.getID() + "}";
    }
}
