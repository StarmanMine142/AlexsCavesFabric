package com.github.alexmodguy.alexscaves.client.particle;

import com.github.alexmodguy.alexscaves.server.misc.ACFluidHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

public class PurpleSodaBubbleParticle extends TextureSheetParticle {

    public PurpleSodaBubbleParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(world, x, y, z);
        this.pickSprite(sprites);
        this.gravity = -0.15F - random.nextFloat() * 0.02F;
        this.lifetime = 80 + random.nextInt(70);
        this.quadSize = 0.15F + random.nextFloat() * 0.1F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

    }

    public void tick() {
        super.tick();
        BlockPos slightlyAbove = BlockPos.containing(this.x, this.y + 0.2F, this.z);
        BlockPos slightlyBelow = BlockPos.containing(this.x, this.y, this.z);
        float fluidHeight = 0.0F;
        BlockPos lastSodaBlock = null;
        if (ACFluidHelper.isPurpleSoda(level.getFluidState(slightlyAbove))) {
            fluidHeight = level.getFluidState(slightlyAbove).getHeight(level, slightlyAbove);
            lastSodaBlock = slightlyAbove;
        } else if (ACFluidHelper.isPurpleSoda(level.getFluidState(slightlyBelow))) {
            fluidHeight = level.getFluidState(slightlyBelow).getHeight(level, slightlyBelow);
            lastSodaBlock = slightlyBelow;
        }
        if(lastSodaBlock == null || lastSodaBlock.getY() + fluidHeight < this.y + 0.2F){
            this.remove();
            double f = lastSodaBlock == null ? this.y : lastSodaBlock.getY() + fluidHeight;
            this.level.addParticle(ACParticleRegistry.PURPLE_SODA_FIZZ, this.x, f + 0.25F, this.z, 0, 0, 0);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PurpleSodaBubbleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }

}
