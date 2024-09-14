//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.init;


import com.google.common.base.Predicates;

import java.util.Arrays;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModModels {
    public ModModels() {}

    public static void setupRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VAULT_JEWEL_APPLICATION_STATION, RenderType.cutout());
    }

    private static void setRenderLayers(Block block, RenderType... renderTypes) {
        ItemBlockRenderTypes.setRenderLayer(block, Predicates.in(Arrays.asList(renderTypes)));
    }
}
