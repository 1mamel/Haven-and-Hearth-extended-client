/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import haven.resources.layers.Tile;
import haven.resources.layers.Tileset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.zip.Inflater;

public class MCache {
    final Tileset[] sets = new Tileset[256];
    Grid last = null;
    final java.util.Map<Coord, Grid> req = new TreeMap<Coord, Grid>();
    final java.util.Map<Coord, Grid> grids = new TreeMap<Coord, Grid>();
    final Session sess;
    final Set<Overlay> ols = new HashSet<Overlay>();
    public static final Coord tilesz = new Coord(11, 11);
    public static final Coord cmaps = new Coord(100, 100);
    final Random gen = new Random();
    final java.util.Map<Integer, Defrag> fragbufs = new TreeMap<Integer, Defrag>();
    public static final Map<Integer, Color> colors = new TreeMap<Integer, Color>();

    static {
        colors.put(0, new Color(0x3152a2));    //deep water
        colors.put(1, new Color(0x4480c8));    //shallow water
        //colors.put(5, new Color(125,125,125));	//mountain
        colors.put(8, new Color(160, 160, 160));    //stone paving
        colors.put(9, new Color(200, 200, 200));    //plowed
        colors.put(10, new Color(0x497937));    //coniferous forest
        colors.put(11, new Color(0x60864f));    //broadleaf forest
        colors.put(12, new Color(220, 220, 200));    //thicket
        colors.put(13, new Color(0x468d37));    //grass
        colors.put(14, new Color(0xac7664));    //moor
        colors.put(15, new Color(0x999927));    //heath
        colors.put(16, new Color(0x60ad8a));    //swamp 1
        colors.put(17, new Color(0x3d6242));    //swamp 2
        colors.put(18, new Color(0x5e6453));    //swamp 3
        colors.put(19, new Color(0xa67936));    //dirt
        colors.put(20, new Color(212, 164, 81));    //sand
        colors.put(21, new Color(212, 212, 212));    //house
        colors.put(24, new Color(80, 80, 80));    //mine
        colors.put(25, new Color(112, 116, 112));    //cave
        colors.put(255, new Color(0, 0, 0));    //void
    }

    public class Overlay {
        Set<Overlay> list;
        Coord c1, c2;
        final int mask;

        public Overlay(Coord c1, Coord c2, int mask) {
            this(ols, c1, c2, mask);
        }

        public Overlay(Set<Overlay> set, Coord c1, Coord c2, int mask) {
            this.list = set;
            this.c1 = c1;
            this.c2 = c2;
            this.mask = mask;
            set.add(this);
        }

        public void destroy() {
            list.remove(this);
            list = null;
        }

        public void update(Coord c1, Coord c2) {
            this.c1 = c1;
            this.c2 = c2;
        }
    }

    public class Grid {
        public final int[][] tiles;
        public final Tile[][] gcache;
        public final Tile[][][] tcache;
        public final int[][] ol;
        Set<Overlay> ols = new HashSet<Overlay>();
        final Collection<Gob> fo = new LinkedList<Gob>();
        boolean regged = false;
        public long lastreq = 0;
        public int reqs = 0;
        final Coord gc;
        final OCache oc = sess.glob.oc;
        String mnm;
        BufferedImage img;
        Tex tex;

        public Grid(Coord gc) {
            this.gc = gc;
            tiles = new int[cmaps.x][cmaps.y];
            ol = new int[cmaps.x][cmaps.y];
            gcache = new Tile[cmaps.x][cmaps.y];
            tcache = new Tile[cmaps.x][cmaps.y][];
        }

        public int gettile(Coord tc) {
            return (tiles[tc.x][tc.y]);
        }

        public int getol(Coord tc) {
            return (ol[tc.x][tc.y]);
        }

        public void remove() {
            if (regged) {
                oc.lrem(fo);
                regged = false;
            }
        }

