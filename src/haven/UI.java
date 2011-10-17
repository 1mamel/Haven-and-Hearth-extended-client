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

import haven.resources.layers.CodeEntry;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UI {
    static public UI instance;
    public final RootWidget root;
    public SlenConsole slenConsole;
    public SlenHud slen;
    public MenuGrid mnu;
    public WikiBrowser wiki;
    private Widget keygrab, mousegrab;
    public final Map<Integer, Widget> widgets = new TreeMap<Integer, Widget>();
    public final Map<Widget, Integer> rwidgets = new HashMap<Widget, Integer>();
    Receiver rcvr;
    public Coord mc, lcc = Coord.z;
    public final Session sess;
    public MapView mainview;
    public boolean modshift, modctrl, modmeta, modsuper;
    long lastevent = System.currentTimeMillis();
    public FSMan fsm;
    public final Console cons = new WidgetConsole();
    private final Collection<AfterDraw> afterdraws = new ConcurrentLinkedQueue<AfterDraw>();


    // Some references
    public static AtomicReference<FlowerMenu> flowerMenu = new AtomicReference<FlowerMenu>(null);
    public static AtomicReference<OptWnd> optionsWindow = new AtomicReference<OptWnd>(null);
    public static AtomicReference<Makewindow> makeWindow = new AtomicReference<Makewindow>(null);
    public static AtomicReference<Equipory> equipory = new AtomicReference<Equipory>(null);
    public static AtomicReference<MenuGrid> menuGrid = new AtomicReference<MenuGrid>(null);
    public static AtomicReference<Item> draggingItem = new AtomicReference<Item>(null);
    public static AtomicReference<Speedget> speedget = new AtomicReference<Speedget>();

    public static CustomConsole console;

    public static String cursorName = null;
    private long last_newwidget_time = 0;

    public static void setCursorName(final String name) {
        cursorName = name.replace("gfx/hud/curs/", "");
    }

    public interface Receiver {
        public void rcvmsg(int widget, String msg, Object... args);
    }

    public interface AfterDraw {
        public void draw(GOut g);
    }

    private class WidgetConsole extends Console {
        {
            setcmd("q", new Command() {
                public void run(final Console cons, final String[] args) {
                    HackThread.tg().interrupt();
                }
            });
            setcmd("lo", new Command() {
                public void run(final Console cons, final String[] args) {
                    sess.close();
                }
            });
            setcmd("fs", new Command() {
                public void run(final Console cons, final String[] args) {
                    if ((args.length >= 2) && (fsm != null)) {
                        if (Utils.atoi(args[1]) != 0)
                            fsm.setfs();
                        else
                            fsm.setwnd();
                    }
                }
            });
        }

        private void findcmds(final Map<String, Command> map, final Widget wdg) {
            if (wdg instanceof Directory) {
                map.putAll(((Directory) wdg).findcmds());
            }
            for (Widget ch = wdg.child; ch != null; ch = ch.next) {
                findcmds(map, ch);
            }
        }

        public Map<String, Command> findcmds() {
            final Map<String, Command> ret = super.findcmds();
            findcmds(ret, root);
            return (ret);
        }
    }

    public static class UIException extends RuntimeException {
        public final String mname;
        public final Object[] args;

        public UIException(final String message, final String mname, final Object... args) {
            super(message);
            this.mname = mname;
            this.args = args;
        }
    }

    public UI(final Dimension size, final Session sess) {
        this(new Coord(size), sess);
    }

    public UI(final Coord size, final Session sess) {
        last_newwidget_time = System.currentTimeMillis();
        instance = this;
        root = new RootWidget(this, size);
        widgets.put(0, root);
        rwidgets.put(root, 0);
        this.sess = sess;
    }

    public void setreceiver(final Receiver rcvr) {
        this.rcvr = rcvr;
    }

    public void bind(final Widget w, final int id) {
        widgets.put(id, w);
        rwidgets.put(w, id);
    }

    public void drawafter(final AfterDraw ad) {
        afterdraws.add(ad);
    }

    public void draw(final GOut g) {
        afterdraws.clear();
        root.draw(g);
        for (final AfterDraw ad : afterdraws) {
            ad.draw(g);
        }
    }

    public void newwidget(final int id, String type, final Coord c, final int parent, final Object... args) throws InterruptedException {
        final WidgetFactory f;
        last_newwidget_time = System.currentTimeMillis();
        if (type.indexOf('/') >= 0) {
            int ver = -1;
            final int p;
            if ((p = type.indexOf(':')) > 0) {
                ver = Integer.parseInt(type.substring(p + 1));
                type = type.substring(0, p);
            }
            final Resource res = Resource.load(type, ver);
            res.loadwaitint();
            f = res.layer(CodeEntry.class).get(WidgetFactory.class);
        } else {
            f = Widget.gettype(type);
        }
        synchronized (this) {
            final Widget pwdg = widgets.get(parent);
            if (pwdg == null)
                throw (new UIException("Null parent widget " + parent + " for " + id, type, args));
            final Widget wdg = f.create(c, pwdg, args);
            bind(wdg, id);
            wdg.binded();
        }
    }

    public void ungrabmouse() {
        mousegrab = null;
    }

    public void ungrabkeys() {
        keygrab = null;
    }

    public void grabmouse(final Widget wdg) {
        mousegrab = wdg;
    }

    public void grabkeys(final Widget wdg) {
        keygrab = wdg;
    }

    private void removeid(final Widget wdg) {
        if (rwidgets.containsKey(wdg)) {
            final int id = rwidgets.get(wdg);
            widgets.remove(id);
            rwidgets.remove(wdg);
        }
        for (Widget child = wdg.child; child != null; child = child.next)
            removeid(child);
    }

    public void destroy(final Widget wdg) {
        if ((mousegrab != null) && mousegrab.hasparent(wdg))
            mousegrab = null;
        if ((keygrab != null) && keygrab.hasparent(wdg))
            keygrab = null;
        removeid(wdg);
        wdg.destroy();
        wdg.unlink();
    }

    public void destroy(final int id) {
        synchronized (this) {
            if (widgets.containsKey(id)) {
                final Widget wdg = widgets.get(id);
                destroy(wdg);
            }
        }
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        final int id;
        synchronized (this) {
            if (!rwidgets.containsKey(sender))
                throw (new UIException("Wdgmsg sender (" + sender.getClass().getName() + ") is not in rwidgets", msg, args));
            id = rwidgets.get(sender);
        }
        if (id < 0) return;
        if (rcvr != null)
            rcvr.rcvmsg(id, msg, args);
    }

    public void uimsg(final int id, final String msg, final Object... args) {
        final Widget wdg;
        synchronized (this) {
            wdg = widgets.get(id);
        }
        if (wdg != null)
            wdg.uimsg(msg.intern(), args);
        else
            throw (new UIException("Uimsg to non-existent widget " + id, msg, args));
    }

    private void setmods(final InputEvent ev) {
        modshift = ev.isShiftDown();
        modctrl = ev.isControlDown();
        modmeta = ev.isMetaDown();
        /*
       modsuper = (mod & InputEvent.SUPER_DOWN_MASK) != 0;
      */
    }

    public void type(final KeyEvent ev) {
        setmods(ev);
        if (keygrab == null) {
            if (!root.type(ev.getKeyChar(), ev))
                root.globtype(ev.getKeyChar(), ev);
        } else {
            keygrab.type(ev.getKeyChar(), ev);
        }
    }

    public void keydown(final KeyEvent ev) {
        setmods(ev);
        if (keygrab == null) {
            if (!root.keydown(ev))
                root.globtype((char) 0, ev);
        } else {
            keygrab.keydown(ev);
        }
    }

    public void keyup(final KeyEvent ev) {
        setmods(ev);
        if (keygrab == null)
            root.keyup(ev);
        else
            keygrab.keyup(ev);
    }

    private static Coord wdgxlate(final Coord c, final Widget wdg) {
        return (c.sub(wdg.c).sub(wdg.parent.rootpos()));
    }

    public static boolean dropthing(final Widget w, final Coord c, final Object thing) {
        if (w instanceof DropTarget) {
            if (((DropTarget) w).dropthing(c, thing))
                return (true);
        }
        for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
            final Coord cc = w.xlate(wdg.c, true);
            if (c.isect(cc, wdg.sz)) {
                if (dropthing(wdg, c.sub(cc), thing))
                    return (true);
            }
        }
        return (false);
    }

    public void mousedown(final MouseEvent ev, final Coord c, final int button) {
        setmods(ev);
        lcc = mc = c;
        if (mousegrab == null)
            root.mousedown(c, button);
        else
            mousegrab.mousedown(wdgxlate(c, mousegrab), button);
    }

    public void mouseup(final MouseEvent ev, final Coord c, final int button) {
        setmods(ev);
        mc = c;
        if (mousegrab == null)
            root.mouseup(c, button);
        else
            mousegrab.mouseup(wdgxlate(c, mousegrab), button);
    }

    public void mousemove(final MouseEvent ev, final Coord c) {
        setmods(ev);
        mc = c;
        if (mousegrab == null)
            root.mousemove(c);
        else
            mousegrab.mousemove(wdgxlate(c, mousegrab));
    }

    public void mousewheel(final MouseEvent ev, final Coord c, final int amount) {
        setmods(ev);
        lcc = mc = c;
        if (mousegrab == null)
            root.mousewheel(c, amount);
        else
            mousegrab.mousewheel(wdgxlate(c, mousegrab), amount);
    }

    public int modflags() {
        return ((modshift ? 1 : 0) |
                (modctrl ? 2 : 0) |
                (modmeta ? 4 : 0) |
                (modsuper ? 8 : 0));
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public synchronized Widget getWidgetById(final int id) {
        return widgets.get(id);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public synchronized int getIdByWidget(final Widget wdg) {
        return rwidgets.get(wdg);
    }


    public synchronized void update(final long dt) {
        if (mainview == null)
            last_newwidget_time = System.currentTimeMillis();
        // если прошло больше 60 сек с момента создания последнего виджета - то крашимся
        if ((System.currentTimeMillis() - last_newwidget_time) > (5 * 60 * 1000))
            if (Config.inactive_exit)
                System.exit(0);
        root.update(dt);
    }
}
