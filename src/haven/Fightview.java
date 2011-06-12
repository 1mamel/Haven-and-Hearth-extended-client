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

import haven.resources.Resource;

import java.util.LinkedList;

public class Fightview extends Widget {
    static final Tex bg = Resource.loadtex("gfx/hud/bosq");
    static final int height = 5;
    static final int ymarg = 5;
    static final Coord cavac = new Coord(CustomConfig.getWindowWidth() - 100, 10);
    static final Coord cgivec = new Coord(CustomConfig.getWindowWidth() - 135, 10);
    static final Coord meterc = new Coord(CustomConfig.getWindowCenter().getX() - 85, 10);
    final LinkedList<Relation> lsrel = new LinkedList<Relation>();
    public Relation current = null;
    public Indir<Resource> blk, batk, iatk;
    public long atkc = -1;
    public int off, def;
    private final GiveButton curgive;
    private final Avaview curava;
    private final Widget comwdg, comwin;

    public class Relation {
        final int gobid;
        int bal, intns;
        int off, def;
        int ip, oip;
        final Avaview ava;
        final GiveButton give;

        public Relation(int gobid) {
            this.gobid = gobid;
            this.ava = new Avaview(Coord.z, Fightview.this, gobid, Avaview.smallSize);
            this.give = new GiveButton(Coord.z, Fightview.this, 0, new Coord(15, 15));
        }

        public Tex name() {
            Gob gob = ui.sess.glob.oc.getgob(gobid);
            if (gob != null) {
                KinInfo k = gob.getattr(KinInfo.class);
                if (k != null) {
                    return k.rendered();
                }
            }
            return null;
        }

        public void give(int state) {
            if (this == current)
                curgive.state = state;
            this.give.state = state;
        }

        public void show(boolean state) {
            ava.visible = state;
            give.visible = state;
        }

        public void remove() {
            ui.destroy(ava);
            ui.destroy(give);
        }
    }

    static {
        Widget.addtype("frv", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new Fightview(new Coord(CustomConfig.getWindowWidth() - 10, c.getY()), parent));
            }
        });
    }

    public Fightview(Coord c, Widget parent) {
        super(c.add(-bg.sz().getX(), 0), new Coord(bg.sz().getX(), (bg.sz().getY() + ymarg) * height), parent);
        SlenHud s = ui.slen;//  ui.root.findchild(SlenHud.class);
        curgive = new GiveButton(cgivec, ui.root, 0) {
            public void wdgmsg(String name, Object... args) {
                if (name.equals("click"))
                    Fightview.this.wdgmsg("give", current.gobid, args[0]);
            }
        };
        curava = new Avaview(cavac, ui.root, -1) {
            public void wdgmsg(String name, Object... args) {
                if (name.equals("click"))
                    Fightview.this.wdgmsg("click", current.gobid, args[0]);
            }
        };
        comwdg = new ComMeter(meterc, ui.root, this);
        comwin = new ComWin(s, this);
    }

    public void destroy() {
        ui.destroy(curgive);
        ui.destroy(curava);
        ui.destroy(comwdg);
        ui.destroy(comwin);
        super.destroy();
    }

    public void draw(GOut g) {
        curava.c.setX(CustomConfig.getWindowWidth() - 100);
        curgive.c.setX(CustomConfig.getWindowWidth() - 135);
        comwdg.c.setX(CustomConfig.getCenterX() - 85);
        c.setX(CustomConfig.getWindowWidth() - 10 - bg.sz().getX());
        int y = 0;
        for (Relation rel : lsrel) {
            if (rel == current) {
                rel.show(false);
                continue;
            }
            g.image(bg, new Coord(0, y));
            rel.ava.c = new Coord(25, ((bg.sz().getY() - rel.ava.sz.getY()) / 2) + y);
            rel.give.c = new Coord(5, 4 + y);
            rel.show(true);
            Tex name = rel.name();
            if (name != null) {
                g.image(name, new Coord(65, y - 2));
            }
            String str = String.format("$img[gfx/hud/combat/bal]%d/%d $img[gfx/hud/combat/ip]%d/%d\n", rel.bal, rel.intns, rel.ip, rel.oip);
            str += "$img[gfx/hud/combat/off]" + ((int) rel.off / 100);
            str += " $img[gfx/hud/combat/def]" + ((int) rel.def / 100);
            Tex text = RichText.render(str, 0).tex();
            g.image(text, new Coord(65, y + 10));
            text.dispose();
            //g.text(String.format("%d %d %d/%d", rel.bal, rel.intns, new Coord(65, y + 10));
            y += bg.sz().getY() + ymarg;
        }
        super.draw(g);
    }

    public static class Notfound extends RuntimeException {
        public final int id;

        public Notfound(int id) {
            super("No relation for Gob ID " + id + " found");
            this.id = id;
        }
    }

    private Relation getrel(int gobid) {
        for (Relation rel : lsrel) {
            if (rel.gobid == gobid)
                return (rel);
        }
        throw (new Notfound(gobid));
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender instanceof Avaview) {
            for (Relation rel : lsrel) {
                if (rel.ava == sender)
                    wdgmsg("click", rel.gobid, args[0]);
            }
            return;
        }
        if (sender instanceof GiveButton) {
            for (Relation rel : lsrel) {
                if (rel.give == sender)
                    wdgmsg("give", rel.gobid, args[0]);
            }
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    private Indir<Resource> n2r(int num) {
        if (num < 0)
            return (null);
        return (ui.sess.getres(num));
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("new")) {
            Relation rel = new Relation((Integer) args[0]);
            rel.bal = (Integer) args[1];
            rel.intns = (Integer) args[2];
            rel.give((Integer) args[3]);
            rel.ip = (Integer) args[4];
            rel.oip = (Integer) args[5];
            rel.off = (Integer) args[6];
            rel.def = (Integer) args[7];
            lsrel.addFirst(rel);
            return;
        } else if (msg.equals("del")) {
            Relation rel = getrel((Integer) args[0]);
            rel.remove();
            lsrel.remove(rel);
            return;
        } else if (msg.equals("upd")) {
            Relation rel = getrel((Integer) args[0]);
            rel.bal = (Integer) args[1];
            rel.intns = (Integer) args[2];
            rel.give((Integer) args[3]);
            rel.ip = (Integer) args[4];
            rel.oip = (Integer) args[5];
            return;
        } else if (msg.equals("updod")) {
            Relation rel = getrel((Integer) args[0]);
            rel.off = (Integer) args[1];
            rel.def = (Integer) args[2];
            return;
        } else if (msg.equals("cur")) {
            try {
                Relation rel = getrel((Integer) args[0]);
                lsrel.remove(rel);
                lsrel.addFirst(rel);
                current = rel;
                curgive.state = rel.give.state;
                curava.avagob = rel.gobid;
            } catch (Notfound e) {
                current = null;
            }
            return;
        } else if (msg.equals("atkc")) {
            atkc = System.currentTimeMillis() + (((Integer) args[0]) * 60);
            return;
        } else if (msg.equals("blk")) {
            blk = n2r((Integer) args[0]);
            return;
        } else if (msg.equals("atk")) {
            batk = n2r((Integer) args[0]);
            iatk = n2r((Integer) args[1]);
            return;
        } else if (msg.equals("offdef")) {
            off = (Integer) args[0];
            def = (Integer) args[1];
            return;
        }
        super.uimsg(msg, args);
    }
}
