package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;

public class AnimalHurtByTargetDefendBabiesGoal extends HurtByTargetGoal {

    private Animal animal;

    public AnimalHurtByTargetDefendBabiesGoal(Animal animal, Class<?>... ignores) {
        super(animal, ignores);
        this.setAlertOthers();
        this.animal = animal;
    }

    protected void alertOther(Mob mob, LivingEntity target) {
        if(animal.isBaby()){
            super.alertOther(mob, target);
        }
    }
}
