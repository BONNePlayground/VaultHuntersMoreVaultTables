package lv.id.bonne.vaulthunters.morevaulttables;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import lv.id.bonne.vaulthunters.morevaulttables.config.Configuration;
import lv.id.bonne.vaulthunters.morevaulttables.network.MoreVaultTablesNetwork;
import lv.id.bonne.vaulthunters.morevaulttables.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(MoreVaultTablesMod.MODID)
public class MoreVaultTablesMod
{
    public MoreVaultTablesMod()
    {
        MoreVaultTablesMod.CONFIGURATION = new Configuration();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
            Configuration.GENERAL_SPEC,
            "more_vault_tables_config.toml");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MoreVaultTablesNetwork.register();

        MoreVaultTablesItemRegistry.REGISTRY.register(modEventBus);
        MoreVaultTablesBlockRegistry.REGISTRY.register(modEventBus);
        MoreVaultTablesTileEntityRegistry.REGISTRY.register(modEventBus);
        MoreVaultTablesContainersRegistry.REGISTRY.register(modEventBus);
        MoreVaultTablesSoundRegistry.REGISTRY.register(modEventBus);
    }


    public static ResourceLocation of(String text)
    {
        return new ResourceLocation(MODID, text);
    }

    /**
     * The mod id text.
     */
    public final static String MODID = "morevaulttables";

    /**
     * The logger for mod
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The configuration file for the mod.
     */
    public static Configuration CONFIGURATION;
}
