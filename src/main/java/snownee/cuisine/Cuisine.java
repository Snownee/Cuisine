package snownee.cuisine;

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
import snownee.cuisine.proxy.CommonProxy;
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

    @SidedProxy(serverSide = "snownee.cuisine.proxy.CommonProxy", clientSide = "snownee.cuisine.proxy.ClientProxy")
    public static CommonProxy proxy;

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
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
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
