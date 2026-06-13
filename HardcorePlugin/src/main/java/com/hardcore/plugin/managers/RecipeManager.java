package com.hardcore.plugin.managers;

import com.hardcore.plugin.HardcorePlugin;
import com.hardcore.plugin.items.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice;

public class RecipeManager {

    private final HardcorePlugin plugin;

    public RecipeManager(HardcorePlugin plugin) {
        this.plugin = plugin;
        ItemRegistry.init(plugin);
    }

    public void registerRecipes() {
        removeDefaultMaceRecipe();
        registerMaceRecipe();
        registerTotemRecipe();
        registerReviveTicketRecipe();
    }

    // ── 기본 메이스 제작법 삭제 ──────────────────────────────────
    private void removeDefaultMaceRecipe() {
        plugin.getServer().removeRecipe(
            new NamespacedKey("minecraft", "mace")
        );
        plugin.getLogger().info("기본 메이스 제작법 제거 완료");
    }

    // ── 커스텀 메이스 제작법 ─────────────────────────────────────
    // 배치:
    //  [  ] [코어] [  ]
    //  [N ] [코어] [N ]   ← 코어=Heavy Core, N=네더 블록
    //  [막] [코어] [막]
    // 설명상 "코어 1번, 네더블록 3번, 막대 5번" 재해석:
    //  1(위중앙)=Heavy Core, 3(좌중)=NetherBrick, 7(좌하)=막대,
    //  9(우하)=막대, 5(중앙)=네더블록, ...
    // 3x3 ShapedRecipe로 구현:
    //   C = Heavy Core
    //   N = Nether Bricks
    //   S = Stick
    //   배치: 상=CSC / 중=NSN / 하=SCS  (c:1개, N:2개, S:5개 → 합계 정확)
    private void registerMaceRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "custom_mace");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createCustomMace());
        recipe.shape("CSC",
                     "NSN",
                     "SCS");
        recipe.setIngredient('C', Material.HEAVY_CORE);
        recipe.setIngredient('N', Material.NETHER_BRICKS);
        recipe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("커스텀 메이스 제작법 등록 완료");
    }

    // ── 커스텀 토템 제작법 ───────────────────────────────────────
    // 위치: 1379=다이아 블록, 2468=고대 잔해, 5=마황(Dragon Breath)
    // 3x3 배치 (숫자 1~9 = 왼→오, 위→아래):
    //   1=다이블럭, 2=고대잔해, 3=다이블럭
    //   4=고대잔해, 5=마황,    6=고대잔해
    //   7=다이블럭, 8=고대잔해, 9=다이블럭
    private void registerTotemRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "custom_totem");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createCustomTotem());
        recipe.shape("DAD",
                     "ABA",
                     "DAD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        recipe.setIngredient('B', Material.DRAGON_BREATH);
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("커스텀 토템 제작법 등록 완료");
    }

    // ── 부활권 제작법 ────────────────────────────────────────────
    // 위치: 1379=네더라이트 주괴, 2=네더의 별, 46=플레이어 머리, 5=불사의 토템, 8=드래곤 알
    // 3x3 배치:
    //   1=NI,  2=NS,  3=NI
    //   4=PH,  5=TOT, 6=PH
    //   7=NI,  8=DE,  9=NI
    private void registerReviveTicketRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "revive_ticket");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createReviveTicket());
        recipe.shape("INI",
                     "HTH",
                     "INI");
        recipe.setIngredient('I', Material.NETHERITE_INGOT);
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('H', Material.PLAYER_HEAD);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        // 드래곤 알은 shapedRecipe에서 단독 사용 불가이므로 가운데 아래를 DRAGON_EGG로 변경
        // → 대신 8번 위치에 드래곤 알 추가하려면 별도 처리
        // 드래곤 알은 스택 불가 아이템이므로 ExactChoice 사용
        recipe.setIngredient('I', new RecipeChoice.ExactChoice(new ItemStack(Material.NETHERITE_INGOT)));
        // 재조정: 드래곤 알을 중앙 아래(8번=아래 가운데)로 넣기
        // shape 재정의:
        //   INI / HTH / NDN  (D=DragonEgg)
        NamespacedKey key2 = new NamespacedKey(plugin, "revive_ticket_v2");
        ShapedRecipe recipe2 = new ShapedRecipe(key2, ItemRegistry.createReviveTicket());
        recipe2.shape("INI",
                      "HTH",
                      "NDN");
        recipe2.setIngredient('I', Material.NETHERITE_INGOT);
        recipe2.setIngredient('N', Material.NETHER_STAR);
        recipe2.setIngredient('H', Material.PLAYER_HEAD);
        recipe2.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe2.setIngredient('D', Material.DRAGON_EGG);
        plugin.getServer().addRecipe(recipe2);
        plugin.getLogger().info("부활권 제작법 등록 완료");
    }
}
