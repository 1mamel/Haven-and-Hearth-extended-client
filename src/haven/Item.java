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

import haven.resources.layers.Pagina;
import haven.scriptengine.InventoryExt;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item extends Widget implements DTarget {
    static Coord shoff = new Coord(1, 3);
    static Pattern patt = Pattern.compile("quality (\\d+) ");
    static final Map<Integer, Tex> numbersMap = new HashMap<Integer, Tex>();
    private static final Resource missing = Resource.load("gfx/invobjs/missing");
    static Color outcol = new Color(0, 0, 0, 255);
    public final boolean dm; // Dragging mode (at cursor)
    private int quality; // quality
    private int innerLiquidQuality;
    private boolean isHighQuality; // Hide big qualities values
    private final Coord doff; // Dragging offset
    public String tooltip;
    private int quantity = -1;
    private Indir<Resource> res;
    private Color olcol = null;
    private Tex mask = null;
    protected int completedPercents = 0;
    protected long lastCPUpdateTime = 0;
    private long timeToComplete = 0;
    private long approximateDelta = -1;

    static {
        Widget.addtype("item", new WidgetFactory() {
            public Widget create(@NotNull final Coord c, @NotNull final Widget parent, final Object[] args) {
                final int res = (Integer) args[0]; // Resource id
                final int q = (Integer) args[1]; // Quality
                int num = -1; // Quantity
                String tooltip = null;
                int ca = 3; // Arguments count
                Coord drag = null;
                if ((Integer) args[2] != 0)
                    drag = (Coord) args[ca++];
                if (args.length > ca)
                    tooltip = (String) args[ca++];
                if ((tooltip != null) && tooltip.length() == 0)
                    tooltip = null;
                if (args.length > ca)
                    //noinspection UnusedAssignment
                    num = (Integer) args[ca++];
                final Item item;
                if (parent instanceof InventoryExt) // Item in inventory
                    item = ((InventoryExt) parent).new InvItem(c, res, q, parent, drag, num);
                else if (parent instanceof RootWidget) // Item at cursor
                    item = new Item(c, res, q, parent, drag, num);
                else {
                    System.err.println("Creation item not ( in inventory | at cursor)" +
                            "\n\tparenttype=" + parent.getClass().getSimpleName() +
                            "\n\tcoordinates=" + c.toString() +
                            "\n\targs=" + Arrays.toString(args));
                    item = new Item(c, res, q, parent, drag, num);
                }
                item.tooltip = tooltip;
                item.innerLiquidQuality = -1;
                if (tooltip != null) {
                    try {
                        final Matcher m = patt.matcher(tooltip);
                        if (m.find()) {
                            item.innerLiquidQuality = Integer.parseInt(m.group(1));
                        }
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                }
                return (item);
            }
        });
        missing.loadwait();
    }

    private void fixsize() {
        if (res.get() != null) {
            final Tex tex = res.get().layer(Resource.imgc).tex();
            sz = tex.sz().add(shoff);
        } else {
            sz = new Coord(30, 30);
        }
    }

    public void draw(final GOut g) {
        final Resource ttres;
        if (res.get() == null) {
            sz = new Coord(30, 30);
            g.image(missing.layer(Resource.imgc).tex(), Coord.z, sz);
            ttres = missing;
        } else {
            Tex tex = res.get().layer(Resource.imgc).tex();
            fixsize();
            if (dm) { // Semitransparent while moving at cursor
                g.chcolor(255, 255, 255, 128);
                g.image(tex, Coord.z);
                g.chcolor();
            } else {
                g.image(tex, Coord.z);
            }
            if (quantity >= 0) {
//                g.chcolor(Color.WHITE);
//                g.atext(Integer.toString(num), new Coord(0, 30), 0, 1);
                g.aimage(getNumberTex(quantity), Coord.z, 0, 0);
            }
            if (completedPercents > 0) {
                final double a = ((double) completedPercents) / 100.0;
                final int r = (int) ((1 - a) * 255);
                final int gr = (int) (a * 255);
                final int b = 0;
                g.chcolor(r, gr, b, 255);
                //g.fellipse(sz.div(2), new Coord(15, 15), 90, (int)(90 + (360 * a)));
                final int h = (int) Math.floor(a * sz.y);
                g.frect(new Coord(sz.x - 5, sz.y - h), new Coord(5, h));
                g.chcolor();
            }
            final int tq = (innerLiquidQuality > 0) ? innerLiquidQuality : quality;
            if (Config.showq && (tq > 0)) {
                tex = getNumberTex(tq);
                g.aimage(tex, sz.sub(1, 1), 1, 1);
            }
            ttres = res.get();
        }
        if (olcol != null) {
            final Tex bg = ttres.layer(Resource.imgc).tex();
            if ((mask == null) && (bg instanceof TexI)) {
                mask = ((TexI) bg).mkmask();
            }
            if (mask != null) {
                g.chcolor(olcol);
                g.image(mask, Coord.z);
                g.chcolor();
            }
        }
    }

    static Tex getNumberTex(final int num) {
        if (numbersMap.containsKey(num)) {
            return numbersMap.get(num);
        }
        synchronized (numbersMap) {
            if (numbersMap.containsKey(num)) {
                return numbersMap.get(num);
            }
            BufferedImage img = Text.render(Integer.toString(num)).img;
            img = Utils.outline2(img, outcol);
            final Tex tex = new TexI(img);
            numbersMap.put(num, tex);
            return tex;
        }
    }

    static Tex makesh(final Resource res) {
        final BufferedImage img = res.layer(Resource.imgc).img;
        final Coord sz = Utils.imgsz(img);
        final BufferedImage sh = new BufferedImage(sz.x, sz.y, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < sz.y; y++) {
            for (int x = 0; x < sz.x; x++) {
                final long c = img.getRGB(x, y) & 0x00000000ffffffffL;
                final int a = (int) ((c & 0xff000000) >> 24);
                sh.setRGB(x, y, (a / 2) << 24);
            }
        }
        return (new TexI(sh));
    }

    public String name() {
        if (this.tooltip != null)
            return (this.tooltip);
        final Resource res = this.res.get();
        if ((res != null) && (res.layer(Resource.tooltip) != null)) {
            return res.layer(Resource.tooltip).t;
        }
        return null;
    }

    String shorttip() {
        if (this.tooltip != null)
            return (this.tooltip);
        final Resource res = this.res.get();
        if ((res != null) && (res.layer(Resource.tooltip) != null)) {
            String tt = res.layer(Resource.tooltip).t;
            if (tt != null) {
                if (quality > 0) {
                    tt = tt + ", quality " + quality;
                    if (isHighQuality)
                        tt = tt + '+';
                }
//		if(meter > 0) {
//		    tt = tt + " (" + meter + "%)";
//		}
                return (tt);
            }
        }
        return (null);
    }

    private long hoverstart;
    private Text shorttip = null;
    private Text longtip = null;

    public Object tooltip(final Coord c, final boolean again) {
        final long now = System.currentTimeMillis();
        if (!again)
            hoverstart = now;
        if ((now - hoverstart) < 500) {
            if (shorttip == null) {
                String tt = shorttip();
                if (tt != null) {
                    if (completedPercents > 0) {
                        tt = tt + " (" + completedPercents + "%)";
                    }
                    shorttip = Text.render(tt);
                }
            }
            return (shorttip);
        } else {
            final Resource res = this.res.get();
            if ((longtip == null) && (res != null)) {
                final Pagina pg = res.layer(Resource.pagina);
                final String tip = shorttip();
                if (tip == null)
                    return (null);
                String tt = RichText.Parser.quote(tip);
                if (completedPercents > 0) {
                    tt = tt + " (" + completedPercents + "%)";
                }
                if (pg != null)
                    tt += "\n\n" + pg.text;
                longtip = RichText.render(tt, 200);
            }
            return (longtip);
        }
    }

    private void resetToolTip() {
        shorttip = null;
        longtip = null;
    }

    private void decodeQuality(final int q) {
        if (q < 0) {
            this.quality = q;
            isHighQuality = false;
        } else {
            this.quality = (q & 0xffffff);
            isHighQuality = (q & 0x01000000) != 0;  // Some optimization
        }
    }

    public Item(final Coord c, final Indir<Resource> res, final int quality, final Widget parent, final Coord drag, final int quantity) {
        super(c, Coord.z, parent);
        this.res = res;
        decodeQuality(quality);
        fixsize();
        this.quantity = quantity;

        doff = drag;
        if (doff == null) {
            dm = false;
        } else {
            dm = true;
            UI.draggingItem.set(this);
            ui.grabmouse(this);
            this.c = ui.mc.sub(doff);
        }
    }

    public Item(final Coord c, final int res, final int quality, final Widget parent, final Coord drag, final int quantity) {
        this(c, parent.ui.sess.getres(res), quality, parent, drag, quantity);
    }

    private Item(final Coord c, final Indir<Resource> res, final int quality, final Widget parent, final Coord drag) {
        this(c, res, quality, parent, drag, -1);
    }

    public Item(final Coord c, final int res, final int quality, final Widget parent, final Coord drag) {
        this(c, parent.ui.sess.getres(res), quality, parent, drag);
    }

    boolean dropon(final Widget w, final Coord c) {
//        if (w != UI.instance.root) {
//            System.err.println("Item dropon. w is not root, " + w.getClass().getSimpleName());
//        }
        for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
            if (wdg == this) {
                continue;
            }
            final Coord cc = w.xlate(wdg.c, true);
            if (c.isect(cc, (wdg.hsz == null) ? wdg.sz : wdg.hsz)) {
                if (dropon(wdg, c.sub(cc))) {
                    return (true);
                }
            }
        }
        if (w instanceof DTarget) {
            if (((DTarget) w).drop(c, c.sub(doff), this)) {
                return (true);
            }
        }
        return (false);
    }

    boolean interact(final Widget w, final Coord c) {
//        if (w != UI.instance.root) {
//            System.err.println("Item interact. w is not root, " + w.getClass().getSimpleName());
//        }
        for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
            if (wdg == this) {
                continue;
            }
            final Coord cc = w.xlate(wdg.c, true);
            if (c.isect(cc, (wdg.hsz == null) ? wdg.sz : wdg.hsz)) {
                if (interact(wdg, c.sub(cc))) {
                    return (true);
                }
            }
        }
        if (w instanceof DTarget) {
            if (((DTarget) w).iteminteract(c, c.sub(doff))) {
                return (true);
            }
        }
        return (false);
    }

    public void chres(final Indir<Resource> res, final int q) {
        this.res = res;
        decodeQuality(q);
    }

    public void uimsg(@NotNull final String name, final Object... args) {
        if (name.equals("num")) { // Change quantity
            quantity = (Integer) args[0];
        } else if (name.equals("chres")) { // Change resource (by id) and quality
            chres(ui.sess.getres((Integer) args[0]), (Integer) args[1]);
            resetToolTip();
        } else if (name.equals("color")) { // Change color (?)
            olcol = (Color) args[0];
        } else if (name.equals("tt")) { // Change tooltip
            if ((args.length > 0) && (((String) args[0]).length() > 0))
                tooltip = (String) args[0];
            else
                tooltip = null;
            resetToolTip();
        } else if (name.equals("meter")) { // may be completion indicator on dying fur, etc.
            final int ncp = (Integer) args[0];
            final int deltaCP = Math.abs(ncp - completedPercents);
            completedPercents = ncp;
            try {
            final long nowTime = System.currentTimeMillis();
            final long deltaTime = lastCPUpdateTime - nowTime;
            // Reapproximates deltaTime
            if (approximateDelta != -1) {
                approximateDelta = (approximateDelta + 3 * deltaTime) / 4;
            } else {
                approximateDelta = deltaTime;
            }
            if (deltaCP != 0){
                timeToComplete = (completedPercents * approximateDelta) / deltaCP;
            }
            lastCPUpdateTime = nowTime;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean mousedown(final Coord c, final int button) {
        if (!dm) {
            if (button == 1) {
                if (ui.modshift)
                    wdgmsg("transfer", c);
                else if (ui.modctrl)
                    wdgmsg("drop", c);
                else
                    wdgmsg("take", c);
                return (true);
            } else if (button == 3) {
                wdgmsg("iact", c);
                return (true);
            }
        } else {
            if (button == 1) {
                dropon(parent, c.add(this.c));
            } else if (button == 3) {
                interact(parent, c.add(this.c));
            }
            return (true);
        }
        return (false);
    }

    public void mousemove(final Coord c) {
        if (dm) {
            this.c = this.c.add(c.sub(doff));
        }
    }

    public boolean drop(final Coord cc, final Coord ul, final Item item) {
        return (false);
    }

    public boolean iteminteract(final Coord cc, final Coord ul) {
        wdgmsg("itemact", ui.modflags());
        return (true);
    }

    // arksu:
    public String getResName() {
        if (res.get() != null)
            return res.get().name;
        else
            return "";
    }

    // arksu: съедобная ли вещь
    public boolean isEatable() {
        final String s = getResName();
        if (s.contains("gfx/invobjs/bread")) return true;
        if (s.contains("gfx/invobjs/meat")) return true;
        if (s.contains("gfx/invobjs/mussel-boiled")) return true;
        return false;
    }

    public String getTooltip() {
        return tooltip;
    }

    public int getQuality() {
        return quality;
    }

    public Coord getCoord() {
        return c;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCompletedPercent() {
        return completedPercents;
    }

    @Override
    public void destroy() {
        UI.draggingItem.compareAndSet(this, null);
        super.destroy();
    }

    public boolean isCompleted() {
        return completedPercents == 0;
    }

    public long getTimeToComplete() {
        return timeToComplete;
    }
}
