package haven.resources.layers;

import haven.resources.Resource;

import java.io.UnsupportedEncodingException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:25
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Pagina extends Layer {
    public final String text;

    public Pagina(byte[] buf) {
        try {
            text = new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (new Resource.LoadException(e));
        }
    }

    public void init() {
    }
}
