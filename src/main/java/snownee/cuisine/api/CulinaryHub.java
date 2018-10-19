package snownee.cuisine.api;

import snownee.cuisine.api.prefab.SimpleCulinarySkillImpl;

/**
 * The core class of Cuisine API that holds the main API interface reference.
 *
 * Cuisine API 的核心类，持有主 API 接口的引用。
 */
public final class CulinaryHub
{
    /**
     * The singleton reference of {@link CuisineAPI}, which will be available
     * after FML pre-initialization stage.
     */
    public static CuisineAPI API_INSTANCE;

    public static final class CommonMaterials
    {
        //        public static final Material SUPER_MATERIAL = find("super");
        public static final Material PEANUT = find("peanut");
        public static final Material SESAME = find("sesame");
        public static final Material SOYBEAN = find("soybean");
        public static final Material RICE = find("rice");
        public static final Material TOMATO = find("tomato");
        public static final Material CHILI = find("chili");
        public static final Material GARLIC = find("garlic");
        public static final Material GINGER = find("ginger");
        public static final Material SICHUAN_PEPPER = find("sichuan_pepper");
        public static final Material SCALLION = find("scallion");
        public static final Material TURNIP = find("turnip");
        public static final Material CHINESE_CABBAGE = find("chinese_cabbage");
        public static final Material LETTUCE = find("lettuce");
        public static final Material CORN = find("corn");
        public static final Material CUCUMBER = find("cucumber");
        public static final Material GREEN_PEPPER = find("green_pepper");
        public static final Material RED_PEPPER = find("red_pepper");
        public static final Material LEEK = find("leek");
        public static final Material ONION = find("onion");
        public static final Material EGGPLANT = find("eggplant");
        public static final Material SPINACH = find("spinach");
        public static final Material TOFU = find("tofu");
        public static final Material CHORUS_FRUIT = find("chorus_fruit");
        public static final Material APPLE = find("apple");
        public static final Material GOLDEN_APPLE = find("golden_apple");
        public static final Material GOLDEN_APPLE_ENCHANTED = find("golden_apple_enchanted");
        public static final Material MELON = find("melon");
        public static final Material PUMPKIN = find("pumpkin");
        public static final Material CARROT = find("carrot");
        public static final Material POTATO = find("potato");
        public static final Material BEETROOT = find("beetroot");
        public static final Material MUSHROOM = find("mushroom");
        public static final Material EGG = find("egg");
        public static final Material CHICKEN = find("chicken");
        public static final Material BEEF = find("beef");
        public static final Material PORK = find("pork");
        public static final Material MUTTON = find("mutton");
        public static final Material RABBIT = find("rabbit");
        public static final Material FISH = find("fish");
        public static final Material PICKLED = find("pickled");
        public static final Material BAMBOO_SHOOT = find("bamboo_shoot");
        public static final Material PUFFERFISH = find("pufferfish");
        public static final Material CACTUS = find("cactus");
        public static final Material WATER = find("water");
        public static final Material MILK = find("milk");
        public static final Material SOY_MILK = find("soymilk");
        public static final Material MANDARIN = find("mandarin");
        public static final Material CITRON = find("citron");
        public static final Material POMELO = find("pomelo");
        public static final Material ORANGE = find("orange");
        public static final Material LEMON = find("lemon");
        public static final Material GRAPEFRUIT = find("grapefruit");
        public static final Material LIME = find("lime");
        public static final Material EMPOWERED_CITRON = find("empowered_citron");

        private static Material find(final String uniqueName)
        {
            return CulinaryHub.API_INSTANCE.findMaterial(uniqueName);
        }

        private CommonMaterials()
        {
        }
    }

    public static final class CommonSpices
    {
        public static final Spice EDIBLE_OIL = find("edible_oil");
        public static final Spice SESAME_OIL = find("sesame_oil");
        public static final Spice SOY_SAUCE = find("soy_sauce");
        public static final Spice RICE_VINEGAR = find("rice_vinegar");
        public static final Spice FRUIT_VINEGAR = find("fruit_vinegar");
        public static final Spice WATER = find("water");
        public static final Spice CHILI_POWDER = find("chili_powder");
        public static final Spice SICHUAN_PEPPER_POWDER = find("sichuan_pepper_powder");
        public static final Spice CRUDE_SALT = find("crude_salt");
        public static final Spice SALT = find("salt");
        public static final Spice SUGAR = find("sugar");

        private static Spice find(final String uniqueName)
        {
            return CulinaryHub.API_INSTANCE.findSpice(uniqueName);
        }

        private CommonSpices()
        {
        }

        public static void init()
        {
            // No-op for now, only triggering classloading
        }
    }

    public static final class CommonEffects
    {
        public static final Effect EXPERIENCED = find("experienced");
        public static final Effect GOLDEN_APPLE = find("golden_apple");
        public static final Effect GOLDEN_APPLE_ENCHANTED = find("golden_apple_enchanted");
        public static final Effect FLAVOR_ENHANCER = find("flavor_enhancer");
        public static final Effect HARMONY = find("harmony");
        public static final Effect ALWAYS_EDIBLE = find("always_edible");
        public static final Effect JUMP_BOOST = find("jump_boost");
        public static final Effect POWER = find("power");
        public static final Effect NIGHT_VISION = find("night_vision");
        public static final Effect HOT = find("hot");
        public static final Effect DISPERSAL = find("dispersal");
        public static final Effect TELEPORT = find("teleport");
        public static final Effect PUFFERFISH_POISON = find("pufferfish_poison");
        public static final Effect WATER_BREATHING = find("water_breathing");
        public static final Effect CURE_POTIONS = find("cure_potions");
        public static final Effect HEAT_RESISTANCE = find("heat_resistance");
        public static final Effect COLD_RESISTANCE = find("cold_resistance");

        private static Effect find(final String uniqueName)
        {
            return CulinaryHub.API_INSTANCE.findEffect(uniqueName);
        }
    }

    public static final class CommonSkills
    {
        public static final CulinarySkill DOUBLE_CHOPPING = CulinarySkillManager.register(new SimpleCulinarySkillImpl("double_chopping", CulinarySkillPoint.PROFICIENCY, 1));
        public static final CulinarySkill SKILLED_CHOPPING = CulinarySkillManager.register(new SimpleCulinarySkillImpl("skilled_chopping", CulinarySkillPoint.EXPERTISE, 1));
        public static final CulinarySkill FEWER_LOSSES = CulinarySkillManager.register(new SimpleCulinarySkillImpl("fewer_losses", CulinarySkillPoint.PROFICIENCY, 2));
        public static final CulinarySkill BIGGER_SIZE = CulinarySkillManager.register(new SimpleCulinarySkillImpl("bigger_size", CulinarySkillPoint.EXPERTISE, 2));

        public static void init()
        {
            // No-op, only trigger class-loading
        }
    }

}
