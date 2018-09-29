package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import snownee.cuisine.Cuisine;
import snownee.kiwi.client.ModelUtil;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.util.VariantsHolder;
import snownee.kiwi.util.VariantsHolder.Variant;

// extends ItemFood to support Spice of Life and Nutrition
public class ItemBasicFood extends ItemFood implements IModItem
{
    private final String name;

    public ItemBasicFood(String name)
    {
        super(1, false); // TODO: new wolf food class
        this.name = name;
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setHasSubtypes(true);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, name);
        setTranslationKey(modid + ".food");
    }

    @Override
    public Item cast()
    {
        return this;
    }

    @Override
    public void mapModel()
    {
        ModelUtil.mapItemVariantsModel(this, "food_", Variants.INSTANCE, "");
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        if (stack.getMetadata() < getVariants().size())
        {
            return getVariants().get(stack.getMetadata()).getValue().getRarity();
        }
        else
        {
            return super.getRarity(stack);
        }
    }

    public List<? extends Variant<? extends Variants.SubItem>> getVariants()
    {
        return Variants.INSTANCE;
    }

    public ItemStack getItemStack(Variant variant)
    {
        return getItemStack(variant, 1);
    }

    public ItemStack getItemStack(Variant variant, int amount)
    {
        return new ItemStack(this, amount, variant.getMeta());
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (stack.getMetadata() < getVariants().size())
        {
            return super.getTranslationKey(stack) + "." + getVariants().get(stack.getMetadata()).getValue().getName();
        }
        else
        {
            return super.getTranslationKey(stack);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            getVariants().forEach(v -> items.add(getItemStack(v)));
        }
    }

    public static class Variants extends VariantsHolder<Variants.SubItem>
    {
        static final Variants INSTANCE = new Variants();

        public static final Variant<SubItem> TOFU = INSTANCE.addVariant(new SubItem("tofu"));
        public static final Variant<SubItem> FLOUR = INSTANCE.addVariant(new SubItem("flour"));
        public static final Variant<SubItem> DOUGH = INSTANCE.addVariant(new SubItem("dough"));
        public static final Variant<SubItem> RICE_POWDER = INSTANCE.addVariant(new SubItem("rice_powder"));
        public static final Variant<SubItem> WHITE_RICE = INSTANCE.addVariant(new SubItem("white_rice"));
        public static final Variant<SubItem> PICKLED_CABBAGE = INSTANCE.addVariant(new SubItem("pickled_cabbage"));
        public static final Variant<SubItem> PICKLED_CUCUMBER = INSTANCE.addVariant(new SubItem("pickled_cucumber"));
        public static final Variant<SubItem> PICKLED_PEPPER = INSTANCE.addVariant(new SubItem("pickled_pepper"));
        public static final Variant<SubItem> PICKLED_TURNIP = INSTANCE.addVariant(new SubItem("pickled_turnip"));

        public static class SubItem implements IStringSerializable
        {
            private final String name;
            private final EnumRarity rarity; // TODO (Snownee): rarity should be based on CulinaryHub?

            protected SubItem(String name)
            {
                this(name, EnumRarity.COMMON);
            }

            protected SubItem(String name, EnumRarity rarity)
            {
                this.name = name;
                this.rarity = rarity;
            }

            @Override
            public String getName()
            {
                return name;
            }

            public EnumRarity getRarity()
            {
                return rarity;
            }
        }
    }
}
