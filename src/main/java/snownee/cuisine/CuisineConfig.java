package snownee.cuisine;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Cuisine.MODID, name = Cuisine.MODID, category = "")
@Config.LangKey("cuisine.config")
@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class CuisineConfig
{

    private CuisineConfig()
    {
        // No-op, no instance for you
    }

    @SubscribeEvent
    public static void onConfigReload(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Cuisine.MODID))
        {
            ConfigManager.sync(Cuisine.MODID, Config.Type.INSTANCE);
        }
    }

    @Config.Comment("General settings of Cuisine.")
    @Config.LangKey("cuisine.config.general")
    @Config.Name("General")
    public static final General GENERAL = new General();

    public static final class General
    {
        General()
        {
            // No-op. Package-level access.
        }

        @Config.Comment("Give new player manual")
        @Config.LangKey("cuisine.config.general.spawn_book")
        @Config.Name("SpawnBook")
        @Config.RequiresMcRestart
        public boolean spawnBook = true;

        @Config.Comment("Larger = Less possible to gen, 0 = Do not gen")
        @Config.LangKey("cuisine.config.general.bamboos_gen_rate")
        @Config.Name("BamboosGenRate")
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int bamboosGenRate = 35;

        @Config.Comment("Use bamboo as a blowpipe, shoot seeds")
        @Config.LangKey("cuisine.config.general.bamboo_blowpipe")
        @Config.Name("BambooBlowpipe")
        @Config.RequiresMcRestart
        public boolean bambooBlowpipe = false;

        @Config.Comment("Larger = Less possible to gen, 0 = Do not gen")
        @Config.LangKey("cuisine.config.general.crops_gen_rate")
        @Config.Name("CropsGenRate")
        @Config.RangeInt(min = 0)
        @Config.RequiresMcRestart
        public int cropsGenRate = 4;

        @Config.Comment("Length of one mill working cycle, measured in ticks. Ideally, 1 second is 20 ticks.")
        @Config.LangKey("cuisine.config.general.mill_work_cycle")
        @Config.Name("MillWorkCycle")
        @Config.RangeInt(min = 1)
        public int millWorkPeriod = 160;
    }

    @Config.Comment("Progression features of Cuisine.")
    @Config.LangKey("cuisine.config.progression")
    @Config.Name("Progression")
    public static final Progression PROGRESSION = new Progression();

    public static final class Progression
    {
        Progression()
        {
            // No-op. Package-level access.
        }

        @Config.Comment("Allow chopping board to chop woods (for pack devs)")
        @Config.LangKey("cuisine.config.progression.axe_chopping")
        @Config.Name("AxeChopping")
        public boolean axeChopping = true;

        @Config.Comment("Axes show in JEI recipes. This doesn't affect actual behavior")
        @Config.LangKey("cuisine.config.progression.axe_list")
        @Config.Name("AxeList")
        @Config.RequiresMcRestart
        public String[] axeList = new String[] { "minecraft:wooden_axe", "minecraft:stone_axe", "minecraft:iron_axe", "minecraft:golden_axe", "minecraft:diamond_axe" };
    }

    @Config.Comment("Config options related to hardcore mode.")
    @Config.LangKey("cuisine.config.hardcore")
    @Config.Name("Hardcore")
    public static final Hardcore HARDCORE = new Hardcore();

    public static final class Hardcore
    {
        Hardcore()
        {
            // No-op, package-level access.
        }

        @Config.Comment("If true, hardcore mode will be turned on.")
        @Config.LangKey("cuisine.config.hardcore.enable")
        @Config.Name("Enable")
        @Config.RequiresMcRestart
        public boolean enable = true;

        @Config.Comment("If true, bread will require mill to make.")
        @Config.LangKey("cuisine.config.hardcore.bread")
        @Config.Name("HardcoreBread")
        @Config.RequiresMcRestart
        public boolean harderBreadProduction = true;

        @Config.Comment("If true, cookie will require furnace to make.")
        @Config.LangKey("cuisine.config.hardcore.cookie")
        @Config.Name("HardcoreCookie")
        @Config.RequiresMcRestart
        public boolean harderCookieProduction = true;

        @Config.Comment("If true, sugar will require mill to make.")
        @Config.LangKey("cuisine.config.hardcore.sugar")
        @Config.Name("HardcoreSugar")
        @Config.RequiresMcRestart
        public boolean harderSugarProduction = true;

        @Config.Comment("If true, certain foods will give you fewer heal amount and saturation.")
        @Config.LangKey("cuisine.config.hardcore.food_level_lose")
        @Config.Name("HardcoreFoodLevel")
        @Config.RequiresMcRestart
        public boolean lowerFoodLevel = true;

        @Config.Comment(
            "If HardcoreFoodLevel is enabled, this will determine how much heal amount and saturation you can still get."
        )
        @Config.LangKey("cuisine.config.hardcore.food_level_retain_ratio")
        @Config.Name("FoodLevelRetainRatio")
        public double foodLevelRetainRatio = 0.5;

        @Config.Comment("If HardcoreFoodLevel is enabled, food item that is listed here will NOT be affected.")
        @Config.LangKey("cuisine.config.hardcore.food_level_lose_blacklist")
        @Config.Name("FoodLevelDowngradeBlacklist")
        public String[] lowerFoodLevelBlacklist = new String[0];

        @Config.Comment("If true, player will lose culinary skill points when they died.")
        @Config.LangKey("cuisine.config.general.skill_lose_on_death")
        @Config.Name("HardcoreCulinarySkill")
        public boolean loseSkillPointsOnDeath = false;

        @Config.Comment(
            { "If Culinary Skill Downgrade on Death is enabled, this will determines how many point are kept.", "For example, 0.5 means 50% are kept." }
        )
        @Config.LangKey("cuisine.config.general.skill_retain_ratio")
        @Config.Name("CulinarySkillRetainRatio")
        @Config.RangeDouble(min = 0, max = 1)
        public double skillPointsRetainRatio = 1.0;

    }

}
