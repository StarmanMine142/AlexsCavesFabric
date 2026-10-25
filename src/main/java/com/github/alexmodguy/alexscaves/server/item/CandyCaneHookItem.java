package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class CandyCaneHookItem extends Item {

    private static final String LAST_LAUNCHED_HOOK_UUID = "LastLaunchedHookUUID";
    private static final String LAST_LAUNCHED_HOOK_GAME_TIME = "LastLaunchedHookGameTime";
    private static final String REELING = "Reeling";
    private static final int MIN_REEL_TICKS = 5;

    public CandyCaneHookItem() {
        super(new Item.Properties().durability(200));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack itemStackOpposite = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (level.isClientSide) {
            boolean canLaunch = canLaunchHook(player, itemstack, level, true, hand)
                    && (hand == InteractionHand.MAIN_HAND || !itemStackOpposite.is(this) || CandyCaneHookItem.isActive(itemStackOpposite));
            boolean canReel = !(player.getRootVehicle() instanceof GumWormSegmentEntity)
                    && !(itemStackOpposite.is(this) && !isActive(itemStackOpposite))
                    && canReelHook(level, itemstack);
            if (canLaunch || canReel) {
                return InteractionResultHolder.sidedSuccess(itemstack, true);
            }
            return InteractionResultHolder.pass(itemstack);
        }
        if (canLaunchHook(player, itemstack, level, true, hand) && (hand == InteractionHand.MAIN_HAND || !itemStackOpposite.is(this) || isHookLaunchedInWorld(level, itemStackOpposite))) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.CANDY_CANE_HOOK_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                CandyCaneHookEntity hookEntity = new CandyCaneHookEntity(player, level, itemstack, hand == InteractionHand.OFF_HAND);
                hookEntity.setOwner(player);
                hookEntity.setReeling(false);
                level.addFreshEntity(hookEntity);
                setLastLaunchedHookUUID(itemstack, hookEntity.getUUID());
                setLastLaunchedHookTime(itemstack, level.getGameTime());
                setReelingIn(itemstack, false);

                player.awardStat(Stats.ITEM_USED.get(this));
                player.gameEvent(GameEvent.ITEM_INTERACT_START);
                player.swing(hand);
                return InteractionResultHolder.consume(itemstack);
        } else if(!(player.getRootVehicle() instanceof GumWormSegmentEntity) && !(itemStackOpposite.is(this) && !isActive(itemStackOpposite))){
            if (canReelHook(level, itemstack)) {
                InteractionHand oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                if(itemStackOpposite.is(this) && canReelHook(level, itemStackOpposite) && !isReelingIn(itemStackOpposite)){
                    setReelingIn(itemStackOpposite, true);
                    itemStackOpposite.hurtAndBreak(1, player, oppositeHand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                }
                if (!isReelingIn(itemstack)) {
                    setReelingIn(itemstack, true);
                    itemstack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.CANDY_CANE_HOOK_REEL, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                    player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                    return InteractionResultHolder.sidedSuccess(itemstack, false);
                }
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    private boolean isHookLaunchedInWorld(Level level, ItemStack stack) {
        if(isActive(stack)){
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag compoundTag = customData.copyTag();
            if (level instanceof ServerLevel serverLevel && compoundTag.contains(LAST_LAUNCHED_HOOK_UUID)) {
                Entity entity = serverLevel.getEntity(compoundTag.getUUID(LAST_LAUNCHED_HOOK_UUID));
                if (entity instanceof CandyCaneHookEntity candyCaneHook) {
                    return candyCaneHook.isAlive() && candyCaneHook.tickCount > 0;
                }
            }
        }
        return false;
    }

    private boolean canReelHook(Level level, ItemStack itemStack) {
        return isActive(itemStack) && getTicksSinceLaunch(level, itemStack) >= MIN_REEL_TICKS;
    }

    private static long getTicksSinceLaunch(Level level, ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundTag = customData.copyTag();
        if (!compoundTag.contains(LAST_LAUNCHED_HOOK_GAME_TIME)) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, level.getGameTime() - compoundTag.getLong(LAST_LAUNCHED_HOOK_GAME_TIME));
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);

        if (entity instanceof Player player && !level.isClientSide && !(stack == player.getItemBySlot(EquipmentSlot.MAINHAND) || stack == player.getItemBySlot(EquipmentSlot.OFFHAND)) && isActive(stack)) {
            if (!isReelingIn(stack)) {
                setReelingIn(stack, true);
            }
            if (canLaunchHook(player, stack, level, false, InteractionHand.MAIN_HAND)) {
                setLastLaunchedHookUUID(stack, null);
            }
        }
    }

    public static boolean isActive(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        return compoundtag.contains(LAST_LAUNCHED_HOOK_UUID);
    }

    @Nullable
    public static UUID getLaunchedHookUUID(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        if (compoundtag.contains(LAST_LAUNCHED_HOOK_UUID)) {
            return compoundtag.getUUID(LAST_LAUNCHED_HOOK_UUID);
        } else {
            return null;
        }
    }

    public static void setLastLaunchedHookUUID(ItemStack itemStack, @Nullable UUID uuid) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        if(uuid == null){
            compoundtag.remove(LAST_LAUNCHED_HOOK_UUID);
            compoundtag.remove(LAST_LAUNCHED_HOOK_GAME_TIME);
            compoundtag.remove(REELING);
        }else{
            compoundtag.putUUID(LAST_LAUNCHED_HOOK_UUID, uuid);
        }
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
    }

    public static void setLastLaunchedHookTime(ItemStack itemStack, long gameTime) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        compoundtag.putLong(LAST_LAUNCHED_HOOK_GAME_TIME, gameTime);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
    }

    public static boolean isReelingIn(ItemStack itemStack) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        return isActive(itemStack) && compoundtag.getBoolean(REELING);
    }

    public static void setReelingIn(ItemStack itemStack, boolean reeling) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        compoundtag.putBoolean(REELING, reeling);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundtag));
    }

    public static boolean canLaunchHook(Player player, ItemStack itemStack, Level level, boolean checkHands, InteractionHand hand) {
        CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag compoundtag = customData.copyTag();
        if (level instanceof ServerLevel serverLevel && compoundtag.contains(LAST_LAUNCHED_HOOK_UUID)) {
            Entity entity = serverLevel.getEntity(compoundtag.getUUID(LAST_LAUNCHED_HOOK_UUID));
            if (entity instanceof CandyCaneHookEntity candyCaneHook) {
                return !(candyCaneHook.isAlive() && candyCaneHook.getOwner() != null && candyCaneHook.getOwner().is(player) && (!checkHands || hand == candyCaneHook.getHandLaunchedFrom()));
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }
}
