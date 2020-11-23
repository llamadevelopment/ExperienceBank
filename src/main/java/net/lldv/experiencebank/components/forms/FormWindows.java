package net.lldv.experiencebank.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import lombok.AllArgsConstructor;
import net.lldv.experiencebank.components.api.ExperienceBankAPI;
import net.lldv.experiencebank.components.forms.custom.CustomForm;
import net.lldv.experiencebank.components.forms.simple.SimpleForm;
import net.lldv.experiencebank.components.provider.Provider;
import net.lldv.experiencebank.components.tools.Language;

@AllArgsConstructor
public class FormWindows {

    private final Provider provider;

    public void openXpBank(final Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("xpbank-menu-title"), Language.getNP("xpbank-menu-content", ExperienceBankAPI.getCachedXp().get(player.getName())))
                .addButton(new ElementButton(Language.getNP("xpbank-menu-deposit"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-deposit-uiicon.png")), e -> this.openDepositMenu(player))
                .addButton(new ElementButton(Language.getNP("xpbank-menu-withdraw"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-withdraw-uiicon.png")), e -> this.openWithdrawMenu(player))
                .build();
        form.send(player);
    }

    public void openDepositMenu(final Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-deposit-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-deposit-menu-slider"), 0, (float) convert(player), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    double xp = convert(player) - i;
                    player.setExperience(0, 0);
                    this.provider.setBankXp(player.getName(), ExperienceBankAPI.getCachedXp().get(player.getName()) + i);
                    player.addExperience((int) xp);
                    player.sendMessage(Language.get("xp-deposit-success", i));
                    this.playSound(player, Sound.RANDOM_LEVELUP);
                })
                .build();
        form.send(player);
    }

    public void openWithdrawMenu(final Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-withdraw-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-withdraw-menu-slider"), 0, ExperienceBankAPI.getCachedXp().get(player.getName()), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    this.provider.setBankXp(player.getName(), ExperienceBankAPI.getCachedXp().get(player.getName()) - i);
                    player.addExperience(i);
                    player.sendMessage(Language.get("xp-withdraw-success", i));
                    this.playSound(player, Sound.RANDOM_LEVELUP);
                })
                .build();
        form.send(player);
    }

    private double convert(final Player player) {
        return ExperienceBankAPI.convertLevelToExperience(player.getExperienceLevel()) + player.getExperience();
    }

    private void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.x = new Double(player.getLocation().getX()).intValue();
        packet.y = (new Double(player.getLocation().getY())).intValue();
        packet.z = (new Double(player.getLocation().getZ())).intValue();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        player.dataPacket(packet);
    }

}
