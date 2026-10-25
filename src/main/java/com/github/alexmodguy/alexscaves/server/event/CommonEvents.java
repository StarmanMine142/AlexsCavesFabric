package com.github.alexmodguy.alexscaves.server.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.citadel.server.tick.ServerTickRateTracker;
import com.github.alexmodguy.alexscaves.mixin.ChunkGeneratorAccessor;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.StrippableLogBlock;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.SeekingArrowEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.entity.util.*;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.AlwaysCombinableOnAnvil;
import com.github.alexmodguy.alexscaves.server.item.ExtinctionSpearItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSourceAccessor;
import com.github.alexmodguy.alexscaves.server.misc.ACFluidHelper;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.misc.CaveBookProgress;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexmodguy.alexscaves.server.potion.SugarRushEffect;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CommonEvents {

    public void livingDie(LivingEntity entity, DamageSource source) {
        if (entity.getType() == EntityType.MAGMA_CUBE && source != null && source.getEntity() instanceof Frog frog) {
            if (frog.getVariant().is(ACFrogRegistry.PRIMORDIAL)) {
                entity.spawnAtLocation(new ItemStack(ACBlockRegistry.CARMINE_FROGLIGHT));
            }
        }
        if (!entity.level().isClientSide && entity instanceof Mob mob && source != null && source.getDirectEntity() instanceof LivingEntity directSource && directSource.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.PRIMITIVE_CLUB)) {
            if (hasEnchantment(directSource.getItemInHand(InteractionHand.MAIN_HAND), entity.level(), ACEnchantmentRegistry.BONKING) && entity.level().random.nextFloat() < 0.33F) {
                Creeper fakeCreeperForSkullDrop = EntityType.CREEPER.create(mob.level());
                if (fakeCreeperForSkullDrop != null) {
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        LightningBolt fakeThunder = EntityType.LIGHTNING_BOLT.create(serverLevel);
                        if (fakeThunder != null) {
                            fakeThunder.setVisualOnly(true);
                            fakeCreeperForSkullDrop.thunderHit(serverLevel, fakeThunder);
                        }
                    }
                    DamageSource fakeCreeperDamage = mob.level().damageSources().mobAttack(fakeCreeperForSkullDrop);
                    HashMap<EquipmentSlot, Float> prevLootDropChances = new HashMap<>();
                    EntityDropChanceAccessor dropChanceAccessor = (EntityDropChanceAccessor) mob;
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        prevLootDropChances.put(slot, dropChanceAccessor.ac_getEquipmentDropChance(slot));
                        dropChanceAccessor.ac_setDropChance(slot, 0.0F);
                    }
                    if (mob.level() instanceof ServerLevel serverLevel2) {
                        dropChanceAccessor.ac_dropCustomDeathLoot(serverLevel2, fakeCreeperDamage, false);
                    }
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        dropChanceAccessor.ac_setDropChance(slot, prevLootDropChances.get(slot));
                    }
                }
            }
        }
        if (entity instanceof Player player) {
            if (player.getUUID().toString().equals("71363abe-fd03-49c9-940d-aae8b8209b7c")) {
                player.spawnAtLocation(new ItemStack(ACItemRegistry.GREEN_SOYLENT, 1 + player.getRandom().nextInt(9)));
            }
            if (player.getUUID().toString().equals("4a463319-625c-4b86-a4e7-8b700f023a60")) {
                player.spawnAtLocation(new ItemStack(ACItemRegistry.STINKY_FISH, 1));
            }
        }
    }

    public InteractionResult playerEntityInteract(Player player, Level level, InteractionHand hand, Entity target) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ACItemRegistry.HOLOCODER) && target instanceof LivingEntity living && !(target instanceof ArmorStand) && living.isAlive()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("BoundEntityUUID", living.getUUID());
            CompoundTag entityTag = living instanceof Player ? new CompoundTag() : living.saveWithoutId(new CompoundTag());
            entityTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).toString());
            if (living instanceof Player) {
                entityTag.putUUID("UUID", living.getUUID());
            }
            tag.put("BoundEntityTag", entityTag);
            ItemStack stackReplacement = new ItemStack(ACItemRegistry.HOLOCODER);
            stack.shrink(1);
            stackReplacement.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            player.swing(hand);
            if (!player.addItem(stackReplacement)) {
                ItemEntity itementity = player.drop(stackReplacement, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setThrower(player);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public boolean livingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity.isPassenger() && entity instanceof FlyingMount && (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.FALL) || source.is(DamageTypes.FLY_INTO_WALL))) {
            return false;
        }
        if (entity instanceof WatcherPossessionAccessor possessed && possessed.isPossessedByWatcher() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !(source.getEntity() instanceof WatcherEntity)) {
            return false;
        }
        if (entity instanceof Player player && player.getUseItem().is(ACItemRegistry.EXTINCTION_SPEAR) && ExtinctionSpearItem.killGrottoGhostsFor(player, true)) {
            player.playSound(SoundEvents.SHIELD_BLOCK);
            return false;
        }
        if (entity instanceof Player player && source.is(DamageTypes.FALL) && player.getItemBySlot(EquipmentSlot.FEET).is(ACItemRegistry.RAINBOUNCE_BOOTS)) {
            player.fallDistance = 0.0F;
            return false;
        }
        return true;
    }

    public boolean livingAttack(LivingEntity entity, DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof AbstractArrow arrow && entity.isBlocking() && entity.getUseItem().is(ACItemRegistry.RESISTOR_SHIELD)) {
            ItemStack shield = entity.getUseItem();
            if (hasEnchantment(shield, entity.level(), ACEnchantmentRegistry.ARROW_INDUCTING) && arrow.getType() != ACEntityRegistry.SEEKING_ARROW) {
                SeekingArrowEntity seekingArrowEntity = new SeekingArrowEntity(entity.level(), entity);
                seekingArrowEntity.copyPosition(arrow);
                seekingArrowEntity.setDeltaMovement(arrow.getDeltaMovement().scale(-0.4D));
                seekingArrowEntity.setYRot(arrow.getYRot() + 180.0F);
                entity.level().addFreshEntity(seekingArrowEntity);
                arrow.discard();
            }
        }
        if (source.getDirectEntity() instanceof LivingEntity directSource && directSource.hasEffect(ACEffectRegistry.STUNNED)) {
            return false;
        }
        return true;
    }

    private boolean hasEnchantment(ItemStack stack, Level level, ResourceKey<Enchantment> enchantmentKey) {
        return ACEnchantmentRegistry.getEnchantmentLevel(level, stack, enchantmentKey) > 0;
    }

    public InteractionResult playerAttack(Player player, Entity target) {
        if (target instanceof DinosaurEntity && player.isPassengerOfSameVehicle(target)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public void livingTick(LivingEntity livingEntity) {
        if (livingEntity.hasEffect(ACEffectRegistry.BUBBLED) && ACFluidHelper.isInAnyFluid(livingEntity)) {
            livingEntity.removeEffect(ACEffectRegistry.BUBBLED);
        }
        if (livingEntity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE) && livingEntity.tickCount % 5 == 0 && DarknessIncarnateEffect.isInLight(livingEntity, 11)) {
            livingEntity.removeEffect(ACEffectRegistry.DARKNESS_INCARNATE);
        }
        if (livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.DIVING_HELMET) && (!livingEntity.isEyeInFluid(FluidTags.WATER) || livingEntity.getVehicle() instanceof SubmarineEntity)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 810, 0, false, false, true));
        }
        if (!livingEntity.level().isClientSide && livingEntity instanceof Mob mob && mob.getTarget() instanceof VallumraptorEntity vallumraptor && vallumraptor.getHideFor() > 0) {
            mob.setTarget(null);
        }
    }

    public void onEntityJoinWorld(Entity entity, ServerLevel level) {
        try {
            if (entity instanceof Creeper creeper) {
                ((MobTargetAccessor) creeper).ac_getTargetSelector().addGoal(3, new AvoidEntityGoal<>(creeper, RaycatEntity.class, 10.0F, 1.0D, 1.2D));
            }
            if (entity instanceof Drowned drowned && drowned.level().getBiome(drowned.blockPosition()).is(ACBiomeRegistry.ABYSSAL_CHASM)) {
                if (drowned.getItemBySlot(EquipmentSlot.FEET).isEmpty() && drowned.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && drowned.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && drowned.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance) {
                        drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ACItemRegistry.DIVING_HELMET));
                        drowned.setDropChance(EquipmentSlot.HEAD, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance) {
                        drowned.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ACItemRegistry.DIVING_CHESTPLATE));
                        drowned.setDropChance(EquipmentSlot.CHEST, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance) {
                        drowned.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ACItemRegistry.DIVING_LEGGINGS));
                        drowned.setDropChance(EquipmentSlot.LEGS, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < AlexsCaves.COMMON_CONFIG.drownedDivingGearSpawnChance) {
                        drowned.setItemSlot(EquipmentSlot.FEET, new ItemStack(ACItemRegistry.DIVING_BOOTS));
                        drowned.setDropChance(EquipmentSlot.FEET, 0.5F);
                    }
                }
            }
            if (entity instanceof Fox fox) {
                ((MobTargetAccessor) fox).ac_getTargetSelector().addGoal(7, new NearestAttackableTargetGoal<>(fox, GingerbreadManEntity.class, 40, false, false, null));
            }
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    public void travelToDimension(Player player) {
        if (player.hasEffect(ACEffectRegistry.SUGAR_RUSH)) {
            SugarRushEffect.leaveSlowMotion(player, player.level());
        }
    }

    public static void onServerStarting(MinecraftServer server) {
        ACBiomeRarity.init();
        RegistryAccess registryAccess = server.registryAccess();
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registries.BIOME);
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new HashMap<>();
        for (ResourceKey<Biome> biomeResourceKey : allBiomes.registryKeySet()) {
            Optional<Holder.Reference<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        for (ResourceKey<LevelStem> levelStemResourceKey : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> holderOptional = levelStems.getHolder(levelStemResourceKey);
            if (holderOptional.isPresent() && holderOptional.get().value().generator().getBiomeSource() instanceof BiomeSourceAccessor expandedBiomeSource) {
                expandedBiomeSource.setResourceKeyMap(biomeMap);
                if (levelStemResourceKey.equals(LevelStem.OVERWORLD)) {
                    ImmutableSet.Builder<Holder<Biome>> biomeHolders = ImmutableSet.builder();
                    for (ResourceKey<Biome> biomeResourceKey : ACBiomeRegistry.ALEXS_CAVES_BIOMES) {
                        allBiomes.getHolder(biomeResourceKey).ifPresent(biomeHolders::add);
                    }
                    expandedBiomeSource.expandBiomesWith(biomeHolders.build());
                    refreshGeneratorFeatures(holderOptional.get().value().generator());
                }
            }
        }
    }

    public static void onServerStopped(MinecraftServer server) {
        if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(server);
            tracker.tickRateModifierList.clear();
        }
    }

    private static void refreshGeneratorFeatures(ChunkGenerator generator) {
        if (!(generator instanceof ChunkGeneratorAccessor accessor)) {
            return;
        }
        Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter = accessor.alexscaves$getGenerationSettingsGetter();
        accessor.alexscaves$setFeaturesPerStep(Suppliers.memoize(() ->
                FeatureSorter.buildFeaturesPerStep(
                        List.copyOf(generator.getBiomeSource().possibleBiomes()),
                        biome -> generationSettingsGetter.apply(biome).features(),
                        true
                )
        ));
        generator.validate();
    }

    public void playerTick(Player player) {
        if (!player.isCreative()) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(player, EquipmentSlot.MAINHAND);
            }
            if (player.getItemInHand(InteractionHand.OFF_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(player, EquipmentSlot.OFFHAND);
            }
        }
    }

    private void checkAndDestroyExploitItem(Player player, EquipmentSlot slot) {
        ItemStack itemInHand = player.getItemBySlot(slot);
        if (itemInHand.is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
            CustomData customData = itemInHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.copyTag();
            if (!tag.isEmpty()) {
                if (itemTagContainsAC(tag, "BiomeKey", false) || itemTagContainsAC(tag, "Structure", true) || itemTagContainsAC(tag, "structurecompass:structureName", true) || itemTagContainsAC(tag, "StructureKey", true)) {
                    itemInHand.shrink(1);
                    player.onEquippedItemBroken(itemInHand.getItem(), slot);
                    player.playSound(ACSoundRegistry.DISAPPOINTMENT);
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("item.alexscaves.natures_compass_warning"), true);
                    }
                }
            }
        }
    }

    public InteractionResultHolder<ItemStack> onRightClickItem(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.GLASS_BOTTLE) {
            HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
                if (level.mayInteract(player, blockpos)) {
                    if (ACFluidHelper.isPurpleSoda(level.getFluidState(blockpos))) {
                        player.gameEvent(GameEvent.ITEM_INTERACT_START);
                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
                        if (!player.addItem(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE))) {
                            player.spawnAtLocation(new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE));
                        }
                        player.swing(hand);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResultHolder.success(stack);
                    }
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluid, player));
    }

    public static boolean onUpdateAnvil(ItemStack left, ItemStack right, ItemStack output, int cost) {
        ItemEnchantments leftEnchants = left.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments rightEnchants = right.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        if (left.getItem() instanceof AlwaysCombinableOnAnvil && left.getItem() == right.getItem() && !leftEnchants.isEmpty() && !rightEnchants.isEmpty()) {
            ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(leftEnchants);
            int newCost = 0;

            for (var entry : rightEnchants.entrySet()) {
                Holder<Enchantment> enchantHolder = entry.getKey();
                int rightLevel = entry.getIntValue();
                int leftLevel = leftEnchants.getLevel(enchantHolder);
                int newLevel = leftLevel == rightLevel ? rightLevel + 1 : Math.max(rightLevel, leftLevel);

                boolean canCombine = true;
                for (var existingEntry : leftEnchants.entrySet()) {
                    if (!existingEntry.getKey().equals(enchantHolder) && !Enchantment.areCompatible(enchantHolder, existingEntry.getKey())) {
                        canCombine = false;
                        newCost++;
                    }
                }

                if (canCombine) {
                    Enchantment enchantment = enchantHolder.value();
                    if (newLevel > enchantment.getMaxLevel()) {
                        newLevel = enchantment.getMaxLevel();
                    }
                    mutableEnchants.set(enchantHolder, newLevel);
                    int enchantCost = enchantment.getAnvilCost();
                    newCost += enchantCost * newLevel;
                }
            }

            output.set(DataComponents.ENCHANTMENTS, mutableEnchants.toImmutable());
            return true;
        }
        return false;
    }

    private boolean itemTagContainsAC(CompoundTag tag, String tagID, boolean allowUndergroundCabin) {
        if (tag.contains(tagID)) {
            String resourceLocation = tag.getString(tagID);
            return resourceLocation.contains("alexscaves:") && (!allowUndergroundCabin || !resourceLocation.contains("underground_cabin"));
        }
        return false;
    }

    public static void register() {
        CommonEvents events = new CommonEvents();
        StrippableLogBlock.registerStrippables();

        ServerLivingEntityEvents.AFTER_DEATH.register(events::livingDie);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            boolean hurtAllowed = events.livingHurt(entity, source, amount);
            boolean attackAllowed = events.livingAttack(entity, source, amount);
            return hurtAllowed && attackAllowed;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> events.playerEntityInteract(player, world, hand, entity));
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> events.playerAttack(player, entity));
        UseItemCallback.EVENT.register((player, world, hand) -> events.onRightClickItem(player, world, hand));

        ServerEntityEvents.ENTITY_LOAD.register(events::onEntityJoinWorld);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> events.travelToDimension(player));

        ServerLifecycleEvents.SERVER_STARTING.register(CommonEvents::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(CommonEvents::onServerStopped);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof LivingEntity living) {
                        events.livingTick(living);
                    }
                }
            }
            for (Player player : server.getPlayerList().getPlayers()) {
                events.playerTick(player);
            }
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 2, factories -> {
            if (AlexsCaves.COMMON_CONFIG.cartographersSellCabinMaps) {
                factories.add(new VillagerUndergroundCabinMapTrade(5, 10, 6));
            }
        });

        TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
            if (AlexsCaves.COMMON_CONFIG.wanderingTradersSellCabinMaps) {
                factories.add(new VillagerUndergroundCabinMapTrade(8, 1, 10));
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Player player = handler.getPlayer();
            if (AlexsCaves.COMMON_CONFIG.warnGenerationIncompatibility && !AlexsCaves.MOD_GENERATION_CONFLICTS.isEmpty() && !player.level().isClientSide) {
                for (String modid : AlexsCaves.MOD_GENERATION_CONFLICTS) {
                    if (FabricLoader.getInstance().isModLoaded(modid)) {
                        player.sendSystemMessage(Component.translatable("alexscaves.startup_warning.generation_incompatible", modid).withStyle(ChatFormatting.RED));
                    }
                }
            }
            if (!player.level().isClientSide) {
                CaveBookProgress.syncCaveBookProgress(player);
            }
        });
    }
}