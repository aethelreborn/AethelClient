package aethel.client.screens.modernui.settings;

import aethel.client.api.settings.Setting;
import aethel.client.api.settings.impl.BindSetting;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.ButtonSetting;
import aethel.client.api.settings.impl.ColorSetting;
import aethel.client.api.settings.impl.ModeSetting;
import aethel.client.api.settings.impl.MultiModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.api.settings.impl.StringSetting;

final class SettingCardFactory {
    private SettingCardFactory() {
    }

    static SettingCardComponent<?> create(Setting<?> setting) {
        if (setting instanceof ModeSetting modeSetting) {
            return new ModeSettingCard(modeSetting);
        }
        if (setting instanceof MultiModeSetting multiModeSetting) {
            return new MultiSettingCard(multiModeSetting);
        }
        if (setting instanceof NumberSetting numberSetting) {
            return new SliderSettingCard(numberSetting);
        }
        if (setting instanceof BooleanSetting booleanSetting) {
            return new BooleanSettingCard(booleanSetting);
        }
        if (setting instanceof ColorSetting colorSetting) {
            return new ColorSettingCard(colorSetting);
        }
        if (setting instanceof BindSetting bindSetting) {
            return new BindSettingCard(bindSetting);
        }
        if (setting instanceof StringSetting stringSetting) {
            return new TextSettingCard(stringSetting);
        }
        if (setting instanceof ButtonSetting buttonSetting) {
            return new ButtonSettingCard(buttonSetting);
        }
        return null;
    }
}
