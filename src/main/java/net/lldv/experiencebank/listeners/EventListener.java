package net.lldv.experiencebank.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.managers.provider.Provider;

import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    Provider api = ExperienceBankAPI.getProvider();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!api.userExists(event.getPlayer().getName())) api.createUserData(event.getPlayer());
            else {
                ExperienceBank.getInstance().xpMap.remove(event.getPlayer().getName());
                ExperienceBank.getInstance().xpMap.put(event.getPlayer().getName(), api.getBankXp(event.getPlayer().getName()));
            }
        });
    }

}
