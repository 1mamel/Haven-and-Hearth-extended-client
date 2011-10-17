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

import static java.lang.Math.PI;

public class FlowerMenu extends Widget {
    public static final Color pink = new Color(255, 0, 128);
    public static final IBox pbox;
    public static final Tex pbg = Resource.loadtex("gfx/hud/bgtex");
    static final Color ptc = Color.YELLOW;
    static final Text.Foundry ptf = new Text.Foundry(new Font("SansSerif", Font.PLAIN, 12));
    static final int ph = 30;
    static final int ppl = 8;
    final Petal[] opts;
    Anim anim;

    static {
        Widget.addtype("sm", new WidgetFactory() {
            public Widget create(@NotNull Coord c, @NotNull final Widget parent, final Object[] args) {
                if ((c.x == -1) && (c.y == -1))
                    c = parent.ui.lcc;
                final String[] opts = new String[args.length];
                for (int i = 0; i < args.length; i++)
                    opts[i] = (String) args[i];
                return (new FlowerMenu(c, parent, opts));
            }
        });
        pbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    }

    public class Petal extends Widget {
        public final String name;
        public double ta, tr;
        public int num;
        final Text text;
        double a = 1;

        public Petal(final String name) {
            super(Coord.z, Coord.z, FlowerMenu.this);
            this.name = name;
            text = ptf.render(name, ptc);
            sz = new Coord(text.sz().x + 25, ph);
        }

        public void move(final Coord c) {
            this.c = c.sub(sz.div(2));
        }

        public void move(final double a, final double r) {
            move(Coord.sc(a, r));
        }

        public void draw(final GOut g) {
            g.chcolor(new Color(255, 255, 255, (int) (255 * a)));
            g.image(pbg, new Coord(3, 3), new Coord(3, 3), sz.add(new Coord(-6, -6)));
            pbox.draw(g, Coord.z, sz);
            g.image(text.tex(), sz.div(2).sub(text.sz().div(2)));
        }

        public boolean mousedown(final Coord c, final int button) {
            wdgmsg(FlowerMenu.this, "cl", num);
            return (true);
        }
    }

    public abstract class Anim {
        final long st = System.currentTimeMillis();
        int ms = 250;
        double s = 0.0;

        public void tick() {
            final int dt = (int) (System.currentTimeMillis() - st);
            final int animlength = (Config.fastFlowerAnim) ? 0 : ms;
            if (dt < animlength)
                s = (double) dt / animlength;
            else
                s = 1;
            if (dt >= animlength)
                end();
            tick2();
        }

        public void end() {
            anim = null;
        }

        public abstract void tick2();
    }

    public class Opening extends Anim {
        public void tick2() {
            for (final Petal p : opts) {
                p.move(p.ta + ((1 - s) * PI), p.tr * s);
                p.a = s;
            }
        }
    }

    public class Chosen extends Anim {
        final Petal chosen;

        Chosen(final Petal c) {
            ms = 750;
            chosen = c;
        }

        public void tick2() {
            for (final Petal p : opts) {
                if (p == chosen) {
                    if (s > 0.6) {
                        p.a = 1 - ((s - 0.6) / 0.4);
                    } else if (s < 0.3) {
                        p.move(p.ta, p.tr * (1 - (s / 0.3)));
                    }
                } else {
                    if (s > 0.3)
                        p.a = 0;
                    else
                        p.a = 1 - (s / 0.3);
                }
            }
        }

        public void end() {
            ui.destroy(FlowerMenu.this);
        }
    }

    public class Cancel extends Anim {
        public void tick2() {
            for (final Petal p : opts) {
                p.move(p.ta + ((s) * PI), p.tr * (1 - s));
                p.a = 1 - s;
            }
        }

        public void end() {
            ui.destroy(FlowerMenu.this);
        }
    }

    private static void organize(final Petal[] opts) {
        int l = 1, p = 0, i;
        int lr = -1;
        for (i = 0; i < opts.length; i++) {
            if (lr == -1) {
                //lr = (int)(ph / (1 - Math.cos((2 * PI) / (ppl * l))));
                lr = 75 + (50 * (l - 1));
            }
            opts[i].ta = (PI / 2) - (p * (2 * PI / (l * ppl)));
            opts[i].tr = lr;
            if (++p >= (ppl * l)) {
                l++;
                p = 0;
                lr = -1;
            }
        }
    }

    public FlowerMenu(final Coord c, final Widget parent, final String... options) {
        super(c, Coord.z, parent);
        opts = new Petal[options.length];
        for (int i = 0; i < options.length; i++) {
            opts[i] = new Petal(options[i]);
            opts[i].num = i;
        }
        organize(opts);
        ui.grabmouse(this);
        ui.grabkeys(this);
        anim = new Opening();
        UI.flowerMenu.set(this);
    }

    public boolean mousedown(final Coord c, final int button) {
        if (anim != null)
            return (true);
        if (!super.mousedown(c, button))
            wdgmsg("cl", -1);
        return (true);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("cancel")) {
            anim = new Cancel();
            ui.ungrabmouse();
            ui.ungrabkeys();
        } else if (msg.equals("act")) {
            anim = new Chosen(opts[(Integer) args[0]]);
            ui.ungrabmouse();
            ui.ungrabkeys();
        }
    }

    public void draw(final GOut g) {
        super.draw(g);
    }

    public void select(final String name) {
        for (final Petal opt : opts) {
            if (opt.name.equals(name)) {
                wdgmsg(this, "cl", opt.num);
                break;
            }
        }
    }

    public boolean type(final char key, final java.awt.event.KeyEvent ev) {
        if ((key >= '0') && (key <= '9')) {
            final int opt = (key == '0') ? 10 : (key - '1');
            if (opt < opts.length)
                wdgmsg("cl", opt);
            ui.ungrabkeys();
            return (true);
        } else if (key == 27) {
            wdgmsg("cl", -1);
            ui.ungrabkeys();
            return (true);
        }
        return (false);
    }

    @Override
    public void destroy() {
        UI.flowerMenu.compareAndSet(this, null);
        super.destroy();
    }

    public void update(final long dt) {
        if (anim != null)
            anim.tick();
    }

}
