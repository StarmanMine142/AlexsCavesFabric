package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.fabric.MultipartEntityLevel;
import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Level.class)
public class LevelMultipartMixin implements MultipartEntityLevel {

    @Unique
    private final Int2ObjectMap<ACMultipartEntity<?>> alexscaves$partEntityMap = new Int2ObjectOpenHashMap<>();

    @Inject(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private void alexscaves$appendMultipartEntities(@Nullable Entity except, AABB area, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        this.alexscaves$appendMultipartEntities(cir.getReturnValue(), area, predicate, Integer.MAX_VALUE, except);
    }

    @Inject(
            method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At("RETURN")
    )
    private <T extends Entity> void alexscaves$appendMultipartEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate, CallbackInfoReturnable<List<T>> cir) {
        this.alexscaves$appendTypedMultipartEntities(cir.getReturnValue(), entityTypeTest, area, predicate, Integer.MAX_VALUE);
    }

    @Inject(
            method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;)V",
            at = @At("RETURN")
    )
    private <T extends Entity> void alexscaves$appendMultipartEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate, List<? super T> entities, CallbackInfo ci) {
        this.alexscaves$appendTypedMultipartEntities(entities, entityTypeTest, area, predicate, Integer.MAX_VALUE);
    }

    @Inject(
            method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V",
            at = @At("RETURN")
    )
    private <T extends Entity> void alexscaves$appendMultipartEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate, List<? super T> entities, int maxResults, CallbackInfo ci) {
        this.alexscaves$appendTypedMultipartEntities(entities, entityTypeTest, area, predicate, maxResults);
    }

    @Unique
    private void alexscaves$appendMultipartEntities(List<? super Entity> entities, AABB area, Predicate<? super Entity> predicate, int maxResults, @Nullable Entity except) {
        for (ACMultipartEntity<?> part : this.alexscaves$partEntityMap.values()) {
            if (entities.size() >= maxResults) {
                return;
            }
            if (part != except && part.getBoundingBox().intersects(area) && predicate.test(part) && !entities.contains(part)) {
                entities.add(part);
            }
        }
    }

    @Unique
    private <T extends Entity> void alexscaves$appendTypedMultipartEntities(List<? super T> entities, EntityTypeTest<Entity, T> entityTypeTest, AABB area, Predicate<? super T> predicate, int maxResults) {
        for (ACMultipartEntity<?> part : this.alexscaves$partEntityMap.values()) {
            if (entities.size() >= maxResults) {
                return;
            }
            T cast = entityTypeTest.tryCast(part);
            if (cast != null && cast.getBoundingBox().intersects(area) && predicate.test(cast) && !entities.contains(cast)) {
                entities.add(cast);
            }
        }
    }

    @Override
    public Int2ObjectMap<ACMultipartEntity<?>> alexscaves$getPartEntityMap() {
        return this.alexscaves$partEntityMap;
    }
}