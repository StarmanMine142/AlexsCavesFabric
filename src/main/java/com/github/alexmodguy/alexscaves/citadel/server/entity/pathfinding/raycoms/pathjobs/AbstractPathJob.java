package com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.pathjobs;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.github.alexmodguy.alexscaves.citadel.Citadel;
import com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.*;
import com.github.alexmodguy.alexscaves.citadel.server.message.SyncPathReachedMessage;
import com.github.alexmodguy.alexscaves.citadel.server.message.SyncePathMessage;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Callable;

import static com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.PathfindingConstants.*;
import static com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.SurfaceType.getSurfaceType;
import static com.github.alexmodguy.alexscaves.citadel.server.entity.pathfinding.raycoms.SurfaceType.isWater;

/**
 * Abstract class for Jobs that run in the multithreaded path finder.
 */
public abstract class AbstractPathJob implements Callable<Path> {
    public static final Map<Player, UUID> trackingMap = new HashMap<>();
    protected final BlockPos start;
    protected final LevelReader world;
    protected final PathResult result;
    private final Queue<MNode> nodesOpen = new PriorityQueue<>(500);
    private final Map<Integer, MNode> nodesVisited = new HashMap<>();
    private final AbstractAdvancedPathNavigate.RestrictionType restrictionType;
    private final boolean hardXzRestriction;
    private final boolean xzRestricted = false;
    protected int maxRange;
    protected BlockPos end = null;
    protected boolean debugDrawEnabled = false;
    @Nullable
    protected Set<MNode> debugNodesVisited = new HashSet<>();
    @Nullable
    protected Set<MNode> debugNodesNotVisited = new HashSet<>();
    @Nullable
    protected Set<MNode> debugNodesPath = new HashSet<>();
    protected WeakReference<LivingEntity> entity;
    IPassabilityNavigator passabilityNavigator;
    private boolean allowJumpPointSearchTypeWalk;
    private int entitySizeXZStart = 0;
    private int entitySizeXZEnd = 1;
    private int entitySizeY = 1;
    private int totalNodesAdded = 0;
    private int totalNodesVisited = 0;
    private PathingOptions pathingOptions = new PathingOptions();
    private int maxX;
    private int minX;
    private int maxZ;
    private int minZ;
    private int maxY;
    private int minY;
    private double maxJumpHeight = MAX_JUMP_HEIGHT;

    private int maxNavigableGroundDist = 1;

    public AbstractPathJob(final Level world, final BlockPos start, final BlockPos end, final int range, final LivingEntity entity) {
        this(world, start, end, range, new PathResult<AbstractPathJob>(), entity);
    }

    public AbstractPathJob(final Level world, final BlockPos start, final BlockPos end, final int range, final PathResult result, final LivingEntity entity) {
        final int minX = Math.min(start.getX(), end.getX()) - (range / 2);
        final int minZ = Math.min(start.getZ(), end.getZ()) - (range / 2);
        final int maxX = Math.max(start.getX(), end.getX()) + (range / 2);
        final int maxZ = Math.max(start.getZ(), end.getZ()) + (range / 2);

        this.restrictionType = AbstractAdvancedPathNavigate.RestrictionType.NONE;
        this.hardXzRestriction = false;

        this.world = new ChunkCache(world, new BlockPos(minX, world.getMinBuildHeight(), minZ), new BlockPos(maxX, world.getMaxBuildHeight(), maxZ), range, world.dimensionType());

        this.start = new BlockPos(start);
        this.end = end;

        this.maxRange = range;

        this.result = result;
        result.setJob(this);
        allowJumpPointSearchTypeWalk = false;

        if (entity != null && trackingMap.containsValue(entity.getUUID())) {
            debugDrawEnabled = true;
            debugNodesVisited = new HashSet<>();
            debugNodesNotVisited = new HashSet<>();
            debugNodesPath = new HashSet<>();
        }
        setEntitySizes(entity);
        if (entity instanceof IPassabilityNavigator) {
            passabilityNavigator = (IPassabilityNavigator) entity;
            maxRange = passabilityNavigator.maxSearchNodes();
        }
        if (entity instanceof ITallWalker tallWalker) {
            maxNavigableGroundDist = tallWalker.getMaxNavigableDistanceToGround();
        }
        maxJumpHeight = (float) Math.floor(entity.maxUpStep() - 0.2F) + 1.3F;
        this.entity = new WeakReference<>(entity);
    }

    public AbstractPathJob(final Level world,
                           final BlockPos start,
                           final BlockPos startRestriction,
                           final BlockPos endRestriction,
                           final int range,
                           final boolean hardRestriction,
                           final PathResult result,
                           final LivingEntity entity,
                           final AbstractAdvancedPathNavigate.RestrictionType restrictionType) {
        this(world, start, startRestriction, endRestriction, range, Vec3i.ZERO, hardRestriction, result, entity, restrictionType);
        setEntitySizes(entity);
        if (entity instanceof IPassabilityNavigator) {
            passabilityNavigator = (IPassabilityNavigator) entity;
            maxRange = passabilityNavigator.maxSearchNodes();
        }
        maxJumpHeight = (float) Math.floor(entity.maxUpStep() - 0.2F) + 1.3F;
    }

