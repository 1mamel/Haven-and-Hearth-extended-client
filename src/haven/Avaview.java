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

import haven.resources.Resource;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Avaview extends Widget {
    public static final Coord dasz = new Coord.U(74, 74);
    public static final Coord smallSize = new Coord.U(27, 27);
    private final Coord asz;
    int avagob;
    boolean none = false;
    AvaRender myown = null;
    public Color color = Color.WHITE;
    public static final Coord unborder = new Coord.U(2, 2);
    public static final Tex missing = Resource.loadtex("gfx/hud/equip/missing");

    static {
        Widget.addtype("av", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new Avaview(c, parent, (Integer) args[0]));
            }
        });
        Widget.addtype("av2", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
                for (Object arg : args)
                    rl.add(parent.ui.sess.getres((Integer) arg));
                return (new Avaview(c, parent, rl));
            }
        });
    }

    private Avaview(Coord c, Widget parent, Coord asz) {
        super(c, asz.add(Window.wbox.bisz()).sub(unborder.mul(2)), parent);
        this.asz = asz;
    }

    public Avaview(Coord c, Widget parent, int avagob, Coord asz) {
        this(c, parent, asz);
        this.avagob = avagob;
    }

    public Avaview(Coord c, Widget parent, int avagob) {
        this(c, parent, avagob, dasz);
    }

    public Avaview(Coord c, Widget parent, List<Indir<Resource>> rl) {
        this(c, parent, dasz);
        if (rl.isEmpty())
            none = true;
        else
            this.myown = new AvaRender(rl);
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("upd")) {
            this.avagob = (Integer) args[0];
            return;
        }
        if (msg.equals("ch")) {
            List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
            for (Object arg : args)
                rl.add(ui.sess.getres((Integer) arg));
            if (rl.isEmpty()) {
                this.myown = null;
                none = true;
            } else {
                if (myown != null)
                    myown.setlay(rl);
                else
                    myown = new AvaRender(rl);
                none = false;
            }
            return;
        }
        super.uimsg(msg, args);
    }

    public void draw(GOut g) {
        Tex at = null;
        if (none) {
        } else if (myown != null) {
            at = myown;
        } else {
            Gob gob = ui.sess.glob.oc.getgob(avagob);
            Avatar ava = null;
            if (gob != null)
                ava = gob.getattr(Avatar.class);
            if (ava != null)
                at = ava.rend;
        }
        GOut g2 = g.reclip(Window.wbox.tloff().sub(unborder), asz);
        int yo;
        if (at == null) {
            at = missing;
            yo = 0;
        } else {
            g2.image(Equipory.bg, new Coord(Equipory.bg.sz().getX() / 2 - asz.getX() / 2, 20).inv());
            yo = (20 * asz.getY()) / dasz.getY();
        }
        Coord tsz = new Coord((at.sz().getX() * asz.getX()) / dasz.getX(), (at.sz().getY() * asz.getY()) / dasz.getY());
        g2.image(at, new Coord(tsz.getX() / 2 - asz.getX() / 2, yo).inv(), tsz);
        g.chcolor(color);
        Window.wbox.draw(g, Coord.z, asz.add(Window.wbox.bisz()).sub(unborder.mul(2)));
    }

    public boolean mousedown(Coord c, int button) {
        wdgmsg("click", button);
        return (true);
    }
}
