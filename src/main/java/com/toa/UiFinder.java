package com.toa;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

/**
 * Refactored UiFinder that avoids Client#getWidgets().
 * Scans interface groups by probing for a root at (groupId, 0) and then
 * looks for a container with many text children that look like invocation names.
 */
public final class UiFinder
{
    private UiFinder() {}

    public static class Entry
    {
        public final Widget nameWidget;
        public Entry(Widget nameWidget) { this.nameWidget = nameWidget; }
    }

    public static List<Entry> findInvocationNameWidgets(Client client)
    {
        List<Entry> out = new ArrayList<>();

        // Typical group id space; adjust if needed.
        final int MAX_GROUP_ID = 1200;

        for (int gi = 0; gi < MAX_GROUP_ID; gi++)
        {
            // Probe for a group root; if null, this group isn't loaded.
            Widget root = client.getWidget(gi, 0);
            if (root == null || root.isHidden())
            {
                continue;
            }

            // Traverse the root and its descendants (simple, non-recursive: just use getChildren on each).
            // We keep the original heuristic: find a container with many text children,
            // then collect the children that look like invocation names.
            collectFromTree(root, out);
            if (out.size() >= 10)
            {
                return out; // good enough threshold, matches original behavior
            }
        }

        return out;
    }

    private static void collectFromTree(Widget node, List<Entry> sink)
    {
        if (node == null) return;

        // Consider this node as a potential container if it has children.
        Widget[] children = node.getChildren();
        if (children != null && children.length > 0)
        {
            int textCount = 0;
            for (Widget c : children)
            {
                if (c == null || c.isHidden()) continue;
                String t = c.getText();
                if (t != null && !t.isEmpty())
                {
                    textCount++;
                }
            }

            // If it looks like the invocation list (lots of text rows), collect candidates from these children
            if (textCount >= 10)
            {
                for (Widget c : children)
                {
                    if (c == null || c.isHidden()) continue;
                    String t = c.getText();
                    if (t != null && t.length() >= 3 && isLikelyInvocationName(t))
                    {
                        sink.add(new Entry(c));
                    }
                }
                // We don't return here; the caller decides when enough are collected.
            }
        }

        // Recurse into descendants (children, dynamic, nested)
        if (children != null)
        {
            for (Widget c : children)
            {
                collectFromTree(c, sink);
            }
        }

        Widget[] dyn = node.getDynamicChildren();
        if (dyn != null)
        {
            for (Widget c : dyn)
            {
                collectFromTree(c, sink);
            }
        }

        Widget[] nested = node.getNestedChildren();
        if (nested != null)
        {
            for (Widget c : nested)
            {
                collectFromTree(c, sink);
            }
        }
    }

    private static boolean isLikelyInvocationName(String t)
    {
        String s = t.trim();
        if (s.length() < 3 || s.length() > 40) return false;
        if (s.matches(".*\\d.*")) return false;                // avoid numeric-only or counters
        return s.matches("[\\p{L} \\-’'()/]+");                 // letters, spaces, simple punctuation
    }
}
