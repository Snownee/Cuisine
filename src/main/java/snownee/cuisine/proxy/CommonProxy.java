package snownee.cuisine.proxy;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
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

public class CommonProxy
{
    @OverridingMethodsMustInvokeSuper
    public void preInit(FMLPreInitializationEvent event)
    {
        CuisineInternalGateway.init();
        CulinarySkillCapability.init();
        FoodContainerCapability.init();
        RecipeRegistry.preInit();
    }

    @OverridingMethodsMustInvokeSuper
    public void init(FMLInitializationEvent event)
    {
        CuisineInternalGateway.deferredInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(Cuisine.getInstance(), new CuisineGuiHandler());

        // MinecraftForge.EVENT_BUS.register(new DropHandler());
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
        MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.RICE), 5);
        MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SESAME), 5);
        MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SOYBEAN), 5);
        MinecraftForge.addGrassSeed(CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.PEANUT), 5);
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

    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        // No operation, animation does not exist on server
        return null;
    }

}
