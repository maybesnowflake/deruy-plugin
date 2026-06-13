package com.hardcore.plugin.listeners;

import com.hardcore.plugin.HardcorePlugin;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Block;

public class CrystalListener implements Listener {

    private final HardcorePlugin plugin;

    public CrystalListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    // 엔더 크리스탈 공격으로 인한 피해 → 취소 후 조용히 제거
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        // 엔더 크리스탈이 폭발로 무언가를 다치게 하는 경우
        if (damager instanceof EnderCrystal crystal) {
            event.setCancelled(true);
            crystal.remove();
        }

        // 엔더 크리스탈 자체가 공격받는 경우 → 터지지 않고 제거
        if (damaged instanceof EnderCrystal crystal) {
            event.setCancelled(true);
            crystal.remove();
        }
    }

    // 엔더 크리스탈 일반 데미지 (폭발 등)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalSelfDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof EnderCrystal crystal) {
            event.setCancelled(true);
            crystal.remove();
        }
    }

    // 정박기(Respawn Anchor) 상호작용 → 폭발 차단
    // 오버월드/엔드에서 정박기 사용 시 폭발하는 것을 막고 조용히 제거
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnchorInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.RESPAWN_ANCHOR) return;

        // 폭발이 일어나는 환경(오버월드, 엔드)에서 상호작용 시 차단
        String envName = block.getWorld().getEnvironment().name();
        if (!envName.equals("NETHER")) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
}
