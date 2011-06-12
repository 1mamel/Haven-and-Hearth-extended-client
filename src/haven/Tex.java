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

public abstract class Tex {
    protected final Coord dim;

    public Tex(Coord sz) {
        dim = sz;
    }

    public Coord sz() {
        return (dim);
    }

    public static int nextp2(int in) {
        int ret;

        //noinspection StatementWithEmptyBody
        for (ret = 1; ret < in; ret <<= 1) ;
        return (ret);
    }

    public abstract void render(GOut g, Coord c, Coord ul, Coord br, Coord sz);

    public void render(GOut g, Coord c) {
        render(g, c, Coord.z, dim, dim);
    }

    public void crender(GOut g, Coord c, Coord ul, Coord sz, Coord tsz) {
        if ((tsz.getX() == 0) || (tsz.getY() == 0))
            return;
        if ((c.getX() >= ul.getX() + sz.getX()) || (c.getY() >= ul.getY() + sz.getY()) ||
                (c.getX() + tsz.getX() <= ul.getX()) || (c.getY() + tsz.getY() <= ul.getY()))
            return;
        Coord t = new Coord(c);
        Coord uld = new Coord(0, 0);
        Coord brd = new Coord(dim);
        Coord szd = new Coord(tsz);
        if (c.getX() < ul.getX()) {
            int pd = ul.getX() - c.getX();
            t.setX(ul.getX());
            uld.setX((pd * dim.getX()) / tsz.getX());
            szd.setX(szd.getX() - pd);
        }
        if (c.getY() < ul.getY()) {
            int pd = ul.getY() - c.getY();
            t.setY(ul.getY());
            uld.setY((pd * dim.getY()) / tsz.getY());
            szd.setY(szd.getY() - pd);
        }
        if (c.getX() + tsz.getX() > ul.getX() + sz.getX()) {
            int pd = (c.getX() + tsz.getX()) - (ul.getX() + sz.getX());
            szd.setX(szd.getX() - pd);
            brd.setX(brd.getX() - (pd * dim.getX()) / tsz.getX());
        }
        if (c.getY() + tsz.getY() > ul.getY() + sz.getY()) {
            int pd = (c.getY() + tsz.getY()) - (ul.getY() + sz.getY());
            szd.setY(szd.getY() - pd);
            brd.setY(brd.getY() - (pd * dim.getY()) / tsz.getY());
        }
        render(g, t, uld, brd, szd);
    }

    public void crender(GOut g, Coord c, Coord ul, Coord sz) {
        crender(g, c, ul, sz, dim);
    }

    public void dispose() {
    }
}
