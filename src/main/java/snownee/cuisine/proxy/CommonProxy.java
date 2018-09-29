package snownee.cuisine.proxy;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.crafting.RecipeRegistry;
import snownee.cuisine.events.BetterHarvest;
import snownee.cuisine.events.OreDictHandler;
import snownee.cuisine.events.SpawnHandler;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.internal.capabilities.CulinarySkillCapability;
import snownee.cuisine.internal.capabilities.FoodContainerCapability;
import snownee.cuisine.inventory.ContainerNameFood;
import snownee.cuisine.items.BehaviorWokInteraction;
import snownee.cuisine.items.BehaviourArmDispense;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.network.PacketNameFood;
import snownee.cuisine.network.PacketSkillLevelIncreased;
import snownee.cuisine.tiles.TileWok;
import snownee.cuisine.world.gen.WorldGenBamboo;
import snownee.cuisine.world.gen.WorldGenGarden;
import snownee.kiwi.network.NetworkChannel;
import tschipp.carryon.common.handler.ListHandler;

public class CommonProxy implements IGuiHandler
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
        NetworkRegistry.INSTANCE.registerGuiHandler(Cuisine.getInstance(), this);

        // MinecraftForge.EVENT_BUS.register(new DropHandler());
        MinecraftForge.EVENT_BUS.register(new SpawnHandler());
        // if (!Loader.isModLoaded("harvestcraft") && !Loader.isModLoaded("reap") && !Loader.isModLoaded("Harvest"))
        // {
        MinecraftForge.EVENT_BUS.register(new BetterHarvest());
        // }
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
        if (CuisineConfig.GENERAL.cropsGenRate > 0)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenGarden());
        }
        MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenBamboo());
    }

    @OverridingMethodsMustInvokeSuper
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("carryon"))
        {
            ListHandler.FORBIDDEN_TILES.add(Cuisine.MODID + ":mortar");
            ListHandler.FORBIDDEN_TILES.add(Cuisine.MODID + ":mill");
        }
    }

    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        // No operation, animation does not exist on server
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
        case CuisineGUI.NAME_FOOD:
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileWok)
            {
                return new ContainerNameFood((TileWok) tile);
            }
            return null;
        default:
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
