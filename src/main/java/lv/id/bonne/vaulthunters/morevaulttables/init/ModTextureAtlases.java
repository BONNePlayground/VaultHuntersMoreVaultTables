//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.atlas.ResourceTextureAtlasHolder;
import iskallia.vault.util.function.Memo;
import javax.annotation.Nullable;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTableMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
    value = {Dist.CLIENT},
    bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModTextureAtlases {
    private static final Map<ResourceLocation, Supplier<ITextureAtlas>> REGISTRY = new HashMap<>();

    public static final Supplier<ITextureAtlas> SLOT = register(MoreVaultTableMod.of("textures/atlas/slot.png"),
        MoreVaultTableMod.of("textures/gui/slot"), null);


    @SubscribeEvent
    public static void on(RegisterClientReloadListenersEvent event) {
        Stream<ITextureAtlas> iTextureAtlasStream = REGISTRY.values().stream().map(Supplier::get);
        Objects.requireNonNull(event);
        iTextureAtlasStream.forEach(event::registerReloadListener);
    }

    private static Supplier<ITextureAtlas> register(ResourceLocation id, ResourceLocation resourceLocation, @Nullable Supplier<List<ResourceLocation>> validationSupplier) {
        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("Duplicate atlas resource location registered: " + id);
        } else {
            Supplier<ITextureAtlas> supplier = Memo.of(() -> {
                TextureManager textureManager = Minecraft.getInstance().textureManager;
                ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
                return new ResourceTextureAtlasHolder(textureManager, resourceManager, id, resourceLocation, validationSupplier);
            });
            REGISTRY.put(id, supplier);
            return supplier;
        }
    }
}

