package haven;

import haven.resources.layers.AButton;
import haven.resources.layers.Pagina;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class ToolbarWnd extends Window implements DTarget, DropTarget {
    public final static Tex bg = Resource.loadtex("gfx/hud/invsq");
    private static final int BELTS_NUM = 10;
    private static final BufferedImage ilockc = Resource.loadimg("gfx/hud/lockc");
    private static final BufferedImage ilockch = Resource.loadimg("gfx/hud/lockch");
    private static final BufferedImage ilocko = Resource.loadimg("gfx/hud/locko");
    private static final BufferedImage ilockoh = Resource.loadimg("gfx/hud/lockoh");
    public final static Coord bgsz = bg.sz().add(-1, -1);
    private static final Properties beltsConfig = new Properties();
    private Coord gsz, off, beltNumC;
    Resource pressed, dragging, layout[];
    private IButton lockButton, flipbtn, minus, plus;
    public boolean flipped = false, locked = false;
    public int belt, key;
    private Tex[] nums;
    private final String name;

    public final static RichText.Foundry ttfnd = new RichText.Foundry(TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, 10);

    public ToolbarWnd(final Coord c, final Widget parent, final String name) {
        super(c, Coord.z, parent, null);
        this.name = name;
        init(1, 10, new Coord(5, 10), KeyEvent.VK_0);
    }

    public ToolbarWnd(final Coord c, final Widget parent, final String name, final int belt, final int sz, final Coord off, final int key) {
        super(c, Coord.z, parent, null);
        this.name = name;
        init(belt, sz, off, key);
    }

    private void loadOpts() {
        if (Boolean.parseBoolean(CustomConfig.getWindowProperty(name + "_locked"))) {
            locked = true;
        }
        if (Boolean.parseBoolean(CustomConfig.getWindowProperty(name + "_flipped"))) {
            flip();
        }
        if (Boolean.parseBoolean(CustomConfig.getWindowProperty(name + "_folded"))) {
            folded = true;
            checkfold();
        }
        final String pos_prop = CustomConfig.getWindowProperty(name + "_pos");
        if (pos_prop != null) {
            c = new Coord(pos_prop);
        }
    }

    private void init(final int belt, final int sz, final Coord off, final int key) {
        gsz = new Coord(1, sz);
        this.off = off;
        foldButton.show();
        mrgn = new Coord(2, 18);
        layout = new Resource[sz];
        loadOpts();
        closeButton.visible = false;
        lockButton = new IButton(Coord.z, this, locked ? ilockc : ilocko, locked ? ilocko : ilockc, locked ? ilockch : ilockoh) {

            public void click() {
                locked = !locked;
                if (locked) {
                    up = ilockc;
                    down = ilocko;
                    hover = ilockch;
                } else {
                    up = ilocko;
                    down = ilockc;
                    hover = ilockoh;
                }
                CustomConfig.setWindowOpt(name + "_locked", locked);
            }
        };
        lockButton.recthit = true;
        flipbtn = new IButton(Coord.z, this, Resource.loadimg("gfx/hud/flip"), Resource.loadimg("gfx/hud/flip"), Resource.loadimg("gfx/hud/flipo")) {
            public void click() {
                flip();
            }
        };
        minus = new IButton(Coord.z, this, Resource.loadimg("gfx/hud/charsh/minusup"), Resource.loadimg("gfx/hud/charsh/minusdown")) {
            public void click() {
                prevBelt();
            }
        };
        plus = new IButton(Coord.z, this, Resource.loadimg("gfx/hud/charsh/plusup"), Resource.loadimg("gfx/hud/charsh/plusdown")) {
            public void click() {
                nextBelt();
            }
        };
        flipbtn.recthit = true;
        loadBelt(belt);
        this.key = key;
        pack();
        /* Text rendering is slow, so pre-cache the hotbar numbers. */
        nums = new Tex[sz];
        for (int i = 0; i < sz; i++) {
            final String slot = (key == KeyEvent.VK_0) ? Integer.toString(i) : "F" + Integer.toString(i + 1);
            nums[i] = Text.render(slot).tex();
        }
    }

    private void nextBelt() {
        loadBelt(belt + 2);
    }

    private void prevBelt() {
        loadBelt(belt - 2);
    }

    public static void loadBelts() {

        final String configFileName = "belts_" + Config.currentCharName.replaceAll("[^a-zA-Z()]", "_") + ".conf";
        try {
            synchronized (beltsConfig) {
                beltsConfig.load(new FileInputStream(configFileName));
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private void loadBelt(final int beltNr) {
        belt = beltNr % BELTS_NUM;
        if (belt < 0)
            belt += BELTS_NUM;
        synchronized (beltsConfig) {
            for (int slot = 0; slot < layout.length; slot++) {
                final String icon = beltsConfig.getProperty("belt_" + belt + "_" + slot, "");
                if (icon.length() > 0) {
                    layout[slot] = Resource.load(icon);
                } else {
                    layout[slot] = null;
                }
            }
        }
    }

    public static void saveBelts() {
        synchronized (beltsConfig) {
            final String configFileName = "belts_" + Config.currentCharName.replaceAll("[^a-zA-Z()]", "_") + ".conf";
            try {
                beltsConfig.store(new FileOutputStream(configFileName), "Belts actions for " + Config.currentCharName);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (checkIsCloseButton(sender))
            ui.destroy(this);
        final Boolean _folded = folded;
        if (checkIsFoldButton(sender))
            super.wdgmsg(sender, msg, args);
        if (_folded != folded) {
            CustomConfig.setWindowOpt(name + "_folded", folded);
        }
    }

    public void draw(final GOut g) {
        super.draw(g);
        if (folded)
            return;
        for (int y = 0; y < gsz.y; y++) {
            for (int x = 0; x < gsz.x; x++) {
                final Coord p = getcoord(x, y);
                g.image(bg, p);
                int slot = x + y;
                if (key == KeyEvent.VK_0)
                    slot = (slot + 1) % 10;
                g.aimage(nums[slot], p.add(bg.sz()), 1, 1);
                final Resource btn = layout[x + y];
                if (btn != null) {
                    final Tex btex = btn.layer(Resource.imgc).tex();
                    g.image(btex, p.add(1, 1));
                    if (btn == pressed) {
                        g.chcolor(new Color(0, 0, 0, 128));
                        g.frect(p.add(1, 1), btex.sz());
                        g.chcolor();
                    }
                }
            }
        }
        g.aimage(Text.renderNumber(belt).tex(), beltNumC, 1, 1);
        g.chcolor();
        if (dragging != null) {
            final Tex dt = dragging.layer(Resource.imgc).tex();
            ui.drawafter(new UI.AfterDraw() {
                public void draw(final GOut g) {
                    g.image(dt, ui.mc.add(dt.sz().div(2).inv()));
                }
            });
        }
    }

    private Coord getcoord(final int x, final int y) {
        final Coord p = xlate(bgsz.mul(new Coord(x, y)), true);
        if (off.x > 0)
            if (flipped) {
                p.setX(p.x + off.y * (x / off.x));
            } else {
                p.setY(p.y + off.y * (y / off.x));
            }
        return p;
    }

    public void checkfold() {
        super.checkfold();
        final Coord max = new Coord(ssz);
        if ((folded) && (flipped)) {
            max.setX(0);
            recalcsz(max);
        }
        placecbtn();
    }

    protected void recalcsz(final Coord max) {
        sz = max.add(wbox.bsz().add(mrgn.mul(2)).add(tlo).add(rbo)).add(-1, -1);
        wsz = sz.sub(tlo).sub(rbo);
        if (folded)
            if (flipped)
                wsz.setX(wsz.x / 2);
            else
                wsz.setY(wsz.y / 2);
        asz = wsz.sub(wbox.bl.sz()).sub(wbox.br.sz()).sub(mrgn.mul(2));
    }

    public void flip() {
        flipped = !flipped;
        gsz = gsz.swap();
        mrgn = mrgn.swap();
        pack();
        CustomConfig.setWindowOpt(name + "_flipped", flipped);
    }

    protected void placecbtn() {
        closeButton.c = new Coord(wsz.x - 3 - closeButtonImages[0].getWidth(), 3).sub(mrgn).sub(wbox.tloff());
        if (flipped) {
            foldButton.c = new Coord(closeButton.c.x, wsz.y - 3 - foldButtonImages[0].getHeight() - mrgn.y - wbox.tloff().y);
            if (lockButton != null)
                lockButton.c = new Coord(3 - wbox.tloff().x - mrgn.x, closeButton.c.y);
            if (flipbtn != null)
                flipbtn.c = new Coord(5 - wbox.tloff().x - mrgn.x, foldButton.c.y);
            if (plus != null)
                plus.c = closeButton.c.sub(16, 0);
            if (minus != null) {
                minus.c = foldButton.c.sub(16, 0);
                beltNumC = minus.c.add(plus.c).div(2).add(36, 22);
            }
        } else {
            foldButton.c = new Coord(3 - wbox.tloff().x, closeButton.c.y);
            if (lockButton != null)
                lockButton.c = new Coord(foldButton.c.x, wsz.y - 21 - mrgn.y - wbox.tloff().y);
            if (flipbtn != null)
                flipbtn.c = new Coord(closeButton.c.x - 2, wsz.y - 21 - mrgn.y - wbox.tloff().y);
            if (plus != null)
                plus.c = flipbtn.c.sub(0, 16);
            if (minus != null) {
                minus.c = lockButton.c.sub(0, 16);
                beltNumC = minus.c.add(plus.c).div(2).add(20, 38);
            }
        }
    }

    public void pack() {
        ssz = bgsz.mul(gsz);
        if (off.x > 0)
            if (flipped) {
                ssz.setX(ssz.x + off.y * ((gsz.x / off.x) - ((gsz.x % off.x == 0) ? 1 : 0)) + 16);
            } else {
                ssz.setY(ssz.y + off.y * ((gsz.y / off.x) - ((gsz.y % off.x == 0) ? 1 : 0)) + 16);
            }
        checkfold();
        placecbtn();
    }

    private Resource bhit(final Coord c) {
        final int i = index(c);
        if (i >= 0)
            return (layout[i]);
        else
            return (null);
    }

    private int index(final Coord c) {
        for (int y = 0; y < gsz.y; y++) {
            for (int x = 0; x < gsz.x; x++) {
                if (c.isect(getcoord(x, y), bgsz))
                    return x + y;
            }
        }
        return -1;
    }

    public boolean mousedown(final Coord c, final int button) {
        final Resource h = bhit(c);
        if (button == 1) {
            if (h != null) {
                pressed = h;
                ui.grabmouse(this);
            } else {
                super.mousedown(c, button);
            }
        }
        return (true);
    }

    public boolean mouseup(final Coord c, final int button) {
        final Resource h = bhit(c);
        if (button == 1) {
            if (dragging != null) {
                UI.dropthing(ui.root, ui.mc, dragging);
                dragging = pressed = null;
            } else if (pressed != null) {
                if (pressed == h)
                    if (ui.mnu != null)
                        ui.mnu.use(h);
                pressed = null;
            }
            ui.ungrabmouse();
        }
        if (dm) {
            CustomConfig.setWindowOpt(name + "_pos", this.c.toString());
        }
        super.mouseup(c, button);

        return (true);
    }

    public void mousemove(final Coord c) {
        if ((!locked) && (dragging == null) && (pressed != null)) {
            dragging = pressed;
            final int slot = index(c);
            if (slot >= 0) {
                layout[slot] = null;
            }
            pressed = null;
            setBeltSlot(slot, "");
        } else {
            super.mousemove(c);
        }

    }

    public boolean drop(final Coord cc, final Coord ul, final Item item) {
        return (true);
    }

    public boolean iteminteract(final Coord cc, final Coord ul) {
        return (true);
    }

    public boolean dropthing(final Coord c, final Object thing) {
        if ((!locked) && (thing instanceof Resource)) {
            final int slot = index(c);
            final Resource res = (Resource) thing;
            setBeltSlot(slot, res.name);
            layout[slot] = res;
            return true;
        }
        return false;
    }

    private void setBeltSlot(final int slot, final String icon) {
        final String key = "belt_" + belt + '_' + slot;
        synchronized (beltsConfig) {
            beltsConfig.setProperty(key, icon);
        }
        saveBelts();
    }

    private Resource curttr = null;
    private boolean curttl = false;
    private Text curtt = null;
    private long hoverstart;

    public Object tooltip(final Coord c, final boolean again) {
        final Resource res = bhit(c);
        final long now = System.currentTimeMillis();
        if ((res != null) && (res.layer(Resource.action) != null)) {
            if (!again)
                hoverstart = now;
            final boolean ttl = (now - hoverstart) > 500;
            if ((res != curttr) || (ttl != curttl)) {
                curtt = rendertt(res, ttl);
                curttr = res;
                curttl = ttl;
            }
            return (curtt);
        } else {
            hoverstart = now;
            return ("");
        }
    }

    private static Text rendertt(final Resource res, final boolean withpg) {
        final AButton ad = res.layer(Resource.action);
        final Pagina pg = res.layer(Resource.pagina);
        String tt = ad.name;
        if (withpg && (pg != null)) {
            tt += "\n\n" + pg.text;
        }
        return (ttfnd.render(tt, 0));
    }

    private boolean checkKey(final char ch, final KeyEvent ev) {
        final int code = ev.getKeyCode();
        int slot = code - key;
        final boolean alt = ev.isAltDown();
        final boolean ctrl = ev.isControlDown();
        if (alt && key == KeyEvent.VK_F1) {
            slot = code - KeyEvent.VK_0;
            if ((slot > 0) && (slot <= 5)) {
                loadBelt(slot * 2);
                return true;
            }
        } else if (ctrl && key == KeyEvent.VK_0) {
            slot = code - KeyEvent.VK_0;
            if ((slot > 0) && (slot <= 5)) {
                slot = ((slot - 1) << 1) + 1;
                loadBelt(slot);
                return true;
            }
        } else if (!alt && !ctrl && (slot >= 0) && (slot < gsz.x * gsz.y)) {
            if (key == KeyEvent.VK_0)
                slot = (slot == 0) ? 9 : slot - 1;
            final Resource h = layout[slot];
            if ((h != null) && (ui.mnu != null))
                ui.mnu.use(h);
            return true;
        }
        return false;
    }

    public boolean globtype(final char ch, final KeyEvent ev) {
        if (!checkKey(ch, ev))
            return (super.globtype(ch, ev));
        else
            return true;
    }

    public boolean type(final char key, final KeyEvent ev) {
        if (key == 27) {
            wdgmsg(foldButton, "click");
            return (true);
        }
        if (!checkKey(key, ev))
            return (super.type(key, ev));
        else
            return true;
    }

}
