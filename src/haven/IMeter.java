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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class IMeter extends Widget {
    static final Coord off = new Coord(13, 7);
    static final Coord fsz = new Coord(63, 18);
    static final Coord msz = new Coord(49, 4);
    final Resource bg;
    List<Meter> meters;

    static {
        Widget.addtype("im", new WidgetFactory() {
            public Widget create(@NotNull final Coord c, @NotNull final Widget parent, final Object[] args) {
                final Resource bg = Resource.load((String) args[0]);
                final List<Meter> meters = new LinkedList<Meter>();
                for (int i = 1; i < args.length; i += 2) {
                    //noinspection ObjectAllocationInLoop
                    meters.add(new Meter((Color) args[i], (Integer) args[i + 1]));
                }
                final IMeter res = new IMeter(c, parent, bg, meters);
                emitCreated(res, (String) args[0]);
                return res;
            }
        });
    }

    public IMeter(final Coord c, final Widget parent, final Resource bg, final List<Meter> meters) {
        super(c, fsz, parent);
        this.bg = bg;
        this.meters = meters;
    }

    public static class Meter {
        final Color c;
        final int a;

        public Meter(final Color c, final int a) {
            this.c = c;
            this.a = a;
        }
    }

    public void draw(final GOut g) {
        if (!bg.loading.get()) {
            final Tex bg = this.bg.layer(Resource.imgc).tex();
            g.chcolor(0, 0, 0, 255);
            g.frect(off, msz);
            g.chcolor();
            for (final Meter m : meters) {
                int w = msz.x;
                w = (w * m.a) / 100;
                g.chcolor(m.c);
                g.frect(off, w, msz.y);
            }
            g.chcolor();
            g.image(bg, Coord.z);
        }
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("set")) {
            final List<Meter> meters = new LinkedList<Meter>();
            for (int i = 0; i < args.length; i += 2) {
                //noinspection ObjectAllocationInLoop
                meters.add(new Meter((Color) args[i], (Integer) args[i + 1]));
            }
            this.meters = meters;
        } else if (msg.equals("tt")) {
            tooltip = args[0];
            emitUpdated(this, (String) args[0]);
        } else {
            super.uimsg(msg, args);
        }
    }

    public static interface Listener {
        void onIMeterAdded(IMeter meter, String resourceName);

        void onIMeterRemoved(IMeter meter);

        void onIMeterUpdated(IMeter meter, String tooltip);
    }

    private static final Set<Listener> ourListeners = new CopyOnWriteArraySet<Listener>();

    public static void subscribe(final Listener listener) {
        ourListeners.add(listener);
    }

    private static void unsubscribe(final Listener listener) {
        ourListeners.remove(listener);
    }

    protected static void emitCreated(final IMeter meter, final String resource) {
        for (final Listener listener : ourListeners) {
            try {
                listener.onIMeterAdded(meter, resource);
            } catch (Exception ignored) {
            }
        }
    }

    protected static void emitUpdated(final IMeter meter, final String tooltip) {
        for (final Listener listener : ourListeners) {
            try {
                listener.onIMeterUpdated(meter, tooltip);
            } catch (Exception ignored) {
            }
        }
    }

    protected static void emitRemoved(final IMeter meter) {
        for (final Listener listener : ourListeners) {
            try {
                listener.onIMeterRemoved(meter);
            } catch (Exception ignored) {
            }
        }
    }
}
