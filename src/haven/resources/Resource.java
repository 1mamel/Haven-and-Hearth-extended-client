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

package haven.resources;

import haven.*;
import haven.resources.layers.*;
import haven.resources.sources.*;
import haven.util.Prioritized;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Resource extends Prioritized implements Comparable<Resource>, Serializable {
    private static final Map<String, Resource> cache = new TreeMap<String, Resource>();
    private static ResourceLoader loader;
    private static CacheSource prscache;
    public static ThreadGroup loadergroup = null;
    private static Map<String, Class<? extends Layer>> ltypes = new TreeMap<String, Class<? extends Layer>>();
    public static final Set<Resource> loadwaited = new HashSet<Resource>();
    public static Class<haven.resources.layers.Image> imgc = haven.resources.layers.Image.class;
    public static Class<Tile> tile = Tile.class;
    public static Class<Neg> negc = Neg.class;
    public static Class<Anim> animc = Anim.class;
    public static Class<Tileset> tileset = Tileset.class;
    public static Class<Pagina> pagina = Pagina.class;
    public static Class<AButton> action = AButton.class;
    public static Class<AudioL> audio = AudioL.class;
    public static Class<Tooltip> tooltip = Tooltip.class;

    static {
        try {
            File file = new File("./custom_res");
            if (file.exists()) {
                chainloader(new ResourceLoader(new FileSource(file)));
            }
        } catch (Exception e) {
            CustomConfig.logger.error("Cannot load ./custom_res repo", e);
        }
        try {
            File file = new File("./res");
            if (file.exists()) {
                chainloader(new ResourceLoader(new FileSource(file)));
            }
        } catch (Exception e) {
            CustomConfig.logger.error("Cannot load ./res repo", e);
        }
        try {
            String dir = Config.resdir;
            if (dir == null)
                dir = System.getenv("HAVEN_RESDIR");
            if (dir != null) {
                File base = new File(dir);
                if (base.exists()) {
                    chainloader(new ResourceLoader(new FileSource(base)));
                }
            }
        } catch (Exception e) {
            /* Ignore these. We don't want to be crashing the client
            * for users just because of errors in development
            * aids. */
        }
        if (!Config.nolocalres)
            chainloader(new ResourceLoader(new JarSource()));
    }

    LoadException error;
    private Collection<? extends Layer> layers = new LinkedList<Layer>();
    public final String name;
    public int ver;
    public final AtomicBoolean loading;
    public ResSource source;
    private transient Indir<Resource> indir = null;

    private Resource(String name, int ver) {
        this.name = name;
        this.ver = ver;
        error = null;
        loading = new AtomicBoolean(true);
    }

    public static void addcache(ResCache cache) {
        if (cache == null) return;
        CacheSource src = new CacheSource(cache);
        prscache = src;
        chainloader(new ResourceLoader(src));
    }

    public static void addurl(URL url) {
        if (url == null) return;
        ResSource src = new HttpSource(url);
        final CacheSource mc = prscache;
        if (mc != null) {
            src = new TeeSource(src) {
                public OutputStream fork(String name) throws IOException {
                    return (mc.cache.store("res/" + name));
                }
            };
        }
        chainloader(new ResourceLoader(src));
    }

    private static void chainloader(ResourceLoader nl) {
        synchronized (Resource.class) {
            if (loader == null) {
                loader = nl;
            } else {
                ResourceLoader l;
                //noinspection StatementWithEmptyBody
                for (l = loader; l.getNext() != null; l = l.getNext()) ;
                l.chain(nl);
            }
        }
    }

    public static Resource load(String name, int ver, int prio) {
        Resource res;
        synchronized (cache) {
            res = cache.get(name);
            if (res != null) {
                if ((res.ver != -1) && (ver != -1)) {
                    if (res.ver < ver) {
                        res = null;
                        cache.remove(name);
                    } else if (res.ver > ver) {
                        throw (new RuntimeException(String.format("Weird version number on %s (%d > %d), loaded from %s", res.name, res.ver, ver, res.source)));
                    }
                } else if (ver == -1) {
                    if (res.error != null) {
                        res = null;
                        cache.remove(name);
                    }
                }
            }
            if (res != null) {
                res.boostprio(prio);
                return (res);
            }
            res = new Resource(name, ver);
            res.setPriority(prio);
            cache.put(name, res);
        }
        loader.load(res);
        return (res);
    }

    public static int numloaded() {
        return (cache.size());
    }

    public static Collection<Resource> cached() {
        return (cache.values());
    }

    public static Resource load(String name, int ver) {
        return (load(name, ver, 0));
    }

    public static int qdepth() {
        int ret = 0;
        for (ResourceLoader l = loader; l != null; l = l.getNext())
            ret += l.queueSize();
        return (ret);
    }

    public static Resource load(String name) {
        return (load(name, -1));
    }

    public void boostprio(int newprio) {
        if (getPriority() < newprio)
            setPriority(newprio);
    }

    public void loadwaitint() throws InterruptedException {
        int tryed = 0;
        synchronized (this) {
            boostprio(10);
            while (loading.get()) {
                if (tryed == 10) {
                    loader.wakeUpChain();
                }
                if (tryed == 30) {
                    tryed = 0;
                    System.err.println("Trying reload" + name);
                    loader.load(this);
                }
                wait(1000);
                tryed++;
            }
        }
    }

    public String basename() {
        int p = name.lastIndexOf('/');
        if (p < 0)
            return (name);
        return (name.substring(p + 1));
    }

    public void loadwait() {
        boolean i = false;
        synchronized (loadwaited) {
            loadwaited.add(this);
        }
        int tryed = 0;
        synchronized (this) {
            boostprio(10);
            while (loading.get()) {
                if (tryed == 10) {
                    loader.wakeUpChain();
                }
                if (tryed == 30) {
                    tryed = 0;
                    System.err.println("Trying reload" + name);
                    loader.load(this);
                }
                try {
                    wait(1000);
                    tryed++;
                } catch (InterruptedException e) {
                    i = true;
                }
            }
        }
        if (i)
            Thread.currentThread().interrupt();
    }

    public static class LoadException extends RuntimeException {

        public LoadException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public LoadException(Throwable cause, Resource res) {
            super("Load error in resource " + res.toString() + ", from " + res.source, cause);
        }

        public LoadException(Throwable cause) {
            super("Load error in resource", cause);
        }

        public LoadException(String msg) {
            super(msg);
        }
    }

    public static Coord cdec(byte[] buf, int off) {
        return (new Coord(Utils.int16d(buf, off), Utils.int16d(buf, off + 2)));
    }

    static {
        ltypes.put("image", haven.resources.layers.Image.class);
        ltypes.put("tooltip", Tooltip.class);
        ltypes.put("tile", Tile.class);
        ltypes.put("neg", Neg.class);
        ltypes.put("anim", Anim.class);
        ltypes.put("tileset", Tileset.class);
        ltypes.put("pagina", Pagina.class);
        ltypes.put("action", AButton.class);
        ltypes.put("code", Code.class);
        ltypes.put("codeentry", CodeEntry.class);
        ltypes.put("audio", AudioL.class);
        ltypes.put("midi", MusicL.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface PublishedCode {
        String name();

        Class<? extends Instancer> instancer() default Instancer.class;

        public interface Instancer {
            public Object make(Class<?> cl) throws InstantiationException, IllegalAccessException;
        }
    }

    private void readall(InputStream in, byte[] buf) throws IOException {
        int ret, off = 0;
        while (off < buf.length) {
            ret = in.read(buf, off, buf.length - off);
            if (ret < 0)
                throw (new LoadException("Incomplete resource at " + name));
            off += ret;
        }
    }

    public <L extends Layer> Collection<L> layers(Class<L> cl) {
        checkerr();
        Collection<L> ret = new LinkedList<L>();
        for (Layer l : layers) {
            if (cl.isInstance(l))
                ret.add(cl.cast(l));
        }
        return (ret);
    }

    public <L extends Layer> L layer(Class<L> cl) {
        checkerr();
        for (Layer l : layers) {
            if (cl.isInstance(l))
                return (cl.cast(l));
        }
        return (null);
    }

    public int compareTo(Resource other) {
        checkerr();
        int nc = name.compareTo(other.name);
        if (nc != 0)
            return (nc);
        if (ver != other.ver)
            return (ver - other.ver);
        if (other != this)
            throw (new RuntimeException("Resource identity crisis!"));
        return (0);
    }

    public boolean equals(Object other) {
        if (other instanceof Resource) {
            return (compareTo((Resource) other) == 0);
        }
        return (false);
    }

    void load(InputStream in) throws IOException {
        String sig = "Haven Resource 1";
        byte buf[] = new byte[sig.length()];
        readall(in, buf);
        if (!sig.equals(new String(buf)))
            throw (new LoadException("Invalid res signature"));
        buf = new byte[2];
        readall(in, buf);
        int ver = Utils.uint16d(buf, 0);
        List<Layer> layers = new LinkedList<Layer>();
        if (this.ver == -1) {
            this.ver = ver;
        } else {
            if (ver != this.ver)
                throw (new LoadException("Wrong res version (" + ver + " != " + this.ver + ')'));
        }
        StringBuilder tbuf = new StringBuilder();
        byte[] lenBuf = new byte[4];

        outer:
        while (true) {
            tbuf.setLength(0);
            while (true) {
                byte bb;
                int ib;
                if ((ib = in.read()) == -1) {
                    if (tbuf.length() == 0)
                        break outer;
                    throw (new LoadException("Incomplete resource at " + name));
                }
                bb = (byte) ib;
                if (bb == 0)
                    break;
                tbuf.append((char) bb);
            }
            readall(in, lenBuf);
            int len = Utils.int32d(lenBuf, 0);
            buf = new byte[len];
            readall(in, buf);
            Class<? extends Layer> lc = ltypes.get(tbuf.toString());
            if (lc == null)
                continue;
            Constructor<? extends Layer> cons;
            boolean isStaticLayerClass = false;
            try {
                cons = lc.getConstructor(Resource.class, byte[].class);
            } catch (NoSuchMethodException e) {
                try {
                    cons = lc.getConstructor(byte[].class);
                    isStaticLayerClass = true;
                } catch (NoSuchMethodException e2) {
                    System.err.println("Failed to loadSettings " + name + "\t| type is " + tbuf.toString());
                    throw (new LoadException(e2, Resource.this));
                }
            }
            Layer l;
            try {
                //noinspection PrimitiveArrayArgumentToVariableArgMethod
                l = (isStaticLayerClass) ? cons.newInstance(buf) : cons.newInstance(this, buf);
            } catch (InstantiationException e) {
                throw (new LoadException(e, Resource.this));
            } catch (InvocationTargetException e) {
                Throwable c = e.getCause();
                if (c instanceof RuntimeException)
                    throw ((RuntimeException) c);
                else
                    throw (new LoadException(c, Resource.this));
            } catch (IllegalAccessException e) {
                throw (new LoadException(e, Resource.this));
            }
            layers.add(l);
        }
        this.layers = layers;
        for (Layer l : layers)
            l.init();
    }

    public Indir<Resource> indir() {
        if (indir != null)
            return (indir);
        indir = new Indir<Resource>() {
            public Resource res = Resource.this;

            public Resource get() {
                if (loading.get())
                    return (null);
                return (Resource.this);
            }

            public void set(Resource r) {
                throw (new RuntimeException());
            }

            public int compareTo(Indir<Resource> x) {
                return (Resource.this.compareTo(this.getClass().cast(x).res));
            }
        };
        return (indir);
    }

    private void checkerr() {
        if (error != null)
            throw (new RuntimeException("Delayed error in resource " + name + " (v" + ver + "), from " + source, error));
    }

    public static BufferedImage loadimg(String name) {
        Resource res = load(name);
        res.loadwait();
        return (res.layer(imgc).img);
    }

    public static Tex loadtex(String name) {
        Resource res = load(name);
        res.loadwait();
        return (res.layer(imgc).tex());
    }

    public String toString() {
        return (name + "(v" + ver + ')');
    }

    public static void loadlist(InputStream list, int prio) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(list, "us-ascii"));
        String ln;
        while ((ln = in.readLine()) != null) {
            int pos = ln.indexOf(':');
            if (pos < 0)
                continue;
            String nm = ln.substring(0, pos);
            int ver;
            try {
                ver = Integer.parseInt(ln.substring(pos + 1));
            } catch (NumberFormatException e) {
                continue;
            }
            try {
                load(nm, ver, prio);
            } catch (RuntimeException ignored) {
            }
        }
        in.close();
    }

    public static void dumplist(Collection<Resource> list, Writer dest) {
        PrintWriter out = new PrintWriter(dest);
        List<Resource> sorted = new ArrayList<Resource>(list);
        Collections.sort(sorted);
        for (Resource res : sorted) {
            if (res.loading.get())
                continue;
            out.println(res.name + ':' + res.ver);
        }
    }

    public static void updateloadlist(File file) throws Exception {
        BufferedReader r = new BufferedReader(new FileReader(file));
        Map<String, Integer> orig = new HashMap<String, Integer>();
        String ln;
        while ((ln = r.readLine()) != null) {
            int pos = ln.indexOf(':');
            if (pos < 0) {
                System.err.println("Weird line: " + ln);
                continue;
            }
            String nm = ln.substring(0, pos);
            int ver = Integer.parseInt(ln.substring(pos + 1));
            orig.put(nm, ver);
        }
        r.close();
        for (String nm : orig.keySet())
            load(nm);
        while (true) {
            int d = qdepth();
            if (d == 0)
                break;
            System.out.print("\033[1GLoading... " + d + "\033[K");
            Thread.sleep(500);
        }
        System.out.println();
        Collection<Resource> cur = new LinkedList<Resource>();
        for (Map.Entry<String, Integer> e : orig.entrySet()) {
            String nm = e.getKey();
            int ver = e.getValue();
            Resource res = load(nm);
            res.loadwait();
            res.checkerr();
            if (res.ver != ver)
                System.out.println(nm + ": " + ver + " -> " + res.ver);
            cur.add(res);
        }
        Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        try {
            dumplist(cur, w);
        } finally {
            w.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String cmd = args[0].intern();
        if (cmd.equals("update")) {
            updateloadlist(new File(args[1]));
        }
    }
}
