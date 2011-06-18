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


    public static MapView getMV() {
        return UI.instance.mainview;
    }

    public static Coord getCenterR() {
        Coord center = UI.instance.mainview.sz.div(2);
        int dx = (int) ((Math.random() - 0.5) * 200);
        int dy = (int) ((Math.random() - 0.5) * 200);
        return center.add(dx, dy);
    }

    public static Coord getSize() {
        return UI.instance.mainview.sz;
    }

    protected static Glob glob() {
        return CustomConfig.glob;
    }

    public static Gob getGob(int id) {
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
    public static int findObjectByType(String type, int radius) {
        Coord my = Player.getPosition();
        double foundedDistance = radius * 11;
        foundedDistance *= foundedDistance;
        Gob foundedObject = null;


        synchronized (CustomConfig.glob.oc) {
            for (Gob gob : CustomConfig.glob.oc) {
                // TODO: Сначала отсекать по прямоугольнику
                double len = gob.getc().distSq(my);
                if (len > foundedDistance) {
                    continue;
                }

                boolean matched;
                if (type.equals("tree")) {
                    // searching for trees with growth stage 0
                    String resName = gob.getResName();
                    matched = resName.contains("trees") && resName.indexOf('0') >= 0;
                } else {
                    String resName = gob.getResName();
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
    public static int findObjectByName(String name, int radius, int offX, int offY) {
        Coord my = Player.getPosition();
        if (my == null) {
            return 0;
        }
        Coord offset = tilesz.mul(offX, offY);
        Coord start = offset.add(MapView.tilify(my));

        double foundedDistance = radius;
        foundedDistance *= foundedDistance;
        Gob foundedObject = null;

        synchronized (glob().oc) {
            for (Gob gob : glob().oc) {
                // TODO: Сначала отсекать по прямоугольнику
                double len = gob.getc().distSq(start);
                if (len > foundedDistance) {
                    continue;
                }
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

    public static void click(int objId, int buttons, int mode) {
        Gob o = getGob(objId);
        if (o == null) {
            return;
        }
        Coord oc = o.getc();
        getMV().wdgmsg("click", getCenterR(), oc, buttons, mode, objId, oc);
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
    public static void click(int dx, int dy, int buttons, int mode) {
        Coord mc = Player.getPosition();
        if (mc == null) {
            return;
        }
        Coord offset = tilesz.mul(dx, dy);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("click", getCenterR(), mc, buttons, mode);
    }


    /**
     * Click on map.
     * Coordinates are absolute.
     * Note: coordinates in tiles.
     *
     * @param x
     * @param y
     * @param buttons
     * @param mode
     */
    public static void clickAbs(int x, int y, int buttons, int mode) {
        Coord mc = new Coord(x, y);
        getMV().wdgmsg("click", getCenterR(), mc, buttons, mode);
    }

    // клик взаимодействия по карте с объектом. координаты относительные.
    public static void interactClick(int x, int y, int mode) {
        Coord mc = Player.getPosition();
        if (mc == null) {
            return;
        }
        Coord offset = new Coord(x, y).mul(tilesz);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("itemact", getCenterR(), mc, mode);
    }

    public static void interactClick(int id, int mode) {
        Gob pgob = Player.getGob();
        Gob gob = getGob(id);
        if (pgob == null || gob == null) {
            return;
        }
        Coord mc = gob.getc();
        getMV().wdgmsg("itemact", getCenterR(), mc, mode, id, mc);
    }

    public static void interactClickAbs(int x, int y, int mode) {
        Coord mc = new Coord(x, y);
        getMV().wdgmsg("itemact", getCenterR(), mc, mode);
    }

    /**
     * Placing building object.
     * Relative coordinates
     *
     * @param x
     * @param y
     * @param button
     * @param mode
     */
    public static void place(int x, int y, int button, int mode) {
        Coord mc = Player.getPosition();
        if (getMV().plob == null || mc == null) {
            return;
        }
        Coord offset = new Coord(x, y).mul(tilesz);
        mc = MapView.tilify(mc).add(offset);
        getMV().wdgmsg("place", mc, button, mode);
    }


    // дропнуть вещь которую держим в руках
    public static void drop(int mode) {
        getMV().wdgmsg("drop", mode);
    }


    public static int getObjectBlob(int id, int index) {
        Gob gob = glob().oc.getgob(id);
        if (gob == null) return -1;
        return gob.getBlob(index);
    }

}
