package com.hardcore.plugin.listeners;

import com.hardcore.plugin.HardcorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildLimitListener implements Listener {

    private final HardcorePlugin plugin;

    public BuildLimitListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        int limit = plugin.getConfig().getInt("build-height-limit", 601);
        Block block = event.getBlockPlaced();

        if (block.getY() > limit) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                Component.text("⛔ 건축 높이 제한: Y " + limit + " 초과 블록을 설치할 수 없습니다!")
                    .color(NamedTextColor.RED)
            );
        }
    }
}
