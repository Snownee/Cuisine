package snownee.cuisine.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

public enum Form implements IStringSerializable
{
    FULL, // 完整
    CUBED, // 块
    SLICED, //片
    SHREDDED, //丝
    DICED, //丁
    MINCED, //碎
    PASTE, //酱
    JUICE; //汁

    public static final EnumSet<Form> ALL_FORMS = EnumSet.complementOf(EnumSet.of(Form.FULL, Form.JUICE));
    public static final EnumSet<Form> ALL_FORMS_INCLUDING_JUICE = EnumSet.complementOf(EnumSet.of(Form.FULL));
    public static final EnumSet<Form> JUICE_ONLY = EnumSet.of(Form.JUICE);

    // Remember to adjust the initial size when there are new forms
    private static final Map<String, Form> LOOKUP_TABLE = new HashMap<>(8);

    static
    {
        for (Form form : Form.values())
        {
            LOOKUP_TABLE.put(form.name(), form);
        }
    }

    /**
     * Exception-free version of {@link Enum#valueOf(Class, String)}.
     * <p>
     * Return value can be null, and care must be taken for processing its return value.
     * </p>
     * @param name Name of enum constant
     * @return Corresponding enum constant, or null if not found.
     */
    @Nullable
    public static Form of(String name)
    {
        return LOOKUP_TABLE.get(name);
    }

    private final double heatAbsorptionModifier;

    /**
     * @deprecated use {@link Form#Form(double)} to explicitly specify the
     *             heat absorption modifier
     */
    @Deprecated
    Form()
    {
        this(1D);
    }

    Form(double heatAbsorptionModifier)
    {
        this.heatAbsorptionModifier = heatAbsorptionModifier;
    }

    public double getHeatAbsorptionModifier()
    {
        return heatAbsorptionModifier;
    }

    @Override
    public String getName()
    {
        return this.name().toLowerCase(Locale.ENGLISH);
    }

    public static Form byActions(int horizontal, int vertical)
    {
        int min = Math.min(horizontal, vertical);
        int max = Math.max(horizontal, vertical);
        if (max + min > 18)
            return PASTE;
        if (max + min > 15)
            return MINCED;
        if (max > 5 && min > 5)
            return DICED;
        if (max > 5 && min > 1)
            return SHREDDED;
        if (max > 5)
            return SLICED;
        if (max > 0)
            return CUBED;
        return FULL;
    }

    public int[] getStandardActions()
    {
        switch (this)
        {
        case PASTE:
            return new int[] { 10, 10 };
        case MINCED:
            return new int[] { 8, 8 };
        case SLICED:
            return new int[] { 6, 0 };
        case SHREDDED:
            return new int[] { 6, 2 };
        case DICED:
            return new int[] { 6, 6 };
        case CUBED:
            return new int[] { 1, 1 };
        default:
            return new int[] { 0, 0 };
        }
    }
}
