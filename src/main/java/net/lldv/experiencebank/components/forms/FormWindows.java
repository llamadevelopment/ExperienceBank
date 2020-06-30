package net.lldv.experiencebank.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementSlider;
import net.lldv.experiencebank.ExperienceBank;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.forms.custom.CustomForm;
import net.lldv.experiencebank.components.forms.simple.SimpleForm;
import net.lldv.experiencebank.components.managers.provider.Provider;
import net.lldv.experiencebank.components.tools.Language;

public class FormWindows {

    private static final ExperienceBank instance = ExperienceBank.getInstance();
    private static final Provider api = ExperienceBankAPI.getProvider();

    public static void openXpBank(Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("xpbank-menu-title"), Language.getNP("xpbank-menu-content", instance.xpMap.get(player.getName())))
                .addButton(new ElementButton(Language.getNP("xpbank-menu-deposit"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-deposit-uiicon.png")), e -> openDepositMenu(player))
                .addButton(new ElementButton(Language.getNP("xpbank-menu-withdraw"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-withdraw-uiicon.png")), e -> openWithdrawMenu(player))
                .build();
        form.send(player);
    }

    public static void openDepositMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-deposit-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-deposit-menu-slider"), 0, (float) convert(player), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        return;
                    }
                    double xp = convert(player) - i;
                    player.setExperience(0, 0);
                    api.setBankXp(player.getName(), instance.xpMap.get(player.getName()) + i);
                    player.addExperience((int) xp);
                    player.sendMessage(Language.get("xp-deposit-success", i));
                })
                .build();
        form.send(player);
    }

    public static void openWithdrawMenu(Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-withdraw-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-withdraw-menu-slider"), 0, instance.xpMap.get(player.getName()), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        return;
                    }
                    api.setBankXp(player.getName(), instance.xpMap.get(player.getName()) - i);
                    player.addExperience(i);
                    player.sendMessage(Language.get("xp-withdraw-success", i));
                })
                .build();
        form.send(player);
    }

    private static double convert(Player player) {
        return ExperienceBankAPI.convertLevelToExperience(player.getExperienceLevel()) + player.getExperience();
    }

}
