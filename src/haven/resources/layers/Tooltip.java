package haven.resources.layers;

import haven.Resource;

import java.io.UnsupportedEncodingException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:28
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Tooltip extends Layer {
    public final String t;

    public Tooltip(final byte[] buf) {
        try {
            t = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (new Resource.LoadException(e));
        }
    }

    public void init() {
    }
}
