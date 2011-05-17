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

public class Window extends Widget implements DTarget {
    // Resources
    private static final Tex bg = Resource.loadtex("gfx/hud/bgtex");
    private static final Tex cl = Resource.loadtex("gfx/hud/cleft");
    private static final Tex cm = Resource.loadtex("gfx/hud/cmain");
    private static final Tex cr = Resource.loadtex("gfx/hud/cright");
    private static final BufferedImage[] closeButtonImages = new BufferedImage[]{
            Resource.loadimg("gfx/hud/cbtn"),
            Resource.loadimg("gfx/hud/cbtnd"),
            Resource.loadimg("gfx/hud/cbtnh")};
    private static final Color cc = Color.YELLOW;
    // EO Resources
    private static final Text.Foundry cf = new Text.Foundry(new Font("Serif", Font.PLAIN, 12));
    static final IBox wbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");

    private final Coord tlo;
    private final Coord rbo;
    private final Coord mrgn = new Coord(13, 13);
    private final IButton closeButton;

    final Text cap;

    boolean dm = false;
    //    private final Coord atl;
    public Coord asz;
    private Coord wsz;
    private Coord doff;
    private boolean dt = false;

    static {
        Widget.addtype("wnd", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                if (args.length < 2)
                    return (new Window(c, (Coord) args[0], parent, null));
                else {
                    c = WindowsLocations.getLocationByName((String) args[1], c);
                    return (new Window(c, (Coord) args[0], parent, (String) args[1]));
                }
            }
        });
    }

    private void placecbtn() {
        closeButton.c = new Coord(wsz.x - 3 - closeButtonImages[0].getWidth(), 3).sub(mrgn).sub(wbox.tloff());
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
        asz = new Coord(wsz.x - wbox.bl.sz().x - wbox.br.sz().x - mrgn.x, wsz.y - wbox.bt.sz().y - wbox.bb.sz().y - mrgn.y);
        if (closable) {
            closeButton = new IButton(Coord.z, this, closeButtonImages);
            placecbtn();
        } else {
            closeButton = null;
        }
        setfocustab(true);
        parent.setfocus(this);
    }

    @SuppressWarnings({"WeakerAccess"})
    public Window(Coord c, Coord sz, Widget parent, String cap, Coord tlo, Coord rbo) {
        this(c, sz, parent, cap, tlo, rbo, true);
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
        Coord bgc = new Coord();
        for (bgc.y = 3; bgc.y < wsz.y - 6; bgc.y += bg.sz().y) {
            for (bgc.x = 3; bgc.x < wsz.x - 6; bgc.x += bg.sz().x)
                g.image(bg, bgc, coord3x3, wsz.add(-6, -6));
        }
        cdraw(og.reclip(xlate(Coord.z, true), sz));
        wbox.draw(g, Coord.z, wsz);
        if (cap != null) {
            GOut cg = og.reclip(new Coord(0, -7), sz.add(0, 7));
            int w = cap.tex().sz().x;
            cg.image(cl, new Coord((sz.x / 2) - (w / 2) - cl.sz().x, 0));
            cg.image(cm, new Coord((sz.x / 2) - (w / 2), 0), new Coord(w, cm.sz().y));
            cg.image(cr, new Coord((sz.x / 2) + (w / 2), 0));
            cg.image(cap.tex(), new Coord((sz.x / 2) - (w / 2), 0));
        }
        super.draw(og);
    }

    public void pack() {
        Coord max = Coord.z.clone();
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (checkIsCloseButton(wdg))
                continue;
            Coord br = wdg.c.add(wdg.sz);
            if (br.x > max.x)
                max.x = br.x;
            if (br.y > max.y)
                max.y = br.y;
        }
        sz = max.add(wbox.bsz().add(mrgn.mul(2)).add(tlo).add(rbo)).add(-1, -1);
        wsz = sz.sub(tlo).sub(rbo);
        asz = new Coord(wsz.x - wbox.bl.sz().x - wbox.br.sz().x, wsz.y - wbox.bt.sz().y - wbox.bb.sz().y).sub(mrgn.mul(2));
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
            ui.grabmouse(null);
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
            wdgmsg("close");
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    public boolean type(char key, java.awt.event.KeyEvent ev) {
        if (key == 27) {
            wdgmsg("close");
            return (true);
        }
        return (super.type(key, ev));
    }

    public boolean drop(Coord cc, Coord ul) {
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
}
