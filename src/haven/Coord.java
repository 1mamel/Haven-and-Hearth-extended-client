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

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.PI;

public class Coord implements Comparable<Coord>, java.io.Serializable, Cloneable {
    public static final Pattern parsePattern = Pattern.compile("\\((-?\\d+), (-?\\d+)\\)");
    public static final Coord z = new Coord(0, 0) {
        @Override
        public void setX(int x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setY(int y) {
            throw new UnsupportedOperationException();
        }
    };

    private int x;
    private int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord(Coord c) {
        this(c.x, c.y);
    }

    @Deprecated
    public Coord() {
        this(0, 0);
    }

    public Coord(String str) {
        Matcher m = parsePattern.matcher(str);
        if (m.find()) {
            x = Integer.parseInt(m.group(1));
            y = Integer.parseInt(m.group(2));
        } else {
            throw new IllegalArgumentException("Point format " + parsePattern.pattern() + " not founded in " + str);
        }
    }

    public Coord(Dimension d) {
        this(d.width, d.height);
    }

    /**
     * Creates new decart coodinates from polar
     *
     * @param a angle in radians
     * @param r radius
     * @return decart coordinates
     */
    public static Coord sc(double a, double r) {
        return (new Coord((int) (Math.cos(a) * r), -(int) (Math.sin(a) * r)));
    }

    public boolean equals(Object o) {
        if (!(o instanceof Coord))
            return (false);
        Coord c = (Coord) o;
        return ((c.x == x) && (c.y == y));
    }

    public int compareTo(Coord c) {
        if (c.y != y)
            return (c.y - y);
        if (c.x != x)
            return (c.x - x);
        return (0);
    }

    public Coord add(int ax, int ay) {
        return (new Coord(x + ax, y + ay));
    }

    public Coord add(Coord b) {
        return (add(b.x, b.y));
    }

    public Coord sub(int ax, int ay) {
        return (new Coord(x - ax, y - ay));
    }

    public Coord sub(Coord b) {
        return (sub(b.x, b.y));
    }

    public Coord mul(int f) {
        return (new Coord(x * f, y * f));
    }

    public Coord mul(double f) {
        return (new Coord((int) (x * f), (int) (y * f)));
    }

    public Coord inv() {
        return (new Coord(-x, -y));
    }

    public Coord mul(Coord f) {
        return (new Coord(x * f.x, y * f.y));
    }

    public Coord mul(final int mx, final int my) {
        return (new Coord(x * mx, y * my));
    }

    public Coord div(final Coord d) {
        return div(d.x, d.y);
    }

    public Coord div(final int d) {
        return div(d, d);
    }

    public Coord div(final int dx, final int dy) {
        int v, w;

        v = ((x < 0) ? (x + 1) : x) / dx;
        w = ((y < 0) ? (y + 1) : y) / dy;
        if (x < 0)
            v--;
        if (y < 0)
            w--;

        return (new Coord(v, w));
    }

    public Coord div(double f) {
        return (new Coord((int) (x / f), (int) (y / f)));
    }

    public Coord mod(Coord d) {
        int v, w;

        v = x % d.x;
        w = y % d.y;
        if (v < 0)
            v += d.x;
        if (w < 0)
            w += d.y;
        return (new Coord(v, w));
    }

    @SuppressWarnings({"SuspiciousNameCombination"})
    public Coord swap() {
        return new Coord(y, x);
    }

    public Coord abs() {
        return new Coord(Math.abs(x), Math.abs(y));
    }

    public int sum() {
        return x + y;
    }

    public boolean isect(Coord c, Coord s) {
        return ((x >= c.x) && (y >= c.y) && (x < c.x + s.x) && (y < c.y + s.y));
    }

    public String toString() {
        return ("(" + x + ", " + y + ')');
    }

    public double angle(Coord o) {
        Coord c = o.sub(this);
        if (c.x == 0) {
            if (c.y < 0)
                return (-PI / 2);
            else
                return (PI / 2);
        } else {
            if (c.x < 0) {
                if (c.y < 0)
                    return (-PI + Math.atan((double) c.y / (double) c.x));
                else
                    return (PI + Math.atan((double) c.y / (double) c.x));
            } else {
                return (Math.atan((double) c.y / (double) c.x));
            }
        }
    }

    public double dist(Coord o) {
        long dx = o.x - x;
        long dy = o.y - y;
        return (Math.sqrt((dx * dx) + (dy * dy)));
    }

    @Override
    protected Coord clone() {
        try {
            return (Coord) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Coord(x, y);
        }
    }

    public Dimension toDimension() {
        return new Dimension(x, y);
    }

    public int x() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int y() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord getUnmodifiableVersion() {
        return new U(this);
    }

    /**
     * Inmodifiable version of Coord.
     * Throws UnsupportedOperationException if any setter invoked.
     */
    public static class U extends Coord {
        public U(Coord c) {
            super(c);
        }

        public U(int x, int y) {
            super(x, y);
        }

        public U(Dimension d) {
            super(d);
        }

        @Override
        public void set(int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setY(int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setX(int x) {
            throw new UnsupportedOperationException();
        }
    }
}
