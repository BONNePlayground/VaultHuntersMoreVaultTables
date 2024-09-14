package lv.id.bonne.vaulthunters.morevaulttables.init;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.eventbus.api.EventPriority.*;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ClientSetupEvents {
  @SubscribeEvent(priority = LOW)
  public static void setupClient(FMLClientSetupEvent event) {
    ModScreens.register();
  }
}