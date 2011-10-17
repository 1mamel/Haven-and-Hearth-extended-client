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

public class Logwindow extends HWindow {
    final Textlog log;

    static {
        Widget.addtype("outdated-slenlog", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                final String t = (String) args[0];
                boolean cl = false;
                if (args.length > 1)
                    cl = (Integer) args[1] != 0;
                return (new Logwindow(parent, t, cl));
            }
        });
    }

    public Logwindow(final Widget parent, final String title, final boolean closable) {
        super(parent, title, closable);
        log = new Textlog(Coord.z, sz, this);
    }

    public void uimsg(@NotNull final String name, final Object... args) {
        if (name.equals("log")) {
            log.append((String) args[0]);
        } else {
            super.uimsg(name, args);
        }
    }
}
