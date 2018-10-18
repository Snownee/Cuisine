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
import snownee.cuisine.items.ItemIngredient;
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
     * Default mapping for Item (metadata-sensitive) to Material conversion, used
     * for inter-mod compatibilities.
     *
     * Item（含 meta）到食材的映射表，用于跨 Mod 兼容等场景。
     */
    // Remember to change key to Item in 1.13; also, if the key is Item, it means we
    // can also use IdentityHashMap in the backend.
    public final Map<ItemDefinition, Material> itemToMaterialMapping = new HashMap<>();
    /**
     * Default mapping for OreDict-to-Material conversion, used for inter-mod
     * compatibilities.
     *
     * 矿物辞典名到食材的映射表，用于跨 Mod 兼容等场景下判断指定物品是否隶属某种特定食材。
     */
    public final Map<String, Material> oreDictToMaterialMapping = new HashMap<>();

    public final Map<String, Material> fluidToMaterialMapping = new HashMap<>();

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

    public final Map<ItemDefinition, SimpleJuice> simpleJuiceMapping = new HashMap<>();

    private static final class SimpleJuice
    {
        public final Material material;
        final double quantity;

        public SimpleJuice(Material material, double quantity)
        {
            this.material = material;
            this.quantity = quantity;
        }

        Ingredient makeIngredient()
        {
            return new Ingredient(material, Form.JUICE, quantity);
        }
    }

    @Override
    public Ingredient findIngredient(ItemStack item)
    {
        if (item.isEmpty())
        {
            return null;
        }

        if (item.getItem().getClass() == ItemIngredient.class)
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

        SimpleJuice juice = simpleJuiceMapping.get(itemDefinition);
        if (juice != null)
        {
            return juice.makeIngredient();
        }

        Material material = this.itemToMaterialMapping.get(itemDefinition);
        if (material != null)
        {
            return new Ingredient(material, Form.FULL, 1.0);
        }
        else
        {
            List<String> possibleOreEntries = OreUtil.getOreNames(item);
            for (String entry : possibleOreEntries)
            {
                if ((material = this.oreDictToMaterialMapping.get(entry)) != null)
                {
                    return new Ingredient(material, Form.FULL, 1.0);
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

        Material material;
        if (fluid.getFluid() == CuisineFluids.JUICE)
        {
            if (fluid.tag == null || !fluid.tag.hasKey("material", Constants.NBT.TAG_STRING))
            {
                return null;
            }
            material = findMaterial(fluid.tag.getString("material"));
        }
        else
        {
            material = fluidToMaterialMapping.get(fluid.getFluid().getName());
        }
        return material == null ? null : new Ingredient(material, Form.JUICE, fluid.amount / 500.0);
    }

    @Override
    public Spice findSpice(@Nullable FluidStack fluid)
    {
        if (fluid == null)
        {
            return null;
        }
        return fluidToSpiceMapping.get(fluid.getFluid().getName());
    }

    @Override
    public boolean isKnownMaterial(ItemStack item)
    {
        if (item.isEmpty())
        {
            return false;
        }

        if (itemToMaterialMapping.containsKey(ItemDefinition.of(item)))
        {
            return true;
        }
        else
        {
            List<String> possibleOreEntries = OreUtil.getOreNames(item);
            for (final String entry : possibleOreEntries)
            {
                if (oreDictToMaterialMapping.containsKey(entry))
                {
                    return true;
                }
            }
            return false;
        }
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
    public boolean isKnownMaterial(@Nullable FluidStack fluid)
    {
        if (fluid == null)
        {
            return false;
        }

        if (fluid.getFluid() == CuisineFluids.JUICE)
        {
            if (fluid.tag == null || !fluid.tag.hasKey("material", Constants.NBT.TAG_STRING))
            {
                return false;
            }
            return findMaterial(fluid.tag.getString("material")) != null;
        }
        return fluidToMaterialMapping.containsKey(fluid.getFluid().getName());
    }

    @Override
    public boolean isKnownSpice(@Nullable FluidStack fluid)
    {
        if (fluid == null)
        {
            return false;
        }
        return fluidToSpiceMapping.containsKey(fluid.getFluid().getName());
    }

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
        api.register(new SimpleMaterialImpl("water", -15831787, 0, 1, 1, 1, -0.1F).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("milk", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.PROTEIN).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("soy_milk", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.PROTEIN).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("mandarin", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("citron", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("pomelo", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("orange", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("lemon", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("grapefruit", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("lime", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        api.register(new SimpleMaterialImpl("empowered_citron", -15831787, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL).setValidForms(Form.JUICE_ONLY));

        CulinaryHub.CommonMaterials.init();

        api.register(new SimpleSpiceImpl("edible_oil", 0));
        api.register(new SimpleSpiceImpl("sesame_oil", 0));
        api.register(new SimpleSpiceImpl("soy_sauce", 0));
        api.register(new SimpleSpiceImpl("rice_vinegar", 0));
        api.register(new SimpleSpiceImpl("fruit_vinegar", 0));
        api.register(new SimpleSpiceImpl("water", 0));
        api.register(new SpiceChiliPowder("chili_powder", 11546150));
        api.register(new SimpleSpiceImpl("sichuan_pepper_powder", 8606770));
        api.register(new SimpleSpiceImpl("crude_salt", 4673362));
        api.register(new SimpleSpiceImpl("salt", 16383998));
        api.register(new SimpleSpiceImpl("sugar", 16383998));

        CulinaryHub.CommonSpices.init();

        CulinaryHub.CommonSkills.init();

        api.itemToMaterialMapping.put(ItemDefinition.of(CuisineRegistry.CROPS, ItemCrops.Variants.RED_PEPPER.getMeta()), CulinaryHub.CommonMaterials.RED_PEPPER);
        api.itemToMaterialMapping.put(ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.EMPOWERED_CITRON.getMeta()), CulinaryHub.CommonMaterials.EMPOWERED_CITRON);

        api.itemToMaterialMapping.put(ItemDefinition.of(Items.GOLDEN_APPLE), CulinaryHub.CommonMaterials.GOLDEN_APPLE);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.GOLDEN_APPLE, 1), CulinaryHub.CommonMaterials.GOLDEN_APPLE_ENCHANTED);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.MELON), CulinaryHub.CommonMaterials.MELON);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.CARROT), CulinaryHub.CommonMaterials.CARROT);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.POTATO), CulinaryHub.CommonMaterials.POTATO);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.BEETROOT), CulinaryHub.CommonMaterials.BEETROOT);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.FISH), CulinaryHub.CommonMaterials.FISH);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.FISH, 1), CulinaryHub.CommonMaterials.FISH);
        api.itemToMaterialMapping.put(ItemDefinition.of(Items.FISH, 3), CulinaryHub.CommonMaterials.PUFFERFISH);

        api.oreDictToMaterialMapping.put("cropPeanut", CulinaryHub.CommonMaterials.PEANUT);
        api.oreDictToMaterialMapping.put("cropSesame", CulinaryHub.CommonMaterials.SESAME);
        api.oreDictToMaterialMapping.put("cropSoybean", CulinaryHub.CommonMaterials.SOYBEAN);
        api.oreDictToMaterialMapping.put("cropTomato", CulinaryHub.CommonMaterials.TOMATO);
        api.oreDictToMaterialMapping.put("cropChilipepper", CulinaryHub.CommonMaterials.CHILI);
        api.oreDictToMaterialMapping.put("foodRice", CulinaryHub.CommonMaterials.RICE);
        api.oreDictToMaterialMapping.put("cropGarlic", CulinaryHub.CommonMaterials.GARLIC);
        api.oreDictToMaterialMapping.put("cropGinger", CulinaryHub.CommonMaterials.GINGER);
        api.oreDictToMaterialMapping.put("cropSichuanpepper", CulinaryHub.CommonMaterials.SICHUAN_PEPPER);
        api.oreDictToMaterialMapping.put("cropScallion", CulinaryHub.CommonMaterials.SCALLION);
        api.oreDictToMaterialMapping.put("cropTurnip", CulinaryHub.CommonMaterials.TURNIP);
        api.oreDictToMaterialMapping.put("cropCabbage", CulinaryHub.CommonMaterials.CHINESE_CABBAGE);
        api.oreDictToMaterialMapping.put("cropLettuce", CulinaryHub.CommonMaterials.LETTUCE);
        api.oreDictToMaterialMapping.put("cropCorn", CulinaryHub.CommonMaterials.CORN);
        api.oreDictToMaterialMapping.put("cropCucumber", CulinaryHub.CommonMaterials.CUCUMBER);
        api.oreDictToMaterialMapping.put("cropLeek", CulinaryHub.CommonMaterials.LEEK);
        api.oreDictToMaterialMapping.put("cropOnion", CulinaryHub.CommonMaterials.ONION);
        api.oreDictToMaterialMapping.put("cropEggplant", CulinaryHub.CommonMaterials.EGGPLANT);
        api.oreDictToMaterialMapping.put("cropSpinach", CulinaryHub.CommonMaterials.SPINACH);
        api.oreDictToMaterialMapping.put("foodFirmtofu", CulinaryHub.CommonMaterials.TOFU);
        api.oreDictToMaterialMapping.put("cropChorusfruit", CulinaryHub.CommonMaterials.CHORUS_FRUIT);
        api.oreDictToMaterialMapping.put("cropApple", CulinaryHub.CommonMaterials.APPLE);
        api.oreDictToMaterialMapping.put("egg", CulinaryHub.CommonMaterials.EGG);
        api.oreDictToMaterialMapping.put("listAllporkraw", CulinaryHub.CommonMaterials.PORK);
        api.oreDictToMaterialMapping.put("listAllmuttonraw", CulinaryHub.CommonMaterials.MUTTON);
        api.oreDictToMaterialMapping.put("listAllbeefraw", CulinaryHub.CommonMaterials.BEEF);
        api.oreDictToMaterialMapping.put("listAllchickenraw", CulinaryHub.CommonMaterials.CHICKEN);
        api.oreDictToMaterialMapping.put("listAllrabbitraw", CulinaryHub.CommonMaterials.RABBIT);
        api.oreDictToMaterialMapping.put("blockCactus", CulinaryHub.CommonMaterials.CACTUS);
        api.oreDictToMaterialMapping.put("foodPickles", CulinaryHub.CommonMaterials.PICKLED);
        api.oreDictToMaterialMapping.put("cropMandarin", CulinaryHub.CommonMaterials.MANDARIN);
        api.oreDictToMaterialMapping.put("cropCitron", CulinaryHub.CommonMaterials.CITRON);
        api.oreDictToMaterialMapping.put("cropPomelo", CulinaryHub.CommonMaterials.POMELO);
        api.oreDictToMaterialMapping.put("cropOrange", CulinaryHub.CommonMaterials.ORANGE);
        api.oreDictToMaterialMapping.put("cropLemon", CulinaryHub.CommonMaterials.LEMON);
        api.oreDictToMaterialMapping.put("cropGrapefruit", CulinaryHub.CommonMaterials.GRAPEFRUIT);
        api.oreDictToMaterialMapping.put("cropLime", CulinaryHub.CommonMaterials.LIME);
        api.oreDictToMaterialMapping.put("cropBambooshoot", CulinaryHub.CommonMaterials.BAMBOO_SHOOT);
        api.oreDictToMaterialMapping.put("cropBellpepper", CulinaryHub.CommonMaterials.GREEN_PEPPER);
        api.oreDictToMaterialMapping.put("foodMushroom", CulinaryHub.CommonMaterials.MUSHROOM);
        api.oreDictToMaterialMapping.put("cropMushroom", CulinaryHub.CommonMaterials.MUSHROOM);
        api.oreDictToMaterialMapping.put("cropPumpkin", CulinaryHub.CommonMaterials.PUMPKIN);

        api.fluidToMaterialMapping.put(FluidRegistry.WATER.getName(), CulinaryHub.CommonMaterials.WATER);
        api.fluidToMaterialMapping.put(CuisineFluids.MILK.getName(), CulinaryHub.CommonMaterials.MILK);
        api.fluidToMaterialMapping.put(CuisineFluids.SOY_MILK.getName(), CulinaryHub.CommonMaterials.SOY_MILK);

        api.fluidToSpiceMapping.put(CuisineFluids.EDIBLE_OIL.getName(), CulinaryHub.CommonSpices.EDIBLE_OIL);
        api.fluidToSpiceMapping.put(CuisineFluids.SESAME_OIL.getName(), CulinaryHub.CommonSpices.SESAME_OIL);
        api.fluidToSpiceMapping.put(CuisineFluids.SOY_SAUCE.getName(), CulinaryHub.CommonSpices.SOY_SAUCE);
        api.fluidToSpiceMapping.put(CuisineFluids.RICE_VINEGAR.getName(), CulinaryHub.CommonSpices.RICE_VINEGAR);
        api.fluidToSpiceMapping.put(CuisineFluids.FRUIT_VINEGAR.getName(), CulinaryHub.CommonSpices.FRUIT_VINEGAR);
        api.fluidToSpiceMapping.put(FluidRegistry.WATER.getName(), CulinaryHub.CommonSpices.WATER);

        api.itemToSpiceMapping.put(ItemDefinition.of(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CHILI_POWDER)), CulinaryHub.CommonSpices.CHILI_POWDER);
        api.itemToSpiceMapping.put(ItemDefinition.of(CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SICHUAN_PEPPER_POWDER)), CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER);
        api.itemToSpiceMapping.put(ItemDefinition.of(Items.SUGAR), CulinaryHub.CommonSpices.SUGAR);

        api.oreDictToSpiceMapping.put("dustSalt", CulinaryHub.CommonSpices.SALT);
        api.oreDictToSpiceMapping.put("dustCrudesalt", CulinaryHub.CommonSpices.CRUDE_SALT);

    }

}
