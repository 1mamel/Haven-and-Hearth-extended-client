/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Bj�rn Johannessen <johannessen.bjorn@gmail.com>
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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class TextEntry extends Widget {
    final LineEdit buf;
    int sx;
    static final Text.Foundry fnd = new Text.Foundry(new Font("SansSerif", Font.PLAIN, 12), Color.BLACK);
    Text.Line tcache = null;
    public String text;
    public String badchars = "";
    public final boolean noNumbers = false;
    public final boolean noLetters = false;
    int pos, limit = 0;
    boolean prompt = false, pw = false;
    int cw = 0;

    static {
        Widget.addtype("text", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new TextEntry(c, (Coord) args[0], parent, (String) args[1]));
            }
        });
    }

    public void settext(final String text) {
        buf.setline(text);
    }

    public void uimsg(@NotNull final String name, final Object... args) {
        if (name.equals("settext")) {
            settext((String) args[0]);
        } else if (name.equals("get")) {
            wdgmsg("text", buf.line);
        } else if (name.equals("pw")) {
            pw = ((Integer) args[0]) == 1;
        } else {
            super.uimsg(name, args);
        }
    }

    public String getText() {
        return buf.line;
    }

    public void draw(final GOut g) {
        super.draw(g);
        final String dtext;
        if (pw) {        //	Replace the text with stars if its a password
            final StringBuilder b = new StringBuilder();
            for (int i = 0; i < buf.line.length(); i++)
                b.append('*');
            dtext = b.toString();
        } else {
            dtext = buf.line != null ? buf.line : "";
        }
        g.frect(Coord.z, sz);
        if ((tcache == null) || !tcache.text.equals(dtext))
            tcache = fnd.render(dtext);
        final int cx = tcache.advance(buf.point);
        if (cx < sx) sx = cx;
        if (cx > sx + (sz.x - 1)) sx = cx - (sz.x - 1);
        g.image(tcache.tex(), new Coord(-sx, 0));
        if (hasfocus && ((System.currentTimeMillis() % 1000) > 500)) {
            final int lx = cx - sx + 1;
            g.chcolor(0, 0, 0, 255);
            g.line(new Coord(lx, 1), new Coord(lx, tcache.sz().y - 1), 1);
            g.chcolor();
        }
    }

    public TextEntry(final Coord c, final Coord sz, final Widget parent, String deftext) {
        super(c, sz, parent);
        if (deftext == null) deftext = "";
        buf = new LineEdit(text = deftext) {
            protected void done(final String line) {
                activate(line);
            }

            protected void changed() {
                TextEntry.this.text = line;
            }
        };
        setcanfocus(true);
    }

    public void activate(final String text) {
        if (canactivate)
            wdgmsg("activate", text);
    }

    public boolean type(final char c, final KeyEvent ev) {
        if (Character.isDigit(c) && noNumbers && !ev.isAltDown() || badchars.indexOf(c) > -1) {
            ev.consume();
            return true;
        }
        if (Character.isLetter(c) && noLetters && !ev.isAltDown() || badchars.indexOf(c) > -1) {
            ev.consume();
            return true;
        }
        return (buf.key(ev));
    }

    public boolean keydown(final KeyEvent e) {
        buf.key(e);
        return (true);
    }

    public boolean mousedown(final Coord c, final int button) {
        parent.setfocus(this);
        if (tcache != null) {
            buf.point = tcache.charat(c.x + sx);
        }
        return (true);
    }

    /**
     * Method lostOwnership
     *
     * @param clipboard
     * @param contents
     */
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        // TODO: Add your code here
    }

    public static String getClipboardContents() {
        String result = "";
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        final Transferable contents = clipboard.getContents(null);
        final boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
