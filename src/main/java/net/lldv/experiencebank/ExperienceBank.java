package net.lldv.experiencebank;

import cn.nukkit.plugin.PluginBase;
import net.lldv.experiencebank.commands.XpbankCommand;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.forms.FormListener;
import net.lldv.experiencebank.components.managers.MongoDBProvider;
import net.lldv.experiencebank.components.managers.MySqlProvider;
import net.lldv.experiencebank.components.managers.YamlProvider;
import net.lldv.experiencebank.components.managers.provider.Provider;
import net.lldv.experiencebank.components.tools.Language;
import net.lldv.experiencebank.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class ExperienceBank extends PluginBase {

    private static ExperienceBank instance;
    public static Provider provider;
    private static final Map<String, Provider> providers = new HashMap<>();

    public HashMap<String, Integer> xpMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        try {
            saveDefaultConfig();
            providers.put("MongoDB", new MongoDBProvider());
            providers.put("MySql", new MySqlProvider());
            providers.put("Yaml", new YamlProvider());
            if (!providers.containsKey(getConfig().getString("Provider"))) {
                getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            provider = providers.get(getConfig().getString("Provider"));
            provider.connect(this);
            getLogger().info("§aSuccessfully loaded " + provider.getProvider() + " provider.");
            ExperienceBankAPI.setProvider(provider);
            Language.init();
            loadPlugin();
            getLogger().info("§aPlugin successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new FormListener(), this);
        getServer().getCommandMap().register(getConfig().getString("Commands.XpbankCommand"), new XpbankCommand());
    }

    @Override
    public void onDisable() {
        provider.disconnect(this);
    }

    public static ExperienceBank getInstance() {
        return instance;
    }
}
