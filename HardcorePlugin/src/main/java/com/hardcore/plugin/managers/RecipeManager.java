package com.hardcore.plugin.managers;
 
import com.hardcore.plugin.HardcorePlugin;
import com.hardcore.plugin.items.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ItemStack;
 
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
    // [Heavy Core] [네더라이트블록] [Heavy Core]
    // [빈칸]       [브리즈 막대]    [빈칸]
    // [빈칸]       [브리즈 막대]    [빈칸]
    private void registerMaceRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "custom_mace");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createCustomMace());
        recipe.shape("CNB",
                     " R ",
                     " R ");
        recipe.setIngredient('C', Material.HEAVY_CORE);
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        recipe.setIngredient('B', Material.HEAVY_CORE);
        recipe.setIngredient('R', Material.BREEZE_ROD);
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("커스텀 메이스 제작법 등록 완료");
    }
 
    // ── 커스텀 토템 제작법 ───────────────────────────────────────
    // [다이아블록] [고대잔해]               [다이아블록]
    // [고대잔해]   [마법부여된 황금사과]    [고대잔해]
    // [다이아블록] [고대잔해]               [다이아블록]
    private void registerTotemRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "custom_totem");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createCustomTotem());
        recipe.shape("DAD",
                     "AEA",
                     "DAD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('A', Material.ANCIENT_DEBRIS);
        recipe.setIngredient('E', Material.ENCHANTED_GOLDEN_APPLE);
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("커스텀 토템 제작법 등록 완료");
    }
 
    // ── 부활권 제작법 ────────────────────────────────────────────
    // [네더라이트블록] [불사의토템]  [네더라이트블록]
    // [플레이어머리]   [드래곤알]    [플레이어머리]
    // [네더의별]       [네더의별]    [네더의별]
    private void registerReviveTicketRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "revive_ticket_v3");
        ShapedRecipe recipe = new ShapedRecipe(key, ItemRegistry.createReviveTicket());
        recipe.shape("NTN",
                     "HDH",
                     "SSS");
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient('H', Material.PLAYER_HEAD);
        recipe.setIngredient('D', new RecipeChoice.ExactChoice(new ItemStack(Material.DRAGON_EGG)));
        recipe.setIngredient('S', Material.NETHER_STAR);
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("부활권 제작법 등록 완료");
    }
}
 
