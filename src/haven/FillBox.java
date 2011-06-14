/**
 * @(#)FillBox.java
 *
 *
 * @author
 * @version 1.00 2009/10/23
 */

// extracted from GameOptions.java

package haven;

class FillBox extends Widget {
    final IBox borders = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
    protected int value;
    boolean mouseDown = false;

    FillBox(Coord loc, Coord size, int startValue, Widget parent) {
        super(loc, size, parent);
        value = startValue;
    }

    public void draw(GOut g) {
        borders.draw(g, Coord.z, sz);
        g.frect(Coord.z.add(10, 6), new Coord(value, sz.y - 12));
    }

    public boolean mousedown(Coord c, int button) {
        if (button == 1) {
            mouseDown = true;
            ui.grabmouse(this);
            if (c.x > 10 && c.x < 110)
                value = (c.x - 10) % 100;
            return true;
        }
        return super.mousedown(c, button);
    }

    public boolean mouseup(Coord c, int button) {
        if (button == 1 && mouseDown) {
            mouseDown = false;
            ui.ungrabmouse();
            return true;
        }
        return super.mouseup(c, button);
    }

    public void mousemove(Coord c) {
        if (mouseDown) {
            if (c.x > 10 && c.x < 110)
                value = (c.x - 10) % 100;
            wdgmsg(this, "change", value);
            return;
        }
        super.mousemove(c);
    }
}
