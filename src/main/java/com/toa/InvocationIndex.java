package com.toa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InvocationIndex
{
    private final Map<String, Integer> byName;

    public InvocationIndex(Map<String, Integer> map)
    {
        if (map == null) map = Collections.emptyMap();
        // normalize keys to lower-case for matching
        Map<String, Integer> norm = new HashMap<>();
        for (Map.Entry<String, Integer> e : map.entrySet())
        {
            norm.put(clean(e.getKey()), e.getValue());
        }
        this.byName = norm;
    }

    public Integer find(String rawName)
    {
        if (rawName == null) return null;
        return byName.get(clean(rawName));
    }

    private static String clean(String s)
    {
        return s.toLowerCase(Locale.ROOT).trim().replace('\u00A0', ' ');
    }
}
