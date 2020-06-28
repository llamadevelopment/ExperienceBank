package net.lldv.experiencebank.components.managers.provider;

import cn.nukkit.Player;
import net.lldv.experiencebank.ExperienceBank;

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

    public int getBankXp(String player) {
        return -1;
    }

    public String getProvider() {
        return null;
    }

}
