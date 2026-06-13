package com.hardcore.plugin;

import com.hardcore.plugin.commands.HardcoreCommands;
import com.hardcore.plugin.listeners.*;
import com.hardcore.plugin.managers.ReviveManager;
import com.hardcore.plugin.managers.RecipeManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HardcorePlugin extends JavaPlugin {

    private static HardcorePlugin instance;
    private Set<UUID> pausePlayers = new HashSet<>();
    private ReviveManager reviveManager;
    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // PAUSE 플레이어 목록 로드
        List<String> pauseList = getConfig().getStringList("pause-players");
        for (String entry : pauseList) {
            try {
                pausePlayers.add(UUID.fromString(entry));
            } catch (IllegalArgumentException ignored) {}
        }

        // 매니저 초기화
        reviveManager = new ReviveManager(this);
        recipeManager = new RecipeManager(this);
        recipeManager.registerRecipes();

        // 리스너 등록
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CrystalListener(this), this);
        getServer().getPluginManager().registerEvents(new ReviveListener(this), this);
        getServer().getPluginManager().registerEvents(new PauseListener(this), this);
        getServer().getPluginManager().registerEvents(new BuildLimitListener(this), this);

        // 명령어 등록
        HardcoreCommands cmd = new HardcoreCommands(this);
        getCommand("hcpause").setExecutor(cmd);
        getCommand("hcrevive").setExecutor(cmd);

        getLogger().info("HardcorePlugin 활성화 완료!");
    }

    @Override
    public void onDisable() {
        // PAUSE 플레이어 목록 저장
        List<String> pauseList = pausePlayers.stream()
                .map(UUID::toString)
                .toList();
        getConfig().set("pause-players", pauseList);
        saveConfig();
        getLogger().info("HardcorePlugin 비활성화!");
    }

    public static HardcorePlugin getInstance() {
        return instance;
    }

    public Set<UUID> getPausePlayers() {
        return pausePlayers;
    }

    public boolean isPausePlayer(UUID uuid) {
        return pausePlayers.contains(uuid);
    }

    public void addPausePlayer(UUID uuid) {
        pausePlayers.add(uuid);
    }

    public void removePausePlayer(UUID uuid) {
        pausePlayers.remove(uuid);
    }

    public ReviveManager getReviveManager() {
        return reviveManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
