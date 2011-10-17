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

import java.util.*;

public class OCache implements Iterable<Gob> {
    /* XXX: Use weak refs */
    private final Collection<Collection<Gob>> local = new LinkedList<Collection<Gob>>();
    private final Map<Integer, Gob> objs = new TreeMap<Integer, Gob>();
    private final Map<Integer, Integer> deleted = new TreeMap<Integer, Integer>();
    private final Glob glob;
    long lastctick = 0;

    public OCache(final Glob glob) {
        this.glob = glob;
    }

    public synchronized void remove(final int id, final int frame) {
        if (objs.containsKey(id)) {
            objs.remove(id);
            deleted.put(id, frame);
        }
    }

    public synchronized void tick() {
        for (final Gob g : objs.values()) {
            g.tick();
        }
    }

    public void ctick() {
        final long now;
        final int dt;

        now = System.currentTimeMillis();
        if (lastctick == 0)
            dt = 0;
        else
            dt = (int) (System.currentTimeMillis() - lastctick); // TODO: see what may be changed if replace .curr...() with 'now' variable
        synchronized (this) {
            for (final Gob g : objs.values())
                g.ctick(dt);
        }
        lastctick = now;
    }

    @SuppressWarnings("unchecked")
    public Iterator<Gob> iterator() {
        final Collection<Iterator<Gob>> is = new LinkedList<Iterator<Gob>>();
        for (final Collection<Gob> gc : local)
            is.add(gc.iterator());
        return (new I2<Gob>(objs.values().iterator(), new I2<Gob>(is)));
    }

    public synchronized void ladd(final Collection<Gob> gob) {
        local.add(gob);
    }

    public synchronized void lrem(final Collection<Gob> gob) {
        local.remove(gob);
    }

    public synchronized Gob getgob(final int id) {
        return (objs.get(id));
    }

    public synchronized Gob getgob(final int id, final int frame) {
        if (!objs.containsKey(id)) {
            boolean r = false;
            if (deleted.containsKey(id)) {
                if (deleted.get(id) < frame)
                    deleted.remove(id);
                else
                    r = true;
            }
            if (r) {
                return (null);
            } else {
                final Gob g = new Gob(glob, Coord.z, id, frame);
                objs.put(id, g);
                return (g);
            }
        } else {
            return (objs.get(id));
        }
        /* XXX: Clean up in deleted */
    }

    public synchronized void move(final int id, final int frame, final Coord c) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        g.move(c);
    }

    public synchronized void cres(final int id, final int frame, final Indir<Resource> res, final Message sdt) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        final ResDrawable d = (ResDrawable) g.getattr(Drawable.class);
        if ((d == null) || (d.res != res) || (d.sdt.blob.length > 0) || (sdt.blob.length > 0)) {
            g.setattr(new ResDrawable(g, res, sdt));
        }
    }

    public synchronized void linbeg(final int id, final int frame, final Coord s, final Coord t, final int c) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        final LinMove lm = new LinMove(g, s, t, c);
        g.setattr(lm);
    }

    public synchronized void linstep(final int id, final int frame, final int l) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        final Moving m = g.getattr(Moving.class);
        if ((m == null) || !(m instanceof LinMove))
            return;
        final LinMove lm = (LinMove) m;
        if ((l < 0) || (l >= lm.c))
            g.delattr(Moving.class);
        else
            lm.setl(l);
    }

    public synchronized void speak(final int id, final int frame, final Coord off, final String text) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        if (text.length() < 1) {
            g.delattr(Speaking.class);
        } else {
            final Speaking m = g.getattr(Speaking.class);
            if (m == null) {
                g.setattr(new Speaking(g, off, text));
            } else {
                m.off = off;
                m.update(text);
            }
        }
    }

    public synchronized void layers(final int id, final int frame, final Indir<Resource> base, final List<Indir<Resource>> layers) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        Layered lay = (Layered) g.getattr(Drawable.class);
        if ((lay == null) || (lay.base != base)) {
            lay = new Layered(g, base);
            g.setattr(lay);
        }
        lay.setlayers(layers);
    }

    public synchronized void avatar(final int id, final int frame, final List<Indir<Resource>> layers) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        Avatar ava = g.getattr(Avatar.class);
        if (ava == null) {
            ava = new Avatar(g);
            g.setattr(ava);
        }
        ava.setlayers(layers);
    }

    public synchronized void drawoff(final int id, final int frame, final Coord off) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        if ((off.x == 0) && (off.y == 0)) {
            g.delattr(DrawOffset.class);
        } else {
            DrawOffset dro = g.getattr(DrawOffset.class);
            if (dro == null) {
                dro = new DrawOffset(g, off);
                g.setattr(dro);
            } else {
                dro.off = off;
            }
        }
    }

    public synchronized void lumin(final int id, final int frame, final Coord off, final int sz, final int str) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        g.setattr(new Lumin(g, off, sz, str));
    }

    public synchronized void follow(final int id, final int frame, final int oid, final Coord off, final int szo) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        if (oid == -1) {
            g.delattr(Following.class);
        } else {
            Following flw = g.getattr(Following.class);
            if (flw == null) {
                flw = new Following(g, oid, off, szo);
                g.setattr(flw);
            } else {
                flw.tgt = oid;
                flw.doff = off;
                flw.szo = szo;
            }
        }
    }

    public synchronized void homostop(final int id, final int frame) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        g.delattr(Homing.class);
    }

    public synchronized void homing(final int id, final int frame, final int oid, final Coord tc, final int v) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        g.setattr(new Homing(g, oid, tc, v));
    }

    public synchronized void homocoord(final int id, final int frame, final Coord tc, final int v) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        final Homing homo = g.getattr(Homing.class);
        if (homo != null) {
            homo.tc = tc;
            homo.v = v;
        }
    }

    public synchronized void overlay(final int id, final int frame, final int olid, final boolean prs, final Indir<Resource> resid, final Message sdt) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        Gob.Overlay ol = g.findol(olid);
        if (resid != null) {
            if (ol == null) {
                g.ols.add(ol = new Gob.Overlay(olid, resid, sdt));
            } else if (!ol.sdt.equals(sdt)) {
                g.ols.remove(ol);
                g.ols.add(ol = new Gob.Overlay(olid, resid, sdt));
            }
            ol.delign = prs;
        } else {
            if ((ol != null) && (ol.spr instanceof Gob.Overlay.CDel))
                ((Gob.Overlay.CDel) ol.spr).delete();
            else
                g.ols.remove(ol);
        }
    }

    public synchronized void health(final int id, final int frame, final int hp) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        g.setattr(new GobHealth(g, hp));
    }

    public synchronized void buddy(final int id, final int frame, final String name, final int group, final int type) {
        final Gob g = getgob(id, frame);
        if (g == null)
            return;
        if ((name.length() == 0) && (group == 0) && (type == 0)) {
            g.delattr(KinInfo.class);
        } else {
            final KinInfo b = g.getattr(KinInfo.class);
            if (b == null) {
                g.setattr(new KinInfo(g, name, group, type));
            } else {
                b.update(name, group, type);
            }
        }
    }
}
