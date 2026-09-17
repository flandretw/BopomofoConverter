package flandretw.bopomofo.converter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
*///?} else {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import flandretw.bopomofo.converter.config.BopomofoConfig;
import flandretw.bopomofo.converter.config.BopomofoConfigScreen;

public class BopomofoConverterClient implements ClientModInitializer {
    private static boolean openConfigRequested = false;

    @Override
    public void onInitializeClient() {
        BopomofoConfig.getInstance(); // Load config

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigRequested) {
                openConfigRequested = false;
                //? if >=26.2 {
                /*Minecraft.getInstance().gui.setScreen(new BopomofoConfigScreen(null));
                *///?} else {
                Minecraft.getInstance().setScreen(new BopomofoConfigScreen(null));
                //?}
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            //? if >=26.1 {
            /*var openConfig = ClientCommands.literal("bopomofo")
                .executes(context -> {
                    openConfigRequested = true;
                    return 1;
                });

            var openConfigAlias = ClientCommands.literal("bopomofo-converter")
                .executes(context -> {
                    openConfigRequested = true;
                    return 1;
                });
            *///?} else {
            var openConfig = ClientCommandManager.literal("bopomofo")
                .executes(context -> {
                    openConfigRequested = true;
                    return 1;
                });

            var openConfigAlias = ClientCommandManager.literal("bopomofo-converter")
                .executes(context -> {
                    openConfigRequested = true;
                    return 1;
                });
            //?}

            dispatcher.register(openConfig);
            dispatcher.register(openConfigAlias);
        });
    }
}
