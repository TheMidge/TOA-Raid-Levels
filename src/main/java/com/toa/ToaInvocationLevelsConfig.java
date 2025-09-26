package com.toa;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("toa-invocation-levels")
public interface ToaInvocationLevelsConfig extends Config
{
    @ConfigItem(
            keyName = "showBrackets",
            name = "Show brackets",
            description = "Display numbers like [+3] instead of +3"
    )
    default boolean showBrackets() { return true; }

    @ConfigItem(
            keyName = "xOffset",
            name = "X offset (px)",
            description = "Horizontal offset relative to the right edge of the name widget."
    )
    default int xOffset() { return 10; }

    @ConfigItem(
            keyName = "yOffset",
            name = "Y offset (px)",
            description = "Vertical offset tweak."
    )
    default int yOffset() { return 0; }

    @ConfigItem(
            keyName = "fontSize",
            name = "Font size",
            description = "Overlay text size."
    )
    default int fontSize() { return 14; }

    @ConfigItem(
            keyName = "drawBackground",
            name = "Draw pill background",
            description = "Draw a subtle rounded rect behind the number."
    )
    default boolean drawBackground() { return true; }

    @ConfigItem(
            keyName = "customMappingJson",
            name = "Custom mapping (JSON)",
            description = "Optional JSON: {\"Invocation Name\": 3, ...} to override/add values."
    )
    default String customMappingJson() { return ""; }
}