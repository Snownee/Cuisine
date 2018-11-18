package snownee.cuisine;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import snownee.cuisine.command.CommandRegistry;
import snownee.cuisine.crafting.RecipeRegistry;
import snownee.cuisine.events.BetterHarvest;
import snownee.cuisine.events.OreDictHandler;
import snownee.cuisine.events.SpawnHandler;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.internal.capabilities.CulinarySkillCapability;
import snownee.cuisine.internal.capabilities.FoodContainerCapability;
import snownee.cuisine.items.BehaviorWokInteraction;
import snownee.cuisine.items.BehaviourArmDispense;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.network.CuisineGuiHandler;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.network.PacketNameFood;
import snownee.cuisine.network.PacketSkillLevelIncreased;
import snownee.cuisine.world.gen.WorldGenBamboo;
import snownee.cuisine.world.gen.WorldGenCitrusTrees;
import snownee.cuisine.world.gen.WorldGenGarden;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.util.VariantsHolder;

@Mod(
        modid = Cuisine.MODID,
        name = Cuisine.NAME,
        version = "@VERSION_INJECT@",
        useMetadata = true,
        acceptedMinecraftVersions = "[1.12, 1.13)",
        guiFactory = "snownee.cuisine.client.CuisineConfigGUI",
        dependencies = "required-after:kiwi@[0.3, 0.4);"
)
public class Cuisine
{
    public static final String MODID = "cuisine";
    public static final String NAME = "Cuisine";
    public static final CreativeTabs CREATIVE_TAB = new CuisineItemGroup();

    public static Logger logger;

    private static final Cuisine INSTANCE = new Cuisine();

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @SidedProxy(serverSide = "snownee.cuisine.server.CuisineServerProxy", clientSide = "snownee.cuisine.client.CuisineClientProxy")
    public static CuisineSidedProxy sidedDelegate;

    @Mod.InstanceFactory
    public static Cuisine getInstance()
    {
        return INSTANCE;
    }

    private Cuisine()
    {
        // No-op, private access only
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        CuisineInternalGateway.init();
        CulinarySkillCapability.init();
        FoodContainerCapability.init();
        RecipeRegistry.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        CuisineInternalGateway.deferredInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new CuisineGuiHandler());

        if (CuisineConfig.GENERAL.spawnBook)
        {
            MinecraftForge.EVENT_BUS.register(new SpawnHandler());
        }
        MinecraftForge.EVENT_BUS.register(new BetterHarvest());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(CuisineRegistry.MATERIAL, new BehaviourArmDispense());
        BehaviorWokInteraction behaviorWokInteraction = new BehaviorWokInteraction();
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(CuisineRegistry.PLACED_DISH), behaviorWokInteraction);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(CuisineRegistry.SPICE_BOTTLE, behaviorWokInteraction);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(CuisineRegistry.IRON_SPATULA, behaviorWokInteraction);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(CuisineRegistry.INGREDIENT, behaviorWokInteraction);
        OreDictHandler.init();
        RecipeRegistry.init();
        NetworkChannel.INSTANCE.register(PacketCustomEvent.class);
        NetworkChannel.INSTANCE.register(PacketSkillLevelIncreased.class);
        NetworkChannel.INSTANCE.register(PacketNameFood.class);
        if (CuisineConfig.GENERAL.basicSeedsWeight > 0)
        {
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.RICE), CuisineConfig.GENERAL.basicSeedsWeight);
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SESAME), CuisineConfig.GENERAL.basicSeedsWeight);
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SOYBEAN), CuisineConfig.GENERAL.basicSeedsWeight);
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.PEANUT), CuisineConfig.GENERAL.basicSeedsWeight);
        }
        if (CuisineConfig.WORLD_GEN.cropsGenRate > 0)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenGarden());
        }
        if (CuisineConfig.WORLD_GEN.bamboosGenRate > 0)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenBamboo());
        }
        if (CuisineConfig.WORLD_GEN.fruitTreesGenRate > 0)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenCitrusTrees());
        }
    }

    public static class Materials extends VariantsHolder<IStringSerializable>
    {
        static final Materials INSTANCE = new Materials();

        public static final Variant WOODEN_ARM = INSTANCE.addVariant(new Type("wooden_arm"));
        public static final Variant WOODEN_HANDLE = INSTANCE.addVariant(new Type("wooden_handle"));
        public static final Variant SALT = INSTANCE.addVariant(new Type("salt"));
        public static final Variant CRUDE_SALT = INSTANCE.addVariant(new Type("crude_salt"));
        public static final Variant CHILI_POWDER = INSTANCE.addVariant(new Type("chili_powder"));
        public static final Variant SICHUAN_PEPPER_POWDER = INSTANCE.addVariant(new Type("sichuan_pepper_powder"));
        public static final Variant BAMBOO_CHARCOAL = INSTANCE.addVariant(new Type("bamboo_charcoal"));
        public static final Variant UNREFINED_SUGAR = INSTANCE.addVariant(new Type("unrefined_sugar"));
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        CommandRegistry.registryCommands(e);
    }
}
