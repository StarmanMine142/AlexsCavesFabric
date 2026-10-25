package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;

public interface AlexsCavesBoat {

    AlexsCavesBoat.Type getACBoatType();

    enum Type {
        PEWEN("pewen", ACBlockRegistry.PEWEN_PLANKS, ACItemRegistry.PEWEN_BOAT, ACItemRegistry.PEWEN_CHEST_BOAT),
        THORNWOOD("thornwood", ACBlockRegistry.PEWEN_PLANKS, ACItemRegistry.THORNWOOD_BOAT, ACItemRegistry.THORNWOOD_CHEST_BOAT);

        private final String name;
        private final Block plank;
        private final Item drop;
        private final Item chestDrop;

        Type(String name, Block plank, Item drop, Item chestDrop) {
            this.name = name;
            this.plank = plank;
            this.drop = drop;
            this.chestDrop = chestDrop;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlankSupplier() {
            return this.plank;
        }


        public Item getDropSupplier() {
            return this.drop;
        }

        public Item getChestDropSupplier() {
            return this.chestDrop;
        }

        public String toString() {
            return this.name;
        }

        public static Type byName(String name) {
            return Arrays.stream(values()).filter(t -> t.getName().equals(name)).findFirst().orElse(values()[0]);
        }

        public static Type byId(int id) {
            return values()[id < 0 || id >= values().length ? 0 : id];
        }
    }
}