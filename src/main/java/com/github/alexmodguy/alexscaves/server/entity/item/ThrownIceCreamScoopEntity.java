package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;

public class ThrownIceCreamScoopEntity extends ThrowableItemProjectile {

    public ThrownIceCreamScoopEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public ThrownIceCreamScoopEntity(Level level, LivingEntity thrower) {
        super(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP, thrower, level);
    }

    public ThrownIceCreamScoopEntity(Level level, double x, double y, double z) {
        super(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP, x, y, z, level);
    }

    public void handleEntityEvent(byte message) {
        if (message == 3) {
            double d0 = 0.08D;
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(damageSources().thrown(this, this.getOwner()), 0.0F);
        if(hitResult.getEntity() instanceof LivingEntity living){
            for (MobEffectInstance mobEffectInstance : new ArrayList<>(living.getActiveEffects())) {
                 if (mobEffectInstance.getEffect().value() != ACEffectRegistry.IRRADIATED.value() && mobEffectInstance.getEffect().value() != ACEffectRegistry.DARKNESS_INCARNATE.value() && mobEffectInstance.getEffect().value() != ACEffectRegistry.BUBBLED.value()){
                     living.removeEffect(mobEffectInstance.getEffect());
                 }
            }
        }
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }

    }

    @Override
    protected Item getDefaultItem() {
        return ACItemRegistry.VANILLA_ICE_CREAM_SCOOP;
    }
}
