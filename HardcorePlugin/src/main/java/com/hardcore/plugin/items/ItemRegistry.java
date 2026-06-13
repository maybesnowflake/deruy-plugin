package com.hardcore.plugin.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ItemRegistry {

    private static Plugin plugin;

    // PersistentData 키
    public static NamespacedKey REVIVE_TICKET_KEY;
    public static NamespacedKey CUSTOM_TOTEM_KEY;
    public static NamespacedKey CUSTOM_MACE_KEY;

    public static void init(Plugin pl) {
        plugin = pl;
        REVIVE_TICKET_KEY = new NamespacedKey(pl, "revive_ticket");
        CUSTOM_TOTEM_KEY  = new NamespacedKey(pl, "custom_totem");
        CUSTOM_MACE_KEY   = new NamespacedKey(pl, "custom_mace");
    }

    // ── 부활권 ──────────────────────────────────────────────────
    // 1379: 네더라이트 주괴, 2: 네더의 별, 46: 플레이어 머리, 5: 불사의 토템, 8: 드래곤 알
    public static ItemStack createReviveTicket() {
        ItemStack item = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("✨ 부활권")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text("비콘에 우클릭하여 부활 의식을 시작합니다.").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("2분간 비콘이 유지되면 부활 완료!").color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(REVIVE_TICKET_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    // ── 커스텀 메이스 ────────────────────────────────────────────
    // 코어 1번, 네더 블록 3번, 막대 5번 (중앙형 배치)
    public static ItemStack createCustomMace() {
        ItemStack item = new ItemStack(Material.MACE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("⚒ 하드코어 메이스")
                .color(NamedTextColor.DARK_RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text("특별 제작된 메이스").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(CUSTOM_MACE_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    // ── 커스텀 불사의 토템 ──────────────────────────────────────
    // 1379: 다이아 블록, 2468: 고대 잔해, 5: 마황(Dragon Breath)
    public static ItemStack createCustomTotem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("💛 강화된 불사의 토템")
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
            Component.text("레이드로는 얻을 수 없는 특별 제작 토템").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(CUSTOM_TOTEM_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    // ── 판별 메서드 ──────────────────────────────────────────────
    public static boolean isReviveTicket(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(REVIVE_TICKET_KEY, PersistentDataType.BYTE);
    }

    public static boolean isCustomTotem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(CUSTOM_TOTEM_KEY, PersistentDataType.BYTE);
    }

    public static boolean isCustomMace(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(CUSTOM_MACE_KEY, PersistentDataType.BYTE);
    }
}
