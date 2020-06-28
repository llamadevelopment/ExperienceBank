package net.lldv.experiencebank.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.forms.FormWindows;

public class XpbankCommand extends Command {

    private static final ExperienceBank instance = ExperienceBank.getInstance();

    public XpbankCommand() {
        super(instance.getConfig().getString("Commands.XpbankCommand"));
        setDescription(instance.getConfig().getString("Commands.XpbankDescription"));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            FormWindows.openXpBank(player);
        }
        return false;
    }
}
