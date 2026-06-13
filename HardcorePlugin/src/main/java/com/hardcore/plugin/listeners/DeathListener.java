package com.hardcore.plugin.listeners;

import com.hardcore.plugin.HardcorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class DeathListener implements Listener {

    private final HardcorePlugin plugin;

    public DeathListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLoc = player.getLocation();

        // 1. 플레이어 머리 드랍
        dropPlayerHead(player, deathLoc);

        // 2. 번개 소리 전체 재생
        playThunderSound(deathLoc);

        // 3. 플레이어 로그아웃 처리 (1틱 뒤에 실행)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Player onlinePlayer = plugin.getServer().getPlayer(player.getUniqueId());
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                onlinePlayer.kickPlayer("§c당신은 사망했습니다. (하드코어)");
            }
        }, 1L);
    }

    private void dropPlayerHead(Player player, Location loc) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.displayName(Component.text(player.getName() + "의 머리")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
            skull.setItemMeta(meta);
        }
        loc.getWorld().dropItemNaturally(loc, skull);
    }

    private void playThunderSound(Location loc) {
        // /playsound minecraft:item.trident.thunder master @a ~ ~ ~ 10000 0.5
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            online.playSound(loc,
                    Sound.ITEM_TRIDENT_THUNDER,
                    SoundCategory.MASTER,
                    10000f,
                    0.5f);
        }
    }
}
