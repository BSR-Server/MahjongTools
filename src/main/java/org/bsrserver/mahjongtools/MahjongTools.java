package org.bsrserver.mahjongtools;

import net.minecraft.server.command.CommandManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import org.bsrserver.mahjongtools.command.CalculateHandler;

public class MahjongTools implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Hello Fabric world!");
    }
}
