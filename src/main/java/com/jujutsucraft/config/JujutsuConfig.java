package com.jujutsucraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jujutsucraft.JujutsuCraftMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JujutsuConfig {
    
    public static float damage = 30.0f;
    public static float explosionRadius = 15.0f;
    public static int cooldown = 15;
    public static float minChargeTime = 2.0f;
    public static float maxChargeTime = 3.0f;
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_PATH = "config/jujutsucraft.json";
    
    public static void loadConfig() {
        File configFile = new File(CONFIG_PATH);
        File configDir = configFile.getParentFile();
        
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        if (!configFile.exists()) {
            saveConfig();
            JujutsuCraftMod.LOGGER.info("[Jujutsu Craft] Created default config at: " + CONFIG_PATH);
            return;
        }
        
        try (FileReader reader = new FileReader(configFile)) {
            JujutsuConfigData data = GSON.fromJson(reader, JujutsuConfigData.class);
            
            if (data != null) {
                damage = data.damage;
                explosionRadius = data.explosionRadius;
                cooldown = data.cooldown;
                minChargeTime = data.minChargeTime;
                maxChargeTime = data.maxChargeTime;
                JujutsuCraftMod.LOGGER.info("[Jujutsu Craft] Config loaded successfully!");
            }
        } catch (IOException e) {
            JujutsuCraftMod.LOGGER.error("[Jujutsu Craft] Failed to load config!", e);
        }
    }
    
    public static void saveConfig() {
        File configFile = new File(CONFIG_PATH);
        
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        
        JujutsuConfigData data = new JujutsuConfigData();
        data.damage = damage;
        data.explosionRadius = explosionRadius;
        data.cooldown = cooldown;
        data.minChargeTime = minChargeTime;
        data.maxChargeTime = maxChargeTime;
        
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
            JujutsuCraftMod.LOGGER.info("[Jujutsu Craft] Config saved!");
        } catch (IOException e) {
            JujutsuCraftMod.LOGGER.error("[Jujutsu Craft] Failed to save config!", e);
        }
    }
    
    public static class JujutsuConfigData {
        public float damage = 30.0f;
        public float explosionRadius = 15.0f;
        public int cooldown = 15;
        public float minChargeTime = 2.0f;
        public float maxChargeTime = 3.0f;
    }
}
