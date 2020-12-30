package net.lldv.experiencebank.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.lldv.experiencebank.ExperienceBank;

public class XpbankCommand extends PluginCommand<ExperienceBank> {

    public XpbankCommand(ExperienceBank owner) {
        super(owner.getConfig().getString("Commands.Xpbank.Name"), owner);
        this.setDescription(owner.getConfig().getString("Commands.Xpbank.Description"));
        this.setAliases(owner.getConfig().getStringList("Commands.Xpbank.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            ExperienceBank.getApi().getFormWindows().openXpBank(player);
        }
        return false;
    }
}
