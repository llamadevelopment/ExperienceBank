package net.lldv.experiencebank.components.provider;

import cn.nukkit.Player;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.api.API;
import net.lldv.experiencebank.components.simplesqlclient.MySqlClient;
import net.lldv.experiencebank.components.simplesqlclient.objects.SqlColumn;
import net.lldv.experiencebank.components.simplesqlclient.objects.SqlDocument;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MySqlProvider extends Provider {

    private MySqlClient client;

    @Override
    public void connect(ExperienceBank server) {
        CompletableFuture.runAsync(() -> {
            try {
                this.client = new MySqlClient(
                        server.getConfig().getString("MySql.Host"),
                        server.getConfig().getString("MySql.Port"),
                        server.getConfig().getString("MySql.User"),
                        server.getConfig().getString("MySql.Password"),
                        server.getConfig().getString("MySql.Database")
                );

                this.client.createTable("xp_data", "player",
                        new SqlColumn("player", SqlColumn.Type.VARCHAR, 30)
                                .append("xp", SqlColumn.Type.INT, 30));
                server.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    @Override
    public void disconnect(ExperienceBank server) {
        server.getLogger().info("[MySqlClient] Connection closed.");
    }

    @Override
    public boolean userExists(String player) {
        SqlDocument document = this.client.find("xp_data", "player", player).first();
        return document != null;
    }

    @Override
    public void createUserData(Player player) {
        this.client.insert("xp_data", new SqlDocument("player", player.getName()).append("xp", 0));
        API.getCachedXp().put(player.getName(), 0);
    }

    @Override
    public void setBankXp(String player, int xp) {
        this.client.update("xp_data", "player", player, new SqlDocument("xp", xp));
        API.getCachedXp().remove(player);
        API.getCachedXp().put(player, xp);
    }

    @Override
    public void getBankXp(String player, Consumer<Integer> xp) {
        CompletableFuture.runAsync(() -> {
            SqlDocument document = this.client.find("xp_data", "player", player).first();
            if (document != null) xp.accept(document.getInt("xp"));
        });
    }

    @Override
    public String getProvider() {
        return "MySql";
    }

}
