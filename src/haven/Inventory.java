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

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class Inventory extends Widget implements DTarget {
    public static final Tex invsq;  // InvisibleSquare = 1x1 cell
    public static final Coord invSqSize; //size of invsq
    public static final Coord invSqSizeSubOne; //size of invsq.sub(1,1)
    protected static final BufferedImage[] trashButtonImages = new BufferedImage[]{
            Resource.loadimg("gfx/hud/trashu"),
            Resource.loadimg("gfx/hud/trashd"),
            Resource.loadimg("gfx/hud/trashh")};
    private final IButton trashButton;
    private AtomicBoolean wait = new AtomicBoolean(false);

    static {
        invsq = Resource.loadtex("gfx/hud/invsq"); // InvisibleSquare = 1x1 cell
        invSqSize = invsq.sz();  //32x32
        invSqSizeSubOne = Inventory.invSqSize.sub(1, 1);
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
        Coord sz = invSqSizeSubOne;
        for (c.setY(0); c.y < isz.y; c.setY(c.y + 1)) {
            for (c.setX(0); c.x < isz.x; c.setX(c.x + 1)) {
                g.image(invsq, c.mul(sz));
            }
        }
        super.draw(g);
    }

    /**
     * Creates new inventory
     *
     * @param c      coordinates
     * @param sz     size of cells
     * @param parent parent widget
     */
    public Inventory(Coord c, Coord sz, Widget parent) {
        super(c, invSqSizeSubOne.mul(sz).add(17, 1), parent);
        isz = sz;
        if (parent.canhastrash) {
            trashButton = new IButton(Coord.z, this, trashButtonImages);
            trashButton.visible = true;
        } else {
            trashButton = null;
        }
        recalculateSize();
    }

    public boolean mousewheel(Coord c, int amount) {
        if (amount < 0)
            wdgmsg("xfer", -1, ui.modflags());
        else if (amount > 0)
            wdgmsg("xfer", 1, ui.modflags());
        return (true);
    }

    public boolean drop(Coord cc, Coord ul, Item item) {
        wdgmsg("drop", ul.add(15, 15).div(Inventory.invSqSize));
        return (true);
    }

    public boolean iteminteract(Coord cc, Coord ul) {
        return (false);
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("sz")) {
            isz = (Coord) args[0];
            recalculateSize();
        }
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (checkTrashButton(sender)) {
            if (wait.get()) {
                return;
            }
            wait.set(true);
            new ConfirmWnd(parent.c.add(c).add(trashButton.c), ui.root, getmsg(), new ConfirmWnd.Callback() {
                public void result(Boolean res) {
                    wait.set(false);
                    if (res) {
                        empty();
                    }
                }
            });
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

//    public void toggleTrash(boolean visible) {
//        if (visible) {
//            if (trashButton == null) {
//                trashButton = new IButton(Coord.z, this, trashButtonImages);
//            }
//            trashButton.visible = visible;
//        } else {
//            if (trashButton != null) {
//                trashButton.visible = visible;
//            }
//        }
//        recalculateSize();
//    }

    private String getmsg() {
        if (parent instanceof Window) {
            String str = ((Window) parent).cap.text;
            return "Drop all items from the " + str.toLowerCase() + " to ground?";
        }
        return "Drop all items to ground?";
    }

    /**
     * Drops all items from inventory.
     */
    private void empty() {
        for (Widget wdg = lchild; wdg != null; wdg = wdg.prev) {
            if (wdg.visible && wdg instanceof Item) {
                wdg.wdgmsg("drop", Coord.z);
            }
        }
    }

    /**
     * Chechs of inventory is small (Finery forge, Oven, Crucible).
     *
     * @return is small
     */
    private boolean isSmall() {
        if (parent instanceof Window) {
            Window wnd = (Window) parent;
            if (wnd.cap != null) {
                String str = wnd.cap.text;
                if (str.equals("Oven") || str.equals("Finery Forge") || str.equals("Steel Crucible")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recalculates inventory size and trash location
     */
    private void recalculateSize() {
        sz = invSqSizeSubOne.mul(isz).add(1, 1);
        if (trashButton != null && trashButton.visible) {
            trashButton.c = sz.sub(0, invSqSize.y);
            hsz = sz.add(16, 0);
            if (isSmall()) {
                trashButton.c.x += 18;
                hsz.x += 18;
            }
        } else {
            hsz = null;
        }
    }

    protected boolean checkTrashButton(Widget w) {
        return trashButton != null && w == trashButton;
    }
}