    public AbstractPathJob(final Level world,
                           final BlockPos start,
                           final BlockPos startRestriction,
                           final BlockPos endRestriction,
                           final int range,
                           final Vec3i grow,
                           final boolean hardRestriction,
                           final PathResult result,
                           final LivingEntity entity,
                           final AbstractAdvancedPathNavigate.RestrictionType restrictionType) {
        this.minX = Math.min(startRestriction.getX(), endRestriction.getX()) - grow.getX();
        this.minZ = Math.min(startRestriction.getZ(), endRestriction.getZ()) - grow.getZ();
        this.maxX = Math.max(startRestriction.getX(), endRestriction.getX()) + grow.getX();
        this.maxZ = Math.max(startRestriction.getZ(), endRestriction.getZ()) + grow.getZ();
        this.minY = Math.min(startRestriction.getY(), endRestriction.getY()) - grow.getY();
        this.maxY = Math.max(startRestriction.getY(), endRestriction.getY()) + grow.getY();

        this.restrictionType = restrictionType;
        this.hardXzRestriction = hardRestriction;

        this.world = new ChunkCache(world, new BlockPos(minX, world.getMinBuildHeight(), minZ), new BlockPos(maxX, world.getMaxBuildHeight(), maxZ), range, world.dimensionType());

        this.start = start;

        this.maxRange = range;

        this.result = result;
        result.setJob(this);

        this.allowJumpPointSearchTypeWalk = false;

        if (entity != null && trackingMap.containsValue(entity.getUUID())) {
            debugDrawEnabled = true;
            debugNodesVisited = new HashSet<>();
            debugNodesNotVisited = new HashSet<>();
            debugNodesPath = new HashSet<>();
        }
        this.entity = new WeakReference<>(entity);
    }

    public static void synchToClient(final HashSet<BlockPos> reached, final Mob mob) {

        if (!Pathfinding.isDebug() || reached.isEmpty()) {
            return;
        }

        for (final Map.Entry<Player, UUID> entry : trackingMap.entrySet()) {
            if (entry.getValue().equals(mob.getUUID())) {
                ServerPlayNetworking.send((ServerPlayer) entry.getKey(), new SyncPathReachedMessage(reached));
            }
        }
    }

    public static BlockPos prepareStart(final LivingEntity entity) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(entity.getBlockX(),
                entity.getBlockY(),
                entity.getBlockZ());
        final Level world = entity.level();

        BlockState bs = world.getBlockState(pos);
        final VoxelShape collisionShape = bs.getBlockSupportShape(world, pos);
        if (bs.blocksMotion() && collisionShape.max(Direction.Axis.X) > 0) {
            final double relPosX = Math.abs(entity.getX() % 1);
            final double relPosZ = Math.abs(entity.getZ() % 1);

            for (final AABB box : collisionShape.toAabbs()) {
                if (relPosX >= box.minX && relPosX <= box.maxX
                        && relPosZ >= box.minZ && relPosZ <= box.maxZ
                        && box.maxY > 0) {
                    pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                    bs = world.getBlockState(pos);
                    break;
                }
            }
        }

        BlockState down = world.getBlockState(pos.below());
        while (!bs.blocksMotion() && !down.blocksMotion() && !(down.getBlock() instanceof LadderBlock) && bs.getFluidState().isEmpty()) {
            pos.move(Direction.DOWN, 1);
            bs = down;
            down = world.getBlockState(pos.below());

            if (pos.getY() < world.getMinBuildHeight()) {
                return entity.blockPosition();
            }
        }

        final Block b = bs.getBlock();

        if (entity.isInWater()) {
            while (!bs.getFluidState().isEmpty()) {
                pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                bs = world.getBlockState(pos);
            }
        } else if (b instanceof FenceBlock || b instanceof WallBlock || bs.isSolid()) {
            final double dX = entity.getX() - Math.floor(entity.getX());
            final double dZ = entity.getZ() - Math.floor(entity.getZ());

            if (dX < ONE_SIDE) {
                pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
            } else if (dX > OTHER_SIDE) {
                pos.set(pos.getX() + 1, pos.getY(), pos.getZ());
            }

            if (dZ < ONE_SIDE) {
                pos.set(pos.getX(), pos.getY(), pos.getZ() - 1);
            } else if (dZ > OTHER_SIDE) {
                pos.set(pos.getX(), pos.getY(), pos.getZ() + 1);
            }
        }