        public void render() {
            img = TexI.mkbuf(cmaps);
            Graphics2D g = img.createGraphics();

            for (int y = 0; y < cmaps.x; ++y) {
                for (int x = 0; x < cmaps.y; ++x) {
                    int id = tiles[x][y];
                    Color col = colors.get(id);
                    if (col == null) {
                        col = new Color(255, 0, 255);
                        System.out.println(id);
                    }
                    g.setColor(col);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        public Tex getTex() {
            if ((tex == null) && (img != null)) {
                tex = new TexI(img);
            }
            return tex;
        }

        public void makeflavor() {
            fo.clear();
            Coord tc = gc.mul(cmaps);
            for (int cy = 0; cy < cmaps.x; ++cy) {
                for (int cx = 0; cx < cmaps.y; ++cx) {
                    Tileset set = sets[tiles[cx][cy]];
                    WeightList<Resource> flavobjs = set.getFlavobjs();
                    if (!flavobjs.isEmpty()) {
                        Random rnd = mkrandoom(cx, cy);
                        if (rnd.nextInt(set.getFlavprob()) == 0) {
                            Resource r = flavobjs.pick(rnd);
                            Gob g = new Gob(sess.glob, tc.add(cx, cy).mul(tilesz), -1, 0);
                            g.setattr(new ResDrawable(g, r));
                            fo.add(g);
                        }
                    }
                }
            }
            if (!regged) {
                oc.ladd(fo);
                regged = true;
            }
        }

        public int randoom(Coord c, int r) {
            return (MCache.this.randoom(c.add(gc.mul(cmaps)), r));
        }

        public Random mkrandoom(int x, int y) {
            return (MCache.mkrandoom((gc.mul(cmaps)).add(x, y)));
        }

        public Random mkrandoom(Coord c) {
            return (MCache.mkrandoom(c.add(gc.mul(cmaps))));
        }
    }

    private Tileset loadset(String name, int ver) {
        Resource res = Resource.load(name, ver);
        res.loadwait();
        Tileset layer = res.layer(Resource.tileset);
        return layer;
    }

    public MCache(Session sess) {
        this.sess = sess;
    }

    private static void initrandoom(Random r, Coord c) {
        r.setSeed(c.x);
        r.setSeed(r.nextInt() ^ c.y);
    }

    public int randoom(Coord c) {
        int ret;

        synchronized (gen) {
            initrandoom(gen, c);
            ret = Math.abs(gen.nextInt());
            return (ret);
        }
    }

    public int randoom(Coord c, int r) {
        return (randoom(c) % r);
    }

    public static Random mkrandoom(Coord c) {
        Random ret = new Random();
        initrandoom(ret, c);
        return (ret);
    }

    private void replace(Grid g) {
        if (g == last)
            last = null;
    }

    public void invalidate(Coord cc) {
        synchronized (req) {
            if (req.get(cc) == null)
                req.put(cc, new Grid(cc));
        }
    }

    public void invalblob(Message msg) {
        int type = msg.uint8();
        if (type == 0) {
            invalidate(msg.coord());
        } else if (type == 1) {
            Coord ul = msg.coord();
            Coord lr = msg.coord();
            trim(ul, lr);
        } else if (type == 2) {
            trimall();
        }
    }

    public Tile[] gettrans(Coord tc) {
        Grid g;
        synchronized (grids) {
            Coord gc = tc.div(cmaps);
            if ((last != null) && last.gc.equals(gc))
                g = last;
            else
                last = g = grids.get(gc);
        }
        if (g == null)
            return (null);
        Coord gtc = tc.mod(cmaps);
        if (g.tcache[gtc.x][gtc.y] == null) {
            int tr[][] = new int[3][3];
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    if ((x == 0) && (y == 0))
                        continue;
                    int tn = gettilen(tc.add(new Coord(x, y)));
                    if (tn < 0)
                        return (null);
                    tr[x + 1][y + 1] = tn;
                }
            }
            if (tr[0][0] >= tr[1][0]) tr[0][0] = -1;
            if (tr[0][0] >= tr[0][1]) tr[0][0] = -1;
            if (tr[2][0] >= tr[1][0]) tr[2][0] = -1;
            if (tr[2][0] >= tr[2][1]) tr[2][0] = -1;
            if (tr[0][2] >= tr[0][1]) tr[0][2] = -1;
            if (tr[0][2] >= tr[1][2]) tr[0][2] = -1;
            if (tr[2][2] >= tr[2][1]) tr[2][2] = -1;
            if (tr[2][2] >= tr[1][2]) tr[2][2] = -1;
            int bx[] = {0, 1, 2, 1};
            int by[] = {1, 0, 1, 2};
            int cx[] = {0, 2, 2, 0};
            int cy[] = {0, 0, 2, 2};
            ArrayList<Tile> buf = new ArrayList<Tile>();
            for (int i = gettilen(tc) - 1; i >= 0; i--) {
                if ((sets[i] == null) || (sets[i].getBtrans() == null) || (sets[i].getCtrans() == null))
                    continue;
                int bm = 0, cm = 0;
                for (int o = 0; o < 4; o++) {
                    if (tr[bx[o]][by[o]] == i)
                        bm |= 1 << o;
                    if (tr[cx[o]][cy[o]] == i)
                        cm |= 1 << o;
                }
                if (bm != 0)
                    buf.add(sets[i].getBtrans()[bm - 1].pick(randoom(tc)));
                if (cm != 0)
                    buf.add(sets[i].getCtrans()[cm - 1].pick(randoom(tc)));
            }
            g.tcache[gtc.x][gtc.y] = buf.toArray(new Tile[buf.size()]);
        }
        return (g.tcache[gtc.x][gtc.y]);
    }

