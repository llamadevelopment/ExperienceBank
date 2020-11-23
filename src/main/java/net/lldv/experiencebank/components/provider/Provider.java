package net.lldv.experiencebank.components.provider;

import cn.nukkit.Player;
import net.lldv.experiencebank.ExperienceBank;

import java.util.function.Consumer;

public class Provider {

    public void connect(ExperienceBank server) {

    }

    public void disconnect(ExperienceBank server) {

    }

    public boolean userExists(String player) {
        return true;
    }

    public void createUserData(Player player) {

    }

    public void setBankXp(String player, int xp) {

    }

    public void getBankXp(String player, Consumer<Integer> xp) {

    }

    public String getProvider() {
        return null;
    }

}
