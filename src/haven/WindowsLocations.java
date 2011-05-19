package haven;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 15.01.11
 * Time: 16:55
 */
public class WindowsLocations {

    public static Coord getLocationByName(String name, Coord defLocation) {
        if (name == null) return defLocation;
        Coord nc = ourLocations.get(name);
        if (nc == null) {
            nc = defLocation;
            ourLocations.put(name,nc);
        }
        return nc;
    }

    public static void coordChanged(Window wnd, Coord newCoords) {
        if (wnd == null || wnd.cap == null || wnd.cap.text == null) return;
        ourLocations.put(wnd.cap.text, newCoords);
    }

    public static void loadFromFile() {
        // TODO: implement with json
    }

    public static void saveToFile() {
        // TODO: implement with json
    }

    static final Map<String, Coord> ourLocations = new HashMap<String, Coord>();
}
