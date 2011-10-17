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

import java.util.LinkedList;
import java.util.List;

public class Tabs {
    private final Coord c, sz;
    private final Widget parent;
    public Tab curtab = null;
    public final List<Tab> tabs = new LinkedList<Tab>();

    public Tabs(final Coord c, final Coord sz, final Widget parent) {
        this.c = c;
        this.sz = sz;
        this.parent = parent;
    }

    void add(final Tab t) {
        if (curtab == null) {
            curtab = t;
        } else {
            t.hide();
        }
        tabs.add(t);
    }

    void remove(final Tab t) {
        tabs.remove(t);
        if (curtab == t) {
            if (tabs.isEmpty()) {
                curtab = null;
            } else {
                curtab = tabs.get(0);
            }
        } else {
            t.hide();
        }
    }

    public class Tab extends Widget {
        public TabButton btn;

        public Tab() {
            super(Tabs.this.c, Tabs.this.sz, Tabs.this.parent);
            add(this);
        }

        public Tab(final Coord bc, final int bw, final String text) {
            this();
            this.btn = new TabButton(bc, bw, text, this);
        }

        @Override
        public void destroy() {
            remove(this);
            super.destroy();
        }
    }

    public class TabButton extends Button {
        public final Tab tab;

        private TabButton(final Coord c, final Integer w, final String text, final Tab tab) {
            super(c, w, Tabs.this.parent, text);
            this.tab = tab;
        }

        public void click() {
            showtab(tab);
        }
    }

    public void showtab(final Tab tab) {
        final Tab old = curtab;
        if (old != null)
            old.hide();
        if ((curtab = tab) != null)
            curtab.show();
        changed(old, tab);
    }

    public void changed(final Tab from, final Tab to) {
    }
}
