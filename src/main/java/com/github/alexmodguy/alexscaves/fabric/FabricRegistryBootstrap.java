package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.layered.ACModelLayers;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.feature.ACFeatureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.ACStructureRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.ACStructurePieceRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACCreativeTabRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDataComponentRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.github.alexmodguy.alexscaves.server.recipe.ACRecipeRegistry;

final class FabricRegistryBootstrap {

    private FabricRegistryBootstrap() {
    }

    static void bootstrapCommon() {
        ACLootTableRegistry.registerLootTables();
        ACEntityDataRegistry.register();
        ACPOIRegistry.registerFabricBlockStates();
        ACFeatureRegistry.init();
        ACStructureRegistry.init();
        ACStructurePieceRegistry.init();
        ACStructureProcessorRegistry.init();
        ACBiomeRegistry.init();
        ACDataComponentRegistry.init();
        ACFluidRegistry.postInit();
        ACCreativeTabRegistry.init();
        ACRecipeRegistry.init();
        ACAdvancementTriggerRegistry.init();
        bootstrapEntities();
    }

    static void bootstrapClient() {
        ACModelLayers.registerFabric();
        ClientLayerRegistry.register();
        ClientProxy.registerFabricClientLifecycle();
        ClientProxy.registerFabricShaders();
        ClientProxy.registerFabricBuiltinItemRenderers();
        ClientProxy.registerFabricFluidRendering();
    }

    private static void bootstrapEntities() {
        ACEntityRegistry.initializeAttributes();
        ACEntityRegistry.spawnPlacements();
    }
}