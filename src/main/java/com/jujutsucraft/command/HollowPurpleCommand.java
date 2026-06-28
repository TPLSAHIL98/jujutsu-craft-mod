package com.jujutsucraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import com.jujutsucraft.ability.HollowPurpleManager;
import com.jujutsucraft.config.JujutsuConfig;
import net.minecraft.command.CommandManager;

public class HollowPurpleCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("hollowpurple")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(HollowPurpleCommand::fireAbility)
                .then(
                    CommandManager.literal("info")
                        .executes(HollowPurpleCommand::showInfo)
                )
                .then(
                    CommandManager.literal("reset")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(HollowPurpleCommand::resetCooldown)
                )
        );
    }
    
    private static int fireAbility(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("§cCommand must be run by a player"));
            return 0;
        }
        
        HollowPurpleManager.fireHollowPurple(player, 60);
        context.getSource().sendFeedback(
            () -> Text.literal("§d✨ Hollow Purple activated!"),
            false
        );
        return 1;
    }
    
    private static int showInfo(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        int cooldownRemaining = HollowPurpleManager.getCooldown(player);
        String cooldownStr = cooldownRemaining > 0 ? 
            String.format("%.1f", cooldownRemaining / 20.0f) : "Ready!";
        
        context.getSource().sendFeedback(
            () -> Text.literal(
                "§5═══════════════════════════\n" +
                "§d✨ Hollow Purple Info\n" +
                "§5═══════════════════════════\n" +
                "§eDamage: §c" + JujutsuConfig.damage + "\n" +
                "§eRadius: §c" + JujutsuConfig.explosionRadius + "m\n" +
                "§eCooldown: §c" + JujutsuConfig.cooldown + "s\n" +
                "§eRemaining: §c" + cooldownStr + "s\n" +
                "§5═══════════════════════════"
            ),
            false
        );
        return 1;
    }
    
    private static int resetCooldown(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        HollowPurpleManager.resetCooldown(player);
        context.getSource().sendFeedback(
            () -> Text.literal("§a✓ Cooldown reset!"),
            false
        );
        return 1;
    }
}
