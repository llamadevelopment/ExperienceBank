package net.lldv.experiencebank.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import lombok.AllArgsConstructor;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.provider.Provider;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class EventListener implements Listener {

    private final Provider provider;

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!this.provider.userExists(event.getPlayer().getName())) this.provider.createUserData(event.getPlayer());
            else {
                this.provider.getBankXp(event.getPlayer().getName(), xp -> {
                    ExperienceBankAPI.getCachedXp().remove(event.getPlayer().getName());
                    ExperienceBankAPI.getCachedXp().put(event.getPlayer().getName(), xp);
                });
            }
        });
    }

}
