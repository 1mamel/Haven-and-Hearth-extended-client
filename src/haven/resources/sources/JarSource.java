package haven.resources.sources;

import haven.resources.Resource;

import java.io.InputStream;
import java.io.Serializable;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 16:52
*
* @author Vlad.Rassokhin@gmail.com
*/
public class JarSource implements ResSource, Serializable {
    public InputStream get(String name) {
        InputStream s = Resource.class.getResourceAsStream("/res/" + name + ".res");
        if (s == null)
            throw (new Resource.LoadException("Could not find resource locally: " + name));
        return (s);
    }

    public String toString() {
        return ("local res source");
    }
}
