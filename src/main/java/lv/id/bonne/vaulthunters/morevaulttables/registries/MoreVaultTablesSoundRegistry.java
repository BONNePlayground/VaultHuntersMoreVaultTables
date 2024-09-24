//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.registries;


import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class MoreVaultTablesSoundRegistry
{
    /**
     * The Sound registry
     */
    public static final DeferredRegister<SoundEvent> REGISTRY =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MoreVaultTablesMod.MODID);

    public static final RegistryObject<SoundEvent> BLENDER =
        REGISTRY.register("blender", () -> new SoundEvent(MoreVaultTablesMod.of("blender")));
}
