//
// Created by BONNe
// Copyright - 2024
//


package lv.id.bonne.vaulthunters.morevaulttables.mixin;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;


/**
 * This mixin injects into fake item slot to display overlay for Legendary modifier.
 */
@Mixin(value = FakeItemSlotElement.class, remap = false)
public class FakeItemSlotElementMixin
{
    @Inject(method = "renderItemStack(Lnet/minecraft/world/item/ItemStack;FFZ)V", at = @At(value = "TAIL"))
    private void addFakeItemDecorationRender(ItemStack itemStack, float x, float y, boolean disabled, CallbackInfo ci)
    {
        if (disabled)
        {
            return;
        }

        GearDataCache cache = GearDataCache.of(itemStack);
        List<ResourceLocation> icons = new ArrayList<>();
        Stream<VaultGearModifier.AffixCategory> affixCategoryStream =
            Stream.of(VaultGearModifier.AffixCategory.values()).filter(cat -> cat.getOverlayIcon() != null);

        Objects.requireNonNull(cache);

        affixCategoryStream.filter(cache::hasModifierOfCategory).forEach(cat -> icons.add(cat.getOverlayIcon()));

        if (cache.getState() == VaultGearState.UNIDENTIFIED && cache.hasAttribute(ModGearAttributes.IS_LEGENDARY))
        {
            icons.add(VaultGearModifier.AffixCategory.LEGENDARY.getOverlayIcon());
        }

        if (!icons.isEmpty())
        {
            icons.forEach((icon) ->
            {
                RenderSystem.setShaderTexture(0, icon);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableDepthTest();
                ScreenDrawHelper.drawTexturedQuads((buf) ->
                {
                    PoseStack pose = new PoseStack();
                    ScreenDrawHelper.rect(buf, pose).at(x, y).dim(16.0F, 16.0F).draw();
                });
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
            });
        }
    }
}
