package snownee.cuisine.internal;

import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.*;
import snownee.cuisine.api.prefab.MaterialBuilder;
import snownee.cuisine.api.prefab.SimpleEffectImpl;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.api.prefab.SimpleSpiceImpl;
import snownee.cuisine.crafting.DrinkBrewingRecipe;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.fluids.FluidJuice;
import snownee.cuisine.internal.effect.*;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.material.*;
import snownee.cuisine.internal.spice.SpiceChiliPowder;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.library.RarityManager;
import snownee.kiwi.util.OreUtil;
import snownee.kiwi.util.definition.ItemDefinition;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

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
        Material actualMaterial = materialRegistry.register(material.getID(), material);
        if (actualMaterial == material && actualMaterial.isUnderCategoryOf(MaterialCategory.FRUIT))
        {
            DrinkBrewingRecipe.add(material);
        }
        return actualMaterial;
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
            ingredient = ingredient.copy();
        }
        else
        {
            List<String> possibleOreEntries = OreUtil.getOreNames(item);
            for (String entry : possibleOreEntries)
            {
                if ((ingredient = this.oreDictIngredients.get(entry)) != null)
                {
                    ingredient = ingredient.copy();
                    break;
                }
            }
        }
        if (ingredient != null && RarityManager.getRarity(item).ordinal() > 0)
        {
            ingredient.addEffect(CulinaryHub.CommonEffects.RARE);
        }
        return ingredient;
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
            return material == null ? null : new Ingredient(material, Form.JUICE);
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
        api.register(new EffectCurePotions());
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
        api.register(new EffectHeatResistance());
        api.register(new EffectSustainedRelease());
        api.register(new EffectSpooky());
        api.register(new EffectRare());
        // api.register(new EffectHeatResistance()); // TODO

        // As mentioned above, Material registration will trigger class loading of the class
        // CulinaryHub.CommonEffects, so we don't have to explicitly trigger it here.

        // TODO (3TUSK): Assign each material a base heal amount and a base saturation
        // modifier.
        // 注：1 点饥饿相当于半个鸡腿！玩家的饥饿值上限是 20。
        // 注："隐藏的饥饿条"（食物饱和度）上限也是 20，但它是 float。
        // 注：参考原版 Wiki 决定数据。

        //api.register(new SimpleMaterialImpl("super", -8531, 0, 0F, MaterialCategory.values()))
        Arrays.asList(
                MaterialBuilder.of("peanut").rawColor(-8531).category(MaterialCategory.NUT).form(Form.MINCED, Form.PASTE).build(),
                MaterialBuilder.of("sesame").rawColor(-15000805).category(MaterialCategory.GRAIN).build(),
                MaterialBuilder.of("soybean").rawColor(-2048665).category(MaterialCategory.GRAIN).build(),
                new MaterialRice("rice"),
                new MaterialTomato("tomato"),
                new MaterialChili("chili"),
                MaterialBuilder.of("garlic").rawColor(-32).category(MaterialCategory.VEGETABLES).form(Form.DICED, Form.MINCED, Form.PASTE).effect(CulinaryHub.CommonEffects.DISPERSAL).build(),
                MaterialBuilder.of("ginger").rawColor(-1828).category(MaterialCategory.VEGETABLES).build(),
                MaterialBuilder.of("sichuan_pepper").rawColor(-8511203).category(MaterialCategory.UNKNOWN).build(),
                MaterialBuilder.of("scallion").rawColor(-12609717).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE).build(),
                MaterialBuilder.of("turnip").rawColor(-3557457).form(Form.ALL_FORMS_INCLUDING_JUICE).category(MaterialCategory.VEGETABLES).build(),
                MaterialBuilder.of("chinese_cabbage").rawColor(-1966111).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE).category(MaterialCategory.VEGETABLES).build(),
                MaterialBuilder.of("lettuce").rawColor(-14433485).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE, Form.JUICE).category(MaterialCategory.VEGETABLES).build(),
                MaterialBuilder.of("corn").rawColor(-3227867).saturation(2f).category(MaterialCategory.GRAIN).form(Form.MINCED, Form.JUICE).build(),
                MaterialBuilder.of("cucumber").rawColor(0xdddce7bd).category(MaterialCategory.VEGETABLES).form(Form.ALL_FORMS_INCLUDING_JUICE).build(),
                MaterialBuilder.of("green_pepper").rawColor(-15107820).category(MaterialCategory.VEGETABLES).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE).build(),
                MaterialBuilder.of("red_pepper").rawColor(-8581357).category(MaterialCategory.VEGETABLES).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE).build(),
                MaterialBuilder.of("leek").rawColor(-15100888).category(MaterialCategory.VEGETABLES).form(Form.CUBED, Form.MINCED, Form.PASTE).build(),
                MaterialBuilder.of("onion").rawColor(-17409).category(MaterialCategory.VEGETABLES).form(Form.ALL_FORMS_INCLUDING_JUICE).build(),
                MaterialBuilder.of("eggplant").rawColor(0xdcd295).category(MaterialCategory.VEGETABLES).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("spinach").rawColor(-15831787).category(MaterialCategory.VEGETABLES).form(Form.SLICED, Form.SHREDDED, Form.MINCED, Form.PASTE, Form.JUICE).effect(CulinaryHub.CommonEffects.POWER).build(),
                new MaterialTofu("tofu"),
                new MaterialChorusFruit("chorus_fruit"),
                new MaterialApple("apple"),
                MaterialBuilder.of("golden_apple").rawColor(-1782472).category(MaterialCategory.FRUIT).form(Form.ALL_FORMS_INCLUDING_JUICE).category(MaterialCategory.SUPERNATURAL).saturation(0.3f).effect(CulinaryHub.CommonEffects.GOLDEN_APPLE).build(),
                new MaterialWithEffect("golden_apple_enchanted", CulinaryHub.CommonEffects.GOLDEN_APPLE_ENCHANTED, -1782472, 0, 1, 1, 1, 0.3F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL)
                {
                    @Override
                    public boolean hasGlowingOverlay(Ingredient ingredient)
                    {
                        return true;
                    }
                }.setValidForms(Form.ALL_FORMS_INCLUDING_JUICE),
                MaterialBuilder.of("melon").rawColor(-769226).category(MaterialCategory.FRUIT).form(Form.CUBED, Form.SLICED, Form.DICED, Form.MINCED, Form.PASTE, Form.JUICE).build(),
                new MaterialPumpkin("pumpkin"),
                MaterialBuilder.of("carrot").rawColor(-1538531).saturation(0.1f).category(MaterialCategory.VEGETABLES).effect(CulinaryHub.CommonEffects.NIGHT_VISION).form(Form.ALL_FORMS_INCLUDING_JUICE).build(),
                MaterialBuilder.of("golden_carrot").rawColor(0xdba213).category(MaterialCategory.VEGETABLES).effect(CulinaryHub.CommonEffects.LONGER_NIGHT_VISION).form(Form.ALL_FORMS_INCLUDING_JUICE).build(),
                MaterialBuilder.of("potato").rawColor(-3764682).heatValue(2).saturation(2f).category(MaterialCategory.GRAIN).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("beetroot").rawColor(-8442327).category(MaterialCategory.VEGETABLES).form(Form.ALL_FORMS_INCLUDING_JUICE).build(),
                MaterialBuilder.of("mushroom").rawColor(-10006976).category(MaterialCategory.VEGETABLES).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("egg").rawColor(-3491187).saturation(0.2f).category(MaterialCategory.PROTEIN).build(),
                MaterialBuilder.of("chicken").rawColor(-929599).category(MaterialCategory.MEAT).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("beef").rawColor(-3392460).category(MaterialCategory.MEAT).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("pork").rawColor(-2133904).category(MaterialCategory.MEAT).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("mutton").rawColor(-3917262).saturation(0f).category(MaterialCategory.MEAT).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("rabbit").rawColor(-4882580).saturation(0.1f).category(MaterialCategory.MEAT).form(Form.ALL_FORMS).effect(CulinaryHub.CommonEffects.JUMP_BOOST).build(),
                MaterialBuilder.of("fish").rawColor(-10583426).category(MaterialCategory.FISH).form(Form.ALL_FORMS).build(),
                new MaterialPufferfish("pufferfish"),
                MaterialBuilder.of("pickled").rawColor(-13784).saturation(0.3f).effect(CulinaryHub.CommonEffects.ALWAYS_EDIBLE).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("bamboo_shoot").rawColor(0xf9ecdd).effect(CulinaryHub.CommonEffects.ALWAYS_EDIBLE).form(Form.ALL_FORMS).build(),
                MaterialBuilder.of("cactus").rawColor(0xa9bc98).saturation(-0.1f).effect(CulinaryHub.CommonEffects.HEAT_RESISTANCE).form(Form.CUBED, Form.DICED, Form.JUICE).build(),
                MaterialBuilder.of("water").rawColor(0x55DDDDFF).boilHeat(100).saturation(-0.1f).form(Form.JUICE).build(),
                MaterialBuilder.of("milk").rawColor(0xCCFFFFFF).boilHeat(100).saturation(-0.1f).category(MaterialCategory.PROTEIN).form(Form.JUICE).build(),
                MaterialBuilder.of("soy_milk").rawColor(-15831787).saturation(-0.1f).category(MaterialCategory.PROTEIN).form(Form.JUICE).build(),
                MaterialBuilder.of("mandarin").rawColor(0xf08a19).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("citron").rawColor(0xddcc58).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("pomelo").rawColor(0xf7f67e).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("orange").rawColor(0xf08a19).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("lemon").rawColor(0xebca4b).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("grapefruit").rawColor(0xf4502b).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                MaterialBuilder.of("lime").rawColor(0xcada76).saturation(-0.1f).category(MaterialCategory.FRUIT).form(Form.JUICE).build(),
                new SimpleMaterialImpl("empowered_citron", 0xE6B701, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL)
                {
                    @Override
                    public boolean hasGlowingOverlay(Ingredient ingredient)
                    {
                        return true;
                    }
                }.setValidForms(Form.JUICE_ONLY)
        ).forEach(api::register);

        api.register(new SimpleSpiceImpl("edible_oil", 0x99D1A71A, true, Collections.singleton("oil")));
        api.register(new SimpleSpiceImpl("sesame_oil", 0x99CE8600, true, Collections.singleton("oil")));
        api.register(new SimpleSpiceImpl("soy_sauce", 0xDD100000, true, Collections.singleton("sauce")));
        api.register(new SimpleSpiceImpl("rice_vinegar", 0xCC100000, true, Collections.singleton("vinegar")));
        api.register(new SimpleSpiceImpl("fruit_vinegar", 0xBB100000, true, Collections.singleton("vinegar")));
        api.register(new SimpleSpiceImpl("water", 0x55FFFFFF, true, Collections.singleton("water")));
        api.register(new SpiceChiliPowder("chili_powder", 11546150));
        api.register(new SimpleSpiceImpl("sichuan_pepper_powder", 8606770, false, Collections.singleton("spicy")));
        api.register(new SimpleSpiceImpl("crude_salt", 4673362, false, ImmutableSet.of("salt", "unrefined")));
        api.register(new SimpleSpiceImpl("salt", 0xE3E3E3, false, Collections.singleton("salt")));
        api.register(new SimpleSpiceImpl("sugar", 16383998, false, Collections.singleton("sugar")));
        api.register(new SimpleSpiceImpl("unrefined_sugar", 0xB35400, false, ImmutableSet.of("sugar", "unrefined")));

        CulinaryHub.CommonSkills.init();
    }

    /**
     * Internal hook for initialization of basic items/fluids -> ingredients/seasoning mapping.
     * Not intended for public usage.
     */
    public static void deferredInit()
    {
        CuisineInternalGateway api = CuisineInternalGateway.INSTANCE;
        api.itemIngredients.put(ItemDefinition.of(CuisineRegistry.CROPS, ItemCrops.Variant.RED_PEPPER.getMeta()), new Ingredient(CulinaryHub.CommonMaterials.RED_PEPPER, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variant.EMPOWERED_CITRON.getMeta()), new Ingredient(CulinaryHub.CommonMaterials.EMPOWERED_CITRON, Form.FULL));

        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_APPLE), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_APPLE, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_APPLE, 1), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_APPLE_ENCHANTED, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.MELON), new Ingredient(CulinaryHub.CommonMaterials.MELON, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.CARROT), new Ingredient(CulinaryHub.CommonMaterials.CARROT, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.GOLDEN_CARROT), new Ingredient(CulinaryHub.CommonMaterials.GOLDEN_CARROT, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.POTATO), new Ingredient(CulinaryHub.CommonMaterials.POTATO, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.BEETROOT), new Ingredient(CulinaryHub.CommonMaterials.BEETROOT, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH), new Ingredient(CulinaryHub.CommonMaterials.FISH, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH, 1), new Ingredient(CulinaryHub.CommonMaterials.FISH, Form.FULL));
        api.itemIngredients.put(ItemDefinition.of(Items.FISH, 3), new Ingredient(CulinaryHub.CommonMaterials.PUFFERFISH, Form.FULL));

        api.oreDictIngredients.put("cropPeanut", new Ingredient(CulinaryHub.CommonMaterials.PEANUT, Form.FULL));
        api.oreDictIngredients.put("cropSesame", new Ingredient(CulinaryHub.CommonMaterials.SESAME, Form.FULL));
        api.oreDictIngredients.put("cropSoybean", new Ingredient(CulinaryHub.CommonMaterials.SOYBEAN, Form.FULL));
        api.oreDictIngredients.put("cropTomato", new Ingredient(CulinaryHub.CommonMaterials.TOMATO, Form.FULL));
        api.oreDictIngredients.put("cropChilipepper", new Ingredient(CulinaryHub.CommonMaterials.CHILI, Form.FULL));
        api.oreDictIngredients.put("foodRice", new Ingredient(CulinaryHub.CommonMaterials.RICE, Form.FULL));
        api.oreDictIngredients.put("cropGarlic", new Ingredient(CulinaryHub.CommonMaterials.GARLIC, Form.FULL));
        api.oreDictIngredients.put("cropGinger", new Ingredient(CulinaryHub.CommonMaterials.GINGER, Form.FULL));
        api.oreDictIngredients.put("cropSichuanpepper", new Ingredient(CulinaryHub.CommonMaterials.SICHUAN_PEPPER, Form.FULL));
        api.oreDictIngredients.put("cropScallion", new Ingredient(CulinaryHub.CommonMaterials.SCALLION, Form.FULL));
        api.oreDictIngredients.put("cropTurnip", new Ingredient(CulinaryHub.CommonMaterials.TURNIP, Form.FULL));
        api.oreDictIngredients.put("cropCabbage", new Ingredient(CulinaryHub.CommonMaterials.CHINESE_CABBAGE, Form.FULL));
        api.oreDictIngredients.put("cropLettuce", new Ingredient(CulinaryHub.CommonMaterials.LETTUCE, Form.FULL));
        api.oreDictIngredients.put("cropCorn", new Ingredient(CulinaryHub.CommonMaterials.CORN, Form.FULL));
        api.oreDictIngredients.put("cropCucumber", new Ingredient(CulinaryHub.CommonMaterials.CUCUMBER, Form.FULL));
        api.oreDictIngredients.put("cropLeek", new Ingredient(CulinaryHub.CommonMaterials.LEEK, Form.FULL));
        api.oreDictIngredients.put("cropOnion", new Ingredient(CulinaryHub.CommonMaterials.ONION, Form.FULL));
        api.oreDictIngredients.put("cropEggplant", new Ingredient(CulinaryHub.CommonMaterials.EGGPLANT, Form.FULL));
        api.oreDictIngredients.put("cropSpinach", new Ingredient(CulinaryHub.CommonMaterials.SPINACH, Form.FULL));
        api.oreDictIngredients.put("foodFirmtofu", new Ingredient(CulinaryHub.CommonMaterials.TOFU, Form.FULL));
        api.oreDictIngredients.put("cropChorusfruit", new Ingredient(CulinaryHub.CommonMaterials.CHORUS_FRUIT, Form.FULL));
        api.oreDictIngredients.put("cropApple", new Ingredient(CulinaryHub.CommonMaterials.APPLE, Form.FULL));
        api.oreDictIngredients.put("egg", new Ingredient(CulinaryHub.CommonMaterials.EGG, Form.FULL));
        api.oreDictIngredients.put("listAllporkraw", new Ingredient(CulinaryHub.CommonMaterials.PORK, Form.FULL));
        api.oreDictIngredients.put("listAllmuttonraw", new Ingredient(CulinaryHub.CommonMaterials.MUTTON, Form.FULL));
        api.oreDictIngredients.put("listAllbeefraw", new Ingredient(CulinaryHub.CommonMaterials.BEEF, Form.FULL));
        api.oreDictIngredients.put("listAllchickenraw", new Ingredient(CulinaryHub.CommonMaterials.CHICKEN, Form.FULL));
        api.oreDictIngredients.put("listAllrabbitraw", new Ingredient(CulinaryHub.CommonMaterials.RABBIT, Form.FULL));
        api.oreDictIngredients.put("blockCactus", new Ingredient(CulinaryHub.CommonMaterials.CACTUS, Form.FULL));
        api.oreDictIngredients.put("foodPickles", new Ingredient(CulinaryHub.CommonMaterials.PICKLED, Form.FULL));
        api.oreDictIngredients.put("cropMandarin", new Ingredient(CulinaryHub.CommonMaterials.MANDARIN, Form.FULL));
        api.oreDictIngredients.put("cropCitron", new Ingredient(CulinaryHub.CommonMaterials.CITRON, Form.FULL));
        api.oreDictIngredients.put("cropPomelo", new Ingredient(CulinaryHub.CommonMaterials.POMELO, Form.FULL));
        api.oreDictIngredients.put("cropOrange", new Ingredient(CulinaryHub.CommonMaterials.ORANGE, Form.FULL));
        api.oreDictIngredients.put("cropLemon", new Ingredient(CulinaryHub.CommonMaterials.LEMON, Form.FULL));
        api.oreDictIngredients.put("cropGrapefruit", new Ingredient(CulinaryHub.CommonMaterials.GRAPEFRUIT, Form.FULL));
        api.oreDictIngredients.put("cropLime", new Ingredient(CulinaryHub.CommonMaterials.LIME, Form.FULL));
        api.oreDictIngredients.put("cropBambooshoot", new Ingredient(CulinaryHub.CommonMaterials.BAMBOO_SHOOT, Form.FULL));
        api.oreDictIngredients.put("cropBellpepper", new Ingredient(CulinaryHub.CommonMaterials.GREEN_PEPPER, Form.FULL));
        api.oreDictIngredients.put("foodMushroom", new Ingredient(CulinaryHub.CommonMaterials.MUSHROOM, Form.FULL));
        api.oreDictIngredients.put("cropMushroom", new Ingredient(CulinaryHub.CommonMaterials.MUSHROOM, Form.FULL));
        api.oreDictIngredients.put("cropPumpkin", new Ingredient(CulinaryHub.CommonMaterials.PUMPKIN, Form.FULL));

        api.fluidIngredients.put(FluidRegistry.WATER.getName(), new Ingredient(CulinaryHub.CommonMaterials.WATER, Form.JUICE));
        api.fluidIngredients.put(CuisineFluids.MILK.getName(), new Ingredient(CulinaryHub.CommonMaterials.MILK, Form.JUICE));
        api.fluidIngredients.put(CuisineFluids.SOY_MILK.getName(), new Ingredient(CulinaryHub.CommonMaterials.SOY_MILK, Form.JUICE));

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

    @Override
    public Potion getEffectResistancePotion()
    {
        return CuisineRegistry.EFFECT_RESISTANCE;
    }

    @Override
    public FluidStack makeJuiceFluid(Material material, int amount)
    {
        return FluidJuice.make(material, amount);
    }

}
