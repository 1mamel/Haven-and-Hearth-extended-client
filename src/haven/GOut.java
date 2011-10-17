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
import javax.media.opengl.GLContext;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GOut {
    final GL gl;
    public Coord ul, sz;
    private Color color = Color.WHITE;
    final GLContext ctx;
    private final Shared sh;

    private static class Shared {
        int curtex = -1;
        GOut root;
    }

    protected GOut(final GOut o) {
        this.gl = o.gl;
        this.ul = o.ul;
        this.sz = o.sz;
        this.color = o.color;
        this.ctx = o.ctx;
        this.sh = o.sh;
    }

    public GOut(final GL gl, final GLContext ctx, final Coord sz) {
        this.gl = gl;
        this.ul = Coord.z;
        this.sz = sz;
        this.ctx = ctx;
        this.sh = new Shared();
        this.sh.root = this;
    }

    public static class GLException extends RuntimeException {
        public final int code;
        public final String str;
        private static final javax.media.opengl.glu.GLU glu = new javax.media.opengl.glu.GLU();

        public GLException(final int code) {
            super("GL Error: " + code + " (" + glu.gluErrorString(code) + ')');
            this.code = code;
            this.str = glu.gluErrorString(code);
        }
    }

    public static void checkerr(final GL gl) {
        final int err = gl.glGetError();
        if (err != 0)
            throw (new GLException(err));
    }

    private void checkerr() {
        checkerr(gl);
    }

    private void glcolor() {
        gl.glColor4f((float) color.getRed() / 255.0f,
                (float) color.getGreen() / 255.0f,
                (float) color.getBlue() / 255.0f,
                (float) color.getAlpha() / 255.0f);
    }

    public GOut root() {
        return (sh.root);
    }

    public void image(final BufferedImage img, final Coord c) {
        if (img == null)
            return;
        final Tex tex = new TexI(img);
        image(tex, c);
        tex.dispose();
    }

    public void image(final haven.resources.layers.Image img, final Coord c) {
        if (img == null)
            return;
        image(img.tex(), c.add(img.o));
    }

    public void image(final Tex tex, final Coord c) {
        if (tex == null)
            return;
        tex.crender(this, c.add(ul), ul, sz);
        checkerr();
    }

    public void image(final Tex tex, final int x, final int y) {
        if (tex == null)
            return;
        tex.crender(this, ul.add(x, y), ul, sz);
        checkerr();
    }

    public void aimage(final Tex tex, final Coord c, final double ax, final double ay) {
        final Coord sz = tex.sz();
        image(tex, c.add((int) ((double) sz.x * -ax), (int) ((double) sz.y * -ay)));
    }

    public void aimage(final Tex tex, final int x, final int y, final double ax, final double ay) {
        final Coord sz = tex.sz();
        image(tex, x - (int) ((double) sz.x * ax), y - (int) ((double) sz.y * ay));
    }

    public void image(final Tex tex, final Coord c, final Coord sz) {
        if (tex == null)
            return;
        tex.crender(this, c.add(ul), ul, this.sz, sz);
        checkerr();
    }

    public void image(final Tex tex, final Coord c, final Coord ul, final Coord sz) {
        if (tex == null)
            return;
        tex.crender(this, c.add(this.ul), this.ul.add(ul), sz);
        checkerr();
    }

    private void vertex(final Coord c) {
        gl.glVertex2i(c.x + ul.x, c.y + ul.y);
    }

    private void vertex(final int x, final int y) {
        gl.glVertex2i(x + ul.x, y + ul.y);
    }

    void texsel(final int id) {
        if (id != sh.curtex) {
            HavenPanel.texmiss++;
            if (id == -1) {
                gl.glDisable(GL.GL_TEXTURE_2D);
            } else {
                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glBindTexture(GL.GL_TEXTURE_2D, id);
            }
            sh.curtex = id;
        } else {
            HavenPanel.texhit++;
        }
    }

    public void line(final Coord c1, final Coord c2, final double w) {
        texsel(-1);
        gl.glLineWidth((float) w);
        gl.glBegin(GL.GL_LINES);
        glcolor();
        vertex(c1);
        vertex(c2);
        gl.glEnd();
        checkerr();
    }

    public void line(final int x1, final int y1, final int x2, final int y2, final double w) {
        texsel(-1);
        gl.glLineWidth((float) w);
        gl.glBegin(GL.GL_LINES);
        glcolor();
        vertex(x1, y1);
        vertex(x2, y2);
        gl.glEnd();
        checkerr();
    }

    public void lineY(final int x, final int y1, final int y2, final double w) {
        line(x, y1, x, y2, w);
    }

    public void lineX(final int y, final int x1, final int x2, final double w) {
        line(x1, y, x2, y, w);
    }

    public void text(final String text, final Coord c) {
        atext(text, c, 0, 0);
    }

    public void atext(final String text, final Coord c, final double ax, final double ay) {
        final Text t = Text.render(text, null);
        final Tex T = t.tex();
        final Coord sz = t.sz();
        image(T, c.add((int) ((double) sz.x * -ax), (int) ((double) sz.y * -ay)));
        T.dispose();
        checkerr();
    }

    public void frect(final Coord ul, final Coord sz) {
        glcolor();
        texsel(-1);
        gl.glBegin(GL.GL_QUADS); // Because summ of ints faster than Coord.add
        vertex(ul);
        vertex(ul.x + sz.x, ul.y);
        vertex(ul.x + sz.x, ul.y + sz.y);
        vertex(ul.x, ul.y + sz.y);
        gl.glEnd();
        checkerr();
    }

    public void frect(final int ulX, final int ulY, final int lenX, final int lenY) {
        glcolor();
        texsel(-1);
        gl.glBegin(GL.GL_QUADS); // Because summ of ints faster than Coord.add
        vertex(ulX, ulY);
        vertex(ulX + lenX, ulY);
        vertex(ulX + lenX, ulY + lenY);
        vertex(ulX, ulY + lenY);
        gl.glEnd();
        checkerr();
    }

    public void frect(final int ulX, final int ulY, final Coord size) {
        frect(ulX, ulY, size.x, size.y);
    }

    public void frect(final Coord ul, final int sizeX, final int sizeY) {
        frect(ul.x, ul.y, sizeX, sizeY);
    }

    public void frect(final Coord c1, final Coord c2, final Coord c3, final Coord c4) {
        glcolor();
        texsel(-1);
        gl.glBegin(GL.GL_QUADS);
        vertex(c1);
        vertex(c2);
        vertex(c3);
        vertex(c4);
        gl.glEnd();
        checkerr();
    }

    public void fellipse(final Coord c, final Coord r, final int a1, final int a2) {
        glcolor();
        texsel(-1);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        vertex(c);
        for (int i = a1; i < a2; i += 5) {
            final double a = (i * Math.PI * 2) / 360.0;
            vertex(c.add((int) (Math.cos(a) * r.x), -(int) (Math.sin(a) * r.y)));
        }
        final double a = (a2 * Math.PI * 2) / 360.0;
        vertex(c.add((int) (Math.cos(a) * r.x), -(int) (Math.sin(a) * r.y)));
        gl.glEnd();
        checkerr();
    }

    public void fellipseInRectangle(final int x1, final int y1, final int x2, final int y2, final int a1, final int a2) {
        fellipse((x2 + x1) / 2, (y2 + y1) / 2, (x2 - x1) / 2, (y2 - y1) / 2, a1, a2);
    }

    public void fellipse(final int cx, final int cy, final int rx, final int ry, final int a1, final int a2) {
        glcolor();
        texsel(-1);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        vertex(cx, cy);
        for (int i = a1; i < a2; i += 5) {
            final double a = (i * Math.PI * 2) / 360.0;
            vertex(cx + (int) (Math.cos(a) * rx), cy - (int) (Math.sin(a) * ry));
        }
        final double a = (a2 * Math.PI * 2) / 360.0;
        vertex(cx + (int) (Math.cos(a) * rx), cx - (int) (Math.sin(a) * ry));
        gl.glEnd();
        checkerr();
    }

    public void fellipse(final int cx, final int cy, final int rx, final int ry) {
        fellipse(cx, cy, rx, ry, 0, 360);
    }

    public void fellipse(final Coord c, final Coord r) {
        fellipse(c.x, c.y, r.x, r.y, 0, 360);
    }

    public void rect(final Coord ul, final Coord sz) {
        final int top;
        final int bottom;
        final int left;
        final int right;

        top = ul.y;
        bottom = ul.y + sz.y - 1;
        left = ul.x;
        right = ul.x + sz.x - 1;

        lineX(top, left, right, 1);
        lineY(right, top, bottom, 1);
        lineX(bottom, right, left, 1);
        lineY(left, bottom, top, 1);
    }

    public void chcolor(final Color c) {
        this.color = c;
    }

    public void chcolor(final int r, final int g, final int b, final int a) {
        chcolor(Utils.clipcol(r, g, b, a));
    }

    public void chcolor() {
        chcolor(Color.WHITE);
    }

    Color getcolor() {
        return (color);
    }

    public GOut reclip(final Coord ul, final Coord sz) {
        final GOut g = new GOut(this);
        g.ul = this.ul.add(ul);
        g.sz = sz;
        return (g);
    }

    public void scale(final double d) {
        gl.glScaled(d, d, d);
    }
}
