package snownee.cuisine;

import java.util.Calendar;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import snownee.cuisine.command.CommandRegistry;
import snownee.cuisine.crafting.DrinkBrewingRecipe;
import snownee.cuisine.crafting.RecipeRegistry;
import snownee.cuisine.events.BetterHarvest;
import snownee.cuisine.events.OreDictHandler;
import snownee.cuisine.events.SpawnHandler;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.internal.capabilities.CulinarySkillCapability;
import snownee.cuisine.internal.capabilities.FoodContainerCapability;
import snownee.cuisine.items.BehaviorWokInteraction;
import snownee.cuisine.items.BehaviorArmDispense;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.network.CuisineGuiHandler;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.network.PacketNameFood;
import snownee.cuisine.network.PacketSkillLevelIncreased;
import snownee.cuisine.world.gen.WorldGenBamboo;
import snownee.cuisine.world.gen.WorldGenCitrusTrees;
import snownee.cuisine.world.gen.WorldGenGarden;
import snownee.kiwi.item.IVariant;
import snownee.kiwi.network.NetworkChannel;

@Mod(
        modid = Cuisine.MODID,
        name = Cuisine.NAME,
        version = "@VERSION_INJECT@",
        useMetadata = true,
        acceptedMinecraftVersions = "[1.12, 1.13)"
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

    public static boolean aprilFools;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        Calendar calendar = Calendar.getInstance();
        aprilFools = calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) == 1;
        CuisineInternalGateway.init();
        CulinarySkillCapability.init();
        FoodContainerCapability.init();
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
        if (CuisineConfig.GENERAL.betterHarvest)
        {
            MinecraftForge.EVENT_BUS.register(new BetterHarvest());
        }
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(CuisineRegistry.MATERIAL, new BehaviorArmDispense());
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
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SESAME), CuisineConfig.GENERAL.basicSeedsWeight);
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SOYBEAN), CuisineConfig.GENERAL.basicSeedsWeight);
            MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.PEANUT), CuisineConfig.GENERAL.basicSeedsWeight);
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
        BrewingRecipeRegistry.addRecipe(new DrinkBrewingRecipe());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        RecipeRegistry.postInit();
    }

    public static enum Materials implements IVariant<Void>
    {
        WOODEN_ARM,
        WOODEN_HANDLE,
        SALT,
        CRUDE_SALT,
        CHILI_POWDER,
        SICHUAN_PEPPER_POWDER,
        BAMBOO_CHARCOAL,
        UNREFINED_SUGAR;

        @Override
        public int getMeta()
        {
            return ordinal();
        }

        @Override
        public Void getValue()
        {
            return null;
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e)
    {
        CommandRegistry.registryCommands(e);
    }
}
