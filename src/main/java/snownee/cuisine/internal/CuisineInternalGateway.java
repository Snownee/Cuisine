package snownee.cuisine.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CuisineAPI;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.Recipe;
import snownee.cuisine.api.Spice;
import snownee.cuisine.api.prefab.SimpleEffectImpl;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.api.prefab.SimpleSpiceImpl;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.internal.effect.EffectCurePotions;
import snownee.cuisine.internal.effect.EffectExperienced;
import snownee.cuisine.internal.effect.EffectHarmony;
import snownee.cuisine.internal.effect.EffectHeatResistance;
import snownee.cuisine.internal.effect.EffectPotions;
import snownee.cuisine.internal.effect.EffectTeleport;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.material.MaterialApple;
import snownee.cuisine.internal.material.MaterialChili;
import snownee.cuisine.internal.material.MaterialChorusFruit;
import snownee.cuisine.internal.material.MaterialPufferfish;
import snownee.cuisine.internal.material.MaterialRice;
import snownee.cuisine.internal.material.MaterialWithEffect;
import snownee.cuisine.internal.spice.SpiceChiliPowder;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemCrops;
import snownee.kiwi.util.OreUtil;
import snownee.kiwi.util.definition.ItemDefinition;

/**
 * The main implementation of CuisineAPI.
 *
 * CuisineAPI 的主要实现。
 */
public final class CuisineInternalGateway implements CuisineAPI
{

    /**
     * The singleton reference of CuisineInternalGateway, for internal usage.
     *
     * CuisineInternalGateway 的单例，供内部使用。
     */
    public static CuisineInternalGateway INSTANCE;

    /**
     * Master registry of all known food materials, used majorly for
     * deserialization.
     *
     * 全部已知食材的总注册表，主要用于反序列化时根据注册名恢复数据。
     */
    private final IdentifierBasedRegistry<Material> materialRegistry = new IdentifierBasedRegistry<>();

    /**
     * Master registry of all known spices, used majorly for deserialization.
     *
     * 全部已知调料的总注册表，主要用于反序列化时根据注册名恢复数据。
     */
    private final IdentifierBasedRegistry<Spice> spiceRegistry = new IdentifierBasedRegistry<>();

    /**
     * Master registry of all known food effects, used majorly for deserialization.
     *
     * 全部已知食材特效的注册表，主要用于反序列化时根据注册名恢复数据。
     */
    private final IdentifierBasedRegistry<Effect> effectRegistry = new IdentifierBasedRegistry<>();

    /**
     * Master registry of all known {@link Recipe}.
     */
    private final IdentifierBasedRegistry<Recipe> recipeRegistry = new IdentifierBasedRegistry<>();

    private final Map<String, Class<?>> foodTypes = new HashMap<>();
    private final Map<String, Function<? extends CompositeFood, NBTTagCompound>> serializers = new HashMap<>();
    private final Map<String, Function<NBTTagCompound, ? extends CompositeFood>> deserializers = new HashMap<>();

    /**
     * Special mapping for Item (metadata-sensitive) to Material conversion, used
     * for inter-mod compatibilities.
     *
     * Item（含 meta）到食材的映射表，用于跨 Mod 兼容等场景。
     */
    // Remember to change key to Item in 1.13; also, if the key is Item, it means we
    // can also use IdentityHashMap in the backend.
    public final Map<ItemDefinition, Ingredient> itemIngredients = new HashMap<>();
    /**
     * Special mapping for OreDict-to-Material conversion, used for inter-mod
     * compatibilities.
     *
     * 矿物辞典名到食材的映射表，用于跨 Mod 兼容等场景下判断指定物品是否可视作某种食材。
     */
    public final Map<String, Ingredient> oreDictIngredients = new HashMap<>();
    /**
     * Special mapping for Fluid-to-Material conversion, used for inter-mod
     * compatibilities.
     *
     * 流体名到食材的映射表，用于跨 Mod 兼容等场景下判断指定流体是否隶属某种特定食材。
     */
    public final Map<String, Ingredient> fluidIngredients = new HashMap<>();

