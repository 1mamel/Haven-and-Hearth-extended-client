package haven.resources.layers;

import haven.Utils;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:29
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Code extends Layer {
    public final String name;
    transient public final byte[] data;

    public Code(byte[] buf) {
        int[] off = new int[1];
        off[0] = 0;
        name = Utils.strd(buf, off);
        data = new byte[buf.length - off[0]];
        System.arraycopy(buf, off[0], data, 0, data.length);
    }

    public void init() {
    }
}
