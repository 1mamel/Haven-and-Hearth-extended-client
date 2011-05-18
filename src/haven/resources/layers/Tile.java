package haven.resources.layers;

import haven.resources.Resource;
import haven.Tex;
import haven.TexI;
import haven.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:23
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Tile extends Layer {
    transient BufferedImage img;
    transient Tex tex;
    int id;
    int w;
    char t;

    public Tile(byte[] buf) {
        t = (char) Utils.ub(buf[0]);
        id = Utils.ub(buf[1]);
        w = Utils.uint16d(buf, 2);
        try {
            img = ImageIO.read(new ByteArrayInputStream(buf, 4, buf.length - 4));
        } catch (IOException e) {
            throw (new Resource.LoadException(e));
        }
        if (img == null)
            throw (new Resource.LoadException("Invalid image data"));
    }

    public synchronized Tex tex() {
        if (tex == null)
            tex = new TexI(img);
        return (tex);
    }

    public void init() {
    }
}
