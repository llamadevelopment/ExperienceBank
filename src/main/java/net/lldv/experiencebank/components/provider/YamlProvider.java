package net.lldv.experiencebank.components.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class YamlProvider extends Provider {

    private Config xpData;

    @Override
    public void connect(ExperienceBank server) {
        CompletableFuture.runAsync(() -> {
            server.saveResource("/data/xpdata.yml");
            this.xpData = new Config(server.getDataFolder() + "/data/xpdata.yml", Config.YAML);
            server.getLogger().info("[Configuration] Ready.");
        });
    }

    @Override
    public boolean userExists(String player) {
        return this.xpData.exists("Data." + player);
    }

    @Override
    public void createUserData(Player player) {
        this.xpData.set("Data." + player.getName(), 0);
        this.xpData.save();
        this.xpData.reload();
        ExperienceBankAPI.getCachedXp().put(player.getName(), 0);
    }

    @Override
    public void setBankXp(String player, int xp) {
        this.xpData.set("Data." + player, xp);
        this.xpData.save();
        this.xpData.reload();
        ExperienceBankAPI.getCachedXp().remove(player);
        ExperienceBankAPI.getCachedXp().put(player, xp);
    }

    @Override
    public void getBankXp(String player, Consumer<Integer> xp) {
        xp.accept(this.xpData.getInt("Data." + player));
    }

    @Override
    public String getProvider() {
        return "Yaml";
    }

}
