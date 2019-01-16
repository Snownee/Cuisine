package snownee.cuisine.items;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.ModelUtil;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.item.IVariant;

// extends ItemFood to support Spice of Life and Nutrition
public class ItemBasicFood<T, E extends IVariant<T> & IRarityGetter> extends ItemFood implements IModItem
{
    private final String name;
    private final E[] values;
    private static int forestbatLastWords;

    public ItemBasicFood(String name, E[] values)
    {
        super(1, false);
        this.name = name;
        this.values = values;
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

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        if (stack.getMetadata() < values.length)
        {
            return getVariants().get(stack.getMetadata()).getRarity();
        }
        else
        {
            return super.getRarity(stack);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            getVariants().stream()
                    .filter(v -> !(this == CuisineRegistry.BASIC_FOOD && v.getMeta() == Variant.EMPOWERED_CITRON.getMeta()))
                    .map(this::getItemStack)
                    .forEach(items::add);
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return stack.getItem() == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variant.EMPOWERED_CITRON.getMeta();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player)
    {
        if (stack.getItem() == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variant.EMPOWERED_CITRON.getMeta())
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
        if (stack.getItem() == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variant.EMPOWERED_CITRON.getMeta())
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

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        ModelUtil.mapItemVariantsModelNew(this, "food_", this.values, "");
    }

    public List<E> getVariants()
    {
        return Arrays.asList(values);
    }

    public ItemStack getItemStack(E variant)
    {
        return getItemStack(variant, 1);
    }

    public ItemStack getItemStack(E variant, int amount)
    {
        return new ItemStack(this, amount, variant.getMeta());
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (stack.getMetadata() < values.length)
        {
            return super.getTranslationKey(stack) + "." + values[stack.getMetadata()].getName();
        }
        else
        {
            return super.getTranslationKey(stack);
        }
    }

    public static enum Variant implements IVariant<Void>, IRarityGetter
    {
        TOFU,
        FLOUR,
        DOUGH,
        RICE_POWDER,
        WHITE_RICE,
        PICKLED_CABBAGE,
        PICKLED_CUCUMBER,
        PICKLED_PEPPER,
        PICKLED_TURNIP,
        MANDARIN,
        CITRON,
        POMELO,
        ORANGE,
        LEMON,
        GRAPEFRUIT,
        LIME,
        EMPOWERED_CITRON(EnumRarity.RARE);

        private final EnumRarity rarity;

        Variant()
        {
            this(EnumRarity.COMMON);
        }

        Variant(EnumRarity rarity)
        {
            this.rarity = rarity;
        }

        @Override
        public int getMeta()
        {
            return ordinal();
        }

        @Override
        public EnumRarity getRarity()
        {
            return rarity;
        }

        @Override
        public Void getValue()
        {
            return null;
        }
    }
}
