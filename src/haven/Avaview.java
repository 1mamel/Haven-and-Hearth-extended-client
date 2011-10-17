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

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Avaview extends Widget {
    public static final Coord dasz = new Coord(74, 74);
    public static final Coord smallSize = new Coord(27, 27);
    private final Coord asz;
    int avagob;
    boolean none = false;
    AvaRender myown = null;
    public Color color = Color.WHITE;
    public static final Coord unborder = new Coord(2, 2);
    public static final Tex missing = Resource.loadtex("gfx/hud/equip/missing");

    static {
        Widget.addtype("av", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new Avaview(c, parent, (Integer) args[0]));
            }
        });
        Widget.addtype("av2", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                final List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
                for (final Object arg : args)
                    rl.add(parent.ui.sess.getres((Integer) arg));
                return (new Avaview(c, parent, rl));
            }
        });
    }

    private Avaview(final Coord c, final Widget parent, final Coord asz) {
        super(c, asz.add(Window.wbox.bisz()).sub(unborder.mul(2)), parent);
        this.asz = asz;
    }

    public Avaview(final Coord c, final Widget parent, final int avagob, final Coord asz) {
        this(c, parent, asz);
        this.avagob = avagob;
    }

    public Avaview(final Coord c, final Widget parent, final int avagob) {
        this(c, parent, avagob, dasz);
    }

    public Avaview(final Coord c, final Widget parent, final List<Indir<Resource>> rl) {
        this(c, parent, dasz);
        if (rl.isEmpty())
            none = true;
        else
            this.myown = new AvaRender(rl);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("upd")) {
            this.avagob = (Integer) args[0];
            return;
        }
        if (msg.equals("ch")) {
            final List<Indir<Resource>> rl = new LinkedList<Indir<Resource>>();
            for (final Object arg : args)
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

    public void draw(final GOut g) {
        Tex at = null;
        if (none) {
        } else if (myown != null) {
            at = myown;
        } else {
            final Gob gob = ui.sess.glob.oc.getgob(avagob);
            Avatar ava = null;
            if (gob != null)
                ava = gob.getattr(Avatar.class);
            if (ava != null)
                at = ava.rend;
        }
        final GOut g2 = g.reclip(Window.wbox.tloff().sub(unborder), asz);
        final int yo;
        if (at == null) {
            at = missing;
            yo = 0;
        } else {
            g2.image(Equipory.bg, new Coord(Equipory.bg.sz().x / 2 - asz.x / 2, 20).inv());
            yo = (20 * asz.y) / dasz.y;
        }
        final Coord tsz = new Coord((at.sz().x * asz.x) / dasz.x, (at.sz().y * asz.y) / dasz.y);
        g2.image(at, new Coord(tsz.x / 2 - asz.x / 2, yo).inv(), tsz);
        g.chcolor(color);
        Window.wbox.draw(g, Coord.z, asz.add(Window.wbox.bisz()).sub(unborder.mul(2)));
    }

    public boolean mousedown(final Coord c, final int button) {
        wdgmsg("click", button);
        return (true);
    }
}
