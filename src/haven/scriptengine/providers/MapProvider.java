package haven.scriptengine.providers;

import haven.*;

import static haven.MCache.tilesz;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.06.11
 * Time: 16:48
 *
 * @author Vlad.Rassokhin@gmail.com
 */
@SuppressWarnings({"UnusedDeclaration"})
public class MapProvider {

    /**
     * Return current MapView
     *
     * @return current MapView
     */
    public static MapView getMV() {
        return UI.instance.mainview;
    }

    public static Coord getCenterR() {
        final Coord center = UI.instance.mainview.sz.div(2);
        final int dx = (int) ((Math.random() - 0.5) * 200);
        final int dy = (int) ((Math.random() - 0.5) * 200);
        return center.add(dx, dy);
    }

    public static Coord getSize() {
        return UI.instance.mainview.sz;
    }

    protected static Glob glob() {
        return CustomConfig.current().getGlob();
    }

    public static Gob getGob(final int id) {
        synchronized (glob().oc) {
            return glob().oc.getgob(id);
        }
    }

    /**
     * Finds object by type near player
     *
     * @param type   object type
     * @param radius searching radius
     * @return object id of nearest object of that type, 0 otherwise
     */
    public static int findObjectByType(final String type, final int radius) {
        final Coord my = Player.getPosition();
        double foundedDistance = radius * 11;
        foundedDistance *= foundedDistance; // Used suqare becase sqrt are slow operation
        Gob foundedObject = null;

        // Creting rectangle.
        final int left = Math.abs(my.x) - radius;
        final int right = Math.abs(my.x) + radius;
        final int top = Math.abs(my.y) - radius;
        final int bottom = Math.abs(my.y) + radius;

        synchronized (glob().oc) {
            for (final Gob gob : glob().oc) {
                final Coord gobPos = gob.getc();
                // First. Check that the rectangle contains the gobPos
                final int x = Math.abs(gobPos.x);
                final int y = Math.abs(gobPos.y);
                if (x < left || x > right || y > top || y < bottom) {
                    continue;
                }
                // First check faster than second.
                // Second. Check is gobPos in circle.
                final double len = gobPos.distSq(my);
                if (len > foundedDistance) {
                    continue;
                }
                // Thrid. Checking type
                final boolean matched;
                if (type.equals("tree")) {
                    // searching for trees with growth stage 0
                    final String resName = gob.getResName();
                    matched = resName.contains("trees") && resName.indexOf('0') >= 0;
                } else {
                    final String resName = gob.getResName();
                    matched = resName.contains(type);
                }

                if (matched) {
                    foundedDistance = len;
                    foundedObject = gob;
                }

            }
        }
        if (foundedObject != null) {
            return foundedObject.id;
        } else {
            return 0;
        }
    }

    /**
     * Finds object by name near player
     *
     * @param name   object name
     * @param radius searching radius in pixels (pixel == 11 * tile)
     * @param offX   offset by X in tiles
     * @param offY   offset by Y in tiles
     * @return object id of nearest object of that type, 0 otherwise
     */
    public static int findObjectByName(final String name, final int radius, final int offX, final int offY) {
        final Coord my = Player.getPosition();
        if (my == null) {
            return 0;
        }
        final Coord offset = tilesz.mul(offX, offY);
        final Coord start = offset.add(MapView.tilify(my));

        double foundedDistance = radius;
        foundedDistance *= foundedDistance;
        Gob foundedObject = null;


        // Creting rectangle.
        final int left = Math.abs(start.x) - radius;
        final int right = Math.abs(start.x) + radius;
        final int top = Math.abs(start.y) - radius;
        final int bottom = Math.abs(start.y) + radius;

        synchronized (glob().oc) {
            for (final Gob gob : glob().oc) {
                final Coord gobPos = gob.getc();
                // First. Check that the rectangle contains the gobPos
                final int x = Math.abs(gobPos.x);
                final int y = Math.abs(gobPos.y);
                if (x < left || x > right || y > top || y < bottom) {
                    continue;
                }
                // First check faster than second.
                // Second. Check is gobPos in circle.
                final double len = gobPos.distSq(start);
                if (len > foundedDistance) {
                    continue;
                }
                // Thrid. Checking name
                if (name.length() == 0 || gob.getResName().contains(name)) {
                    foundedDistance = len;
                    foundedObject = gob;
                }
            }
        }
        if (foundedObject != null) {
            return foundedObject.id;
        } else {
            return 0;
        }
    }

    /**
     * Click on map object.
     *
     * @param objectId object id
     * @param buttons
     * @param mode
     */
    public static void click(final int objectId, final int buttons, final int mode) {
        final Gob o = getGob(objectId);
        if (o == null) {
            return;
        }
        final Coord oc = o.getc();
        getMV().wdgmsg("click", getCenterR(), oc, buttons, mode, objectId, oc);
    }

    /**
     * Click on map.
     * Coordinates are relative of user.
     * Note: coordinates in tiles.
     *
     * @param dx
     * @param dy
     * @param buttons
     * @param mode
     */
    public static void click(final int dx, final int dy, final int buttons, final int mode) {
        Coord mc = Player.getPosition();
        if (mc == null) {
            return;
        }
        final Coord offset = tilesz.mul(dx, dy);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("click", getCenterR(), mc, buttons, mode);
    }


    /**
     * Click on map.
     * Coordinates are absolute.
     * Note: coordinates in pixels.
     *
     * @param x
     * @param y
     * @param buttons
     * @param mode
     */
    public static void clickAbs(final int x, final int y, final int buttons, final int mode) {
        final Coord mc = new Coord(x, y);
        getMV().wdgmsg("click", getCenterR(), mc, buttons, mode);
    }

    // клик взаимодействия по карте с объектом. координаты относительные.
    public static void interactClick(final int x, final int y, final int mode) {
        Coord mc = Player.getPosition();
        if (mc == null) {
            return;
        }
        final Coord offset = new Coord(x, y).mul(tilesz);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("itemact", getCenterR(), mc, mode);
    }

    public static void interactClickObj(final int id, final int mode) {
        final Gob pgob = Player.getGob();
        final Gob gob = getGob(id);
        if (pgob == null || gob == null) {
            return;
        }
        final Coord mc = gob.getc();
        getMV().wdgmsg("itemact", getCenterR(), mc, mode, id, mc);
    }

    public static void interactClickAbs(final int x, final int y, final int mode) {
        final Coord mc = new Coord(x, y);
        getMV().wdgmsg("itemact", getCenterR(), mc, mode);
    }

    /**
     * Placing building object.
     * Relative coordinates in TILES.
     *
     * @param dx
     * @param dy
     * @param button
     * @param mode
     */
    public static void place(final int dx, final int dy, final int button, final int mode) {
        Coord mc = Player.getPosition();
        if (getMV().plob == null || mc == null) {
            return;
        }
        final Coord offset = new Coord(dx, dy).mul(tilesz);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("place", mc, button, mode);
    }


    // дропнуть вещь которую держим в руках
    public static void drop(final int mode) {
        getMV().wdgmsg("drop", mode);
    }


    public static int getObjectBlob(final int id, final int index) {
        final Gob gob = glob().oc.getgob(id);
        if (gob == null) return -1;
        return gob.getBlob(index);
    }

}
