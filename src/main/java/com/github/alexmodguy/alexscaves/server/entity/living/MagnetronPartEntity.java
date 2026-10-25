package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetronJoint;
import com.github.alexmodguy.alexscaves.server.message.MultipartEntityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MagnetronPartEntity extends ACMultipartEntity<MagnetronEntity> {

    private final MagnetronJoint joint;
    private BlockPos startPosition;
    private BlockState blockState;
    private EntityDimensions size;
    public boolean left;
    public float scale = 1;
    private final BlockState STONE = Blocks.STONE.defaultBlockState();

    public MagnetronPartEntity(MagnetronEntity parent, MagnetronJoint joint, boolean left) {
        super(parent);
        this.blocksBuilding = true;
        this.size = EntityDimensions.fixed(0.9F, 0.9F);
        this.joint = joint;
        this.left = left;
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    public MagnetronJoint getJoint() {
        return joint;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        MagnetronEntity parent = this.getParent();
        if (parent == null) {
            return InteractionResult.PASS;
        } else {
            this.playSound(SoundEvents.ITEM_BREAK);
            if (player.level().isClientSide) {
                AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), player.getId(), 0));
            }
            return parent.interact(player, hand);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        MagnetronEntity parent = this.getParent();
        if (!this.isInvulnerableTo(source) && parent != null) {
            Entity attacker = source.getEntity();
            if (attacker != null && attacker.level().isClientSide) {
                AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), attacker.getId(), 1));
                return true;
            }
            if (attacker == null || !attacker.level().isClientSide) {
                return parent.hurt(source, amount);
            }
        }
        return false;
    }

    public void positionMultipart(MagnetronEntity entity) {
        Vec3 targetPos = entity.position().add(this.joint.getTargetPosition(entity, left));
        Vec3 start = startPosition == null ? entity.position() : Vec3.atCenterOf(startPosition);
        Vec3 addToStart = targetPos.subtract(start);
        this.setPos(start.add(addToStart.scale(entity.getFormProgress(1.0F))));
    }

    public void setStartsAt(BlockPos pos) {
        startPosition = pos;
    }

    public BlockPos getStartPosition() {
        return startPosition;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public BlockState getVisualBlockState() {
        MagnetronEntity parent = this.getParent();
        return blockState == null && parent != null && parent.isAlive() ? STONE : blockState;
    }

    public void setBlockState(BlockState state) {
        blockState = state;
    }

    public double getLowPoint() {
        return this.getBoundingBox().minY;
    }
}