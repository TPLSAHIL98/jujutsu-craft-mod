package com.jujutsucraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import com.jujutsucraft.JujutsuCraftMod;
import com.jujutsucraft.ability.client.HollowPurpleClientManager;

@Environment(EnvType.CLIENT)
public class JujutsuCraftClient implements ClientModInitializer {
    
    public static KeyBinding HOLLOW_PURPLE_KEY;
    
    @Override
    public void onInitializeClient() {
        JujutsuCraftMod.LOGGER.info("[Jujutsu Craft] Initializing client module...");
        
        HOLLOW_PURPLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.jujutsucraft.hollow_purple",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.jujutsucraft.abilities"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HollowPurpleClientManager.handleClientTick(client);
        });
        
        JujutsuCraftMod.LOGGER.info("[Jujutsu Craft] Client module ready!");
    }
}
