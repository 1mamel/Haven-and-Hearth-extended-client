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
import java.awt.font.TextAttribute;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Textlog extends Widget {
    static final Tex texpap = Resource.loadtex("gfx/hud/texpap");
    static final Tex schain = Resource.loadtex("gfx/hud/schain");
    static final Tex sflarp = Resource.loadtex("gfx/hud/sflarp");
    static final RichText.Foundry fnd = new RichText.Foundry(TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, 9, TextAttribute.FOREGROUND, Color.BLACK);
    private static final int textpapWidth = texpap.sz().x;
    private static final int textpapHeight = texpap.sz().y;
    public static final Color DEFAULT_COLOR = Color.BLACK;
    static final int margin = 3;

    private final boolean background;
    final Collection<Text> lines = new ConcurrentLinkedQueue<Text>();
    int maxy = 0, cury = 0;
    private boolean sdrag = false;

    static {
        Widget.addtype("log", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new Textlog(c, (Coord) args[0], parent));
            }
        });
    }

    public void draw(final GOut g) {
        final int height = sz.y;
        final int width = sz.x;

        // Draw background
        if (background) {
            for (int dcY = 0; dcY < height; dcY += textpapHeight) {
                for (int dcX = 0; dcX < width; dcX += textpapWidth) {
                    g.image(texpap, dcX, dcY);
                }
            }
        }

        // Draw text
        g.chcolor();
        int y = -cury;
        for (final Text line : lines) {
            final int lineHeight = line.sz().y;
            final int dy1 = height + y;
            final int dy2 = dy1 + lineHeight;
            if ((dy2 > 0) && (dy1 < height)) {
                g.image(line.tex(), margin, dy1);
            }
            y += lineHeight;
        }

        // Draw scroller
        if (maxy > y) {
            final int fx = width - sflarp.sz().x;
            final int cx = fx + (sflarp.sz().x / 2) - (schain.sz().x / 2);
            for (y = 0; y < height; y += schain.sz().y - 1) {
                g.image(schain, cx, y);
            }
            final double a = (double) (cury - height) / (double) (maxy - height);
            final int fy = (int) ((height - sflarp.sz().y) * a);
            g.image(sflarp, new Coord(fx, fy));
        }
    }

    public Textlog(final Coord c, final Coord sz, final Widget parent) {
        super(c, sz, parent);
        this.background = true;
    }

    public Textlog(final Coord c, final Coord sz, final Widget parent, final boolean background) {
        super(c, sz, parent);
        this.background = background;
    }

    public void append(String line, Color col) {
        if (col == null) {
            col = DEFAULT_COLOR;
        }

        line = RichText.Parser.quote(line);
        if (Config.use_smileys) {
            line = Config.mksmiley(line);
        }
        final int lineWidth = sz.x - ((margin * 2) + sflarp.sz().x);

        final Text renderedLine = fnd.render(line, lineWidth, TextAttribute.FOREGROUND, col, TextAttribute.SIZE, 12);
        lines.add(renderedLine);

        if (cury == maxy)
            cury += renderedLine.sz().y;
        maxy += renderedLine.sz().y;
    }

    public void append(final String line) {
        append(line, DEFAULT_COLOR);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("apnd")) {
            append((String) args[0]);
        }
    }

    public boolean mousewheel(final Coord c, final int amount) {
        cury += amount * 20;
        if (cury < sz.y) {
            cury = sz.y;
        }
        if (cury > maxy) {
            cury = maxy;
        }
        return (true);
    }

    public boolean mousedown(final Coord c, final int button) {
        if (button != 1)
            return (false);
        final int fx = sz.x - sflarp.sz().x;
//        int cx = fx + (sflarp.sz().x / 2) - (schain.sz().x / 2);
        if ((maxy > sz.y) && (c.x >= fx)) {
            sdrag = true;
            ui.grabmouse(this);
            mousemove(c);
            return (true);
        }
        return (false);
    }

    public void mousemove(final Coord c) {
        if (sdrag) {
            double a = (double) (c.y - (sflarp.sz().y / 2)) / (double) (sz.y - sflarp.sz().y);
            if (a < 0)
                a = 0;
            if (a > 1)
                a = 1;
            cury = (int) (a * (maxy - sz.y)) + sz.y;
        }
    }

    public boolean mouseup(final Coord c, final int button) {
        if ((button == 1) && sdrag) {
            sdrag = false;
            ui.ungrabmouse();
            return (true);
        }
        return (false);
    }
}

