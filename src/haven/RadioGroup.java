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

import java.util.ArrayList;
import java.util.HashMap;

public class RadioGroup {
    private final Widget parent;
    private final ArrayList<RadioButton> btns = new ArrayList<RadioButton>();
    private final HashMap<String, RadioButton> map = new HashMap<String, RadioButton>();
    private final HashMap<RadioButton, String> rmap = new HashMap<RadioButton, String>();
    private RadioButton checked;

    public RadioGroup(final Widget parent) {
        this.parent = parent;
    }

    public class RadioButton extends CheckBox {
        RadioButton(final Coord c, final Widget parent, final String lbl) {
            super(c, parent, lbl);
        }

        public boolean mousedown(final Coord c, final int button) {
            if (a || button != 1 || c.y < 16 || c.y > sz.y - 10) {
                return (false);
            }
            check(this);
            return (true);
        }

        public void changed(final boolean val) {
            a = val;
            super.changed(val);
            lbl = Text.std.render(lbl.text, a ? java.awt.Color.YELLOW : java.awt.Color.WHITE);
        }
    }

    public RadioButton add(final String lbl, final Coord c) {
        final RadioButton rb = new RadioButton(c, parent, lbl);
        btns.add(rb);
        map.put(lbl, rb);
        rmap.put(rb, lbl);
        if (checked == null) {
            checked = rb;
        }
        return (rb);
    }

    public void check(final String lbl) {
        if (map.containsKey(lbl)) {
            check(map.get(lbl));
        }
    }

    public void check(final RadioButton rb) {
        if (checked != null) {
            checked.changed(false);
        }
        checked = rb;
        checked.changed(true);
        changed(btns.indexOf(checked), rmap.get(checked));
    }

    public void hide() {
        for (final RadioButton rb : btns) {
            rb.hide();
        }
    }

    public void show() {
        for (final RadioButton rb : btns) {
            rb.show();
        }
    }

    public void changed(final int btn, final String lbl) {
    }
}