    /**
     * 调料瓶默认使用的 Item 到调料的映射表。
     */
    // Same as itemToMaterialMapping.
    public final Map<ItemDefinition, Spice> itemToSpiceMapping = new HashMap<>();

    public final Map<String, Spice> oreDictToSpiceMapping = new HashMap<>();
    /**
     * 调料瓶默认使用的 Fluid 到调料的映射表。
     */
    public final Map<String, Spice> fluidToSpiceMapping = new HashMap<>();

    private CuisineInternalGateway()
    {
        // No-op, only restricting access level
    }

    @Override
    public Material register(Material material)
    {
        return materialRegistry.register(material.getID(), material);
    }

    @Override
    public Spice register(Spice spice)
    {
        return spiceRegistry.register(spice.getID(), spice);
    }

    @Override
    public Effect register(Effect effect)
    {
        return effectRegistry.register(effect.getID(), effect);
    }

    @Override
    public Recipe register(Recipe recipe)
    {
        return recipeRegistry.register(recipe.name(), recipe);
    }

    @Override
    public <F extends CompositeFood> void registerFoodType(ResourceLocation uniqueLocator, Class<F> typeToken, Function<F, NBTTagCompound> serializer, Function<NBTTagCompound, F> deserializer)
    {
        if (uniqueLocator.getNamespace().equals(Loader.instance().activeModContainer().getModId()))
        {
            final String id = uniqueLocator.toString();
            this.foodTypes.putIfAbsent(id, typeToken);
            this.serializers.putIfAbsent(id, serializer);
            this.deserializers.putIfAbsent(id, deserializer);
        }
        else
        {
            throw new IllegalStateException("Registering at the time when current mod container mismatches");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F extends CompositeFood> NBTTagCompound serialize(F dishObject)
    {
        String identifier = dishObject.getIdentifier().toString();
        Function<F, NBTTagCompound> serializer = (Function<F, NBTTagCompound>) this.serializers.get(identifier);
        if (serializer == null)
        {
            return new NBTTagCompound();
        }
        else
        {
            NBTTagCompound data = serializer.apply(dishObject);
            data.setString(CuisineSharedSecrets.KEY_TYPE, identifier);
            return data;
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <F extends CompositeFood> F deserialize(ResourceLocation identifier, NBTTagCompound data)
    {
        ResourceLocation rl = new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE));
        if (!identifier.equals(rl))
        {
            Cuisine.logger.warn("Cannot use {} to deserialize CompositeFood with type of {}", identifier, rl);
            return null;
        }
        Function<NBTTagCompound, F> deserializer = (Function<NBTTagCompound, F>) this.deserializers.get(identifier.toString());
        if (deserializer == null)
        {
            return null;
        }
        else
        {
            return deserializer.apply(data);
        }
    }

    @Override
    public Collection<Material> getKnownMaterials()
    {
        return Collections.unmodifiableCollection(this.materialRegistry.getView().values());
    }

    @Override
    public Collection<Spice> getKnownSpices()
    {
        return Collections.unmodifiableCollection(this.spiceRegistry.getView().values());
    }

    @Override
    public Collection<Effect> getKnownEffects()
    {
        return Collections.unmodifiableCollection(this.effectRegistry.getView().values());
    }

    @Override
    public Material findMaterial(String uniqueId)
    {
        return materialRegistry.lookup(uniqueId);
    }

    @Override
    public Spice findSpice(String uniqueId)
    {
        return spiceRegistry.lookup(uniqueId);
    }

    @Override
    public Effect findEffect(String uniqueId)
    {
        return effectRegistry.lookup(uniqueId);
    }

    // public final Map<ItemDefinition, Material> itemToMaterialMapping = new HashMap<>();

    @Override
    public Ingredient findIngredient(ItemStack item)
    {
        if (item.isEmpty())
        {
            return null;
        }

        if (item.getItem() == CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound data = item.getTagCompound();
            if (data == null)
            {
                return null;
            }
            else
            {
                return CuisinePersistenceCenter.deserializeIngredient(data);
            }
        }

        ItemDefinition itemDefinition = ItemDefinition.of(item);

        Ingredient ingredient = this.itemIngredients.get(itemDefinition);
        if (ingredient != null)
        {
            return ingredient.copy();
        }
        else
        {
            List<String> possibleOreEntries = OreUtil.getOreNames(item);
            for (String entry : possibleOreEntries)
            {
                if ((ingredient = this.oreDictIngredients.get(entry)) != null)
                {
                    return ingredient.copy();
                }
            }
            return null;
        }
    }

    @Override
    public Spice findSpice(ItemStack item)
    {
        Spice spice = itemToSpiceMapping.get(ItemDefinition.of(item));
        if (spice != null)
        {
            return spice;
        }

        List<String> possibleOreEntries = OreUtil.getOreNames(item);
        for (String entry : possibleOreEntries)
        {
            if ((spice = this.oreDictToSpiceMapping.get(entry)) != null)
            {
                return spice;
            }
        }

        return null;
    }

    @Override
    public Ingredient findIngredient(@Nullable FluidStack fluid)
    {
        if (fluid == null)
        {
            return null;
        }

        if (fluid.getFluid() == CuisineFluids.JUICE) // Special-casing Cuisine "juice" fluid
        {
            if (fluid.tag == null || !fluid.tag.hasKey(CuisineSharedSecrets.KEY_MATERIAL, Constants.NBT.TAG_STRING))
            {
                return null;
            }
            Material material = findMaterial(fluid.tag.getString(CuisineSharedSecrets.KEY_MATERIAL));
            return material == null ? null : new Ingredient(material, Form.JUICE, fluid.amount / 500.0);
        }
        else // And then fallback to regular lookup
        {
            Ingredient ingredient = fluidIngredients.get(fluid.getFluid().getName());
            return ingredient == null ? null : ingredient.copy();
        }
    }

    @Override
    public Spice findSpice(@Nullable FluidStack fluid)
    {
        return fluid == null ? null : fluidToSpiceMapping.get(fluid.getFluid().getName());
    }

    @Override
    public boolean isKnownIngredient(ItemStack item)
    {
        return this.findIngredient(item) != null;
    }

    @Override
    public boolean isKnownSpice(ItemStack item)
    {
        if (item.isEmpty())
        {
            return false;
        }

        if (itemToSpiceMapping.containsKey(ItemDefinition.of(item)))
        {
            return true;
        }
        else
        {
            List<String> possibleOreEntries = OreUtil.getOreNames(item);
            for (final String entry : possibleOreEntries)
            {
                if (oreDictToSpiceMapping.containsKey(entry))
                {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean isKnownIngredient(@Nullable FluidStack fluid)
    {
        return this.findIngredient(fluid) != null;
    }

    @Override
    public boolean isKnownSpice(@Nullable FluidStack fluid)
    {
        return fluid != null && fluidToSpiceMapping.containsKey(fluid.getFluid().getName());
    }

    /**
     * Internal hook for initialization of CuisineAPI instance. Not intended for public usage.
     * Please use {@link CulinaryHub#API_INSTANCE} or {@link #INSTANCE the internal counterpart}
     * whenever possible.
     */
    public static void init()
    {
        // API initialization
        CuisineInternalGateway api = new CuisineInternalGateway();
        // Distribute API references
        CulinaryHub.API_INSTANCE = CuisineInternalGateway.INSTANCE = api;

        api.registerFoodType(Dish.DISH_ID, Dish.class, Dish::serialize, Dish::deserialize);
        api.registerFoodType(Drink.DRINK_ID, Drink.class, Drink::serialize, Drink::deserialize);

        // Initialize Effects first, Material registration will use them
        api.register(new EffectExperienced());
        api.register(new EffectPotions("golden_apple").addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1)).addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0)));
        api.register(new EffectPotions("golden_apple_enchanted").addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1)).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0)).addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0)).addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3)));
        api.register(new SimpleEffectImpl("flavor_enhancer", 0xFAFAD2));
        api.register(new EffectCurePotions("cure_potions"));
        api.register(new EffectHarmony());
        api.register(new EffectTeleport());
        api.register(new SimpleEffectImpl("always_edible", 0xFFFFEE));
        api.register(new EffectPotions("jump_boost").addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 400, 1)));
        api.register(new EffectPotions("power").addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 400, 1)));
        api.register(new EffectPotions("night_vision").addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400, 0)));
        api.register(new EffectPotions("longer_night_vision").addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 1200, 0)));
        api.register(new EffectPotions("hot").addPotionEffect(new PotionEffect(CuisineRegistry.HOT, 1200, 0)));
        api.register(new EffectPotions("dispersal").addPotionEffect(new PotionEffect(CuisineRegistry.DISPERSAL, 400, 1)));
        api.register(new EffectPotions("pufferfish_poison").addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 3)).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 300, 2)).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1)));
        api.register(new EffectPotions("water_breathing").addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 1500, 0)));
        api.register(new EffectHeatResistance("heat_resistance"));
        api.register(new EffectHeatResistance("cold_resistance")); // TODO

        // As mentioned above, Material registration will trigger class loading of the class
        // CulinaryHub.CommonEffects, so we don't have to explicitly trigger it here.

        // TODO (3TUSK): Assign each material a base heal amount and a base saturation
        // modifier.
        // 注：1 点饥饿相当于半个鸡腿！玩家的饥饿值上限是 20。
        // 注："隐藏的饥饿条"（食物饱和度）上限也是 20，但它是 float。
        // 注：参考原版 Wiki 决定数据。

        //api.register(new SimpleMaterialImpl("super", -8531, 0, 0F, MaterialCategory.values()))
        api.register(new SimpleMaterialImpl("peanut", -8531, 0, 1, 1, 1, 0F, MaterialCategory.NUT).setValidForms(EnumSet.of(Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("sesame", -15000805, 0, 1, 1, 1, 0F, MaterialCategory.GRAIN));
        api.register(new SimpleMaterialImpl("soybean", -2048665, 0, 1, 1, 1, 0F, MaterialCategory.GRAIN));
        api.register(new MaterialRice("rice", -4671304, 0, 1, 1, 2, 2F, MaterialCategory.GRAIN));
        api.register(new SimpleMaterialImpl("tomato", -2681308, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialChili("chili", -2878173, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.CUBED, Form.SHREDDED, Form.MINCED)));
        api.register(new MaterialWithEffect("garlic", CulinaryHub.CommonEffects.DISPERSAL, -32, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.DICED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("ginger", -1828, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("sichuan_pepper", -8511203, 0, 1, 1, 1, 0F, MaterialCategory.UNKNOWN));
        api.register(new SimpleMaterialImpl("scallion", -12609717, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("turnip", -3557457, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("chinese_cabbage", -1966111, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("lettuce", -14433485, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE, Form.JUICE)));
        api.register(new SimpleMaterialImpl("corn", -3227867, 0, 1, 1, 2, 2F, MaterialCategory.GRAIN).setValidForms(EnumSet.of(Form.MINCED, Form.JUICE)));
        api.register(new SimpleMaterialImpl("cucumber", 0xDDDCE7BD, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("green_pepper", -15107820, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("red_pepper", -8581357, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("leek", -15100888, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.CUBED, Form.MINCED, Form.PASTE)));
        api.register(new SimpleMaterialImpl("onion", -17409, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("eggplant", 0xDCD295, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialWithEffect("spinach", CulinaryHub.CommonEffects.POWER, -15831787, 0, 1, 1, 1, 0.1F, MaterialCategory.VEGETABLES).setValidForms(EnumSet.of(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE, Form.JUICE)));
        api.register(new MaterialWithEffect("tofu", CulinaryHub.CommonEffects.HARMONY, -2311026, 0, 1, 1, 1, 0.4F, MaterialCategory.PROTEIN, MaterialCategory.GRAIN).setValidForms(EnumSet.of(Form.CUBED, Form.SLICED, Form.DICED, Form.MINCED)));
        api.register(new MaterialChorusFruit("chorus_fruit", -6271615, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialApple("apple", 0xEEEBE5CB, 0, 1, 1, 1, 0.1F, MaterialCategory.FRUIT).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialWithEffect("golden_apple", CulinaryHub.CommonEffects.GOLDEN_APPLE, -1782472, 0, 1, 1, 1, 0.3F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialWithEffect("golden_apple_enchanted", CulinaryHub.CommonEffects.GOLDEN_APPLE_ENCHANTED, -1782472, 0, 1, 1, 1, 0.3F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL)
        {
            @Override
            public boolean hasGlowingOverlay(Ingredient ingredient)
            {
                return true;
            }
        }.setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("melon", -769226, 0, 1, 1, 1, 0F, MaterialCategory.FRUIT).setValidForms(EnumSet.of(Form.CUBED, Form.SLICED, Form.DICED, Form.MINCED, Form.PASTE, Form.JUICE)));
        api.register(new SimpleMaterialImpl("pumpkin", -663885, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialWithEffect("carrot", CulinaryHub.CommonEffects.NIGHT_VISION, -1538531, 0, 1, 1, 1, 0.1F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new MaterialWithEffect("golden_carrot", CulinaryHub.CommonEffects.LONGER_NIGHT_VISION, 0xDBA213, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES, MaterialCategory.SUPERNATURAL).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("potato", -3764682, 0, 1, 1, 2, 2F, MaterialCategory.GRAIN).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("beetroot", -8442327, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS_INCLUDING_JUICE));
        api.register(new SimpleMaterialImpl("mushroom", -10006976, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("egg", -3491187, 0, 1, 1, 1, 0.2F, MaterialCategory.PROTEIN));
        api.register(new SimpleMaterialImpl("chicken", -929599, 0, 1, 1, 1, 0F, MaterialCategory.MEAT).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("beef", -3392460, 0, 1, 1, 1, 0F, MaterialCategory.MEAT).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("pork", -2133904, 0, 1, 1, 1, 0F, MaterialCategory.MEAT).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("mutton", -3917262, 0, 1, 1, 1, 0F, MaterialCategory.MEAT).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialWithEffect("rabbit", CulinaryHub.CommonEffects.JUMP_BOOST, -4882580, 0, 1, 1, 1, 0.1F, MaterialCategory.MEAT).setValidForms(Form.ALL_FORMS));
        api.register(new SimpleMaterialImpl("fish", -10583426, 0, 1, 1, 1, 0F, MaterialCategory.FISH).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialPufferfish("pufferfish", 0xFFFFE1C4, 0, 1, 1, 1, 0.2F, MaterialCategory.FISH).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialWithEffect("pickled", CulinaryHub.CommonEffects.ALWAYS_EDIBLE, -13784, 0, 1, 1, 1, 0.3F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialWithEffect("bamboo_shoot", CulinaryHub.CommonEffects.ALWAYS_EDIBLE, 0xF9ECDD, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES).setValidForms(Form.ALL_FORMS));
        api.register(new MaterialWithEffect("cactus", CulinaryHub.CommonEffects.HEAT_RESISTANCE, 0xA9BC98, 0, 1, 1, 1, -0.1F).setValidForms(EnumSet.of(Form.CUBED, Form.DICED, Form.JUICE)));
        api.register(new SimpleMaterialImpl("water", 0x55DDDDFF, 0, 1, 1, 1, -0.1F).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("milk", 0xCCFFFFFF, 0, 1, 1, 1, -0.1F, MaterialCategory.PROTEIN).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("soy_milk", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.PROTEIN).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("mandarin", 0xF08A19, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("citron", 0xDDCC58, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("pomelo", 0xF7F67E, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("orange", 0xF08A19, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("lemon", 0xEBCA4B, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("grapefruit", 0xF4502B, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("lime", 0xCADA76, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("empowered_citron", 0xE6B701, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL).setValidForms(Form.JUICE_ONLY));

        api.register(new SimpleSpiceImpl("edible_oil", 0x99D1A71A, true));
        api.register(new SimpleSpiceImpl("sesame_oil", 0x99CE8600, true));
        api.register(new SimpleSpiceImpl("soy_sauce", 0xDD100000, true));
        api.register(new SimpleSpiceImpl("rice_vinegar", 0xCC100000, true));
        api.register(new SimpleSpiceImpl("fruit_vinegar", 0xBB100000, true));
        api.register(new SimpleSpiceImpl("water", 0x55FFFFFF, true));
        api.register(new SpiceChiliPowder("chili_powder", 11546150));
        api.register(new SimpleSpiceImpl("sichuan_pepper_powder", 8606770, false));
        api.register(new SimpleSpiceImpl("crude_salt", 4673362, false));
        api.register(new SimpleSpiceImpl("salt", 0xE3E3E3, false));
        api.register(new SimpleSpiceImpl("sugar", 16383998, false));
        api.register(new SimpleSpiceImpl("unrefined_sugar", 0xB35400, false));

        CulinaryHub.CommonSkills.init();
    }

    /**
     * Internal hook for initialization of basic items/fluids -> ingredients/seasoning mapping.
     * Not intended for public usage.
     */
    public static void deferredInit()
    {
        CuisineInternalGateway api = CuisineInternalGateway.INSTANCE;
        api.itemIngredients.put(ItemDefinition.of(CuisineRegistry.CROPS, ItemCrops.Variants.RED_PEPPER.getMeta()), new Ingredient(CulinaryHub.CommonMaterials.RED_PEPPER, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.EMPOWERED_CITRON.getMeta()), new Ingredient(CulinaryHub.CommonMaterials.EMPOWERED_CITRON, Form.FULL, 1));

        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_APPLE), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_APPLE, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_APPLE, 1), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_APPLE_ENCHANTED, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.MELON), new Ingredient(CulinaryHub.CommonMaterials.MELON, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.CARROT), new Ingredient(CulinaryHub.CommonMaterials.CARROT, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_CARROT), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_CARROT, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.POTATO), new Ingredient(CulinaryHub.CommonMaterials.POTATO, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.BEETROOT), new Ingredient(CulinaryHub.CommonMaterials.BEETROOT, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH), new Ingredient(CulinaryHub.CommonMaterials.FISH, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH, 1), new Ingredient(CulinaryHub.CommonMaterials.FISH, Form.FULL, 1));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH, 3), new Ingredient(CulinaryHub.CommonMaterials.PUFFERFISH, Form.FULL, 1));

        api.oreDictIngredients.put("cropPeanut", new Ingredient(CulinaryHub.CommonMaterials.PEANUT, Form.FULL, 1));
        api.oreDictIngredients.put("cropSesame", new Ingredient(CulinaryHub.CommonMaterials.SESAME, Form.FULL, 1));
        api.oreDictIngredients.put("cropSoybean", new Ingredient(CulinaryHub.CommonMaterials.SOYBEAN, Form.FULL, 1));
        api.oreDictIngredients.put("cropTomato", new Ingredient(CulinaryHub.CommonMaterials.TOMATO, Form.FULL, 1));
        api.oreDictIngredients.put("cropChilipepper", new Ingredient(CulinaryHub.CommonMaterials.CHILI, Form.FULL, 1));
        api.oreDictIngredients.put("foodRice", new Ingredient(CulinaryHub.CommonMaterials.RICE, Form.FULL, 1));
        api.oreDictIngredients.put("cropGarlic", new Ingredient(CulinaryHub.CommonMaterials.GARLIC, Form.FULL, 1));
        api.oreDictIngredients.put("cropGinger", new Ingredient(CulinaryHub.CommonMaterials.GINGER, Form.FULL, 1));
        api.oreDictIngredients.put("cropSichuanpepper", new Ingredient(CulinaryHub.CommonMaterials.SICHUAN_PEPPER, Form.FULL, 1));
        api.oreDictIngredients.put("cropScallion", new Ingredient(CulinaryHub.CommonMaterials.SCALLION, Form.FULL, 1));
        api.oreDictIngredients.put("cropTurnip", new Ingredient(CulinaryHub.CommonMaterials.TURNIP, Form.FULL, 1));
        api.oreDictIngredients.put("cropCabbage", new Ingredient(CulinaryHub.CommonMaterials.CHINESE_CABBAGE, Form.FULL, 1));
        api.oreDictIngredients.put("cropLettuce", new Ingredient(CulinaryHub.CommonMaterials.LETTUCE, Form.FULL, 1));
        api.oreDictIngredients.put("cropCorn", new Ingredient(CulinaryHub.CommonMaterials.CORN, Form.FULL, 1));
        api.oreDictIngredients.put("cropCucumber", new Ingredient(CulinaryHub.CommonMaterials.CUCUMBER, Form.FULL, 1));
        api.oreDictIngredients.put("cropLeek", new Ingredient(CulinaryHub.CommonMaterials.LEEK, Form.FULL, 1));
        api.oreDictIngredients.put("cropOnion", new Ingredient(CulinaryHub.CommonMaterials.ONION, Form.FULL, 1));
        api.oreDictIngredients.put("cropEggplant", new Ingredient(CulinaryHub.CommonMaterials.EGGPLANT, Form.FULL, 1));
        api.oreDictIngredients.put("cropSpinach", new Ingredient(CulinaryHub.CommonMaterials.SPINACH, Form.FULL, 1));
        api.oreDictIngredients.put("foodFirmtofu", new Ingredient(CulinaryHub.CommonMaterials.TOFU, Form.FULL, 1));
        api.oreDictIngredients.put("cropChorusfruit", new Ingredient(CulinaryHub.CommonMaterials.CHORUS_FRUIT, Form.FULL, 1));
        api.oreDictIngredients.put("cropApple", new Ingredient(CulinaryHub.CommonMaterials.APPLE, Form.FULL, 1));
        api.oreDictIngredients.put("egg", new Ingredient(CulinaryHub.CommonMaterials.EGG, Form.FULL, 1));
        api.oreDictIngredients.put("listAllporkraw", new Ingredient(CulinaryHub.CommonMaterials.PORK, Form.FULL, 1));
        api.oreDictIngredients.put("listAllmuttonraw", new Ingredient(CulinaryHub.CommonMaterials.MUTTON, Form.FULL, 1));
        api.oreDictIngredients.put("listAllbeefraw", new Ingredient(CulinaryHub.CommonMaterials.BEEF, Form.FULL, 1));
        api.oreDictIngredients.put("listAllchickenraw", new Ingredient(CulinaryHub.CommonMaterials.CHICKEN, Form.FULL, 1));
        api.oreDictIngredients.put("listAllrabbitraw", new Ingredient(CulinaryHub.CommonMaterials.RABBIT, Form.FULL, 1));
        api.oreDictIngredients.put("blockCactus", new Ingredient(CulinaryHub.CommonMaterials.CACTUS, Form.FULL, 1));
        api.oreDictIngredients.put("foodPickles", new Ingredient(CulinaryHub.CommonMaterials.PICKLED, Form.FULL, 1));
        api.oreDictIngredients.put("cropMandarin", new Ingredient(CulinaryHub.CommonMaterials.MANDARIN, Form.FULL, 1));
        api.oreDictIngredients.put("cropCitron", new Ingredient(CulinaryHub.CommonMaterials.CITRON, Form.FULL, 1));
        api.oreDictIngredients.put("cropPomelo", new Ingredient(CulinaryHub.CommonMaterials.POMELO, Form.FULL, 1));
        api.oreDictIngredients.put("cropOrange", new Ingredient(CulinaryHub.CommonMaterials.ORANGE, Form.FULL, 1));
        api.oreDictIngredients.put("cropLemon", new Ingredient(CulinaryHub.CommonMaterials.LEMON, Form.FULL, 1));
        api.oreDictIngredients.put("cropGrapefruit", new Ingredient(CulinaryHub.CommonMaterials.GRAPEFRUIT, Form.FULL, 1));
        api.oreDictIngredients.put("cropLime", new Ingredient(CulinaryHub.CommonMaterials.LIME, Form.FULL, 1));
        api.oreDictIngredients.put("cropBambooshoot", new Ingredient(CulinaryHub.CommonMaterials.BAMBOO_SHOOT, Form.FULL, 1));
        api.oreDictIngredients.put("cropBellpepper", new Ingredient(CulinaryHub.CommonMaterials.GREEN_PEPPER, Form.FULL, 1));
        api.oreDictIngredients.put("foodMushroom", new Ingredient(CulinaryHub.CommonMaterials.MUSHROOM, Form.FULL, 1));
        api.oreDictIngredients.put("cropMushroom", new Ingredient(CulinaryHub.CommonMaterials.MUSHROOM, Form.FULL, 1));
        api.oreDictIngredients.put("cropPumpkin", new Ingredient(CulinaryHub.CommonMaterials.PUMPKIN, Form.FULL, 1));

        api.fluidIngredients.put(FluidRegistry.WATER.getName(), new Ingredient(CulinaryHub.CommonMaterials.WATER, Form.JUICE, 1));
        api.fluidIngredients.put(CuisineFluids.MILK.getName(), new Ingredient(CulinaryHub.CommonMaterials.MILK, Form.JUICE, 1));
        api.fluidIngredients.put(CuisineFluids.SOY_MILK.getName(), new Ingredient(CulinaryHub.CommonMaterials.SOY_MILK, Form.JUICE, 1));

        api.fluidToSpiceMapping.put(CuisineFluids.EDIBLE_OIL.getName(), CulinaryHub.CommonSpices.EDIBLE_OIL);
        api.fluidToSpiceMapping.put(CuisineFluids.SESAME_OIL.getName(), CulinaryHub.CommonSpices.SESAME_OIL);
        api.fluidToSpiceMapping.put(CuisineFluids.SOY_SAUCE.getName(), CulinaryHub.CommonSpices.SOY_SAUCE);
        api.fluidToSpiceMapping.put(CuisineFluids.RICE_VINEGAR.getName(), CulinaryHub.CommonSpices.RICE_VINEGAR);
        api.fluidToSpiceMapping.put(CuisineFluids.FRUIT_VINEGAR.getName(), CulinaryHub.CommonSpices.FRUIT_VINEGAR);
        api.fluidToSpiceMapping.put(FluidRegistry.WATER.getName(), CulinaryHub.CommonSpices.WATER);

        api.itemToSpiceMapping.put(ItemDefinition.of(CuisineRegistry.MATERIAL, Cuisine.Materials.CHILI_POWDER.getMeta()), CulinaryHub.CommonSpices.CHILI_POWDER);
        api.itemToSpiceMapping.put(ItemDefinition.of(CuisineRegistry.MATERIAL, Cuisine.Materials.SICHUAN_PEPPER_POWDER.getMeta()), CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER);
        api.itemToSpiceMapping.put(ItemDefinition.of(Items.SUGAR), CulinaryHub.CommonSpices.SUGAR);
        api.itemToSpiceMapping.put(ItemDefinition.of(CuisineRegistry.MATERIAL, Cuisine.Materials.UNREFINED_SUGAR.getMeta()), CulinaryHub.CommonSpices.UNREFINED_SUGAR);

        api.oreDictToSpiceMapping.put("dustSalt", CulinaryHub.CommonSpices.SALT);
        api.oreDictToSpiceMapping.put("dustCrudesalt", CulinaryHub.CommonSpices.CRUDE_SALT);
    }

    @Override
    public void registerMapping(ItemDefinition item, Ingredient ingredient)
    {
        itemIngredients.put(item, ingredient);
    }

    //    @Override
    //    public void registerMapping(ItemDefinition item, Material material)
    //    {
    //        // TODO Auto-generated method stub
    //        CuisineAPI.super.registerMapping(item, material);
    //    }

    @Override
    public void registerMapping(String ore, Ingredient ingredient)
    {
        oreDictIngredients.put(ore, ingredient);
    }

    //    @Override
    //    public void registerMapping(String ore, Material material)
    //    {
    //        // TODO Auto-generated method stub
    //        CuisineAPI.super.registerMapping(ore, material);
    //    }

    @Override
    public void registerMapping(ItemDefinition item, Spice spice)
    {
        itemToSpiceMapping.put(item, spice);
    }

    @Override
    public void registerMapping(String ore, Spice spice)
    {
        oreDictToSpiceMapping.put(ore, spice);
    }

}
