package haven;

import haven.resources.Resource;

import java.awt.image.BufferedImage;

public class MinimapPanel extends Window {

    static final BufferedImage grip = Resource.loadimg("gfx/hud/gripbr");
    static final Coord gzsz = new Coord.U(16, 17);
    static final Coord minsz = new Coord.U(150, 125);

    boolean rsm = false;
    MiniMap mm;
    IButton btncave;

    public MinimapPanel(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent, "Minimap");
        mrgn = Coord.z;
        foldButton.visible = true;
        closeButton.visible = false;
        {
            new IButton(new Coord(0, -2), this, Resource.loadimg("gfx/hud/slen/dispauth"), Resource.loadimg("gfx/hud/slen/dispauthd")) {
                private boolean v = false;

                public void click() {
                    MapView mv = ui.mainview;
                    BufferedImage tmp = down;
                    down = up;
                    up = tmp;
                    hover = tmp;
                    if (v) {
                        mv.disol(2, 3);
                        v = false;
                    } else {
                        mv.enol(2, 3);
                        v = true;
                    }
                }
            };
        }
        {
            new IButton(new Coord(0, 4), this, Resource.loadimg("gfx/hud/slen/dispclaim"), Resource.loadimg("gfx/hud/slen/dispclaimd")) {
                private boolean v = false;

                public void click() {
                    MapView mv = ui.mainview;
                    BufferedImage tmp = down;
                    down = up;
                    up = tmp;
                    hover = tmp;
                    if (v) {
                        mv.disol(0, 1);
                        v = false;
                    } else {
                        mv.enol(0, 1);
                        v = true;
                    }
                }
            };
        }

        mm = new MiniMap(new Coord(0, 32), minsz, this, ui.mainview);

        new IButton(new Coord(45, 8), this, Resource.loadimg("gfx/hud/buttons/gridu"), Resource.loadimg("gfx/hud/buttons/gridd")) {
            public void click() {
                BufferedImage tmp = down;
                down = up;
                up = tmp;
                hover = tmp;
                mm.grid = !mm.grid;
            }
        };

        new IButton(new Coord(65, 8), this, Resource.loadimg("gfx/hud/buttons/centeru"), Resource.loadimg("gfx/hud/buttons/centerd")) {
            public void click() {
                mm.off = Coord.z;
            }
        };

        new IButton(new Coord(88, 12), this, Resource.loadimg("gfx/hud/charsh/plusup"), Resource.loadimg("gfx/hud/charsh/plusdown")) {
            public void click() {
                mm.setScale(mm.scale + 1);
            }
        };

        new IButton(new Coord(103, 12), this, Resource.loadimg("gfx/hud/charsh/minusup"), Resource.loadimg("gfx/hud/charsh/minusdown")) {
            public void click() {
                mm.setScale(mm.scale - 1);
            }
        };

        btncave = new IButton(new Coord(121, 8), this, Resource.loadimg("gfx/hud/buttons/saveu"), Resource.loadimg("gfx/hud/buttons/saved")) {
            public void click() {
                mm.saveCaveMaps();
            }
        };

        pack();
        this.c = new Coord(CustomConfig.getWindowWidth() - this.sz.getX(), 7);
    }

    protected void placecbtn() {
        foldButton.c = new Coord(wsz.getX() - 3 - closeButtonImages[0].getWidth(), 3).add(mrgn.inv().add(wbox.tloff().inv()));
        //fbtn.c = new Coord(cbtn.c.x - 1 - Utils.imgsz(fbtni[0]).x, cbtn.c.y);
    }

    public void draw(GOut g) {
        super.draw(g);
        btncave.visible = !folded && mm.isCave();
        if (!folded)
            g.image(grip, sz.sub(gzsz));
    }

    public boolean mousedown(Coord c, int button) {
        if (folded) {
            return super.mousedown(c, button);
        }
        parent.setfocus(this);
        raise();
        if (button == 1) {
            ui.grabmouse(this);
            doff = c;
            if (c.isect(sz.sub(gzsz), gzsz)) {
                rsm = true;
                return true;
            }
        }
        return super.mousedown(c, button);
    }

    public boolean mouseup(Coord c, int button) {
        if (rsm) {
            ui.grabmouse(null);
            rsm = false;
        } else {
            super.mouseup(c, button);
        }
        return (true);
    }

    public void mousemove(Coord c) {
        if (rsm) {
            Coord d = c.sub(doff);
            mm.sz = mm.sz.add(d);
            mm.sz.setX(Math.max(minsz.getX(), mm.sz.getX()));
            mm.sz.setY(Math.max(minsz.getY(), mm.sz.getY()));
            doff = c;
            pack();
        } else {
            super.mousemove(c);
        }
    }

    public boolean type(char key, java.awt.event.KeyEvent ev) {
        if (key == 27) {
            wdgmsg(foldButton, "click");
            return (true);
        }
        return (super.type(key, ev));
    }

}
