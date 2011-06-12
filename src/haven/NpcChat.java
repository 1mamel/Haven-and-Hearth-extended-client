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
import java.util.LinkedList;
import java.util.List;

public class NpcChat extends Window {
    final Textlog out;
    final List<Button> btns = new LinkedList<Button>();

    static {
        Widget.addtype("npc", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new NpcChat(c, (Coord) args[0], parent, (String) args[1]));
            }
        });
    }

    public NpcChat(Coord c, Coord sz, Widget parent, String title) {
        super(c, sz, parent, title);
        out = new Textlog(Coord.z, new Coord(sz.getX(), sz.getY()), this);
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("log")) {
            Color col = null;
            if (args.length > 1)
                col = (Color) args[1];
            out.append((String) args[0], col);
        } else if (msg.equals("btns")) {
            if (!btns.isEmpty()) {
                for (Button b : btns)
                    ui.destroy(b);
            }
            btns.clear();
            if (args.length > 0) {
                int y = out.sz.getY() + 3;
                for (Object text : args) {
                    Button b = Button.wrapped(new Coord(0, y), out.sz.getX(), this, (String) text);
                    btns.add(b);
                    y += b.sz.getY() + 3;
                }
            }
            pack();
        } else {
            super.uimsg(msg, args);
        }
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender instanceof Button) {
            Button b = (Button) sender;
            int i = btns.indexOf(b);
            if (i != -1) {
                wdgmsg("btn", i);
                return;
            }
        }
        super.wdgmsg(sender, msg, args);
    }
}
