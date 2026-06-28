package com.jujutsucraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.jujutsucraft.ability.HollowPurpleManager;
import com.jujutsucraft.config.JujutsuConfig;
import com.jujutsucraft.network.HollowPurpleC2SPacket;
import com.jujutsucraft.command.HollowPurpleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujutsuCraftMod implements ModInitializer {
    public static final String MOD_ID = "jujutsucraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Jujutsu Craft] Initializing mod for Minecraft 1.21.1...");
        
        JujutsuConfig.loadConfig();
        
        ServerPlayNetworking.registerGlobalReceiver(
            HollowPurpleC2SPacket.ID,
            (packet, player, responseSender) -> {
                HollowPurpleManager.fireHollowPurple(player, packet.getChargeTime());
            }
        );
        
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(HollowPurpleManager::updateCooldown);
        });
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            HollowPurpleCommand.register(dispatcher);
        });
        
        LOGGER.info("[Jujutsu Craft] Mod initialized! Press G to use Hollow Purple");
    }
}
