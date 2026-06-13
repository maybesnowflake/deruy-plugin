package com.hardcore.plugin.listeners;

import com.hardcore.plugin.HardcorePlugin;
import com.hardcore.plugin.items.ItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class ReviveListener implements Listener {

    private final HardcorePlugin plugin;

    public ReviveListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onReviveTicketUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.BEACON) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!ItemRegistry.isReviveTicket(item)) return;

        event.setCancelled(true);
        Location beaconLoc = event.getClickedBlock().getLocation();

        if (plugin.getReviveManager().hasActiveSession(beaconLoc)) {
            player.sendMessage(Component.text("이 비콘에는 이미 부활 의식이 진행 중입니다!")
                    .color(NamedTextColor.RED));
            return;
        }

        placeBeaconBase(beaconLoc);
        setBeaconColor(beaconLoc);

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        plugin.getReviveManager().startSession(beaconLoc, player);

        int seconds = plugin.getConfig().getInt("beacon-revive-time", 120);
        plugin.getServer().broadcast(
            Component.text("🔴 부활 의식 시작! ", NamedTextColor.DARK_RED)
                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                .append(Component.text(" 님이 부활 의식을 시작했습니다. (" + seconds + "초)", NamedTextColor.RED))
        );

        player.sendMessage(Component.text(
            "✅ 부활 의식이 시작되었습니다. " + seconds + "초 동안 비콘이 유지되어야 부활이 완료됩니다!")
            .color(NamedTextColor.GOLD));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBeaconBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.BEACON) return;

        Location loc = block.getLocation();
        if (!plugin.getReviveManager().hasActiveSession(loc)) return;

        event.setCancelled(true);
        plugin.getReviveManager().breakBeacon(loc, event.getPlayer());

        event.getPlayer().sendMessage(Component.text(
            "⚡ 비콘 파괴 성공! 부활 의식이 중단되었습니다.")
            .color(NamedTextColor.DARK_RED));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBaseBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.CRYING_OBSIDIAN) return;

        for (int dy = 1; dy <= 4; dy++) {
            Block above = block.getRelative(BlockFace.UP, dy);
            if (above.getType() == Material.BEACON) {
                if (plugin.getReviveManager().hasActiveSession(above.getLocation())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(
                        Component.text("부활 의식 중에는 비콘 기반을 파괴할 수 없습니다!")
                            .color(NamedTextColor.RED));
                    return;
                }
            }
        }
    }

    private void placeBeaconBase(Location beaconLoc) {
        int bx = beaconLoc.getBlockX();
        int by = beaconLoc.getBlockY() - 1;
        int bz = beaconLoc.getBlockZ();
        World world = beaconLoc.getWorld();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block b = world.getBlockAt(bx + dx, by, bz + dz);
                b.setType(Material.CRYING_OBSIDIAN);
            }
        }
    }

    private void setBeaconColor(Location beaconLoc) {
        Block block = beaconLoc.getBlock();
        if (block.getState() instanceof Beacon beacon) {
            beacon.setPrimaryEffect(PotionEffectType.REGENERATION);
            beacon.setSecondaryEffect(PotionEffectType.REGENERATION);
            beacon.update();
        }
    }
}
