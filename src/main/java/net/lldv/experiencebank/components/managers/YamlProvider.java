package net.lldv.experiencebank.components.managers;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.managers.provider.Provider;

import java.util.concurrent.CompletableFuture;

public class YamlProvider extends Provider {

    Config xpData;

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
        return xpData.exists("Data." + player);
    }

    @Override
    public void createUserData(Player player) {
        xpData.set("Data." + player.getName(), player.getExperienceLevel());
        xpData.save();
        xpData.reload();
        ExperienceBank.getInstance().xpMap.put(player.getName(), getBankXp(player.getName()));
    }

    @Override
    public void setBankXp(String player, int xp) {
        int newXp = ExperienceBank.getInstance().xpMap.get(player) + xp;
        xpData.set("Data." + player, newXp);
        xpData.save();
        xpData.reload();
        ExperienceBank.getInstance().xpMap.remove(player);
        ExperienceBank.getInstance().xpMap.put(player, newXp);
    }

    @Override
    public int getBankXp(String player) {
        return xpData.getInt("Data." + player);
    }

    @Override
    public String getProvider() {
        return "Yaml";
    }
}