    public Tile getground(Coord tc) {
        Grid g;
        synchronized (grids) {
            Coord gc = tc.div(cmaps);
            if ((last != null) && last.gc.equals(gc))
                g = last;
            else
                last = g = grids.get(gc);
        }
        if (g == null)
            return (null);
        Coord gtc = tc.mod(cmaps);
        if (g.gcache[gtc.x][gtc.y] == null) {
            Tileset ts = sets[g.gettile(gtc)];
            if (ts != null) {
                g.gcache[gtc.x][gtc.y] = ts.getGround().pick(randoom(tc));
            }
        }
        return (g.gcache[gtc.x][gtc.y]);
    }

    public int gettilen(Coord tc) {
        Grid g;
        synchronized (grids) {
            Coord gc = tc.div(cmaps);
            if ((last != null) && last.gc.equals(gc))
                g = last;
            else
                last = g = grids.get(gc);
        }
        if (g == null)
            return (-1);
        return (g.gettile(tc.mod(cmaps)));
    }

    public Tileset gettile(Coord tc) {
        int tn = gettilen(tc);
        if (tn == -1)
            return (null);
        return (sets[tn]);
    }

    public int getol(Coord tc) {
        Grid g;
        synchronized (grids) {
            Coord gc = tc.div(cmaps);
            if ((last != null) && last.gc.equals(gc))
                g = last;
            else
                last = g = grids.get(gc);
        }
        if (g == null)
            return (-1);
        int ol = g.getol(tc.mod(cmaps));
        for (Overlay lol : ols) {
            if (tc.isect(lol.c1, lol.c2.sub(lol.c1).add(1, 1)))
                ol |= lol.mask;
        }
        return (ol);
    }

    public void mapdata2(Message msg) {
        Coord c = msg.coord();
        String mmname = msg.string().intern();
        if (mmname.length() == 0)
            mmname = null;
        int[] pfl = new int[256];
        while (true) {
            int pidx = msg.uint8();
            if (pidx == 255)
                break;
            pfl[pidx] = msg.uint8();
        }
        Message blob = new Message(0);
        {
            Inflater z = new Inflater();
            z.setInput(msg.blob, msg.off, msg.blob.length - msg.off);
            byte[] buf = new byte[10000];
            while (true) {
                try {
                    int len;
                    if ((len = z.inflate(buf)) == 0) {
                        if (!z.finished())
                            throw (new RuntimeException("Got unterminated map blob"));
                        break;
                    }
                    blob.addbytes(buf, 0, len);
                } catch (java.util.zip.DataFormatException e) {
                    throw (new RuntimeException("Got malformed map blob", e));
                }
            }
        }
        synchronized (req) {
            synchronized (grids) {
                if (req.containsKey(c)) {
                    Grid g = req.get(c);
                    g.mnm = mmname;
                    for (int y = 0; y < cmaps.y; y++) {
                        for (int x = 0; x < cmaps.x; x++) {
                            g.tiles[x][y] = blob.uint8();
                        }
                    }
                    for (int y = 0; y < cmaps.y; y++) {
                        for (int x = 0; x < cmaps.x; x++)
                            g.ol[x][y] = 0;
                    }
                    while (true) {
                        int pidx = blob.uint8();
                        if (pidx == 255)
                            break;
                        int fl = pfl[pidx];
                        int type = blob.uint8();
                        Coord c1 = new Coord(blob.uint8(), blob.uint8());
                        Coord c2 = new Coord(blob.uint8(), blob.uint8());
                        int ol;
                        if (type == 0) {
                            if ((fl & 1) == 1)
                                ol = 2;
                            else
                                ol = 1;
                        } else if (type == 1) {
                            if ((fl & 1) == 1)
                                ol = 8;
                            else
                                ol = 4;
                        } else {
                            throw (new RuntimeException("Unknown plot type " + type));
                        }
                        for (int y = c1.y; y <= c2.y; y++) {
                            for (int x = c1.x; x <= c2.x; x++) {
                                g.ol[x][y] |= ol;
                            }
                        }
                        new Overlay(g.ols, c1, c2, ol);
                    }
                    req.remove(c);
                    g.makeflavor();
                    if (grids.containsKey(c)) {
                        grids.get(c).remove();
                        replace(grids.remove(c));
                    }
                    grids.put(c, g);
                    g.render();
                }
            }
        }
    }

