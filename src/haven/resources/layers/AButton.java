package haven.resources.layers;

import haven.Resource;
import haven.Utils;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:26
*
* @author Vlad.Rassokhin@gmail.com
*/
public class AButton extends Layer {
    public final String name;
    public final Resource parent;
    public final char hk;
    public final String[] ad;

    public AButton(final byte[] buf) {
        final int[] off = new int[1];
        off[0] = 0;
        final String pr = Utils.strd(buf, off);
        final int pver = Utils.uint16d(buf, off[0]);
        off[0] += 2;
        if (pr.length() == 0) {
            parent = null;
        } else {
            try {
                parent = Resource.load(pr, pver);
            } catch (RuntimeException e) {
                throw (new Resource.LoadException("Illegal resource dependency", e));
            }
        }
        name = Utils.strd(buf, off);
        Utils.strd(buf, off); /* Prerequisite skill */
        hk = (char) Utils.uint16d(buf, off[0]);
        off[0] += 2;
        ad = new String[Utils.uint16d(buf, off[0])];
        off[0] += 2;
        for (int i = 0; i < ad.length; i++)
            ad[i] = Utils.strd(buf, off);
    }

    public void init() {
    }
}
