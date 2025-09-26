package com.toa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "TOA: Invocation Raid Levels",
        description = "Shows each invocation’s raid-level increase directly in the Invocations panel.",
        tags = {"toa", "tombs", "invocation", "raid", "levels"}
)
public class ToaInvocationLevelsPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private ToaInvocationLevelsOverlay overlay;
    @Inject private ToaInvocationLevelsConfig config;

    private InvocationIndex index;
    private final Gson gson = new Gson();

    @Provides
    ToaInvocationLevelsConfig provideConfig(ConfigManager cm)
    {
        return cm.getConfig(ToaInvocationLevelsConfig.class);
    }

    @Override
    protected void startUp()
    {
        index = loadIndex();
        overlay.setIndex(index);
        overlay.setConfig(config);
        overlayManager.add(overlay);
        log.info("[TOA Levels] started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        overlay.setIndex(null);
        log.info("[TOA Levels] stopped");
    }

    private InvocationIndex loadIndex()
    {
        // Load built-in JSON
        Map<String, Integer> map = null;
        try (InputStream in = getClass().getResourceAsStream("/toa_invocation_levels.json"))
        {
            if (in != null)
            {
                Type t = new TypeToken<Map<String, Integer>>(){}.getType();
                map = gson.fromJson(new InputStreamReader(in), t);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to load embedded toa_invocation_levels.json", e);
        }

        // Merge user overrides from config (JSON text)
        try
        {
            String userJson = config.customMappingJson().trim();
            if (!userJson.isEmpty())
            {
                Type t = new TypeToken<Map<String, Integer>>(){}.getType();
                Map<String, Integer> user = gson.fromJson(userJson, t);
                if (map != null) map.putAll(user);
                else map = user;
            }
        }
        catch (Exception e)
        {
            log.warn("Custom JSON parse failed; ignoring overrides", e);
        }

        return new InvocationIndex(map);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded e)
    {
        // Optional: if you want to hard-pin the group when it appears,
        // you can react here. The overlay itself is robust and scans when needed.
    }

    @Subscribe
    public void onGameTick(GameTick t)
    {
        // lightweight periodic refresh (overlay uses a cache & short-circuits)
        if (overlay != null)
        {
            overlay.requestRefresh();
        }
    }
}
