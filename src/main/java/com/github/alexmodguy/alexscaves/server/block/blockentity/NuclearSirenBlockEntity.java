package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.NuclearSirenBlock;
import com.github.alexmodguy.alexscaves.server.entity.util.ActivatesSirens;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NuclearSirenBlockEntity extends BlockEntity {

    private float volumeProgress;
    private float prevVolumeProgress;

    public int age;
    private Entity nearestNuclearBomb = null;
    private BlockPos nearestMeltdownFurnace = null;
    private int bombId = -1;
    private boolean wasPowered;

    public NuclearSirenBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.NUCLEAR_SIREN, pos, state);
        if (state.getValue(NuclearSirenBlock.POWERED)) {
            prevVolumeProgress = volumeProgress = 10.0F;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, NuclearSirenBlockEntity entity) {
        entity.prevVolumeProgress = entity.volumeProgress;
        boolean powered = entity.isActivated(state);
        entity.age++;
        if (entity.wasPowered != powered) {
            entity.wasPowered = powered;
            entity.setChanged();
        }
        if (powered && entity.volumeProgress < 10.0F) {
            entity.volumeProgress += 0.5F;
        } else if (!powered && entity.volumeProgress > 0.0F) {
            entity.volumeProgress -= 0.5F;
        }
        if (powered && !entity.isRemoved()) {
            int j = entity.age % 18;
            if (level.isClientSide && j >= 9 && j % 3 == 0) {
                level.gameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Context.of(state));
                Vec3 particlesFrom = blockPos.getCenter().add(0, 0.2, 0);
                for (Direction direction : ACMath.HORIZONTAL_DIRECTIONS) {
                    Vec3 vec3 = particlesFrom.add(direction.getStepX() * 0.5F, 0, direction.getStepZ() * 0.5F);
                    float yRot = direction.toYRot();
                    level.addAlwaysVisibleParticle(ACParticleRegistry.NUCLEAR_SIREN_SONAR, true, vec3.x, vec3.y, vec3.z, 0, yRot, 0);
                }
            }
        }
        if (!level.isClientSide) {
            if (entity.nearestMeltdownFurnace == null || !entity.isTrackedFurnaceCritical()) {
                entity.nearestMeltdownFurnace = null;
                boolean flag = false;
                if (entity.age % 20 == 0 && level instanceof ServerLevel) {
                    BlockPos pos = entity.getNearbyCriticalFurnaces((ServerLevel) level, 128).findAny().orElse(null);
                    if (pos != null && entity.nearestMeltdownFurnace == null) {
                        entity.nearestMeltdownFurnace = pos;
                        flag = true;
                    }
                }
                if (flag) {
                    level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                }
            }
            if (entity.nearestNuclearBomb == null || entity.nearestNuclearBomb.isRemoved() || entity.nearestNuclearBomb instanceof ActivatesSirens sirens && sirens.shouldStopBlaringSirens()) {
                entity.nearestNuclearBomb = null;
                int prevBombId = entity.bombId;
                entity.bombId = -1;
                if (prevBombId != entity.bombId) {
                    level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                }
            } else {
                int prevBombId = entity.bombId;
                entity.bombId = entity.nearestNuclearBomb != null ? entity.nearestNuclearBomb.getId() : -1;
                if (prevBombId != entity.bombId) {
                    level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                }
            }
        }else{
            if (powered) {
                AlexsCaves.playWorldSound(entity, (byte) 0);
            }
        }
    }

    public boolean isTrackedFurnaceCritical() {
        if (nearestMeltdownFurnace != null && level.getBlockEntity(nearestMeltdownFurnace) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity) {
            return nuclearFurnaceBlockEntity.getCriticality() >= 2;
        }
        return false;
    }

    public void setNearestNuclearBomb(Entity bomb) {
        Vec3 center = getBlockPos().getCenter();
        if (nearestNuclearBomb == null || nearestNuclearBomb.distanceToSqr(center) > bomb.distanceToSqr(center)) {
            nearestNuclearBomb = bomb;
        }
    }

    public boolean isActivated(BlockState state) {
        return state.is(ACBlockRegistry.NUCLEAR_SIREN) && state.getValue(NuclearSirenBlock.POWERED) || this.bombId != -1 || this.isTrackedFurnaceCritical();
    }

    public float getVolume(float partialTicks) {
        return (prevVolumeProgress + (volumeProgress - prevVolumeProgress) * partialTicks) * 0.1F;
    }

    private Stream<BlockPos> getNearbyCriticalFurnaces(ServerLevel world, int range) {
        return getNearbyBlockEntities(world, this.getBlockPos(), range, this::isCriticalFurnace);
    }

    private boolean isCriticalFurnace(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof NuclearFurnaceBlockEntity nuclearFurnaceBlockEntity && nuclearFurnaceBlockEntity.getCriticality() >= 2;
    }

    public static Stream<BlockPos> getNearbySirens(ServerLevel world, BlockPos origin, int range) {
        return getNearbyBlockEntities(world, origin, range, pos -> world.getBlockEntity(pos) instanceof NuclearSirenBlockEntity);
    }

    private static Stream<BlockPos> getNearbyBlockEntities(ServerLevel world, BlockPos origin, int range, java.util.function.Predicate<BlockPos> predicate) {
        int minChunkX = SectionPos.blockToSectionCoord(origin.getX() - range);
        int maxChunkX = SectionPos.blockToSectionCoord(origin.getX() + range);
        int minChunkZ = SectionPos.blockToSectionCoord(origin.getZ() - range);
        int maxChunkZ = SectionPos.blockToSectionCoord(origin.getZ() + range);
        double maxDistanceSqr = range * range;
        List<BlockPos> matches = new ArrayList<>();
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (BlockPos pos : chunk.getBlockEntities().keySet()) {
                    if (pos.distSqr(origin) <= maxDistanceSqr && predicate.test(pos)) {
                        matches.add(pos.immutable());
                    }
                }
            }
        }
        return matches.stream();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.bombId = tag.getInt("BombID");
        if (tag.contains("NearestFurnaceX")) {
            this.nearestMeltdownFurnace = new BlockPos(tag.getInt("NearestFurnaceX"), tag.getInt("NearestFurnaceY"), tag.getInt("NearestFurnaceZ"));

        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("BombID", this.bombId);
        if (nearestMeltdownFurnace != null) {
            tag.putInt("NearestFurnaceX", nearestMeltdownFurnace.getX());
            tag.putInt("NearestFurnaceY", nearestMeltdownFurnace.getY());
            tag.putInt("NearestFurnaceZ", nearestMeltdownFurnace.getZ());
        }
    }

    public void setRemoved() {
        AlexsCaves.clearSoundCacheFor(this);
        super.setRemoved();
    }


    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        if (packet != null && packet.getTag() != null) {
            applyUpdateTag(packet.getTag(), registries);
        }
    }

    public void applyUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }
}
