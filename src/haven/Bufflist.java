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

import haven.resources.layers.AButton;
import haven.resources.layers.Tooltip;

import java.awt.*;

@SuppressWarnings({"SynchronizeOnNonFinalField"})
public class Bufflist extends Widget {
    static final Tex frame = Resource.loadtex("gfx/hud/buffs/frame");
    static final Tex cframe = Resource.loadtex("gfx/hud/buffs/cframe");
    static final int imgoffX = 3;
    static final int imgoffY = 3;
    static final int ameteroffX = 3;
    static final int ameteroffY = 36;
    static final int ameterszX = 30;
    static final int ameterszY = 2;
    //    static final Coord imgoff = new Coord(imgoffX, imgoffY);
//    static final Coord ameteroff = new Coord(ameteroffX, ameteroffY);
//    static final Coord ametersz = new Coord(ameterszX, ameterszY);
    static final int margin = 2;
    static final int num = 5;
    private StringBuilder tooltipBuilder = new StringBuilder(100);


    static {
        Widget.addtype("buffs", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new Bufflist(c, parent));
            }
        });
    }

    public Bufflist(final Coord c, final Widget parent) {
        super(c, new Coord((num * frame.sz().x) + ((num - 1) * margin), cframe.sz().y), parent);
    }

    public void draw(final GOut g) {
        int i = 0;
        final int w = frame.sz().x + margin;
        final long now = System.currentTimeMillis();
        synchronized (ui.sess.glob.buffs) {
            for (final Buff b : ui.sess.glob.buffs.values()) {
                if (!b.major)
                    continue;
                final int bcX = i * w;
                if (b.ameter >= 0) {
                    g.image(cframe, bcX, 0);
                    g.chcolor(Color.BLACK);
                    g.frect(ameteroffX + bcX, ameteroffY, ameterszX, ameterszY);
                    g.chcolor(Color.WHITE);
                    g.frect(ameteroffX + bcX, ameteroffY, (b.ameter * ameterszX) / 100, ameterszY);
                    g.chcolor();
                } else {
                    g.image(frame, bcX, 0);
                }
                if (b.res.get() != null) {
                    final Tex img = b.res.get().layer(Resource.imgc).tex();
                    g.image(img, imgoffX + bcX, imgoffY);
                    final int imgX = img.sz().x;
                    final int imgY = img.sz().y;
                    if (b.nmeter >= 0) {
                        final Tex ntext = b.nmeter();
                        g.image(ntext, bcX + imgoffX + imgX - ntext.sz().x - 1, imgoffY + imgY - ntext.sz().y - 1);
                    }
                    if (b.cmeter >= 0) {
                        double m = b.cmeter / 100.0;
                        if (b.cticks >= 0) {
                            final double ot = b.cticks * 0.06;
                            final double pt = ((double) (now - b.gettime)) / 1000.0;
                            m *= (ot - pt) / ot;
                        }
                        g.chcolor(0, 0, 0, 128);
                        g.fellipseInRectangle(imgoffX, imgoffY, imgoffX + imgX, imgoffY + imgY, 90, (int) (90 + (360 * m)));
                        g.chcolor();
                    }
                }
                if (++i >= 5)
                    break;
            }
        }
    }

    public Object tooltip(final Coord c, final boolean again) {
        int i = 0;
        final int w = frame.sz().x + margin;
        synchronized (ui.sess.glob.buffs) {
            final StringBuilder sb = tooltipBuilder;
            for (final Buff b : ui.sess.glob.buffs.values()) {
                if (!b.major)
                    continue;
                final Coord bc = new Coord(i * w, 0);
                if (c.isect(bc, frame.sz())) {
                    sb.delete(0, sb.length());
                    final Resource res;
                    if (b.ameter > 0) {
                        sb.append(" (").append(b.ameter).append("%)");
                    }
                    if (b.cmeter > 0) {
                        if (b.cticks >= 0) {
                            final long now = System.currentTimeMillis();
                            final double t = (b.cticks * 0.06) - (((double) (now - b.gettime)) / 1000.0);
                            final int m = (int) (t / 60);
                            final int s = (int) (t % 60);
                            sb.append(" [");
                            if (m > 0) {
                                sb.append(m).append("m ");
                            }
                            sb.append(s).append("s]");
                        } else {
                            sb.append(" [").append(b.cmeter).append("%]");
                        }
                    }
                    if (b.tt != null)         // TODO: move to front
                        return (b.tt + sb);
                    else if ((res = b.res.get()) != null) {
                        final Tooltip tt;
                        final AButton act;
                        if ((tt = res.layer(Resource.tooltip)) != null) {
                            return tt.t + sb;
                        } else if ((act = res.layer(Resource.action)) != null) {
                            return act.name + sb;
                        }
                    }
                }
                if (++i >= 5)
                    break;
            }
        }
        return (null);
    }
}
