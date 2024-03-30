package com.mortisdevelopment.regionplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    public static String color(String message) {
        if (message == null) {
            return null;
        }
        return LegacyComponentSerializer.legacyAmpersand().serialize(getComponent(message));
    }

    public static String color(Component message) {
        if (message == null) {
            return null;
        }
        return LegacyComponentSerializer.legacyAmpersand().serialize(message);
    }

    public static Component getComponent(String message) {
        if (message == null) {
            return null;
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message).applyFallbackStyle(TextDecoration.ITALIC.withState(false));
    }
}