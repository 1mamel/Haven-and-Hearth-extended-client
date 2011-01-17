/**
 * @(#)CustomConfig.java
 *
 *
 * @author
 * @version 1.00 2009/10/19
 */

package haven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"UnusedDeclaration"})
public class CustomConfig {
    static UI ui;
    public static boolean debugMsgs = true;

    static class CharData {
        String name;
        int hudActiveBelt = 1;
        String[][] hudBelt = new String[SlenHud._BELTSIZE][SlenHud._BELTSIZE];

        CharData(String name) {
            this.name = name;
        }

        public String toString() {
            return "Name=\"" + name + '\"';
        }
    }

    //windows last positions
    public static Map<String, Coord> windowsCoordinates = new HashMap<String, Coord>();

    public static void setWindowPosition(String windowName, Coord coordinate) {
        windowsCoordinates.put(windowName, coordinate);
    }

    public static Coord getWindowPosition(String windowName, Coord defcoordinates) {
        if (!windowsCoordinates.containsKey(windowName)) return defcoordinates;
        return windowsCoordinates.get(windowName);
    }

    public static Coord windowSize = new Coord(800, 600);
    public static Coord windowCenter = windowSize.div(2);
    public static Coord invCoord = Coord.z;
    public static int sfxVol = 100;
    public static int musicVol = 100;
    public static String ircServerAddress = "irc.synirc.net";
    public static List<Listbox.Option> ircChannelList = new ArrayList<Listbox.Option>();
    public static List<CharData> characterList = new ArrayList<CharData>();
    public static String ircDefNick = "";
    public static String ircAltNick = "";
    public static CharData activeCharacter;
    public static AtomicInteger wdgtID = new AtomicInteger(-10); // for Userspace widgets
    public static boolean isMusicOn = true;
    public static boolean isSoundOn = true;
    public static boolean isIRCOn = true;
    public static boolean hasNightVision = false;
    public static boolean isSaveable = false;
    public static boolean noChars = true;
    public static CustomConsole console;

    public static boolean logLoad = false;
    public static boolean logSave = false;
    public static boolean logIRC = false;
    public static boolean logServerMessages = false;

    public static void setActiveCharacter(String name) {
        for (CharData cData : characterList) {
            if (cData.name.equalsIgnoreCase(name)) {
                activeCharacter = cData;
                CustomConfig.isSaveable = true;
                CustomConfig.noChars = false;
                return;
            }
        }
        activeCharacter = new CharData(name);
        characterList.add(activeCharacter);
        CustomConfig.isSaveable = true;
        CustomConfig.noChars = false;
    }

    public static void setWindowSize(int x, int y) {
        setWindowSize(new Coord(x, y));
    }

    public static void setWindowSize(Coord size) {
        windowSize = new Coord(size);
        windowCenter = windowSize.div(2);
    }

    public static double getSFXVolume() {
        return (double) sfxVol / 100;
    }
}
