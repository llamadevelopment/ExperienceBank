package net.lldv.experiencebank;

import cn.nukkit.plugin.PluginBase;
import net.lldv.experiencebank.commands.XpbankCommand;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.forms.FormListener;
import net.lldv.experiencebank.components.forms.FormWindows;
import net.lldv.experiencebank.components.provider.MongoDBProvider;
import net.lldv.experiencebank.components.provider.MySqlProvider;
import net.lldv.experiencebank.components.provider.YamlProvider;
import net.lldv.experiencebank.components.provider.Provider;
import net.lldv.experiencebank.components.tools.Language;
import net.lldv.experiencebank.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class ExperienceBank extends PluginBase {

    private Provider provider;
    private final Map<String, Provider> providers = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            this.providers.put("MongoDB", new MongoDBProvider());
            this.providers.put("MySql", new MySqlProvider());
            this.providers.put("Yaml", new YamlProvider());
            if (!this.providers.containsKey(this.getConfig().getString("Provider"))) {
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            this.provider = this.providers.get(this.getConfig().getString("Provider"));
            this.provider.connect(this);
            this.getLogger().info("§aSuccessfully loaded " + this.provider.getProvider() + " provider.");
            ExperienceBankAPI.setProvider(this.provider);
            ExperienceBankAPI.setFormWindows(new FormWindows(this.provider));
            Language.init(this);
            this.loadPlugin();
            this.getLogger().info("§aPlugin successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load plugin ExperienceBank.");
        }
    }

    private void loadPlugin() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this.provider), this);
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getCommandMap().register("experiencebank", new XpbankCommand(this));
    }

    @Override
    public void onDisable() {
        this.provider.disconnect(this);
    }

}
