package lv.id.bonne.vaulthunters.jewelpacktable;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.util.stream.Collectors;

import lv.id.bonne.vaulthunters.jewelpacktable.init.ModBlocks;
import lv.id.bonne.vaulthunters.jewelpacktable.network.ModNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(JewelPackTableMod.MODID)
public class JewelPackTableMod
{
    public JewelPackTableMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModNetwork.register();
    }


    public static ResourceLocation of(String text)
    {
        return new ResourceLocation(MODID, text);
    }

    /**
     * The mod id text.
     */
    public final static String MODID = "jewelpacktable";

    /**
     * The logger for mod
     */
    public static final Logger LOGGER = LogUtils.getLogger();
}
