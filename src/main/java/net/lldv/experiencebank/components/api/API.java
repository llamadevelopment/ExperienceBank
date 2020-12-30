package net.lldv.experiencebank.components.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.experiencebank.components.forms.FormWindows;
import net.lldv.experiencebank.components.provider.Provider;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class API {

    private final Provider provider;
    private final FormWindows formWindows;

    @Getter
    public static HashMap<String, Integer> cachedXp = new HashMap<>();

}
