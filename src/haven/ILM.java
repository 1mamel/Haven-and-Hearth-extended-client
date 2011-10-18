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

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ILM extends TexRT {
    public final static BufferedImage ljusboll;
    final OCache oc;
    final TexI lbtex;
    Color amb;

    static {
        final int sz = 200;
        final int min = 50;
        final BufferedImage lb = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = lb.createGraphics();
        for (int y = 0; y < sz; y++) {
            for (int x = 0; x < sz; x++) {
                final double dx = sz / 2 - x;
                final double dy = sz / 2 - y;
                final double d = Math.sqrt(dx * dx + dy * dy);
                int gs;
                if (d > sz / 2)
                    gs = 255;
                else if (d < min)
                    gs = 0;
                else
                    gs = (int) (((d - min) / ((sz / 2) - min)) * 255);
                gs /= 2;
                final Color c = new Color(gs, gs, gs, 128 - gs);
                g.setColor(c);
                g.fillRect(x, y, 1, 1);
            }
        }
        ljusboll = lb;
    }

//    public void UpdateSize(Coord sz) {
//        dim = sz;
//    }

    public ILM(final Coord sz, final OCache oc) {
        super(sz);
        this.oc = oc;
        amb = new Color(0, 0, 0, 0);
        lbtex = new TexI(ljusboll);
    }

    protected Color setenv(final GL gl) {
        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        return (amb);
    }

    protected boolean subrend(final GOut g) {
        if (CustomConfig.isHasNightVision()) {
            return false;
        }
        final GL gl = g.gl;
        gl.glClearColor(255, 255, 255, 255);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        synchronized (oc) {
            for (final Gob gob : oc) {
                if (gob.sc == null) {
                    /* Might not have been set up by the MapView yet */
                    continue;
                }
                final Lumin lum = gob.getattr(Lumin.class);
                if (lum == null)
                    continue;
                final Coord sc = gob.sc.add(lum.off).add(-lum.sz, -lum.sz);
                g.image(lbtex, sc, new Coord(lum.sz * 2, lum.sz * 2));
            }
        }
        return (true);
    }

    protected byte[] initdata() {
        return (null);
    }
}
