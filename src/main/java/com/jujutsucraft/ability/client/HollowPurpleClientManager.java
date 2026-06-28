package com.jujutsucraft.ability.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import com.jujutsucraft.JujutsuCraftMod;
import com.jujutsucraft.client.JujutsuCraftClient;
import com.jujutsucraft.network.HollowPurpleC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class HollowPurpleClientManager {
    
    private static boolean isCharging = false;
    private static int chargeTime = 0;
    private static final int MAX_CHARGE_TIME = 60;
    private static final int MIN_CHARGE_TIME = 40;
    private static boolean soundPlayed = false;
    
    public static void handleClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        ClientPlayerEntity player = client.player;
        
        if (JujutsuCraftClient.HOLLOW_PURPLE_KEY.isPressed()) {
            if (!isCharging) {
                isCharging = true;
                chargeTime = 0;
                soundPlayed = false;
            }
            
            chargeTime++;
            
            if (chargeTime > MAX_CHARGE_TIME) {
                chargeTime = MAX_CHARGE_TIME;
            }
            
            if (chargeTime == 1 && !soundPlayed) {
                player.getWorld().playSound(
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_BEACON_POWER_SELECT,
                    SoundCategory.PLAYERS,
                    0.8f, 1.5f, false
                );
                soundPlayed = true;
            }
            
            if (chargeTime % 3 == 0) {
                spawnChargeParticles(player);
            }
            
            if (chargeTime >= MAX_CHARGE_TIME && chargeTime % 5 == 0) {
                screenShake(client);
            }
            
        } else if (isCharging) {
            if (chargeTime >= MIN_CHARGE_TIME) {
                fireHollowPurple(player, chargeTime);
            }
            
            isCharging = false;
            chargeTime = 0;
            soundPlayed = false;
        }
    }
    
    private static void spawnChargeParticles(ClientPlayerEntity player) {
        Vec3d rightHandPos = player.getPos().add(0.3, player.getEyeY() - player.getY() + 0.1, 0);
        Vec3d leftHandPos = player.getPos().add(-0.3, player.getEyeY() - player.getY() + 0.1, 0);
        
        for (int i = 0; i < 2; i++) {
            double angle = System.currentTimeMillis() * 0.01 + i * Math.PI;
            double x = Math.cos(angle) * 0.4;
            double z = Math.sin(angle) * 0.4;
            
            player.getWorld().addParticle(
                ParticleTypes.EFFECT,
                rightHandPos.x + x,
                rightHandPos.y,
                rightHandPos.z + z,
                x * 0.05,
                0.05,
                z * 0.05
            );
            
            player.getWorld().addParticle(
                ParticleTypes.EFFECT,
                leftHandPos.x - x,
                leftHandPos.y,
                leftHandPos.z - z,
                -x * 0.05,
                0.05,
                -z * 0.05
            );
        }
    }
    
    private static void screenShake(MinecraftClient client) {
        if (client.gameRenderer == null) return;
        double intensity = 0.3;
        client.gameRenderer.bobView(intensity);
    }
    
    private static void fireHollowPurple(ClientPlayerEntity player, int chargeTime) {
        HollowPurpleC2SPacket packet = new HollowPurpleC2SPacket(chargeTime);
        ClientPlayNetworking.send(packet);
        
        player.getWorld().playSound(
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_GENERIC_EXPLODE,
            SoundCategory.PLAYERS,
            1.5f, 0.8f, false
        );
    }
    
    public static boolean isCurrentlyCharging() {
        return isCharging;
    }
    
    public static int getChargeProgress() {
        return (int) ((chargeTime / (float) MAX_CHARGE_TIME) * 100);
    }
}
