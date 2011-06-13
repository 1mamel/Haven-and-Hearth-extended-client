package haven.resources.layers;

import haven.*;
import haven.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:15
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Image extends Layer implements Comparable<Image> {
    public transient BufferedImage img;
    transient private Tex tex;
    public final int z, subz;
    public final boolean nooff;
    public final int id;
    private int gay = -1;
    public Coord sz;
    public Coord o;

    public Image(byte[] buf) {
        z = Utils.int16d(buf, 0);
        subz = Utils.int16d(buf, 2);
        /* Obsolete flag 1: Layered */
        nooff = (buf[4] & 2) != 0;
        id = Utils.int16d(buf, 5);
        o = Resource.cdec(buf, 7);
        try {
            img = ImageIO.read(new ByteArrayInputStream(buf, 11, buf.length));  // -11 are not needed. see constructor JavaDoc
        } catch (IOException e) {
            throw (new Resource.LoadException(e, null));
        }
        if (img == null)
            throw (new Resource.LoadException("Invalid image data"));
        sz = Utils.imgsz(img);
    }

    public synchronized Tex tex() {
        if (tex != null)
            return tex;
        tex = new TexI(img);
        return tex;
    }

    private boolean detectgay() {
        for (int y = 0; y < sz.y(); y++) {
            for (int x = 0; x < sz.x(); x++) {
                if ((img.getRGB(x, y) & 0x00ffffff) == 0x00ff0080)
                    return (true);
            }
        }
        return (false);
    }

    public boolean gayp() {
        if (gay == -1)
            gay = detectgay() ? 1 : 0;
        return (gay == 1);
    }

    public int compareTo(Image other) {
        return (z - other.z);
    }

    public void init() {
        gayp();
    }
}
