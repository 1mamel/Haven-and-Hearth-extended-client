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
import java.awt.image.BufferedImage;

public class Button extends Widget {
    // Drawing resources start
    private static final TexI bl;
    private static final TexI br;
    private static final TexI bt;
    private static final TexI bb;
    private static final TexI dt;
    private static final TexI ut;
    private static final int bRigthWidth;
    private static final int bLeftWidth;
    private static final int bTopHeight;
    private static final int bBottomHeight;
    private static final int bLRWidth;
    private static final int bTBHeight;

    static {
        bl = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/left"));
        br = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/right"));
        bt = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/top"));
        bb = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/bottom"));
        dt = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/dtex"));
        ut = new TexI(Resource.loadimg("gfx/hud/buttons/tbtn/utex"));
        bRigthWidth = br.sz().x();
        bLeftWidth = bl.sz().x();
        bTopHeight = bt.sz().y();
        bBottomHeight = bb.sz().y();
        bLRWidth = bLeftWidth + bRigthWidth;
        bTBHeight = bTopHeight + bBottomHeight;
    }
    // EO resources

    private static final Text.Foundry tf = new Text.Foundry(new Font("Serif", Font.PLAIN, 12), Color.YELLOW);
    private static final Color DEFAULT_COLOR = Color.YELLOW;

    private Text text;
    private BufferedImage contentImage;
    private boolean isMouseDown = false;

    static {
        Widget.addtype("btn", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (new Button(c, (Integer) args[0], parent, (String) args[1]));
            }
        });
        Widget.addtype("ltbtn", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return (wrapped(c, (Integer) args[0], parent, (String) args[1]));
            }
        });
    }

    /**
     * Creates text-based button with wrapped text.
     *
     * @param c      relative coordinates in parent
     * @param width  width
     * @param parent parent widget
     * @param text   text to show
     */
    public static Button wrapped(Coord c, int width, Widget parent, String text) {
        Button ret = new Button(c, width, parent, tf.renderwrap(text, width - 10));
        return (ret);
    }

    /**
     * Creates text-based button.
     *
     * @param c      relative coordinates in parent
     * @param width  width
     * @param parent parent widget
     * @param text   text to show
     */
    public Button(Coord c, Integer width, Widget parent, String text) {
        this(c, width, parent, tf.render(text));
    }

    /**
     * Creates text-based button.
     *
     * @param c      relative coordinates in parent
     * @param width  width
     * @param parent parent widget
     * @param text   text to show
     */
    private Button(Coord c, Integer width, Widget parent, Text text) {
        super(c, new Coord(width, 19), parent);
        this.text = text;
        this.contentImage = this.text.img;
    }

    /**
     * Creates image-based button.
     *
     * @param c       relative coordinates in parent
     * @param width   width
     * @param parent  parent widget
     * @param content image
     */
    public Button(Coord c, Integer width, Widget parent, BufferedImage content) {
        super(c, new Coord(width, 19), parent);
        this.text = null;
        this.contentImage = content;
    }

    public synchronized void draw(GOut g) {
        //Graphics g = graphics();
        int width = sz.x();
        int height = sz.y();

        g.image(isMouseDown ? dt : ut, new Coord.U(bLeftWidth - 3, bTopHeight - 2), new Coord.U(width - (bLRWidth - 6), height - (bTBHeight - 4))); // Background
        g.image(bl, Coord.z, new Coord.U(bLeftWidth, height)); // Left Border
        g.image(br, new Coord.U(width - bRigthWidth, 0), new Coord.U(bRigthWidth, height)); // Rigth Border
        g.image(bt, new Coord.U(bLeftWidth, 0), new Coord.U(width - bLRWidth, bTopHeight)); // Top Border
        g.image(bb, new Coord.U(bLeftWidth, height - bBottomHeight), new Coord.U(width - bLRWidth, bBottomHeight)); // Bottom Border

        int textX = width / 2 - contentImage.getWidth() / 2;
        int textY = height / 2 - contentImage.getHeight() / 2;
        if (isMouseDown) {
            textX++;
            textY++;
        }
        g.image(contentImage, new Coord.U(textX, textY));
    }

    public void change(String text, Color col) {
        if (col == null) {
            col = DEFAULT_COLOR;
        }
        this.text = tf.render(text, col);
        this.contentImage = this.text.img;
    }

    public void change(String text) {
        change(text, DEFAULT_COLOR);
    }

    public void click() {
        wdgmsg("activate");
    }

    public void uimsg(String msg, Object... args) {
        if (msg.equals("ch")) { // Change text & image
            if (args.length > 1)
                change((String) args[0], (Color) args[1]);
            else
                change((String) args[0]);
        } else if (msg.equals("change_image")) { // Change only image
            changeImage((BufferedImage) args[0]);
        }
    }

    private void changeImage(BufferedImage arg) {
        this.contentImage = arg;
    }

    public boolean mousedown(Coord c, int button) {
        if (button != 1)
            return (false);
        isMouseDown = true;
        ui.grabmouse(this);
        return (true);
    }

    public boolean mouseup(Coord c, int button) {
        if (isMouseDown && button == 1) {
            isMouseDown = false;
            ui.ungrabmouse();
            if (c.isect(Coord.z, sz))
                click();
            return (true);
        }
        return (false);
    }

    public String getText() {
        if (text == null) return null;
        return text.text;
    }
}
