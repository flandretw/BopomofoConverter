package flandretw.bopomofo.translator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.2 {
/*import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
*///?} else {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import flandretw.bopomofo.translator.config.BopomofoConfig;
import flandretw.bopomofo.translator.config.BopomofoConfigScreen;

public class BopomofoTranslatorClient implements ClientModInitializer {
    private static boolean openConfigRequested = false;

    @Override
    public void onInitializeClient() {
        BopomofoConfig.getInstance(); // Load config

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigRequested) {
                openConfigRequested = false;
                //? if >=26.2 {
                /*client.gui.setScreen(new BopomofoConfigScreen(null));
                *///?} else {
                client.setScreen(new BopomofoConfigScreen(null));
                //?}
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            //? if >=26.2 {
            /*var openConfig = ClientCommands.literal("bopomofo")
                .executes(context -> {
                    openConfigRequested = true;
                    return 1;
                });

            var openConfigAlias = ClientCommands.literal("bopomofo-translator")
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

            var openConfigAlias = ClientCommandManager.literal("bopomofo-translator")
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
