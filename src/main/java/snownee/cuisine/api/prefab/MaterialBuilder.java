package snownee.cuisine.api.prefab;

import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.internal.material.MaterialWithEffect;

import java.util.*;

/**
 * Simple builder for a material
 */
public class MaterialBuilder
{
    private final String id;
    private int rawColor, cookedColor, waterValue, oilValue, heatValue;
    private float saturationModifier;
    private EnumSet<MaterialCategory> categories = EnumSet.noneOf(MaterialCategory.class);
    private EnumSet<Form> validForms = EnumSet.noneOf(Form.class);
    private Effect effect;
    private float boilHeat = 90;
    private int boilTime = 150;

    public MaterialBuilder(String id)
    {
        this.id = id;
    }

    public static MaterialBuilder of(String id) {
        return new MaterialBuilder(id);
    }

    public MaterialBuilder rawColor(int rawColor)
    {
        this.rawColor = rawColor;
        return this;
    }

    public MaterialBuilder effect(Effect effect)
    {
        this.effect = effect;
        return this;
    }

    public MaterialBuilder cookedColor(int cookedColor)
    {
        this.cookedColor = cookedColor;
        return this;
    }

    public MaterialBuilder waterValue(int waterValue)
    {
        this.waterValue = waterValue;
        return this;
    }

    public MaterialBuilder oilValue(int oilValue)
    {
        this.oilValue = oilValue;
        return this;
    }

    public MaterialBuilder heatValue(int heatValue)
    {
        this.heatValue = heatValue;
        return this;
    }

    public MaterialBuilder saturation(float saturationModifier)
    {
        this.saturationModifier = saturationModifier;
        return this;
    }

    public MaterialBuilder form(Form... forms)
    {
        validForms.addAll(Arrays.asList(forms));
        return this;
    }

    public MaterialBuilder form(Collection<? extends Form> forms)
    {
        validForms.addAll(forms);
        return this;
    }

    public MaterialBuilder form(EnumSet<Form> forms)
    {
        validForms = forms;
        return this;
    }

    public MaterialBuilder category(MaterialCategory... categories)
    {
        this.categories.addAll(Arrays.asList(categories));
        return this;
    }

    public MaterialBuilder category(Collection<? extends MaterialCategory> categories)
    {
        this.categories.addAll(categories);
        return this;
    }

    public MaterialBuilder boilHeat(float boilHeat)
    {
        this.boilHeat = boilHeat;
        return this;
    }

    public MaterialBuilder boilTime(int boilTime)
    {
        this.boilTime = boilTime;
        return this;
    }

    public SimpleMaterialImpl build()
    {
        if (effect != null)
            return new MaterialWithEffect(id, effect, rawColor, cookedColor, waterValue, oilValue, heatValue, saturationModifier, boilHeat, boilTime, categories.toArray(new MaterialCategory[0])).setValidForms(validForms);
        return new SimpleMaterialImpl(id, rawColor, cookedColor, waterValue, oilValue, heatValue, saturationModifier, boilHeat, boilTime, categories.toArray(new MaterialCategory[0])).setValidForms(validForms);
    }
}
