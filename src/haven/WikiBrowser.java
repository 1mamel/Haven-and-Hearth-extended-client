package haven;

import haven.resources.layers.AButton;
import haven.resources.layers.Tooltip;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikiBrowser extends Window implements DTarget, DropTarget, IHWindowParent {
    static final BufferedImage grip = Resource.loadimg("gfx/hud/gripbr");
    static final Coord gzsz = new Coord(16, 17);
    static final Coord minsz = new Coord(230, 150);
    static final int addrh = 40;
    static final int minbtnw = 90;
    static final int maxbtnw = 200;
    static final int sbtnw = 50;
    static final int btnh = 30;

    boolean rsm = false, recalcsz = true;
    Button sub, sdb;
    HWindow awnd;
    List<HWindow> wnds = new ArrayList<HWindow>();
    Map<HWindow, Button> btns = new HashMap<HWindow, Button>();
    int woff = 0;
    Coord btnc;
    TextEntry search;


    public WikiBrowser(final Coord c, final Coord sz, final Widget parent) {
        super(c, sz, parent, "Wiki");
        ssz = new Coord(minsz);
        ui.wiki = this;
        mrgn = Coord.z;
        btnc = Coord.z.add(0, addrh);
        search = new TextEntry(new Coord(5, 15), new Coord(sz.x - 5, 20), this, "");
        sub = new Button(new Coord(300, 260), sbtnw, this,
                Resource.loadimg("gfx/hud/slen/sau")) {
            public void click() {
                sup();
            }
        };
        sdb = new Button(new Coord(300, 280), sbtnw, this,
                Resource.loadimg("gfx/hud/slen/sad")) {
            public void click() {
                sdn();
            }
        };
        sub.visible = sdb.visible = false;

        pack();
    }

    private void sup() {
        woff--;
        updbtns();
    }

    private void sdn() {
        woff++;
        updbtns();
    }

    public void draw(final GOut g) {
        if (recalcsz) {
            recalcsz = false;
            deltasz();
        }
        super.draw(g);
        if (!folded)
            g.image(grip, sz.sub(gzsz));
    }

    public boolean mousedown(final Coord c, final int button) {
        if (folded) {
            return super.mousedown(c, button);
        }
        parent.setfocus(this);
        raise();
        if (button == 1) {
            ui.grabmouse(this);
            doff = c;
            if (c.isect(sz.sub(gzsz), gzsz)) {
                rsm = true;
                return true;
            }
        }
        return super.mousedown(c, button);
    }

    public boolean mouseup(final Coord c, final int button) {
        if (rsm) {
            ui.ungrabmouse();
            rsm = false;
            deltasz();
        } else {
            super.mouseup(c, button);
        }
        return (true);
    }

    public void mousemove(final Coord c) {
        if (rsm) {
            final Coord d = c.sub(doff);
            doff = c;
            ssz = ssz.add(d);
            ssz.setX(Math.max(ssz.x, minsz.x));
            ssz.setY(Math.max(ssz.y, minsz.y));
            pack();
        } else {
            super.mousemove(c);
        }
    }

    public void pack() {
        checkfold();
        placecbtn();
    }

    private void deltasz() {
        final Coord s = ssz.sub(0, btnh + gzsz.y + addrh);
        for (final HWindow wnd : wnds) {
            wnd.setsz(s);
        }
        search.sz = new Coord(s.x - 10, 20);
        updbtns();
    }

    public boolean type(final char key, final java.awt.event.KeyEvent ev) {
        if (key == 27) {
            fold();
            return true;
        }
        if ((key == 10) && (focused == search)) {
            open(search.text);
            return true;
        }
        return (super.type(key, ev));
    }

    private void open(final String request) {
        new WikiPage(this, request);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (checkIsCloseButton(sender)) {
            close();
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    public boolean drop(final Coord cc, final Coord ul, final Item item) {
        //ui.slen.wdgmsg("setbelt", 1, 0);
        final String name = item.name();
        if (name != null) {
            open(name);
        }
        return (true);
    }

    void close() {
        while (!wnds.isEmpty()) {
            ui.destroy(wnds.get(0));
        }
        ui.destroy(this);
    }

    public boolean dropthing(final Coord c, final Object thing) {
        if (thing instanceof Resource) {
            final Resource res = (Resource) thing;
            String name = null;
            final Tooltip tt = res.layer(Resource.tooltip);
            if (tt != null) {
                name = tt.t;
            } else {
                final AButton ad = res.layer(Resource.action);
                if (ad != null) {
                    name = ad.name;
                }
            }
            if (name != null)
                open(name);
            return true;
        }
        return false;
    }

    public void addwnd(final HWindow wnd) {
        wnd.sz = ssz.sub(0, btnh + gzsz.y + addrh);
        wnd.c = new Coord(0, btnh + gzsz.y + addrh);
        wnds.add(wnd);
        final Button btn = new Button(new Coord(0, 0), maxbtnw + 1, this, wnd.title) {
            public void click() {
                setawnd(wnd, true);
            }
        };
        btns.put(wnd, btn);
        setawnd(wnd);
        recalcsz = true;
    }

    public void remwnd(final HWindow wnd) {
        if (wnd == awnd) {
            final int i = wnds.indexOf(wnd);
            if (wnds.size() == 1)
                setawnd(null);
            else if (i < 0)
                setawnd(wnds.get(0));
            else if (i >= wnds.size() - 1)
                setawnd(wnds.get(i - 1));
            else
                setawnd(wnds.get(i + 1));
        }
        wnds.remove(wnd);
        ui.destroy(btns.get(wnd));
        btns.remove(wnd);
        updbtns();

    }

    @Override
    public void destroy() {
        ui.wiki = null;
        super.destroy();
    }

    public void updurgency(final HWindow wnd, final int level) {
        btns.get(wnd).change(wnd.title, wnd.visible ? Color.WHITE : null);
    }

    public void setawnd(final HWindow wnd) {
        setawnd(wnd, true);

    }

    public void setawnd(final HWindow wnd, final boolean focus) {
        awnd = wnd;
        for (final HWindow w : wnds)
            w.visible = false;
        if (wnd != null) {
            wnd.visible = !folded;
            updurgency(wnd, -1);
        }
        updbtns();
    }

    private void updbtns() {
        final int ws = wnds.size();
        int k = Math.max((ssz.x - sbtnw) / minbtnw, 1);
        if (k > (ws >> 1)) {
            k = Math.max(ws >> 1, 1);
            if ((ws % 2) != 0)
                k++;
        }
        final int bw = Math.min((ssz.x - sbtnw) / k, maxbtnw);
        final int bpp = 2 * k;

        if (ws <= bpp) {
            woff = 0;
        } else {
            if (woff < 0)
                woff = 0;
            if (woff > ws - bpp)
                woff = ws - bpp;
        }
        for (final Button b : btns.values())
            b.visible = false;
        sub.visible = sdb.visible = false;
        for (int i = 0; i < bpp; i++) {
            final int wi = i + woff;
            if (wi >= ws)
                continue;
            if (woff > 0) {
                sub.visible = true;
                sub.c = btnc.add(ssz.x - sbtnw, 0);
            }
            if (woff < ws - bpp) {
                sdb.visible = true;
                sdb.c = btnc.add(ssz.x - sbtnw, 20);
            }
            final HWindow w = wnds.get(wi);
            final Button b = btns.get(w);
            //w.sz = ssz.sub(0, btnh + addrh+gzsz.y);
            b.change(w.title, w.visible ? Color.WHITE : null);
            b.visible = true;
            b.sz = new Coord(bw, b.sz.y);
            b.c = btnc.add(bw * (i % k), (i / k) * 20);
        }
    }
}
