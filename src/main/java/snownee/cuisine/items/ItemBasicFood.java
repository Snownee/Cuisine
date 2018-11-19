package snownee.cuisine.items;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.FakePlayer;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.ModelUtil;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.util.VariantsHolder;
import snownee.kiwi.util.VariantsHolder.Variant;

// extends ItemFood to support Spice of Life and Nutrition
public class ItemBasicFood extends ItemFood implements IModItem
{
    private final String name;
    private static int forestbatLastWords;

    public ItemBasicFood(String name)
    {
        super(1, false);
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
            getVariants().stream().filter(v -> !(this == CuisineRegistry.BASIC_FOOD && v.getMeta() == Variants.EMPOWERED_CITRON.getMeta())).forEach(v -> items.add(getItemStack(v)));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return this == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variants.EMPOWERED_CITRON.getMeta();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        if (this == CuisineRegistry.BASIC_FOOD && item.getMetadata() == Variants.EMPOWERED_CITRON.getMeta())
        {
            if (player.world.rand.nextInt(4) == 0)
            {
                citronSays(player, "drop_success");
            }
            else
            {
                citronSays(player, "drop_failure");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        if (this == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variants.EMPOWERED_CITRON.getMeta())
        {
            if (attacker.world.rand.nextInt(5) == 0)
            {
                citronSays(attacker, "hit");
            }
        }
        return false;
    }

    public static void citronSays(EntityLivingBase player, String key)
    {
        if (player instanceof FakePlayer || player.world.isRemote)
        {
            return;
        }
        int hash = key.hashCode();
        if (hash == forestbatLastWords)
        {
            return;
        }
        forestbatLastWords = hash;
        key = "forestbat." + key;
        if (I18nUtil.canTranslate(key))
        {
            player.sendMessage(new TextComponentTranslation(I18nUtil.getFullKey(key)));
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
        public static final Variant<SubItem> MANDARIN = INSTANCE.addVariant(new SubItem("mandarin"));
        public static final Variant<SubItem> CITRON = INSTANCE.addVariant(new SubItem("citron"));
        public static final Variant<SubItem> POMELO = INSTANCE.addVariant(new SubItem("pomelo"));
        public static final Variant<SubItem> ORANGE = INSTANCE.addVariant(new SubItem("orange"));
        public static final Variant<SubItem> LEMON = INSTANCE.addVariant(new SubItem("lemon"));
        public static final Variant<SubItem> GRAPEFRUIT = INSTANCE.addVariant(new SubItem("grapefruit"));
        public static final Variant<SubItem> LIME = INSTANCE.addVariant(new SubItem("lime"));
        public static final Variant<SubItem> EMPOWERED_CITRON = INSTANCE.addVariant(new SubItem("empowered_citron", EnumRarity.RARE));

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
