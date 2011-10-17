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

public class ISBox extends Widget implements DTarget {
    static final Tex bg = Resource.loadtex("gfx/hud/bosq");
    static final Text.Foundry lf;
    private final Resource res;
    private Text label;

    static {
        lf = new Text.Foundry(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18), java.awt.Color.WHITE);
        lf.aa = true;
    }

    static {
        Widget.addtype("isbox", new WidgetFactory() {
            public Widget create(@NotNull final Coord c, @NotNull final Widget parent, final Object[] args) {
                return (new ISBox(c, parent, Resource.load((String) args[0]), (Integer) args[1], (Integer) args[2], (Integer) args[3]));
            }
        });
    }

    private void setlabel(final int rem, final int av, final int bi) {
        label = lf.renderf("%d/%d/%d", rem, av, bi);
    }

    public ISBox(final Coord c, final Widget parent, final Resource res, final int rem, final int av, final int bi) {
        super(c, bg.sz(), parent);
        this.res = res;
        setlabel(rem, av, bi);
    }

    public void draw(final GOut g) {
        g.image(bg, Coord.z);
        if (!res.loading.get()) {
            final Tex t = res.layer(Resource.imgc).tex();
            final Coord dc = new Coord(6, (bg.sz().y / 2) - (t.sz().y / 2));
            g.image(t, dc);
        }
        g.image(label.tex(), new Coord(40, (bg.sz().y / 2) - (label.tex().sz().y / 2)));
    }

    public Object tooltip(final Coord c, final boolean again) {
        if (!res.loading.get() && (res.layer(Resource.tooltip) != null))
            return (res.layer(Resource.tooltip).t);
        return (null);
    }

    public boolean mousedown(final Coord c, final int button) {
        if (button == 1) {
            if (ui.modshift)
                wdgmsg("xfer");
            else
                wdgmsg("click");
            return (true);
        }
        return (false);
    }

    public boolean mousewheel(final Coord c, final int amount) {
        if (amount < 0)
            wdgmsg("xfer2", -1, ui.modflags());
        if (amount > 0)
            wdgmsg("xfer2", 1, ui.modflags());
        return (true);
    }

    public boolean drop(final Coord cc, final Coord ul, final Item item) {
        wdgmsg("drop");
        return (true);
    }

    public boolean iteminteract(final Coord cc, final Coord ul) {
        wdgmsg("iact");
        return (true);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("chnum")) {
            setlabel((Integer) args[0], (Integer) args[1], (Integer) args[2]);
        } else {
            super.uimsg(msg, args);
        }
    }
}
