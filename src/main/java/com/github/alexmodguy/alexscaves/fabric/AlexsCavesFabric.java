package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.event.CommonEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class AlexsCavesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ACNetworkingFabric.registerCommon();
        ACMultipartFabric.registerCommon();
        FabricRegistryBootstrap.bootstrapCommon();
        AlexsCaves.setProxy(new CommonProxy());
        ACFluidRegistry.register();
        AlexsCaves.init();
        ServerLifecycleEvents.SERVER_STARTING.register(CommonEvents::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(CommonEvents::onServerStopped);
    }
}
