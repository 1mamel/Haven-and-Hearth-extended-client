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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Charlist extends Widget {
    public static final Tex bg = Resource.loadtex("gfx/hud/avakort");
    public static final int margin = 6;
    public int height, y;
    public Button sau, sad;
    public final List<Char> chars = new ArrayList<Char>();

    public static class Char {
        static final Text.Foundry tf = new Text.Foundry("Serif", 20);
        String name;
        Text nt;
        Avaview ava;
        Button plb;

        public Char(final String name) {
            this.name = name;
            nt = tf.render(name);
        }
    }

    static {
        Widget.addtype("charlist", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new Charlist(c, parent, (Integer) args[0]));
            }
        });
    }

    public Charlist(final Coord c, final Widget parent, final int height) {
        super(c, new Coord(bg.sz().x, 40 + (bg.sz().y * height) + (margin * (height - 1))), parent);
        this.height = height;
        y = 0;
        sau = new Button(new Coord(0, 0), 100, this, Resource.loadimg("gfx/hud/slen/sau")) {
            public void click() {
                scroll(-1);
            }
        };
        sad = new Button(new Coord(0, sz.y - 19), 100, this, Resource.loadimg("gfx/hud/slen/sad")) {
            public void click() {
                scroll(1);
            }
        };
        sau.visible = sad.visible = false;
    }

    public void scroll(final int amount) {
        y += amount;
        synchronized (chars) {
            if (y > chars.size() - height)
                y = chars.size() - height;
        }
        if (y < 0)
            y = 0;
    }

    public void draw(final GOut g) {
        int y = 20;
        synchronized (chars) {
            for (final Char c : chars) {
                c.ava.visible = false;
                c.plb.visible = false;
            }
            for (int i = 0; (i < height) && (i + this.y < chars.size()); i++) {
                final Char c = chars.get(i + this.y);
                g.image(bg, new Coord(0, y));
                c.ava.visible = true;
                c.plb.visible = true;
                final int off = (bg.sz().y - c.ava.sz.y) / 2;
                c.ava.c = new Coord(off, off + y);
                c.plb.c = bg.sz().add(-105, -24 + y);
                g.image(c.nt.tex(), new Coord(off + c.ava.sz.x + 5, off + y));
                y += bg.sz().y + margin;
            }
        }
        try {
            super.draw(g);
        } catch (Exception e) {
            System.out.println("qweqwe");    // TODO WTF???
        }
    }

    public boolean mousewheel(final Coord c, final int amount) {
        scroll(amount);
        return (true);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (sender instanceof Button) {
            synchronized (chars) {
                for (final Char c : chars) {
                    if (sender == c.plb) {
                        wdgmsg("play", c.name);
                        CustomConfig.setActiveCharacter(c.name);
                        MainFrame.setWindowTitle(c.name);
                        final SlenHud hud = ui.slen;//parent.findchild(SlenHud.class);
                        if (hud != null) {
                            hud.initBelt();
                        }
                    }
                }
            }
        } else if (sender instanceof Avaview) {
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        if (msg.equals("add")) {
            final Char c = new Char((String) args[0]);
            final List<Indir<Resource>> resl = new LinkedList<Indir<Resource>>();
            for (int i = 1; i < args.length; i++)
                resl.add(ui.sess.getres((Integer) args[i]));
            c.ava = new Avaview(new Coord(0, 0), this, resl);
            c.ava.visible = false;
            c.plb = new Button(new Coord(0, 0), 100, this, "Play");
            c.plb.visible = false;
            synchronized (chars) {
                chars.add(c);
                if (chars.size() > height)
                    sau.visible = sad.visible = true;
            }
        }
    }
}
