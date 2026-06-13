package com.hardcore.plugin.managers;

import com.hardcore.plugin.HardcorePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ReviveManager {

    private final HardcorePlugin plugin;

    // beaconLocation → ReviveSession
    private final Map<Location, ReviveSession> activeSessions = new HashMap<>();

    public ReviveManager(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasActiveSession(Location loc) {
        return activeSessions.containsKey(normalizeLocation(loc));
    }

    public void startSession(Location beaconLoc, Player activator) {
        Location key = normalizeLocation(beaconLoc);
        if (activeSessions.containsKey(key)) return;

        int seconds = plugin.getConfig().getInt("beacon-revive-time", 120);
        ReviveSession session = new ReviveSession(beaconLoc, activator, seconds);
        activeSessions.put(key, session);

        // 타이머 시작
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // 세션이 아직 살아있으면 부활 성공
            if (activeSessions.containsKey(key)) {
                completeRevive(key);
            }
        }, seconds * 20L);

        session.setTask(task);
    }

    public void breakBeacon(Location beaconLoc, Player breaker) {
        Location key = normalizeLocation(beaconLoc);
        ReviveSession session = activeSessions.remove(key);
        if (session == null) return;

        // 타이머 취소
        if (session.getTask() != null) {
            session.getTask().cancel();
        }

        // 부순 사람 인벤에 드래곤 알 지급
        if (breaker != null) {
            breaker.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.DRAGON_EGG, 1));
            breaker.sendMessage(net.kyori.adventure.text.Component.text(
                "💀 부활 의식을 방해했습니다! 드래곤 알을 획득했습니다.")
                .color(net.kyori.adventure.text.format.NamedTextColor.DARK_RED));
        }

        plugin.getLogger().info("ReviveSession 파괴됨: " + beaconLoc);
    }

    private void completeRevive(Location key) {
        ReviveSession session = activeSessions.remove(key);
        if (session == null) return;

        Player activator = plugin.getServer().getPlayer(session.getActivatorUUID());
        plugin.getLogger().info("부활 완료: " + key);

        if (activator != null) {
            activator.sendMessage(net.kyori.adventure.text.Component.text(
                "✨ 부활이 완료되었습니다! 목숨이 회복되었습니다.")
                .color(net.kyori.adventure.text.format.NamedTextColor.GOLD));
        }
        // 비콘 3x3 흑요석 제거 (선택)
        clearBeaconStructure(key);
    }

    private void clearBeaconStructure(Location beaconLoc) {
        // 비콘 아래 3x3 흑요석 제거
        int bx = beaconLoc.getBlockX();
        int by = beaconLoc.getBlockY() - 1;
        int bz = beaconLoc.getBlockZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block block = beaconLoc.getWorld().getBlockAt(bx + dx, by, bz + dz);
                if (block.getType() == Material.CRYING_OBSIDIAN) {
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private Location normalizeLocation(Location loc) {
        return new Location(loc.getWorld(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Map<Location, ReviveSession> getActiveSessions() {
        return activeSessions;
    }

    // 내부 세션 클래스
    public static class ReviveSession {
        private final Location beaconLoc;
        private final UUID activatorUUID;
        private final int seconds;
        private BukkitTask task;

        public ReviveSession(Location loc, Player activator, int seconds) {
            this.beaconLoc = loc;
            this.activatorUUID = activator.getUniqueId();
            this.seconds = seconds;
        }

        public Location getBeaconLoc() { return beaconLoc; }
        public UUID getActivatorUUID() { return activatorUUID; }
        public int getSeconds() { return seconds; }
        public BukkitTask getTask() { return task; }
        public void setTask(BukkitTask task) { this.task = task; }
    }
}
