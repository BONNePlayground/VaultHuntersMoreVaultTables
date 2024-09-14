package lv.id.bonne.vaulthunters.jewelpacktable.init;


import lv.id.bonne.vaulthunters.jewelpacktable.block.screen.VaultJewelApplicationStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ModScreens
{
  public static void register() {
    MenuScreens.register(ModContainers.JEWEL_SELECTOR_TABLE_CONTAINER, VaultJewelApplicationStationScreen::new);
  }
}


/* Location:              C:\Users\BONNe\Desktop\vault-hunters-official-mod-458203-5631250_mapped_official_1.18.2.jar!\iskallia\vault\init\ModScreens.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */