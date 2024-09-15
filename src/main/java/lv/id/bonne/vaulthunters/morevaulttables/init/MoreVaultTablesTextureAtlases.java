//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.atlas.ResourceTextureAtlasHolder;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.util.function.Memo;
import lv.id.bonne.vaulthunters.morevaulttables.MoreVaultTablesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = MoreVaultTablesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MoreVaultTablesTextureAtlases
{
    @SubscribeEvent
    public static void on(RegisterClientReloadListenersEvent event)
    {
        Stream<ITextureAtlas> iTextureAtlasStream = REGISTRY.values().stream().map(Supplier::get);
        Objects.requireNonNull(event);
        iTextureAtlasStream.forEach(event::registerReloadListener);
    }


    private static Supplier<ITextureAtlas> register(ResourceLocation id,
        ResourceLocation resourceLocation,
        @Nullable Supplier<List<ResourceLocation>> validationSupplier)
    {
        if (REGISTRY.containsKey(id))
        {
            throw new IllegalStateException("Duplicate atlas resource location registered: " + id);
        }
        else
        {
            Supplier<ITextureAtlas> supplier = Memo.of(() ->
            {
                TextureManager textureManager = Minecraft.getInstance().textureManager;
                ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
                return new ResourceTextureAtlasHolder(textureManager,
                    resourceManager,
                    id,
                    resourceLocation,
                    validationSupplier);
            });
            REGISTRY.put(id, supplier);
            return supplier;
        }
    }

    private static final Map<ResourceLocation, Supplier<ITextureAtlas>> REGISTRY = new HashMap<>();

    public static final Supplier<ITextureAtlas> SLOT = register(MoreVaultTablesMod.of("textures/atlas/slot.png"),
        MoreVaultTablesMod.of("textures/gui/slot"), null);

    public static final TextureAtlasRegion POUCH_NO_ITEM =
        TextureAtlasRegion.of(MoreVaultTablesTextureAtlases.SLOT, MoreVaultTablesMod.of("gui/slot/pouch_no_item"));

    public static final TextureAtlasRegion PACK_NO_ITEM =
        TextureAtlasRegion.of(MoreVaultTablesTextureAtlases.SLOT, MoreVaultTablesMod.of("gui/slot/pack_no_item"));

    public static final TextureAtlasRegion CARD_NO_ITEM =
        TextureAtlasRegion.of(MoreVaultTablesTextureAtlases.SLOT, MoreVaultTablesMod.of("gui/slot/card_no_item"));
}

