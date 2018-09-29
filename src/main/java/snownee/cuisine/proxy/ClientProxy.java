package snownee.cuisine.proxy;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.gui.GuiManual;
import snownee.cuisine.client.gui.GuiNameFood;
import snownee.cuisine.client.particle.CuisineParticles;
import snownee.cuisine.client.renderer.CuisineTEISR;
import snownee.cuisine.tiles.TileWok;

public class ClientProxy extends CommonProxy
{
    public static final ResourceLocation EMPTY = new ResourceLocation(Cuisine.MODID, "empty");

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        // if (!CuisineConfig.GENERAL.disableEssence)
        // {
        MinecraftForge.EVENT_BUS.register(new CuisineParticles());
        // }
        OBJLoader.INSTANCE.addDomain(Cuisine.MODID);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        CuisineRegistry.ITEM_CHOPPING_BOARD.setTileEntityItemStackRenderer(CuisineTEISR.INSTANCE);
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return ModelLoaderRegistry.loadASM(identifier, parameters);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
        case CuisineGUI.MANUAL:
            ItemStack stack = player.inventory.getStackInSlot(x);
            return new GuiManual(x, stack);
        case CuisineGUI.NAME_FOOD:
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileWok)
            {
                return new GuiNameFood((TileWok) tile);
            }
            return null;
        default:
            return null;
        }
    }
}
