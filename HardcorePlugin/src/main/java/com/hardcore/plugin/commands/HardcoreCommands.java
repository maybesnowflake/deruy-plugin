package com.hardcore.plugin.commands;

import com.hardcore.plugin.HardcorePlugin;
import com.hardcore.plugin.items.ItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HardcoreCommands implements CommandExecutor {

    private final HardcorePlugin plugin;

    public HardcoreCommands(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "hcpause" -> handlePause(sender, args);
            case "hcrevive" -> handleRevive(sender, args);
        }
        return true;
    }

    // /hcpause <add|remove|list> [player]
    private void handlePause(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("사용법: /hcpause <add|remove|list> [player]").color(NamedTextColor.RED));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                sender.sendMessage(Component.text("=== PAUSE 플레이어 목록 ===").color(NamedTextColor.GOLD));
                if (plugin.getPausePlayers().isEmpty()) {
                    sender.sendMessage(Component.text("없음").color(NamedTextColor.GRAY));
                } else {
                    plugin.getPausePlayers().forEach(uuid -> {
                        OfflinePlayer op = plugin.getServer().getOfflinePlayer(uuid);
                        sender.sendMessage(Component.text("- " + (op.getName() != null ? op.getName() : uuid.toString()))
                                .color(NamedTextColor.YELLOW));
                    });
                }
            }
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("플레이어 이름을 입력하세요.").color(NamedTextColor.RED));
                    return;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.text("온라인 플레이어를 찾을 수 없습니다: " + args[1]).color(NamedTextColor.RED));
                    return;
                }
                plugin.addPausePlayer(target.getUniqueId());
                sender.sendMessage(Component.text(target.getName() + " 님을 PAUSE 목록에 추가했습니다.")
                        .color(NamedTextColor.GREEN));
                target.sendMessage(Component.text("당신은 이제 PAUSE 플레이어입니다. (체력 0 → 1 변환)")
                        .color(NamedTextColor.GOLD));
            }
            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("플레이어 이름을 입력하세요.").color(NamedTextColor.RED));
                    return;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.text("온라인 플레이어를 찾을 수 없습니다: " + args[1]).color(NamedTextColor.RED));
                    return;
                }
                plugin.removePausePlayer(target.getUniqueId());
                sender.sendMessage(Component.text(target.getName() + " 님을 PAUSE 목록에서 제거했습니다.")
                        .color(NamedTextColor.GREEN));
            }
            default -> sender.sendMessage(Component.text("사용법: /hcpause <add|remove|list> [player]").color(NamedTextColor.RED));
        }
    }

    // /hcrevive <player>
    private void handleRevive(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("사용법: /hcrevive <player>").color(NamedTextColor.RED));
            return;
        }
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("온라인 플레이어를 찾을 수 없습니다: " + args[0]).color(NamedTextColor.RED));
            return;
        }
        target.getInventory().addItem(ItemRegistry.createReviveTicket());
        sender.sendMessage(Component.text(target.getName() + " 님에게 부활권을 지급했습니다.").color(NamedTextColor.GREEN));
        target.sendMessage(Component.text("관리자로부터 부활권을 받았습니다!").color(NamedTextColor.GOLD));
    }
}
