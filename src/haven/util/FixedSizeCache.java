package haven.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vlad.Rassokhin@gmail.com
 */
public class FixedSizeCache<K,V> extends LinkedHashMap<K,V> {
    private final int myMaxSize;
    private static final int DEFAULT_MAX_SIZE = 50;

    public FixedSizeCache(final int maxSize) {
        myMaxSize = maxSize;
    }

    public FixedSizeCache() {
        this(DEFAULT_MAX_SIZE);
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > myMaxSize;
    }
}
