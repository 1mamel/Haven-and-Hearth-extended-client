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
        if (ourLocations.containsKey(name)) {
            return ourLocations.get(name);
        } else {
            ourLocations.put(name, defLocation);
            return defLocation;
        }
    }

    public static void coordChanged(Window wnd, Coord newCoords) {
        if (wnd.cap == null) return;
        String name = wnd.cap.text;
        if (name == null) return;
        ourLocations.put(name, newCoords);
    }

    public static void loadFromFile() {
        // TODO: implement with json
    }

    public static void saveToFile() {
        // TODO: implement with json
    }

    static final Map<String, Coord> ourLocations = new HashMap<String, Coord>();
}
