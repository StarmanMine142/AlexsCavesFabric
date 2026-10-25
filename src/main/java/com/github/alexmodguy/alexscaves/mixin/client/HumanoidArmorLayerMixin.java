package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.render.item.ACArmorRenderProperties;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin extends RenderLayer {

    private static final Map<String, ResourceLocation> AC_ARMOR_LOCATION_CACHE = Maps.newHashMap();

    @Shadow
    protected abstract void setPartVisibility(HumanoidModel humanoidModel, EquipmentSlot equipmentSlot);

    @Shadow
    private void renderTrim(Holder<ArmorMaterial> material, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ArmorTrim armorTrim, HumanoidModel humanoidModel, boolean innerModel) {
    }

    public HumanoidArmorLayerMixin(RenderLayerParent renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"},
            at = @At(value = "HEAD"),
            remap = true,
            cancellable = true
    )
    private void ac_renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int light, HumanoidModel humanoidModel, CallbackInfo ci) {
        ItemStack itemstack = livingEntity.getItemBySlot(equipmentSlot);
        Item item = itemstack.getItem();
        if (!(item instanceof ArmorItem armorItem) || !isAlexsCavesArmor(item) || armorItem.getEquipmentSlot() != equipmentSlot) {
            return;
        }

        ci.cancel();
        boolean innerModel = equipmentSlot == EquipmentSlot.LEGS;
        HumanoidModel parentModel = this.getParentModel() instanceof HumanoidModel humanoidModel1 ? humanoidModel1 : humanoidModel;
        HumanoidModel armorModel = ACArmorRenderProperties.getHumanoidArmorModel(livingEntity, itemstack, equipmentSlot, parentModel);
        parentModel.copyPropertiesTo(armorModel);
        setPartVisibility(armorModel, equipmentSlot);

        ResourceLocation texture = getACArmorResource(livingEntity, itemstack, equipmentSlot, null);
        if (item instanceof CustomArmorPostRender) {
            ACArmorRenderProperties.renderCustomArmor(poseStack, multiBufferSource, light, itemstack, armorItem, armorModel, innerModel, texture);
        } else {
            int color = itemstack.is(ItemTags.DYEABLE) ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemstack, -6265536)) : -1;
            VertexConsumer vertexConsumer;
            if (itemstack.hasFoil()) {
                vertexConsumer = VertexMultiConsumer.create(multiBufferSource.getBuffer(RenderType.armorEntityGlint()), multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(texture)));
            } else {
                vertexConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(texture));
            }
            armorModel.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, color);
        }

        ArmorTrim trim = itemstack.get(DataComponents.TRIM);
        if (trim != null) {
            this.renderTrim(armorItem.getMaterial(), poseStack, multiBufferSource, light, trim, armorModel, innerModel);
        }
    }

    private ResourceLocation getACArmorResource(LivingEntity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        ArmorMaterial material = item.getMaterial().value();
        boolean innerModel = slot == EquipmentSlot.LEGS;

        if (!material.layers().isEmpty()) {
            ArmorMaterial.Layer layer = material.layers().get(0);
            return ACArmorRenderProperties.getArmorTexture(entity, stack, slot, layer, innerModel);
        }

        ResourceLocation materialName = item.getMaterial().unwrapKey().get().location();
        String domain = materialName.getNamespace();
        String texture = materialName.getPath();
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (innerModel ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        ResourceLocation resourcelocation = AC_ARMOR_LOCATION_CACHE.get(s1);
        if (resourcelocation == null) {
            resourcelocation = ResourceLocation.parse(s1);
            AC_ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }
        return resourcelocation;
    }

    private static boolean isAlexsCavesArmor(Item item) {
        return item instanceof PrimordialArmorItem
                || item instanceof HazmatArmorItem
                || item instanceof DivingArmorItem
                || item instanceof DarknessArmorItem
                || item instanceof RainbounceBootsItem
                || item instanceof GingerbreadArmorItem;
    }
}