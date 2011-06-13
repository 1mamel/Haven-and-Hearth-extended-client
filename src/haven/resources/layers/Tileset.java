package haven.resources.layers;

import haven.*;
import haven.Resource;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:22
*
* @author Vlad.Rassokhin@gmail.com
*/
public class Tileset extends Layer {
    private int fl;
    private String[] fln;
    private int[] flv;
    private int[] flw;
    private WeightList<Resource> flavobjs;
    private WeightList<Tile> ground;
    private WeightList<Tile>[] ctrans;
    private WeightList<Tile>[] btrans;
    private int flavprob;
    private Resource resource;

    public Tileset(Resource resource, byte[] buf) {
        this.resource = resource;
        int[] off = new int[1];
        off[0] = 0;
        fl = Utils.ub(buf[off[0]++]);
        int flnum = Utils.uint16d(buf, off[0]);
        off[0] += 2;
        flavprob = Utils.uint16d(buf, off[0]);
        off[0] += 2;
        fln = new String[flnum];
        flv = new int[flnum];
        flw = new int[flnum];
        for (int i = 0; i < flnum; i++) {
            fln[i] = Utils.strd(buf, off);
            flv[i] = Utils.uint16d(buf, off[0]);
            off[0] += 2;
            flw[i] = Utils.ub(buf[off[0]++]);
        }
    }

    private void packtiles(Collection<Tile> tiles, Coord tsz) {
        int min = -1, minw = -1, minh = -1;
        int nt = tiles.size();
        for (int i = 1; i <= nt; i++) {
            int w = Tex.nextp2(tsz.x() * i);
            int h;
            if ((nt % i) == 0)
                h = nt / i;
            else
                h = (nt / i) + 1;
            h = Tex.nextp2(tsz.y() * h);
            int a = w * h;
            if ((min == -1) || (a < min)) {
                min = a;
                minw = w;
                minh = h;
            }
        }
        TexIM packbuf = new TexIM(new Coord(minw, minh));
        Graphics g = packbuf.graphics();
        int x = 0, y = 0;
        for (Tile t : tiles) {
            g.drawImage(t.img, x, y, null);
            t.tex = new TexSI(packbuf, new Coord(x, y), tsz);
            if ((x += tsz.x()) > (minw - tsz.x())) {
                x = 0;
                if ((y += tsz.y()) >= minh)
                    throw (new Resource.LoadException("Could not pack tiles into calculated minimum texture"));
            }
        }
        packbuf.update();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        flavobjs = new WeightList<Resource>();
        for (int i = 0; i < flw.length; i++) {
            try {
                flavobjs.add(Resource.load(fln[i], flv[i]), flw[i]);
            } catch (RuntimeException e) {
                throw (new Resource.LoadException("Illegal resource dependency", e));
            }
        }
        Collection<Tile> tiles = new LinkedList<Tile>();
        ground = new WeightList<Tile>();
        boolean hastrans = (fl & 1) != 0;
        if (hastrans) {
            ctrans = new WeightList[15];
            btrans = new WeightList[15];
            for (int i = 0; i < 15; i++) {
                ctrans[i] = new WeightList<Tile>();
                btrans[i] = new WeightList<Tile>();
            }
        }
        Coord tsz = null;
        for (Tile t : resource.layers(Tile.class)) {
            if (t.t == 'g')
                ground.add(t, t.w);
            else if (t.t == 'b' && hastrans)
                btrans[t.id - 1].add(t, t.w);
            else if (t.t == 'c' && hastrans)
                ctrans[t.id - 1].add(t, t.w);
            tiles.add(t);
            if (tsz == null) {
                tsz = Utils.imgsz(t.img);
            } else {
                if (!Utils.imgsz(t.img).equals(tsz)) {
                    throw (new Resource.LoadException("Different tile sizes within set"));
                }
            }
        }
        packtiles(tiles, tsz);
    }

    public WeightList<Resource> getFlavobjs() {
        return flavobjs;
    }

    public void setFlavobjs(WeightList<Resource> flavobjs) {
        this.flavobjs = flavobjs;
    }

    public WeightList<Tile> getGround() {
        return ground;
    }

    public void setGround(WeightList<Tile> ground) {
        this.ground = ground;
    }

    public WeightList<Tile>[] getCtrans() {
        return ctrans;
    }

    public void setCtrans(WeightList<Tile>[] ctrans) {
        this.ctrans = ctrans;
    }

    public WeightList<Tile>[] getBtrans() {
        return btrans;
    }

    public void setBtrans(WeightList<Tile>[] btrans) {
        this.btrans = btrans;
    }

    public int getFlavprob() {
        return flavprob;
    }

    public void setFlavprob(int flavprob) {
        this.flavprob = flavprob;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