        return pos.immutable();
    }

    private static void setLadderFacing(final LevelReader world, final BlockPos pos, final PathPointExtended p) {
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        if (block instanceof VineBlock) {
            if (state.getValue(VineBlock.SOUTH)) {
                p.setLadderFacing(Direction.NORTH);
            } else if (state.getValue(VineBlock.WEST)) {
                p.setLadderFacing(Direction.EAST);
            } else if (state.getValue(VineBlock.NORTH)) {
                p.setLadderFacing(Direction.SOUTH);
            } else if (state.getValue(VineBlock.EAST)) {
                p.setLadderFacing(Direction.WEST);
            }
        } else if (block instanceof LadderBlock) {
            p.setLadderFacing(state.getValue(LadderBlock.FACING));
        } else {
            p.setLadderFacing(Direction.UP);
        }
    }

    private static boolean onALadder(final MNode node, @Nullable final MNode nextInPath, final BlockPos pos) {
        return nextInPath != null && node.isLadder()
                &&
                (nextInPath.pos.getX() == pos.getX() && nextInPath.pos.getZ() == pos.getZ());
    }

    private static int computeNodeKey(final BlockPos pos) {
        return ((pos.getX() & 0xFFF) << SHIFT_X_BY)
                | ((pos.getY() & 0xFF) << SHIFT_Y_BY)
                | (pos.getZ() & 0xFFF);
    }

    private static boolean nodeClosed(@Nullable final MNode node) {
        return node != null && node.isClosed();
    }

    private static boolean calculateSwimming(final LevelReader world, final BlockPos pos, @Nullable final MNode node) {
        return (node == null) ? isWater(world, pos.below()) : node.isSwimming();
    }

    public static Direction getXZFacing(final BlockPos pos, final BlockPos neighbor) {
        final BlockPos vector = neighbor.subtract(pos);
        return Direction.getNearest(vector.getX(), 0, vector.getZ());
    }

    public void synchToClient(final LivingEntity mob) {
        for (final Iterator<Map.Entry<Player, UUID>> iter = trackingMap.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry<Player, UUID> entry = iter.next();
            if (entry.getKey().isRemoved()) {
                iter.remove();
            } else if (entry.getValue().equals(mob.getUUID())) {
                ServerPlayNetworking.send((ServerPlayer) entry.getKey(), new SyncePathMessage(debugNodesVisited, debugNodesNotVisited, debugNodesPath));
            }
        }
    }

    protected boolean onLadderGoingUp(final MNode currentNode, final BlockPos dPos) {
        return currentNode.isLadder() && (dPos.getY() >= 0 || dPos.getX() != 0 || dPos.getZ() != 0);
    }

    public void setEntitySizes(LivingEntity entity) {
        if (entity instanceof ICustomSizeNavigator) {
            entitySizeXZStart = -(int) ((ICustomSizeNavigator) entity).getXZNavSize();
            entitySizeXZEnd = (int) ((ICustomSizeNavigator) entity).getXZNavSize();
            entitySizeY = ((ICustomSizeNavigator) entity).getYNavSize();
        } else {
            float bbWidth = entity.getBbWidth();
            if (bbWidth <= 1.0F) {
                entitySizeXZStart = 0;
                entitySizeXZEnd = 1;
            } else {
                entitySizeXZStart = -(int) Math.floor(entity.getBbWidth() / 2.0F);
                entitySizeXZEnd = (int) Math.floor(entity.getBbWidth() / 2.0F);
            }
            entitySizeY = Mth.ceil(entity.getBbHeight());
        }
        allowJumpPointSearchTypeWalk = false;
    }

    protected double computeCost(
            final BlockPos dPos,
            final boolean isSwimming,
            final boolean onPath,
            final boolean onRails,
            final boolean railsExit,
            final boolean swimStart,
            final boolean corner,
            final BlockState state,
            final BlockPos blockPos) {
        double cost = Math.sqrt(dPos.getX() * dPos.getX() + dPos.getY() * dPos.getY() + dPos.getZ() * dPos.getZ());

        if (dPos.getY() != 0 && !(Math.abs(dPos.getY()) <= 1 && world.getBlockState(blockPos).getBlock() instanceof StairBlock)) {
            if (dPos.getY() > 0) {
                cost *= pathingOptions.jumpCost * Math.abs(dPos.getY());
            } else {
                cost *= pathingOptions.dropCost * Math.abs(dPos.getY());
            }
        }

        if (world.getBlockState(blockPos).hasProperty(BlockStateProperties.OPEN)) {
            cost *= pathingOptions.traverseToggleAbleCost;
        }

        if (onPath) {
            cost *= pathingOptions.onPathCost;
        }

        if (onRails) {
            cost *= pathingOptions.onRailCost;
        }

        if (railsExit) {
            cost *= pathingOptions.railsExitCost;
        }

        if (state.getBlock() instanceof VineBlock) {
            cost *= pathingOptions.vineCost;
        }

        if (isSwimming) {
            if (swimStart) {
                cost *= pathingOptions.swimCostEnter;
            } else {
                cost *= pathingOptions.swimCost;
            }
        }

        return cost;
    }

    public PathResult getResult() {
        return result;
    }

    @Override
    public final Path call() {
        try {
            return search();
        } catch (final Exception e) {
            Citadel.LOGGER.warn("Pathfinding Exception", e);
        }

        return null;
    }

    protected Path search() {
        MNode bestNode = getAndSetupStartNode();

        double bestNodeResultScore = Double.MAX_VALUE;

        while (!nodesOpen.isEmpty()) {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            final MNode currentNode = nodesOpen.poll();

            totalNodesVisited++;

            if (totalNodesVisited > PathfindingConstants.maxPathingNodes || totalNodesVisited > maxRange * maxRange) {
                break;
            }
            currentNode.setCounterVisited(totalNodesVisited);

            handleDebugOptions(currentNode);
            currentNode.setClosed();

            final boolean isViablePosition =
                    isInRestrictedArea(currentNode.pos) && getSurfaceType(world, world.getBlockState(currentNode.pos.below()), currentNode.pos.below()) == SurfaceType.WALKABLE;
            if (isViablePosition && isAtDestination(currentNode)) {
                bestNode = currentNode;
                result.setPathReachesDestination(true);
                break;
            }

            final double nodeResultScore =
                    getNodeResultScore(currentNode);
            if (isViablePosition && nodeResultScore < bestNodeResultScore && !currentNode.isCornerNode()) {
                bestNode = currentNode;
                bestNodeResultScore = nodeResultScore;
            }

            if (!hardXzRestriction || isViablePosition) {
                walkCurrentNode(currentNode);
            }
        }

        return finalizePath(bestNode);
    }

    private void handleDebugOptions(final MNode currentNode) {
        if (debugDrawEnabled) {
            addNodeToDebug(currentNode);
        }

        if (Pathfinding.isDebug()) {
            Citadel.LOGGER.info(String.format("Examining MNode [%d,%d,%d] ; g=%f ; f=%f",
                    currentNode.pos.getX(), currentNode.pos.getY(), currentNode.pos.getZ(), currentNode.getCost(), currentNode.getScore()));
        }
    }

    private void addNodeToDebug(final MNode currentNode) {
        debugNodesNotVisited.remove(currentNode);
        debugNodesVisited.add(currentNode);
    }

    private void addPathNodeToDebug(final MNode node) {
        debugNodesVisited.remove(node);
        debugNodesPath.add(node);
    }

    private void walkCurrentNode(final MNode currentNode) {
        BlockPos dPos = BLOCKPOS_IDENTITY;
        if (currentNode.parent != null) {
            dPos = currentNode.pos.subtract(currentNode.parent.pos);
        }

        if (onLadderGoingUp(currentNode, dPos)) {
            walk(currentNode, BLOCKPOS_UP);
        }

        if (onLadderGoingDown(currentNode, dPos)) {
            walk(currentNode, BLOCKPOS_DOWN);
        }
        if (pathingOptions.canClimb()) {
            if (getHighest(currentNode).getFirst() > 1) {
                walk(currentNode, BLOCKPOS_IDENTITY.above(getHighest(currentNode).getFirst()));
            }
            if (currentNode.parent != null && dPos.getX() == 0 && dPos.getZ() == 0 && dPos.getY() > 1) {
                if (getHighest(currentNode.parent).getSecond() != null)
                    walk(currentNode, getHighest(currentNode.parent).getSecond());
            }
        }

        if ((currentNode.parent == null || !currentNode.parent.pos.equals(currentNode.pos.below())) && currentNode.isCornerNode()) {
            walk(currentNode, BLOCKPOS_DOWN);
            return;
        }

        if ((isPassable(currentNode.pos.below(), currentNode.parent) && (!currentNode.isSwimming() && isLiquid(world.getBlockState(currentNode.pos.below()))))) {
            walk(currentNode, BLOCKPOS_DOWN);
        }

        if (dPos.getZ() <= 0) {
            walk(currentNode, BLOCKPOS_NORTH);
        }

        if (dPos.getX() >= 0) {
            walk(currentNode, BLOCKPOS_EAST);
        }

        if (dPos.getZ() >= 0) {
            walk(currentNode, BLOCKPOS_SOUTH);
        }

        if (dPos.getX() <= 0) {
            walk(currentNode, BLOCKPOS_WEST);
        }
    }

    private boolean onLadderGoingDown(final MNode currentNode, final BlockPos dPos) {
        return (dPos.getY() <= 0 || dPos.getX() != 0 || dPos.getZ() != 0) && isLadder(currentNode.pos.below());
    }

    private MNode getAndSetupStartNode() {
        MNode startNode = new MNode(start, computeHeuristic(start));
        if (pathingOptions.isFlying() && start.closerThan(end, maxRange)) {
            startNode = new MNode(end, computeHeuristic(end));
        }
        if (isLadder(start)) {
            startNode.setLadder();
        } else if (isLiquid(world.getBlockState(start.below()))) {
            startNode.setSwimming();
        }

        startNode.setOnRails(pathingOptions.canUseRails() && world.getBlockState(start).getBlock() instanceof BaseRailBlock);

        nodesOpen.offer(startNode);
        nodesVisited.put(computeNodeKey(start), startNode);

        ++totalNodesAdded;

        return startNode;
    }

    public boolean isLiquid(final BlockState state) {
        return state.liquid() || (!state.blocksMotion() && !state.getFluidState().isEmpty());
    }

    private Path finalizePath(final MNode targetNode) {
        int pathLength = 1;
        int railsLength = 0;
        @Nullable MNode node = targetNode;
        while (node.parent != null) {
            ++pathLength;
            if (node.isOnRails()) {
                ++railsLength;
            }
            node = node.parent;
        }

        final Node[] points = new Node[pathLength];
        points[0] = new PathPointExtended(node.pos);
        if (debugDrawEnabled) {
            addPathNodeToDebug(node);
        }

        @Nullable MNode nextInPath = null;
        @Nullable Node next = null;
        node = targetNode;
        while (node.parent != null) {
            if (debugDrawEnabled) {
                addPathNodeToDebug(node);
            }

            --pathLength;

            final BlockPos pos = node.pos;

            if (node.isSwimming()) {
                pos.offset(BLOCKPOS_DOWN);
            }

            final PathPointExtended p = new PathPointExtended(pos);
            if (railsLength >= 8) {
                p.setOnRails(node.isOnRails());
                if (p.isOnRails() && (!node.parent.isOnRails() || node.parent.parent == null)) {
                    p.setRailsEntry();
                } else if (p.isOnRails() && points.length > pathLength + 1) {
                    final PathPointExtended point = ((PathPointExtended) points[pathLength + 1]);
                    if (!point.isOnRails()) {
                        point.setRailsExit();
                    }
                }
            }

            if (onALadder(node, nextInPath, pos)) {
                p.setOnLadder(true);
                if (nextInPath.pos.getY() > pos.getY()) {
                    setLadderFacing(world, pos, p);
                }
            } else if (onALadder(node.parent, node.parent, pos)) {
                p.setOnLadder(true);
            }

            if (next != null) {
                next.cameFrom = p;
            }
            next = p;
            points[pathLength] = p;

            nextInPath = node;
            node = node.parent;
        }

        doDebugPrinting(points);

        return new Path(Arrays.asList(points), getPathTargetPos(targetNode), isAtDestination(targetNode));
    }

    protected BlockPos getPathTargetPos(final MNode finalNode) {
        return finalNode.pos;
    }

    private void doDebugPrinting(final Node[] points) {
        if (Pathfinding.isDebug()) {
            Citadel.LOGGER.info("Path found:");

            for (final Node p : points) {
                Citadel.LOGGER.info("Step: [{},{},{}]", p.x, p.y, p.z);
            }

            Citadel.LOGGER.info("Total Nodes Visited {} / {}", totalNodesVisited, totalNodesAdded);
        }
    }

    protected abstract double computeHeuristic(BlockPos pos);

    protected abstract boolean isAtDestination(MNode n);

    protected abstract double getNodeResultScore(MNode n);

    protected final boolean walk(final MNode parent, BlockPos dPos) {
        BlockPos pos = parent.pos.offset(dPos);

        final int newY = getGroundHeight(parent, pos);

        if (newY < world.getMinBuildHeight()) {
            return false;
        }

        boolean corner = false;
        if (pos.getY() != newY) {
            if (!parent.isCornerNode() && newY - pos.getY() > 0 && (parent.parent == null || !parent.parent.pos.equals(parent.pos.offset(new BlockPos(0, newY - pos.getY(), 0))))) {
                dPos = new BlockPos(0, newY - pos.getY(), 0);
                pos = parent.pos.offset(dPos);
                corner = true;
            }
            else if (!parent.isCornerNode() && newY - pos.getY() < 0 && (dPos.getX() != 0 || dPos.getZ() != 0) && (parent.parent == null || !parent.pos.below()
                    .equals(parent.parent.pos))) {
                dPos = new BlockPos(dPos.getX(), 0, dPos.getZ());
                pos = parent.pos.offset(dPos);
                corner = true;
            }
            else if (!(pathingOptions.canClimb() && dPos.getY() > 1)) {
                dPos = dPos.offset(0, newY - pos.getY(), 0);
                pos = new BlockPos(pos.getX(), newY, pos.getZ());
            }
        }

        int nodeKey = computeNodeKey(pos);
        MNode node = nodesVisited.get(nodeKey);
        if (nodeClosed(node)) {
            return false;
        }

        final boolean isSwimming = calculateSwimming(world, pos, node);

        if (isSwimming && !pathingOptions.canSwim()) {
            return false;
        }

        final boolean swimStart = isSwimming && !parent.isSwimming();
        final BlockState state = world.getBlockState(pos);
        final boolean onRoad = false;
        final boolean onRails = pathingOptions.canUseRails() && world.getBlockState(corner ? pos.below() : pos).getBlock() instanceof BaseRailBlock;
        final boolean railsExit = !onRails && parent.isOnRails();
        final double stepCost = computeCost(dPos, isSwimming, onRoad, onRails, railsExit, swimStart, corner, state, pos);
        final double heuristic = computeHeuristic(pos);
        final double cost = parent.getCost() + stepCost;
        final double score = cost + heuristic;

        if (node == null) {
            node = createNode(parent, pos, nodeKey, isSwimming, heuristic, cost, score);
            node.setOnRails(onRails);
            node.setCornerNode(corner);
        } else if (updateCurrentNode(parent, node, heuristic, cost, score)) {
            return false;
        }

        nodesOpen.offer(node);

        if (pathingOptions.canClimb() && dPos.getY() > 1)
            return true;

        performJumpPointSearch(parent, dPos, node);

        return true;
    }

    private void performJumpPointSearch(final MNode parent, final BlockPos dPos, final MNode node) {
        if (allowJumpPointSearchTypeWalk && node.getHeuristic() <= parent.getHeuristic()) {
            walk(node, dPos);
        }
    }

    private MNode createNode(
            final MNode parent, final BlockPos pos, final int nodeKey,
            final boolean isSwimming, final double heuristic, final double cost, final double score) {
        final MNode node;
        node = new MNode(parent, pos, cost, heuristic, score);
        nodesVisited.put(nodeKey, node);
        if (debugDrawEnabled) {
            debugNodesNotVisited.add(node);
        }

        if (isLadder(pos)) {
            node.setLadder();
        } else if (isSwimming) {
            node.setSwimming();
        }

        totalNodesAdded++;
        node.setCounterAdded(totalNodesAdded);
        return node;
    }

    private boolean updateCurrentNode(final MNode parent, final MNode node, final double heuristic, final double cost, final double score) {
        if (score >= node.getScore()) {
            return true;
        }

        if (!nodesOpen.remove(node)) {
            return true;
        }

        node.parent = parent;
        node.setSteps(parent.getSteps() + 1);
        node.setCost(cost);
        node.setHeuristic(heuristic);
        node.setScore(score);
        return false;
    }

    protected int getGroundHeight(final MNode parent, final BlockPos pos) {
        if (checkHeadBlock(parent, pos)) {
            return handleTargetNotPassable(parent, pos.above(), world.getBlockState(pos.above()));
        }

        final BlockState target = world.getBlockState(pos);
        if (parent != null && !isPassableBB(parent.pos, pos, parent)) {
            return handleTargetNotPassable(parent, pos, target);
        }

        int i = 0;
        BlockState below = null;
        SurfaceType lastSurfaceType = null;
        while (i < maxNavigableGroundDist) {
            i++;
            below = world.getBlockState(pos.below(i));
            if (pathingOptions.isFlying()) {
                lastSurfaceType = isFlyable(below, pos, parent);
                if (lastSurfaceType == SurfaceType.FLYABLE) {
                    return pos.getY();
                }
            } else {
                lastSurfaceType = isWalkableSurface(below, pos);
                if (lastSurfaceType == SurfaceType.WALKABLE) {
                    return pos.getY();
                }
            }
        }
        return lastSurfaceType != SurfaceType.NOT_PASSABLE && below != null ? handleNotStanding(parent, pos, below) : -1;
    }

    private int handleNotStanding(@Nullable final MNode parent, final BlockPos pos, final BlockState below) {
        final boolean isSwimming = parent != null && parent.isSwimming();

        if (isLiquid(below)) {
            return handleInLiquid(pos, below, isSwimming);
        }

        if (isLadder(below.getBlock(), pos.below())) {
            return pos.getY();
        }

        return checkDrop(parent, pos, isSwimming);
    }

    private int checkDrop(@Nullable final MNode parent, final BlockPos pos, final boolean isSwimming) {
        final boolean canDrop = parent != null && !parent.isLadder();
        boolean isChonker = true;

        if (pathingOptions.canClimb() && parent != null && pos.getY() > parent.pos.getY() + 1) {
            return pos.getY();
        }
        if (!isChonker) {
            if (!canDrop || isSwimming || ((parent.pos.getX() != pos.getX() || parent.pos.getZ() != pos.getZ()) && isPassableBBFull(parent.pos.below(), parent)
                    && isWalkableSurface(world.getBlockState(parent.pos.below()), parent.pos.below()) == SurfaceType.DROPABLE)) {
                return -1;
            }
        }

        for (int i = 2; i <= 10; i++) {
            final BlockState below = world.getBlockState(pos.below(i));
            if (isWalkableSurface(below, pos) == SurfaceType.WALKABLE && i <= 4 || below.liquid()) {
                return pos.getY() - i + 1;
            } else if (below.isAir()) {
                return -1;
            }
        }

        return -1;
    }

    private int handleInLiquid(final BlockPos pos, final BlockState below, final boolean isSwimming) {
        if (isSwimming) {
            return pos.getY();
        }

        if (pathingOptions.canSwim() && isWater(world, pos.below())) {
            return pos.getY();
        }

        return -1;
    }

    private int handleTargetNotPassable(@Nullable final MNode parent, final BlockPos pos, final BlockState target) {
        final boolean canJump = parent != null && !parent.isLadder() && !parent.isSwimming();
        if (!canJump || getSurfaceType(world, target, pos) != SurfaceType.WALKABLE) {
            return -1;
        }
        if (!isPassable(pos.above(2), parent)) {
            final VoxelShape bb1 = world.getBlockState(pos).getBlockSupportShape(world, pos);
            final VoxelShape bb2 = world.getBlockState(pos.above(2)).getBlockSupportShape(world, pos.above(2));
            if ((pos.above(2).getY() + getStartY(bb2, 1)) - (pos.getY() + getEndY(bb1, 0)) < 2) {
                return -1;
            }
        }

        if (!isPassable(parent.pos.above(2), parent)) {
            final VoxelShape bb1 = world.getBlockState(pos).getBlockSupportShape(world, pos);
            final VoxelShape bb2 = world.getBlockState(parent.pos.above(2)).getBlockSupportShape(world, parent.pos.above(2));
            if ((parent.pos.above(2).getY() + getStartY(bb2, 1)) - (pos.getY() + getEndY(bb1, 0)) < 2) {
                return -1;
            }
        }

        final BlockState parentBelow = world.getBlockState(parent.pos.below());
        final VoxelShape parentBB = parentBelow.getBlockSupportShape(world, parent.pos.below());

        double parentY = parentBB.max(Direction.Axis.Y);
        double parentMaxY = parentY + parent.pos.below().getY();
        final double targetMaxY = target.getBlockSupportShape(world, pos).max(Direction.Axis.Y) + pos.getY();
        if (targetMaxY - parentMaxY < maxJumpHeight) {
            return pos.getY() + 1;
        }
        if (target.getBlock() instanceof StairBlock
                && parentY - HALF_A_BLOCK < maxJumpHeight
                && target.getValue(StairBlock.HALF) == Half.BOTTOM
                && getXZFacing(parent.pos, pos) == target.getValue(StairBlock.FACING)) {
            return pos.getY() + 1;
        }
        return -1;
    }

    private Pair<Integer, BlockPos> getHighest(MNode node) {
        int max = 1;
        BlockPos pos = node.pos;
        BlockPos direction = null;
        if (world.getBlockState(pos.north()).canOcclude()) {
            if (climbableTop(pos.north(), Direction.SOUTH, node) > max) {
                max = climbableTop(pos.north(), Direction.SOUTH, node);
                direction = BLOCKPOS_NORTH;
            }
        }
        if (world.getBlockState(pos.east()).canOcclude()) {
            if (climbableTop(pos.east(), Direction.WEST, node) > max) {
                max = climbableTop(pos.east(), Direction.WEST, node);
                direction = BLOCKPOS_EAST;
            }
        }
        if (world.getBlockState(pos.south()).canOcclude()) {
            if (climbableTop(pos.south(), Direction.NORTH, node) > max) {
                max = climbableTop(pos.south(), Direction.NORTH, node);
                direction = BLOCKPOS_SOUTH;
            }
        }
        if (world.getBlockState(pos.west()).canOcclude()) {
            if (climbableTop(pos.west(), Direction.EAST, node) > max) {
                max = climbableTop(pos.west(), Direction.EAST, node);
                direction = BLOCKPOS_WEST;
            }
        }
        return new Pair<>(max, direction);
    }

    private int climbableTop(BlockPos pos, Direction direction, MNode node) {
        BlockState target = world.getBlockState(pos);
        BlockState origin;
        int i = 0;
        while (target.canOcclude()) {
            pos = pos.above();
            target = world.getBlockState(pos);
            origin = world.getBlockState(pos.relative(direction));
            if (!isPassable(origin, pos.relative(direction), node)) {
                i = 0;
                break;
            }
            i++;
        }
        return i;
    }

    private boolean checkHeadBlock(@Nullable final MNode parent, final BlockPos pos) {
        BlockPos localPos = pos;
        final VoxelShape bb = world.getBlockState(localPos).getCollisionShape(world, localPos);
        if (bb.max(Direction.Axis.Y) < 1) {
            localPos = pos.above();
        }

        if (parent == null || !isPassableBB(parent.pos, pos.above(), parent)) {
            final VoxelShape bb1 = world.getBlockState(pos.below()).getBlockSupportShape(world, pos.below());
            final VoxelShape bb2 = world.getBlockState(pos.above()).getBlockSupportShape(world, pos.above());
            if ((pos.above().getY() + getStartY(bb2, 1)) - (pos.below().getY() + getEndY(bb1, 0)) < 2) {
                return true;
            }
            if (parent != null) {
                final VoxelShape bb3 = world.getBlockState(parent.pos.below()).getBlockSupportShape(world, pos.below());
                if ((pos.above().getY() + getStartY(bb2, 1)) - (parent.pos.below().getY() + getEndY(bb3, 0)) < 1.75) {
                    return true;
                }
            }
        }

        if (parent != null) {
            final BlockState hereState = world.getBlockState(localPos.below());
            final VoxelShape bb1 = world.getBlockState(pos).getBlockSupportShape(world, pos);
            final VoxelShape bb2 = world.getBlockState(localPos.above()).getBlockSupportShape(world, localPos.above());
            if ((localPos.above().getY() + getStartY(bb2, 1)) - (pos.getY() + getEndY(bb1, 0)) >= 2) {
                return false;
            }

            return isLiquid(hereState) && !isPassable(pos, parent);
        }
        return false;
    }

    private double getStartY(final VoxelShape bb, final int def) {
        return bb.isEmpty() ? def : bb.min(Direction.Axis.Y);
    }

    private double getEndY(final VoxelShape bb, final int def) {
        return bb.isEmpty() ? def : bb.max(Direction.Axis.Y);
    }

    protected boolean isPassable(final BlockState block, final BlockPos pos, final MNode parent) {
        final BlockPos parentPos = parent == null ? start : parent.pos;
        final BlockState parentBlock = world.getBlockState(parentPos);

        if (parentBlock.getBlock() instanceof TrapDoorBlock) {
            final BlockPos dir = pos.subtract(parentPos);
            if (dir.getX() != 0 || dir.getZ() != 0) {
                final Direction direction = getXZFacing(parentPos, pos);
                final Direction facing = parentBlock.getValue(TrapDoorBlock.FACING);
                if (direction == facing.getOpposite()) {
                    return false;
                }
            }
        }

        if (!block.isAir()) {
            final VoxelShape shape = block.getBlockSupportShape(world, pos);
            if (block.blocksMotion() && !(shape.isEmpty() || shape.max(Direction.Axis.Y) <= 0.1)) {
                if (block.getBlock() instanceof TrapDoorBlock) {
                    final BlockPos dir = pos.subtract(parentPos);
                    if (dir.getY() != 0 && dir.getX() == 0 && dir.getZ() == 0) {
                        return true;
                    }

                    final Direction direction = getXZFacing(parentPos, pos);
                    final Direction facing = block.getValue(TrapDoorBlock.FACING);

                    if (direction == facing.getOpposite()) {
                        return true;
                    }

                    return direction != facing;
                } else {
                    return pathingOptions.canEnterDoors() && (block.getBlock() instanceof DoorBlock
                            || block.getBlock() instanceof FenceGateBlock)
                            || block.getBlock() instanceof PressurePlateBlock
                            || block.getBlock() instanceof SignBlock
                            || block.getBlock() instanceof AbstractBannerBlock;
                }
            } else if (block.getBlock() instanceof FireBlock || block.getBlock() instanceof SweetBerryBushBlock) {
                return false;
            } else {
                if (isLadder(block.getBlock(), pos)) {
                    return true;
                }

                if (shape.isEmpty() || shape.max(Direction.Axis.Y) <= 0.125 && !isLiquid((block)) && (block.getBlock() != Blocks.SNOW || block.getValue(SnowLayerBlock.LAYERS) == 1)) {
                    return block.isPathfindable(PathComputationType.LAND);
                }
                return false;
            }
        }

        return true;
    }

    protected boolean isPassable(final BlockPos pos, final MNode parent) {
        final BlockState state = world.getBlockState(pos);
        final VoxelShape shape = state.getBlockSupportShape(world, pos);
        if (passabilityNavigator != null && passabilityNavigator.isBlockExplicitlyNotPassable(state, pos, pos)) {
            return false;
        }
        if ((shape.isEmpty() || shape.max(Direction.Axis.Y) <= 0.1)) {
            if (passabilityNavigator != null && passabilityNavigator.isBlockExplicitlyPassable(state, pos, pos)) {
                return isPassable(state, pos, parent);
            }
            return true;
        }
        return isPassable(state, pos, parent);
    }

    protected boolean isPassableBBFull(final BlockPos pos, MNode parent) {
        for (int i = entitySizeXZStart; i <= entitySizeXZEnd; i++) {
            for (int j = 0; j < entitySizeY; j++) {
                for (int k = entitySizeXZStart; k <= entitySizeXZEnd; k++) {
                    if (!isPassable(pos.offset(i, j, k), parent)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean isPassableBB(final BlockPos parentPos, final BlockPos pos, MNode parent) {
        Direction facingDir = getXZFacing(parentPos, pos);
        if (facingDir == Direction.DOWN || facingDir == Direction.UP)
            return false;
        facingDir = facingDir.getClockWise();
        for (int i = entitySizeXZStart; i <= entitySizeXZEnd; i++) {
            for (int j = 0; j < entitySizeY; j++) {
                if (!isPassable(pos.relative(facingDir, i).above(j), parent)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean isPassableBBDown(final BlockPos parentPos, final BlockPos pos, MNode parent) {
        Direction facingDir = getXZFacing(parentPos, pos);
        if (facingDir == Direction.DOWN || facingDir == Direction.UP)
            return false;
        facingDir = facingDir.getClockWise();
        for (int i = entitySizeXZStart; i <= entitySizeXZEnd; i++) {
            for (int j = 0; j < entitySizeY; j++) {
                if (!isPassable(pos.relative(facingDir, i).above(j), parent) || pos.getY() <= parentPos.getY()) {
                    return false;
                }
            }
        }
        return true;
    }

    protected SurfaceType isFlyable(final BlockState blockState, final BlockPos pos, MNode parent) {
        final Block block = blockState.getBlock();
        if (block instanceof FenceBlock
                || block instanceof FenceGateBlock
                || block instanceof WallBlock
                || block instanceof FireBlock
                || block instanceof CampfireBlock
                || block instanceof BambooStalkBlock
                || block instanceof BambooSaplingBlock
                || (blockState.getShape(world, pos).max(Direction.Axis.Y) > 1.0)) {
            return SurfaceType.NOT_PASSABLE;
        }
        final FluidState fluid = world.getFluidState(pos);
        if (!fluid.isEmpty() && (fluid.getType() == Fluids.LAVA || fluid.getType() == Fluids.FLOWING_LAVA)) {
            return SurfaceType.NOT_PASSABLE;
        }
        if (isPassable(blockState, pos, parent)) {
            return SurfaceType.FLYABLE;
        }
        return SurfaceType.DROPABLE;
    }

    protected SurfaceType isWalkableSurface(final BlockState blockState, final BlockPos pos) {
        final Block block = blockState.getBlock();
        if (block instanceof FenceBlock
                || block instanceof FenceGateBlock
                || block instanceof WallBlock
                || block instanceof FireBlock
                || block instanceof CampfireBlock
                || block instanceof BambooStalkBlock
                || block instanceof BambooSaplingBlock
                || (blockState.getShape(world, pos).max(Direction.Axis.Y) > 1.0)) {
            return SurfaceType.NOT_PASSABLE;
        }

        final FluidState fluid = world.getFluidState(pos);
        if (!fluid.isEmpty() && (fluid.getType() == Fluids.LAVA || fluid.getType() == Fluids.FLOWING_LAVA)) {
            return SurfaceType.NOT_PASSABLE;
        }

        if (block instanceof SignBlock) {
            return SurfaceType.DROPABLE;
        }

        if (blockState.isSolid()
                || (blockState.getBlock() == Blocks.SNOW && blockState.getValue(SnowLayerBlock.LAYERS) > 1)
                || block instanceof WoolCarpetBlock) {
            return SurfaceType.WALKABLE;
        }

        return SurfaceType.DROPABLE;
    }

    protected boolean isLadder(final Block block, final BlockPos pos) {
        return block instanceof LadderBlock;
    }

    protected boolean isLadder(final BlockPos pos) {
        return isLadder(world.getBlockState(pos).getBlock(), pos);
    }

    public void setPathingOptions(final PathingOptions pathingOptions) {
        this.pathingOptions = pathingOptions;
    }

    public boolean isInRestrictedArea(final BlockPos pos) {
        if (restrictionType == AbstractAdvancedPathNavigate.RestrictionType.NONE) {
            return true;
        }

        final boolean isInXZ = pos.getX() <= maxX && pos.getZ() <= maxZ && pos.getZ() >= minZ && pos.getX() >= minX;
        if (!isInXZ) {
            return false;
        }

        if (restrictionType == AbstractAdvancedPathNavigate.RestrictionType.XZ) {
            return true;
        }
        return pos.getY() <= maxY && pos.getY() >= minY;
    }
}