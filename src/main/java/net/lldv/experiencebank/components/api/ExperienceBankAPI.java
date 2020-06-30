package net.lldv.experiencebank.components.api;

import lombok.Getter;
import lombok.Setter;
import net.lldv.experiencebank.components.managers.provider.Provider;

public class ExperienceBankAPI {

    @Getter
    @Setter
    public static Provider provider;

    public static double convertLevelToExperience(double level) {
        double experience = 0;
        if (level <= 16) experience = (level * level) + 6 * level;
        else if (level >= 17 && level <= 31) experience = 2.5 * (level * level) - 40.5 * level + 360;
        else if (level >= 32) experience = 4.5 * (level * level) - 162.5 * level + 2220;
        return experience;
    }

}
