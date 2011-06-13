package haven.resources.layers;

import haven.Resource;
import haven.Utils;

import java.util.LinkedList;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:20
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Anim extends Layer {
    private int[] ids;
    public int id, d;
    public Image[][] f;
    private Resource resource;

    public Anim(Resource resource, byte[] buf) {
        this.resource = resource;
        id = Utils.int16d(buf, 0);
        d = Utils.uint16d(buf, 2);
        ids = new int[Utils.uint16d(buf, 4)];
        if (buf.length - 6 != ids.length * 2)
            throw (new Resource.LoadException("Invalid anim descriptor in " + resource.name));
        for (int i = 0; i < ids.length; i++)
            ids[i] = Utils.int16d(buf, 6 + (i * 2));
    }

    public void init() {
        f = new Image[ids.length][];
        Image[] typeinfo = new Image[0];
        for (int i = 0; i < ids.length; i++) {
            LinkedList<Image> buf = new LinkedList<Image>();
            for (Image img : resource.layers(Image.class)) {
                if (img.id == ids[i])
                    buf.add(img);
            }
            f[i] = buf.toArray(typeinfo);
        }
    }
}
