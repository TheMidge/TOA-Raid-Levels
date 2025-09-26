package com.toa;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ToaInvocationLevelsOverlay extends Overlay
{
    private final Client client;

    @Setter private InvocationIndex index;
    @Setter private ToaInvocationLevelsConfig config;

    private boolean refreshRequested = true;
    private List<UiFinder.Entry> cache;

    @Inject
    public ToaInvocationLevelsOverlay(Client client)
    {
        this.client = client;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_MED);
    }

    public void requestRefresh()
    {
        refreshRequested = true;
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (index == null || config == null || client.getGameState() == null)
            return null;

        if (refreshRequested || cache == null)
        {
            cache = UiFinder.findInvocationNameWidgets(client);
            refreshRequested = false;
        }
        if (cache == null || cache.isEmpty())
            return null;

        final Font font = g.getFont().deriveFont((float) config.fontSize());
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (UiFinder.Entry e : cache)
        {
            Widget w = e.nameWidget;
            if (w == null || w.isHidden()) continue;

            String name = w.getText();
            Integer delta = index.find(name);
            if (delta == null) continue;

            String text = (config.showBrackets() ? "[+" + delta + "]" : "+" + delta);

            int textWidth = g.getFontMetrics().stringWidth(name);
            int textHeight = g.getFontMetrics().getAscent(); // baseline ascent
            int baseX = w.getCanvasLocation().getX() + textWidth + config.xOffset();
            int baseY = w.getCanvasLocation().getY() + textHeight + config.yOffset();

            // optional pill background
            if (config.drawBackground())
            {
                int padX = 4, padY = 2;
                int tw = g.getFontMetrics().stringWidth(text);
                int th = g.getFontMetrics().getAscent();
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(
                        baseX - padX, baseY - th - padY, tw + padX * 2, th + padY * 2, 10, 10);
                g.setColor(new Color(0, 0, 0, 140));
                g.fill(rr);
                g.setStroke(new BasicStroke(1f));
                g.setColor(new Color(255, 255, 255, 70));
                g.draw(rr);
            }

            g.setColor(Color.WHITE);
            g.drawString(text, baseX, baseY);
        }

        return null;
    }
}
