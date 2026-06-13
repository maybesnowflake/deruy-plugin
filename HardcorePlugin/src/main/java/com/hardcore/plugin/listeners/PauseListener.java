package com.hardcore.plugin.listeners;

import com.hardcore.plugin.HardcorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PauseListener implements Listener {

    private final HardcorePlugin plugin;

    public PauseListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.isPausePlayer(player.getUniqueId())) return;

        // 체력이 데미지 이후 0 이하가 될 경우 → 1로 변환
        double finalHealth = player.getHealth() - event.getFinalDamage();
        if (finalHealth <= 0) {
            event.setCancelled(true);
            player.setHealth(1.0);

            // 전체 채팅에 PAUSE 표시
            Component pauseMsg = Component.text("⏸ PAUSE", NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD)
                    .append(Component.text(" - " + player.getName() + " 님의 체력이 0이 되었습니다!", NamedTextColor.RED));

            plugin.getServer().broadcast(pauseMsg);
        }
    }
}
