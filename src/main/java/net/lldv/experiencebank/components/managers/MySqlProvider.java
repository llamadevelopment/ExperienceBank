package net.lldv.experiencebank.components.managers;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.managers.provider.Provider;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class MySqlProvider extends Provider {

    Connection connection;

    @Override
    public void connect(ExperienceBank server) {
        CompletableFuture.runAsync(() -> {
            try {
                Config config = server.getConfig();
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("MySql.Host") + ":" + config.getString("MySql.Port") + "/" + config.getString("MySql.Database") + "?autoReconnect=true", config.getString("MySql.User"), config.getString("MySql.Password"));
                update("CREATE TABLE IF NOT EXISTS xp_data(player VARCHAR(30), xp INTEGER(30), PRIMARY KEY (player));");
                server.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    public Connection getConnection() {
        return connection;
    }

    public void update(String query) {
        CompletableFuture.runAsync(() -> {
            if (connection != null) {
                try {
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.executeUpdate();
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disconnect(ExperienceBank server) {
        if (connection != null) {
            try {
                connection.close();
                server.getLogger().info("[MySqlClient] Connection closed.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to close connection.");
            }
        }
    }

    @Override
    public boolean userExists(String player) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM xp_data WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getString("PLAYER") != null;
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createUserData(Player player) {
        update("INSERT INTO xp_data (PLAYER, XP) VALUES ('" + player.getName() + "', '0');");
        ExperienceBank.getInstance().xpMap.put(player.getName(), 0);
    }

    @Override
    public void setBankXp(String player, int xp) {
        update("UPDATE xp_data SET XP= '" + xp + "' WHERE PLAYER= '" + player + "';");
        ExperienceBank.getInstance().xpMap.remove(player);
        ExperienceBank.getInstance().xpMap.put(player, xp);
    }

    @Override
    public int getBankXp(String player) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM xp_data WHERE PLAYER = ?");
            preparedStatement.setString(1, player);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getInt("XP");
            rs.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String getProvider() {
        return "MySql";
    }
}
