package haven;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 15.01.11
 * Time: 16:55
 */
public class WindowsLocations {

    @NotNull
    public static Coord getLocationByName(@Nullable final String name, @NotNull final Coord defLocation) {
        if (name == null) return defLocation;
        Coord nc = ourContainer.locations.get(name);
        if (nc == null) {
            nc = defLocation;
            ourContainer.locations.put(name, nc);
        }
        return nc;
    }

    public static void coordChanged(@Nullable final Window wnd, @NotNull final Coord newCoords) {
        if (wnd == null || wnd.cap == null || wnd.cap.text == null) return;
        ourContainer.locations.put(wnd.cap.text, newCoords);
    }

    static class LocationsContainer {
        final Map<String, Coord> locations = new HashMap<String, Coord>();

        public static LocationsContainer deserialize(@NotNull final String string) {
            return new Gson().fromJson(string, LocationsContainer.class);
        }

        public static String serialize(@NotNull final LocationsContainer lc) {
            return new Gson().toJson(lc);
        }
    }

    @NotNull
    static LocationsContainer ourContainer = new LocationsContainer();
}
