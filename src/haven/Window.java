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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window extends Widget implements DTarget {
    // Resources
    private static final Tex bg = Resource.loadtex("gfx/hud/bgtex");
    private static final Tex cl = Resource.loadtex("gfx/hud/cleft");
    private static final Tex cm = Resource.loadtex("gfx/hud/cmain");
    private static final Tex cr = Resource.loadtex("gfx/hud/cright");
    protected static final BufferedImage[] closeButtonImages = new BufferedImage[]{
            Resource.loadimg("gfx/hud/cbtn"),
            Resource.loadimg("gfx/hud/cbtnd"),
            Resource.loadimg("gfx/hud/cbtnh")};
    protected static final BufferedImage[] foldButtonImages = new BufferedImage[]{
            Resource.loadimg("gfx/hud/fbtn"),
            Resource.loadimg("gfx/hud/fbtnd"),
            Resource.loadimg("gfx/hud/fbtnh")};
    private static final Color cc = Color.YELLOW;
    // EO Resources
    private static final Text.Foundry cf = new Text.Foundry(new Font("Serif", Font.PLAIN, 12));
    static final IBox wbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    public boolean justclose = false;

    protected final Coord tlo;
    protected final Coord rbo;
    protected Coord mrgn = new Coord(13, 13);
    protected final IButton closeButton;
    protected final IButton foldButton;

    public boolean folded;
    ArrayList<Widget> wfolded;
    protected Coord ssz;

    final Text cap;

    boolean dm = false;
    //    private final Coord atl;
    public Coord asz;
    protected Coord wsz;
    protected Coord doff;
    private boolean dt = false;

    static {
        Widget.addtype("wnd", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                if (args.length < 2)
                    return (new Window(c, (Coord) args[0], parent, null));
                else {
                    String name = (String) args[1];
                    c = WindowsLocations.getLocationByName(name, c);
                    return (new Window(c, (Coord) args[0], parent, name));
                }
            }
        });
    }

    protected void placecbtn() {
        closeButton.c = new Coord(wsz.x() - 3 - closeButtonImages[0].getWidth(), 3).sub(mrgn).sub(wbox.tloff());
        foldButton.c = new Coord(closeButton.c.x() - 1 - foldButtonImages[0].getWidth(), closeButton.c.y());
    }

    @SuppressWarnings({"WeakerAccess", "WeakerAccess"})
    public Window(Coord c, Coord sz, Widget parent, String cap, Coord tlo, Coord rbo, boolean closable) {
        super(c, new Coord(0, 0), parent);
        this.tlo = tlo;
        this.rbo = rbo;
        if (cap != null) {
            this.cap = cf.render(cap, cc);
        } else {
            this.cap = null;
        }
        sz = sz.add(tlo).add(rbo).add(wbox.bisz()).add(mrgn.mul(2));
        this.sz = sz;
//        atl = new Coord(wbox.bl.sz().x, wbox.bt.sz().y).add(tlo);
        wsz = sz.sub(tlo).sub(rbo);
        asz = new Coord(wsz.x() - wbox.bl.sz().x() - wbox.br.sz().x() - mrgn.x(), wsz.y() - wbox.bt.sz().y() - wbox.bb.sz().y() - mrgn.y());
        if (closable) {
            closeButton = new IButton(Coord.z, this, closeButtonImages);
            placecbtn();
        } else {
            closeButton = null;
        }
        foldButton = new IButton(Coord.z, this, foldButtonImages);
        setfocustab(true);
        parent.setfocus(this);
    }

    @SuppressWarnings({"WeakerAccess"})
    public Window(Coord c, Coord sz, Widget parent, String cap, Coord tlo, Coord rbo) {
        super(c, new Coord(0, 0), parent);
        this.tlo = tlo;
        this.rbo = rbo;
        closeButton = new IButton(Coord.z, this, closeButtonImages);
        foldButton = new IButton(Coord.z, this, foldButtonImages);
        foldButton.hide();
        folded = false;
        wfolded = new ArrayList<Widget>();
        if (cap != null) {
            this.cap = cf.render(cap, cc);
        } else {
            this.cap = null;
        }
        ssz = new Coord(sz);
        sz = sz.add(tlo).add(rbo).add(wbox.bisz()).add(mrgn.mul(2));
        this.sz = sz;
//	atl = new Coord(wbox.bl.sz().x, wbox.bt.sz().y).add(tlo);
        wsz = sz.add(tlo.inv()).add(rbo.inv());
        asz = new Coord(wsz.x() - wbox.bl.sz().x() - wbox.br.sz().x() - mrgn.x(), wsz.y() - wbox.bt.sz().y() - wbox.bb.sz().y() - mrgn.y());
        placecbtn();
        setfocustab(true);
        parent.setfocus(this);

        // TODO simplify with  this(c, sz, parent, cap, tlo, rbo, true);
    }

    public Window(Coord c, Coord sz, Widget parent, String cap) {
        this(c, sz, parent, cap, new Coord(0, 0), new Coord(0, 0));
    }

    public Window(Coord c, Coord sz, Widget parent, String cap, boolean closable) {
        this(c, sz, parent, cap, new Coord(0, 0), new Coord(0, 0), closable);
    }

    public void cdraw(GOut g) {
    }

    private static final Coord coord3x3 = new Coord(3, 3);

    public void draw(GOut og) {
        GOut g = og.reclip(tlo, wsz);
        Coord bgc = new Coord(3,3);
        for (bgc.setY(3); bgc.y() < wsz.y() - 6; bgc.setY(bgc.y() + bg.sz().y())) {
            for (bgc.setX(3); bgc.x() < wsz.x() - 6; bgc.setX(bgc.x() + bg.sz().x()))
                g.image(bg, bgc, coord3x3, wsz.add(-6, -6));
        }
        cdraw(og.reclip(xlate(Coord.z, true), sz));
        wbox.draw(g, Coord.z, wsz);
        if (cap != null) {
            GOut cg = og.reclip(new Coord(0, -7), sz.add(0, 7));
            int w = cap.tex().sz().x();
            int x0 = (folded) ? (mrgn.x() + (w / 2)) : (sz.x() / 2) - (w / 2);
            cg.image(cl, new Coord(x0 - cl.sz().x(), 0));
            cg.image(cm, new Coord(x0, 0), new Coord(w, cm.sz().y()));
            cg.image(cr, new Coord(x0 + w, 0));
            cg.image(cap.tex(), new Coord(x0, 0));
        }
        super.draw(og);
    }

    public void checkfold() {
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (checkIsCloseButton(wdg) || checkIsFoldButton(wdg))
                continue;
            if (folded) {
                if (wdg.visible) {
                    wdg.hide();
                    wfolded.add(wdg);
                }
            } else if (wfolded.contains(wdg)) {
                wdg.show();
            }
        }
        Coord max = new Coord(ssz);
        if (folded) {
            max.setY(0);
        } else {
            wfolded.clear();
        }

        recalcsz(max);
    }

    protected void recalcsz(Coord max) {
        sz = max.add(wbox.bsz().add(mrgn.mul(2)).add(tlo).add(rbo)).add(-1, -1);
        wsz = sz.sub(tlo).sub(rbo);
        if (folded)
            wsz.setY(wsz.y() / 2);
        asz = wsz.sub(wbox.bl.sz()).sub(wbox.br.sz()).sub(mrgn.mul(2));
    }

    public void pack() {
        boolean isrunestone = cap.text.equals("Runestone");
        Coord max = new Coord(0, 0);
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (checkIsCloseButton(wdg) || checkIsFoldButton(wdg))
                continue;
            if ((isrunestone) && (wdg instanceof Label)) {
                Label lbl = (Label) wdg;
                lbl.settext(Config.translator.translate(lbl.texts));
            }
            Coord br = wdg.c.add(wdg.sz);
            if (br.x() > max.x())
                max.setX(br.x());
            if (br.y() > max.y())
                max.setY(br.y());
        }
        ssz = max;
