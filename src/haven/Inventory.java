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

import haven.scriptengine.InventoryExp;

public class Inventory extends Widget implements DTarget {
    public static final Tex invsq;  // InvisibleSquare = 1x1 cell
    public static final Coord invsqSize; //size of invsq
    public static final Coord invsqSizeSubOne; //size of invsq.sub(1,1)

    static {
        invsq = Resource.loadtex("gfx/hud/invsq"); // InvisibleSquare = 1x1 cell
        invsqSize = invsq.sz();
        invsqSizeSubOne = invsqSize.sub(1, 1);
    }

    Coord isz; // size of inventory in cells

    static {
        Widget.addtype("inv", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new InventoryExp(c, (Coord) args[0], parent)); // Changed for processing inv features
            }
        });
    }

    public void draw(GOut g) {
        Coord c = new Coord();
        Coord sz = invsqSizeSubOne;
        for (c.y = 0; c.y < isz.y; c.y++) {
            for (c.x = 0; c.x < isz.x; c.x++) {
                g.image(invsq, c.mul(sz));
            }
        }
        super.draw(g);
    }

    public Inventory(Coord c, Coord sz, Widget parent) {
        super(c, invsqSizeSubOne.mul(sz).add(1, 1), parent);
        isz = sz;
    }

    public boolean mousewheel(Coord c, int amount) {
        if (amount < 0)
            wdgmsg("xfer", -1, ui.modflags());
        if (amount > 0)
            wdgmsg("xfer", 1, ui.modflags());
        return (true);
    }

    public boolean drop(Coord cc, Coord ul) {
        wdgmsg("drop", ul.add(15, 15).div(invsqSize));
        return (true);
    }

    public boolean iteminteract(Coord cc, Coord ul) {
        return (false);
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("sz")) {
            isz = (Coord) args[0];
            sz = invsqSizeSubOne.mul(isz).add(1, 1);
        }
    }
}
