package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.world.entity.ai.goal.GoalSelector;

public interface MobTargetAccessor {

    GoalSelector ac_getGoalSelector();

    GoalSelector ac_getTargetSelector();
}