//        sz = max.add(wbox.bsz().add(mrgn.mul(2)).add(tlo).add(rbo)).add(-1, -1);
//        
        checkfold();
        placecbtn();
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("pack")) {
            pack();
        } else if (msg.equals("dt")) {
            dt = (Integer) args[0] != 0;
        } else {
            super.uimsg(msg, args);
        }
    }

    public Coord xlate(Coord c, boolean in) {
        Coord ctl = wbox.tloff();
        if (in)
            return (c.add(ctl).add(tlo).add(mrgn));
        else
            return (c.sub(ctl).sub(tlo).sub(mrgn));
    }

    public boolean mousedown(Coord c, int button) {
        parent.setfocus(this);
        raise();
        if (super.mousedown(c, button))
            return (true);
        if (!c.isect(tlo, sz.sub(tlo).sub(rbo)))
            return (false);
        if (button == 1) {
            ui.grabmouse(this);
            dm = true;
            doff = c;
        }
        return (true);
    }

    public boolean mouseup(Coord c, int button) {
        if (dm) {
            ui.ungrabmouse();
            dm = false;
            WindowsLocations.coordChanged(this, this.c);
        } else {
            super.mouseup(c, button);
        }
        return (true);
    }

    public void mousemove(Coord c) {
        if (dm) {
            this.c = this.c.add(c.sub(doff));
        } else {
            super.mousemove(c);
        }
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (checkIsCloseButton(sender)) {
            if (justclose)
                ui.destroy(this);
            else
                wdgmsg("close");
        } else if (checkIsFoldButton(sender)) {
            folded = !folded;
            checkfold();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    public boolean type(char key, java.awt.event.KeyEvent ev) {
        if (key == 27) {
            if (justclose)
                ui.destroy(this);
            else
                wdgmsg("close");
            return (true);
        }
        return (super.type(key, ev));
    }

    public boolean drop(Coord cc, Coord ul, Item item) {
        if (dt) {
            wdgmsg("drop", cc);
            return (true);
        }
        return (false);
    }

    public boolean iteminteract(Coord cc, Coord ul) {
        return (false);
    }

    public Object tooltip(Coord c, boolean again) {
        Object ret = super.tooltip(c, again);
        if (ret != null)
            return (ret);
        else
            return ("");
    }

    protected boolean checkIsCloseButton(Widget w) {
        return closeButton != null && w == closeButton;
    }

    protected boolean checkIsFoldButton(Widget w) {
        return foldButton != null && w == foldButton;
    }
}
