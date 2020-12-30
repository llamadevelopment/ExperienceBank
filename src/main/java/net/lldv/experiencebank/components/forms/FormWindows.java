package net.lldv.experiencebank.components.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import lombok.AllArgsConstructor;
import net.lldv.experiencebank.components.api.API;
import net.lldv.experiencebank.components.forms.custom.CustomForm;
import net.lldv.experiencebank.components.forms.simple.SimpleForm;
import net.lldv.experiencebank.components.provider.Provider;
import net.lldv.experiencebank.components.tools.Language;

@AllArgsConstructor
public class FormWindows {

    private final Provider provider;

    public void openXpBank(final Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("xpbank-menu-title"), Language.getNP("xpbank-menu-content", API.getCachedXp().get(player.getName())))
                .addButton(new ElementButton(Language.getNP("xpbank-menu-deposit"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-deposit-uiicon.png")), this::openDepositMenu)
                .addButton(new ElementButton(Language.getNP("xpbank-menu-withdraw"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-withdraw-uiicon.png")), this::openWithdrawMenu)
                .addButton(new ElementButton(Language.getNP("xpbank-menu-pay"),
                        new ElementButtonImageData("url", "http://system01.lldv.net:3000/img/experiencebank-pay-uiicon.png")), this::openPayMenu)
                .build();
        form.send(player);
    }

    public void openDepositMenu(final Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-deposit-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-deposit-menu-slider"), 0, (float) this.convert(player), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    double xp = this.convert(player) - i;
                    player.setExperience(0, 0);
                    this.provider.setBankXp(player.getName(), API.getCachedXp().get(player.getName()) + i);
                    player.addExperience((int) xp);
                    player.sendMessage(Language.get("xp-deposit-success", i));
                    this.playSound(player, Sound.RANDOM_LEVELUP);
                })
                .build();
        form.send(player);
    }

    public void openWithdrawMenu(final Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-withdraw-menu-title"))
                .addElement(new ElementSlider(Language.getNP("xpbank-withdraw-menu-slider"), 0, API.getCachedXp().get(player.getName()), 1, 0))
                .onSubmit((e, r) -> {
                    int i = (int) r.getSliderResponse(0);
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    this.provider.setBankXp(player.getName(), API.getCachedXp().get(player.getName()) - i);
                    player.addExperience(i);
                    player.sendMessage(Language.get("xp-withdraw-success", i));
                    this.playSound(player, Sound.RANDOM_LEVELUP);
                })
                .build();
        form.send(player);
    }

    public void openPayMenu(final Player player) {
        CustomForm form = new CustomForm.Builder(Language.getNP("xpbank-pay-menu-title"))
                .addElement(new ElementInput(Language.getNP("xpbank-pay-menu-player"), Language.getNP("xpbank-pay-menu-player-placeholder")))
                .addElement(new ElementSlider(Language.getNP("xpbank-pay-menu-slider"), 0, API.getCachedXp().get(player.getName()), 1, 0))
                .onSubmit((g, h) -> {
                    String t =  h.getInputResponse(0);
                    int i = (int) h.getSliderResponse(1);
                    if (t == null || t.isEmpty() || !this.provider.userExists(t) || t.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Language.get("invalid-player"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    if (i == 0) {
                        player.sendMessage(Language.get("amount-not-null"));
                        this.playSound(player, Sound.NOTE_PLING);
                        return;
                    }
                    this.provider.setBankXp(player.getName(), API.getCachedXp().get(player.getName()) - i);
                    Player tt = Server.getInstance().getPlayer(t);
                    if (tt != null) {
                        this.provider.setBankXp(t, API.getCachedXp().get(t) + i);
                        tt.sendMessage(Language.get("xp-pay-got", player.getName(), i));
                        this.playSound(player, Sound.RANDOM_LEVELUP);
                    } else {
                        this.provider.getBankXp(t, xp -> this.provider.setBankXp(t, xp + i));
                    }
                    player.sendMessage(Language.get("xp-pay-paid", t, i));
                    this.playSound(player, Sound.RANDOM_LEVELUP);
                })
                .build();
        form.send(player);
    }

    private double convert(final Player player) {
        return this.provider.convertLevelToExperience(player.getExperienceLevel()) + player.getExperience();
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