    public void mapdata(Message msg) {
        long now = System.currentTimeMillis();
        int pktid = msg.int32();
        int off = msg.uint16();
        int len = msg.uint16();
        Defrag fragbuf;
        synchronized (fragbufs) {
            if ((fragbuf = fragbufs.get(pktid)) == null) {
                fragbuf = new Defrag(len);
                fragbufs.put(pktid, fragbuf);
            }
            fragbuf.add(msg.blob, 8, msg.blob.length - 8, off);
            fragbuf.last = now;
            if (fragbuf.done()) {
                mapdata2(fragbuf.msg());
                fragbufs.remove(pktid);
            }

            /* Clean up old buffers */
            for (Iterator<Map.Entry<Integer, Defrag>> i = fragbufs.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, Defrag> e = i.next();
                Defrag old = e.getValue();
                if (now - old.last > 10000)
                    i.remove();
            }
        }
    }

    public void tilemap(Message msg) {
        while (!msg.eom()) {
            int id = msg.uint8();
            String resnm = msg.string();
            int resver = msg.uint16();
            sets[id] = loadset(resnm, resver);
        }
    }

    public void trimall() {
        synchronized (req) {
            synchronized (grids) {
                for (Grid g : req.values())
                    g.remove();
                for (Grid g : grids.values())
                    g.remove();
                grids.clear();
                req.clear();
            }
        }
        synchronized (MiniMap.caveTex) {
            MiniMap.caveTex.clear();
        }
        UI.instance.mainview.resetcam();
    }

    public void trim(Coord ul, Coord lr) {
        synchronized (grids) {
            for (Iterator<Map.Entry<Coord, Grid>> i = grids.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Coord, Grid> e = i.next();
                Coord gc = e.getKey();
                Grid g = e.getValue();
                if ((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y)) {
                    i.remove();
                    g.remove();
                }
            }
        }
        synchronized (req) {
            for (Iterator<Map.Entry<Coord, Grid>> i = req.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Coord, Grid> e = i.next();
                Coord gc = e.getKey();
                Grid g = e.getValue();
                if ((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y)) {
                    i.remove();
                    g.remove();
                }
            }
        }
    }

    public void request(Coord gc) {
        synchronized (req) {
            if (!req.containsKey(gc))
                req.put(gc, new Grid(gc));
        }
    }

    public void sendreqs() {
        long now = System.currentTimeMillis();
        synchronized (req) {
            for (Iterator<Map.Entry<Coord, Grid>> i = req.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Coord, Grid> e = i.next();
                Coord c = e.getKey();
                Grid gr = e.getValue();
                if (now - gr.lastreq > 1000) {
                    gr.lastreq = now;
                    if (++gr.reqs >= 5) {
                        i.remove();
                    } else {
                        Message msg = new Message(Session.MSG_MAPREQ);
                        msg.addcoord(c);
                        sess.sendmsg(msg);
                    }
                }
            }
        }
    }
}
