package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.BiomeTreatItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.ItemStack;

public class JellyBeanEatParticle extends BreakingItemParticle {

    protected JellyBeanEatParticle(ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(clientLevel, x, y, z, stack);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += xd;
        this.yd += yd;
        this.zd += zd;
        int colorizer = -1;
        if(stack.getItem() == ACItemRegistry.BIOME_TREAT){
            colorizer = BiomeTreatItem.getBiomeTreatColorOf(Minecraft.getInstance().level, stack);
        }
        if(colorizer != -1){
            float f = (float)(colorizer >> 16 & 255) / 255.0F;
            float f1 = (float)(colorizer >> 8 & 255) / 255.0F;
            float f2 = (float)(colorizer & 255) / 255.0F;
            this.setColor(f, f1, f2);
        }
    }

    public static class Factory implements ParticleProvider<ItemParticleOption> {
        public Particle createParticle(ItemParticleOption itemParticleOption, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) {
            return new JellyBeanEatParticle(clientLevel, x, y, z, xd, yd, zd, itemParticleOption.getItem());
        }
    }

}
