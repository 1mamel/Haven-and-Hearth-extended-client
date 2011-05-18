package haven.resources.sources;

import haven.ResCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 16:54
*
* @author Vlad.Rassokhin@gmail.com
*/
public class CacheSource implements ResSource, Serializable {
    public transient ResCache cache;

    public CacheSource(ResCache cache) {
        this.cache = cache;
    }

    public InputStream get(String name) throws IOException {
        return (cache.fetch("res/" + name));
    }

    public String toString() {
        return ("cache source backed by " + cache);
    }
}
