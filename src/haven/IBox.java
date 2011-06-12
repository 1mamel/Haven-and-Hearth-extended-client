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

public class IBox {
    final Tex ctl;
    final Tex ctr;
    final Tex cbl;
    final Tex cbr;
    final Tex bl;
    final Tex br;
    final Tex bt;
    final Tex bb;

    public IBox(Tex ctl, Tex ctr, Tex cbl, Tex cbr, Tex bl, Tex br, Tex bt, Tex bb) {
        this.ctl = ctl;
        this.ctr = ctr;
        this.cbl = cbl;
        this.cbr = cbr;
        this.bl = bl;
        this.br = br;
        this.bt = bt;
        this.bb = bb;
    }

    public IBox(String base, String ctl, String ctr, String cbl, String cbr, String bl, String br, String bt, String bb) {
        this(Resource.loadtex(base + '/' + ctl),
                Resource.loadtex(base + '/' + ctr),
                Resource.loadtex(base + '/' + cbl),
                Resource.loadtex(base + '/' + cbr),
                Resource.loadtex(base + '/' + bl),
                Resource.loadtex(base + '/' + br),
                Resource.loadtex(base + '/' + bt),
                Resource.loadtex(base + '/' + bb));
    }

    public Coord tloff() {
        return (new Coord(bl.sz().getX(), bt.sz().getY()));
    }

    public Coord ctloff() {
        return (ctl.sz());
    }

    public Coord bisz() {
        return (new Coord(bl.sz().getX() + br.sz().getX(), bt.sz().getY() + bb.sz().getY()));
    }

    public Coord bsz() {
        return (ctl.sz().add(cbr.sz()));
    }

    public void draw(GOut g, Coord tl, Coord sz) {
        g.image(bt, tl.add(new Coord(ctl.sz().getX(), 0)), new Coord(sz.getX() - ctr.sz().getX() - ctl.sz().getX(), bt.sz().getY()));
        g.image(bb, tl.add(new Coord(cbl.sz().getX(), sz.getY() - bb.sz().getY())), new Coord(sz.getX() - cbr.sz().getX() - cbl.sz().getX(), bb.sz().getY()));
        g.image(bl, tl.add(new Coord(0, ctl.sz().getY())), new Coord(bl.sz().getX(), sz.getY() - cbl.sz().getY() - ctl.sz().getY()));
        g.image(br, tl.add(new Coord(sz.getX() - br.sz().getX(), ctr.sz().getY())), new Coord(br.sz().getX(), sz.getY() - cbr.sz().getY() - ctr.sz().getY()));
        g.image(ctl, tl);
        g.image(ctr, tl.add(sz.getX() - ctr.sz().getX(), 0));
        g.image(cbl, tl.add(0, sz.getY() - cbl.sz().getY()));
        g.image(cbr, new Coord(sz.getX() - cbr.sz().getX() + tl.getX(), sz.getY() - cbr.sz().getY() + tl.getY()));
    }
}
