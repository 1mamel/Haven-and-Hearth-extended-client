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

import haven.resources.layers.AButton;
import haven.resources.layers.Pagina;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.util.*;

public class MenuGrid extends Widget {
    public final static Tex bg = Resource.loadtex("gfx/hud/invsq");
    public final static Coord bgsz = bg.sz().add(-1, -1);
    public final static Resource next = Resource.load("gfx/hud/sc-next");
    public final static Resource bk = Resource.load("gfx/hud/sc-back");
    public final static RichText.Foundry ttfnd = new RichText.Foundry(TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, 10);
    private static final Coord gsz = new Coord(4, 4);
    private Resource cur;
    private Resource pressed;
    private Resource dragging;
    private final Resource[][] layout = new Resource[gsz.x][gsz.y];
    private int curoff = 0;
    private final Map<Character, Resource> hotmap = new TreeMap<Character, Resource>();

    static {
        Widget.addtype("scm", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new MenuGrid(c, parent));
            }
        });
    }

    public static class PaginaException extends RuntimeException {
        public final Resource res;

        public PaginaException(final Resource r) {
            super("Invalid pagina: " + r.name);
            res = r;
        }
    }

    private Resource[] cons(final Resource p) {
        final Resource[] cp = new Resource[0];
        final Resource[] all;
        {
            final Collection<Resource> ta = new HashSet<Resource>();
            final Collection<Resource> open;
            synchronized (ui.sess.glob.paginae) {
                open = new HashSet<Resource>(ui.sess.glob.paginae);
            }
            while (!open.isEmpty()) {
                for (final Resource r : open.toArray(cp)) {
                    if (!r.loading.get()) {
                        final AButton ad = r.layer(Resource.action);
                        if (ad == null)
                            throw (new PaginaException(r));
                        if ((ad.parent != null) && !ta.contains(ad.parent))
                            open.add(ad.parent);
                        ta.add(r);
                        open.remove(r);
                    }
                }
            }
            all = ta.toArray(cp);
        }
        final Collection<Resource> tobe = new HashSet<Resource>();
        for (final Resource r : all) {
            if (r.layer(Resource.action).parent == p)
                tobe.add(r);
        }
        return (tobe.toArray(cp));
    }

    public MenuGrid(final Coord c, final Widget parent) {
        super(c, bgsz.mul(gsz).add(1, 1), parent);
        cons(null);
        ui.mnu = this;
        ToolbarWnd.loadBelts();
        new ToolbarWnd(new Coord(0, 300), ui.root, "toolbar1");
        new ToolbarWnd(new Coord(50, 300), ui.root, "toolbar2", 2, 12, new Coord(4, 10), KeyEvent.VK_F1);
        UI.menuGrid.set(this);
    }

    @Override
    public void destroy() {
        UI.menuGrid.compareAndSet(this, null);
        super.destroy();
    }

    private static final Comparator<Resource> sorter = new Comparator<Resource>() {
        public int compare(final Resource a, final Resource b) {
            final AButton aa = a.layer(Resource.action);
            final AButton ab = b.layer(Resource.action);
            if ((aa.ad.length == 0) && (ab.ad.length > 0))
                return (-1);
            if ((aa.ad.length > 0) && (ab.ad.length == 0))
                return (1);
            return (aa.name.compareTo(ab.name));
        }
    };

    private void updlayout() {
        final Resource[] cur = cons(this.cur);
        Arrays.sort(cur, sorter);
        int i;
//	int i = curoff;
        hotmap.clear();
        for (i = 0; i < cur.length; i++) {
            final AButton ad = cur[i].layer(Resource.action);
            if (ad.hk != 0)
                hotmap.put(Character.toUpperCase(ad.hk), cur[i]);
        }
        i = curoff;
        for (int y = 0; y < gsz.y; y++) {
            for (int x = 0; x < gsz.x; x++) {
                Resource btn = null;
                if ((this.cur != null) && (x == gsz.x - 1) && (y == gsz.y - 1)) {
                    btn = bk;
                } else if ((cur.length > ((gsz.x * gsz.y) - 1)) && (x == gsz.x - 2) && (y == gsz.y - 1)) {
                    btn = next;
                } else if (i < cur.length) {
//                    Resource.AButton ad = cur[i].layer(Resource.action);
//                    if (ad.hk != 0)
//                        hotmap.put(Character.toUpperCase(ad.hk), cur[i]);
                    btn = cur[i++];
                }
                layout[x][y] = btn;
            }
        }
    }

    private static Text rendertt(final Resource res, final boolean withpg) {
        final AButton ad = res.layer(Resource.action);
        final Pagina pg = res.layer(Resource.pagina);
        String tt = ad.name;
        final int pos = tt.toUpperCase().indexOf(Character.toUpperCase(ad.hk));
        if (pos >= 0)
            tt = tt.substring(0, pos) + "$col[255,255,0]{" + tt.charAt(pos) + '}' + tt.substring(pos + 1);
        else if (ad.hk != 0)
            tt += " [$col[255,255,0]{" + ad.hk + "}]";
        if (withpg && (pg != null)) {
            tt += "\n\n" + pg.text;
        }
        return (ttfnd.render(tt, 0));
    }

    public void draw(final GOut g) {
        updlayout();
        for (int y = 0; y < gsz.y; y++) {
            for (int x = 0; x < gsz.x; x++) {
                final Coord p = bgsz.mul(x, y);
                g.image(bg, p);
                final Resource btn = layout[x][y];
                if (btn != null) {
                    final Tex btex = btn.layer(Resource.imgc).tex();
                    g.image(btex, p.add(1, 1));
                    if (btn == pressed) {
                        g.chcolor(0, 0, 0, 128);
                        g.frect(p.add(1, 1), btex.sz());
                        g.chcolor();
                    }
                }
            }
        }
        if (dragging != null) {
            final Tex dt = dragging.layer(Resource.imgc).tex();
            ui.drawafter(new UI.AfterDraw() {
                public void draw(final GOut g) {
                    g.image(dt, ui.mc.sub(dt.sz().div(2)));
                }
            });
        }
    }

    private Resource curttr = null;
    private boolean curttl = false;
    private Text curtt = null;
    private long hoverstart;

    public Object tooltip(final Coord c, final boolean again) {
        final Resource res = bhit(c);
        final long now = System.currentTimeMillis();
        if ((res != null) && (res.layer(Resource.action) != null)) {
            if (!again)
                hoverstart = now;
            final boolean ttl = (now - hoverstart) > 500;
            if ((res != curttr) || (ttl != curttl)) {
                curtt = rendertt(res, ttl);
                curttr = res;
                curttl = ttl;
            }
            return (curtt);
        } else {
            hoverstart = now;
            return ("");
        }
    }

    private Resource bhit(final Coord c) {
        final Coord bc = c.div(bgsz);
        if ((bc.x >= 0) && (bc.y >= 0) && (bc.x < gsz.x) && (bc.y < gsz.y))
            return (layout[bc.x][bc.y]);
        else
            return (null);
    }

    public boolean mousedown(final Coord c, final int button) {
        final Resource h = bhit(c);
        if ((button == 1) && (h != null)) {
            pressed = h;
            ui.grabmouse(this);
        }
        return (true);
    }

    public void mousemove(final Coord c) {
        if ((dragging == null) && (pressed != null)) {
            final Resource h = bhit(c);
            if (h != pressed)
                dragging = pressed;
        }
    }

    protected void use(final Resource r) {
        if (cons(r).length > 0) {
            cur = r;
            curoff = 0;
        } else if (r == bk) {
            cur = cur.layer(Resource.action).parent;
            curoff = 0;
        } else if (r == next) {
            if ((curoff + 14) >= cons(cur).length)
                curoff = 0;
            else
                curoff += 14;
        } else {
            final String[] ad = r.layer(Resource.action).ad;
            if (ad[0].equals("@")) {
                usecustom(ad);
//	    } else if (ad[0].equals("declaim")){
//		new DeclaimVerification(ui.root, ad);
            } else {
                int k = 0;
                if (ad[0].equals("crime")) {
                    k = -1;
                }
                if (ad[0].equals("tracking")) {
                    k = -2;
                }
                if (ad[0].equals("swim")) {
                    k = -3;
                }
                if (k < 0) {
                    synchronized (ui.sess.glob.buffs) {
                        if (ui.sess.glob.buffs.containsKey(k)) {
                            ui.sess.glob.buffs.remove(k);
                        } else {
                            final Buff buff = new Buff(k, r.indir());
                            buff.major = true;
                            ui.sess.glob.buffs.put(k, buff);
                        }
                    }
                }
                CustomConfig.logger.info("Sending menu grid action " + Arrays.toString(ad));
                wdgmsg("act", (Object[]) ad);
            }
        }
    }

    private void usecustom(final String[] list) {
        if (list[1].equals("radius")) {
            Config.showRadius = !Config.showRadius;
            final String str = "Radius highlight is turned " + ((Config.showRadius) ? "ON" : "OFF");
            ui.cons.out.println(str);
            ui.slen.error(str);
            Config.saveOptions();
        } else if (list[1].equals("hidden")) {
            Config.showHidden = !Config.showHidden;
            final String str = "Hidden object highlight is turned " + ((Config.showHidden) ? "ON" : "OFF");
            ui.cons.out.println(str);
            ui.slen.error(str);
            Config.saveOptions();
        } else if (list[1].equals("hide")) {
            for (int i = 2; i < list.length; i++) {
                final String item = list[i];
                if (Config.hideObjectList.contains(item)) {
                    Config.hideObjectList.remove(item);
                } else {
                    Config.hideObjectList.add(item);
                }
            }
        } else if (list[1].equals("simple plants")) {
            Config.simple_plants = !Config.simple_plants;
            final String str = "Simplified plants is turned " + ((Config.simple_plants) ? "ON" : "OFF");
            ui.cons.out.println(str);
            ui.slen.error(str);
            Config.saveOptions();
        } else if (list[1].equals("timers")) {
            TimerPanel.toggleS();
        } else if (list[1].equals("animal")) {
            Config.showBeast = !Config.showBeast;
            final String str = "Animal highlight is turned " + ((Config.showBeast) ? "ON" : "OFF");
            ui.cons.out.println(str);
            ui.slen.error(str);
            Config.saveOptions();
        } else if (list[1].equals("globalchat")) {
            ui.root.wdgmsg("gk", 3);
        } else if (list[1].equals("wiki")) {
            if (ui.wiki == null) {
                new WikiBrowser(CustomConfig.getWindowCenter().sub(115, 75), Coord.z, ui.root);
            } else {
                ui.wiki.close();
            }
        }
        use(null);
    }

    public boolean mouseup(final Coord c, final int button) {
        final Resource h = bhit(c);
        if (button == 1) {
            if (dragging != null) {
                UI.dropthing(ui.root, ui.mc, dragging);
                dragging = pressed = null;
            } else if (pressed != null) {
                if (pressed == h)
                    use(h);
                pressed = null;
            }
            ui.ungrabmouse();
        }
        updlayout();
        return (true);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("goto")) {
            final String res = (String) args[0];
            if (res.length() == 0)
                cur = null;
            else
                cur = Resource.load(res);
            curoff = 0;
        }
    }

    public boolean globtype(final char k, final KeyEvent ev) {
        if ((k == 27) && (this.cur != null)) {
            this.cur = null;
            curoff = 0;
            updlayout();
            return (true);
        } else if ((k == 'N') && (layout[gsz.x - 2][gsz.y - 1] == next)) {
            use(next);
            return (true);
        }
        final Resource r = hotmap.get(Character.toUpperCase(k));
        if (r != null) {
            use(r);
            return (true);
        }
        return (false);
    }
}
