package haven.resources.layers;

import haven.Coord;
import haven.MapView;
import haven.resources.Resource;
import haven.Utils;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:18
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Neg extends Layer {
    public Coord cc;
    public Coord bc, bs;
    public Coord sz;
    public Coord[][] ep;

    public Neg(byte[] buf) {
        int off;

        cc = Resource.cdec(buf, 0);
        bc = Resource.cdec(buf, 4);
        bs = Resource.cdec(buf, 8);
        sz = Resource.cdec(buf, 12);
        bc = MapView.s2m(bc);
        bs = MapView.s2m(bs).sub(bc);
        ep = new Coord[8][0];
        int en = buf[16];
        off = 17;
        for (int i = 0; i < en; i++) {
            int epid = buf[off];
            int cn = Utils.uint16d(buf, off + 1);
            off += 3;
            ep[epid] = new Coord[cn];
            for (int o = 0; o < cn; o++) {
                ep[epid][o] = Resource.cdec(buf, off);
                off += 4;
            }
        }
    }

    public void init() {
    }
}
