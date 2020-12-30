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

    public double convertLevelToExperience(double level) {
        double experience = 0;
        if (level <= 16) experience = (level * level) + 6 * level;
        else if (level >= 17 && level <= 31) experience = 2.5 * (level * level) - 40.5 * level + 360;
        else if (level >= 32) experience = 4.5 * (level * level) - 162.5 * level + 2220;
        return experience;
    }

}
