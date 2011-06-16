package haven.scriptengine.providers;

import haven.Coord;
import haven.CustomConfig;
import haven.Gob;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 22:41
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class Util {

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
                boolean matched = false;
                if (type.equals("tree")) {
                    // searching for trees with growth stage 0
                    String resName = gob.getResName();
                    matched = resName.contains("trees") && resName.indexOf('0') >= 0;
                }

                if (matched) {
                    double len = gob.getc().distSq(my);
                    if (len < foundedDistance) {
                        foundedDistance = len;
                        foundedObject = gob;
                    }
                }
            }
        }
        if (foundedObject != null) {
            return foundedObject.id;
        } else {
            return 0;
        }
    }

}
