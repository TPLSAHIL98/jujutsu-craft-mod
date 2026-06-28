package com.jujutsucraft.ability;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;

import com.jujutsucraft.entity.HollowPurpleProjectileEntity;
import com.jujutsucraft.config.JujutsuConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HollowPurpleManager {
    
    private static final Map<UUID, Integer> cooldowns = new HashMap<>();
    
    public static void fireHollowPurple(ServerPlayerEntity player, int chargeTime) {
        if (isOnCooldown(player)) {
            return;
        }
        
        float chargeMultiplier = Math.min(1.5f, 1.0f + (chargeTime - 40) / 20.0f);
        float damage = JujutsuConfig.damage * chargeMultiplier;
        float radius = JujutsuConfig.explosionRadius * chargeMultiplier;
        
        World world = player.getWorld();
        Vec3d lookDir = player.getRotationVec(1.0f).normalize();
        
        HollowPurpleProjectileEntity projectile = new HollowPurpleProjectileEntity(
            world,
            player,
            damage,
            radius,
            chargeMultiplier
        );
        
        projectile.setPosition(
            player.getX() + lookDir.x * 1.5,
            player.getEyeY(),
            player.getZ() + lookDir.z * 1.5
        );
        
        float speed = 1.2f + (chargeMultiplier - 1.0f) * 0.4f;
        projectile.setVelocity(
            lookDir.x * speed,
            lookDir.y * speed,
            lookDir.z * speed
        );
        
        world.spawnEntity(projectile);
        
        cooldowns.put(player.getUuid(), JujutsuConfig.cooldown * 20);
        
        world.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_GENERIC_EXPLODE,
            SoundCategory.PLAYERS,
            2.0f, 0.8f
        );
    }
    
    public static void updateCooldown(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (cooldowns.containsKey(uuid)) {
            int remaining = cooldowns.get(uuid);
            if (remaining <= 0) {
                cooldowns.remove(uuid);
            } else {
                cooldowns.put(uuid, remaining - 1);
            }
        }
    }
    
    public static boolean isOnCooldown(ServerPlayerEntity player) {
        return cooldowns.getOrDefault(player.getUuid(), 0) > 0;
    }
    
    public static int getCooldown(ServerPlayerEntity player) {
        return cooldowns.getOrDefault(player.getUuid(), 0);
    }
    
    public static void resetCooldown(ServerPlayerEntity player) {
        cooldowns.remove(player.getUuid());
    }
}
